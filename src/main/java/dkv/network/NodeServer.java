package dkv.network;

import com.google.gson.Gson;
import dkv.KeyValueStore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NodeServer {
    private final int port;
    private final ExecutorService pool= Executors.newFixedThreadPool(10);
    private final KeyValueStore store;
    private final Gson gson = new Gson();
    public NodeServer(int port, KeyValueStore store) {
        this.port = port;
        this.store = store;
    }
    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("NodeServer started. Listening on port " + port);
        while(true){
            Socket clientSocket = serverSocket.accept();
            pool.submit(() -> handleClient(clientSocket));
        }
    }
private void handleClient(Socket clientSocket) {
        try(BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)){
            String requestJson=in.readLine();
            System.out.println("Received req: " + requestJson);
            Request request=gson.fromJson(requestJson,Request.class);
            Response response=process(request);
            String responseJson = gson.toJson(response);
            System.out.println("Sending res: " + responseJson);
            out.println(responseJson);
        }
        catch (Exception e){
            e.printStackTrace();
        }
}
private Response process(Request request) throws IOException {
        switch (request.type){
            case "PUT":
                store.put(request.key,request.value);
                return new Response("OK",null);
            case "GET":
                String val=store.get(request.key);
                return new Response("OK",val);
            case "DELETE":
                store.remove(request.key);
                return new Response("OK",null);
            default:
                return new Response("ERROR","Invalid operation");

        }
}
}

