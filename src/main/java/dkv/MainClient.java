package dkv;

import dkv.network.NodeClient;

public class MainClient {
    public static void main(String[] args) throws Exception {
        NodeClient client = new NodeClient("localhost", 5050);

        System.out.println("PUT name → " + client.put("name", "Laukik"));
        System.out.println("GET name → " + client.get("name"));
        System.out.println("DEL name → " + client.delete("name"));
    }
}
