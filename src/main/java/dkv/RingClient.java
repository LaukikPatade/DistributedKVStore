package dkv;

import dkv.network.NodeClient;
import dkv.routing.ConsistentHashRing;
public class RingClient {
    public static void main(String[] args) throws Exception {
        ConsistentHashRing ring = new ConsistentHashRing();

        ring.addNode("NodeA");
        ring.addNode("NodeB");
        ring.addNode("NodeC");

        System.out.println("Key 'name' → " + ring.getNodeByKey("name"));
        System.out.println("Key 'email' → " + ring.getNodeByKey("email"));
        System.out.println("Key 'age' → " + ring.getNodeByKey("age"));

    }
}
