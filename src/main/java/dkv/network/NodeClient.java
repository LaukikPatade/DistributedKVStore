package dkv.network;
import java.io.*;
import java.net.*;
import java.util.List;

import com.google.gson.*;
import dkv.cluster.ClusterManager;
import dkv.cluster.ClusterManager.NodeInfo;
import dkv.cluster.ConsistencyLevel;
import org.w3c.dom.Node;

public class NodeClient {
    private final String host;
    private final int port;
    private final Gson gson=new Gson();
    private final ClusterManager cm;

    // for direct node communication
    public NodeClient(String host, int port) {
        this.cm=null;
        this.host = host;
        this.port = port;
    }
    // for cluster based routing
    public NodeClient(ClusterManager cm) {
        this.cm = cm;
        this.host = null;
        this.port = -1;
    }

    public String put(String key, String value, ConsistencyLevel level) throws IOException {
        if(this.cm==null){
            return put(key,value);
        }
        List<NodeInfo> replicas=cm.getReplicaNodes(key);
        int needed=cm.acksRequired(level);
        int success=0;
        String lastStatus="";

        Request req=new Request("PUT",key,value);
        for (NodeInfo e:replicas){
            Response r=sendTo(e,req);
            lastStatus=r.status;
            if(r.status.equals("OK")){
                success++;
                if(success>=needed) return "OK";
            }
        }

        return lastStatus;
    }
    public String delete(String key,ConsistencyLevel level) throws IOException {
        if(this.cm==null){
            return delete(key);
        }
        List<NodeInfo> replicas=cm.getReplicaNodes(key);
        int needed=cm.acksRequired(level);
        int success=0;
        String lastStatus="";

        Request req=new Request("DELETE",key,null);
        for (NodeInfo e:replicas){
            Response r=sendTo(e,req);
            lastStatus=r.status;
            if(r.status.equals("OK")){
                success++;
                if(success>=needed) return "OK";
            }
        }

        return lastStatus;
    }
    public String get(String key,ConsistencyLevel level) throws IOException {
        if(this.cm==null)  return get(key);

        List<NodeInfo> replicas=this.cm.getReplicaNodes(key);
        int needed=cm.acksRequired(level);
        int success=0;
        String result=null;

        Request req = new Request("GET", key, null);

        for (NodeInfo node : replicas) {
            Response r = sendTo(node, req);

            if (r.value != null && !"null".equals(r.value)) {
                result = r.value;
                success++;
                if (success >= needed) return result;
            }
        }
        return result;
    }

    public String put(String key, String value) throws IOException {
        return put(key, value, ConsistencyLevel.ONE);
    }

    public String delete(String key) throws IOException {
        return delete(key, ConsistencyLevel.ONE);
    }

    public String get(String key) throws IOException {
        return get(key, ConsistencyLevel.ONE);
    }



    private Response send(Request req) throws IOException {
        try(Socket socket=new Socket(host, port);
        PrintWriter out=new PrintWriter(socket.getOutputStream(),true);
        BufferedReader in=new BufferedReader(new InputStreamReader(socket.getInputStream()));){
            String json=gson.toJson(req);
            out.println(json);

            String resJson=in.readLine();
            if (resJson == null || resJson.trim().isEmpty()) {
                throw new IOException("Received null or empty response from server");
            }
            return gson.fromJson(resJson,Response.class);
        }
    }
    private Response sendTo(NodeInfo info,Request req) {
        try{
            return new NodeClient(info.getHost(),info.getPort()).send(req);
        }catch (Exception e) {
            return new Response("ERR", "Node unreachable");
        }
    }
}

