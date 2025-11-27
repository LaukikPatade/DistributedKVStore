package dkv.cluster;
import dkv.KeyValueStore;
import dkv.network.NodeServer;
import dkv.routing.ConsistentHashRing;

import java.io.IOException;
import java.security.Key;
import java.util.*;

public class ClusterManager {
    private final ConsistentHashRing ring;
    private final Map<String,NodeInfo> clusterMap;
    private int rf=2;

    public ClusterManager() {
        this.ring = new ConsistentHashRing();
        this.clusterMap=new HashMap<>();
    }
    public int acksRequired(ConsistencyLevel level) {
        return switch(level){
            case ONE -> 1;
            case QUORUM -> (rf/2)+1;
            case ALL -> rf;
        };
    }
    public void startCluster() throws Exception {
        startNode("NodeA", 5050, "commitA.log");
        startNode("NodeB", 5051, "commitB.log");
        startNode("NodeC", 5052, "commitC.log");
    }
    private void startNode(String id, int port, String logFile) throws Exception {
        KeyValueStore store = new KeyValueStore(logFile);
        NodeServer server = new NodeServer(port, store);

        new Thread(() -> {
            try { server.start(); }
            catch (Exception e) { e.printStackTrace(); }
        }).start();

        ring.addNode(id);
        clusterMap.put(id, new NodeInfo("localhost", port));

        System.out.println("Started " + id + " on port " + port);
    }

    public NodeInfo getPrimaryNodeForKey(String key){
        String nodeId=ring.getNodeByKey(key);
        return clusterMap.get(nodeId);
    }

    public List<NodeInfo> getReplicaNodes(String key){
        List<String> nodeIds=ring.getReplicasForKey(key,rf);
        List<NodeInfo> replicaNodes=new ArrayList<>();
        for (String id:nodeIds){
            NodeInfo node=clusterMap.get(id);
            if(node!=null){
                replicaNodes.add(node);
            }
        }
        return replicaNodes;
    }

    public void addNode(String nodeId, int port) throws Exception {
        System.out.println("Adding node " + nodeId + " on port " + port);

        KeyValueStore store=new KeyValueStore(nodeId+"_commit.log");
        NodeServer server=new NodeServer(port,store);

        new Thread(()->{
            try{
                server.start();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }).start();
        clusterMap.put(nodeId,new NodeInfo("localhost",port));
        ring.addNode(nodeId);

    }
    public ConsistentHashRing getRing() {
        return ring;
    }
    public Map<String, NodeInfo> getClusterMap() {
        return clusterMap;
    }

    public static class NodeInfo{
        private final String host;
        private final int port;
        public NodeInfo(String host, int port) {
            this.host = host;
            this.port = port;
        }
        public String getHost(){
            return host;
        }
        public int getPort(){
            return port;
        }
    }
}
