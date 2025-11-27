package dkv;

import dkv.cluster.ClusterManager;
import dkv.cluster.ConsistencyLevel;
import dkv.network.NodeClient;

public class Main {
    public static void main(String[] args) throws Exception {

        System.out.println("===== STARTING CLUSTER =====");
        ClusterManager cm = new ClusterManager();
        cm.startCluster();

        NodeClient client = new NodeClient(cm);

        Thread.sleep(1000);  // Allow servers to fully boot up

        System.out.println("\n===== TEST 1: PUT with ONE =====");
        System.out.println("PUT name=Laukik -> " +
                client.put("name", "Laukik", ConsistencyLevel.ONE));

        System.out.println("\n===== TEST 2: GET with ONE =====");
        System.out.println("GET name (ONE) = " +
                client.get("name", ConsistencyLevel.ONE));

        System.out.println("\n===== TEST 3: PUT with QUORUM =====");
        System.out.println("PUT city=Amherst -> " +
                client.put("city", "Amherst", ConsistencyLevel.QUORUM));

        System.out.println("\n===== TEST 4: GET with QUORUM =====");
        System.out.println("GET city (QUORUM) = " +
                client.get("city", ConsistencyLevel.QUORUM));

        System.out.println("\n===== TEST 5: PUT with ALL =====");
        System.out.println("PUT key=CS685 -> " +
                client.put("course", "CS685", ConsistencyLevel.ALL));

        System.out.println("\n===== TEST 6: GET with ALL =====");
        System.out.println("GET course (ALL) = " +
                client.get("course", ConsistencyLevel.ALL));

        System.out.println("\n===== TEST 7: DELETE with QUORUM =====");
        System.out.println("DELETE name -> " +
                client.delete("name", ConsistencyLevel.QUORUM));

        System.out.println("\n===== TEST 8: GET after DELETE =====");
        System.out.println("GET name (QUORUM) = " +
                client.get("name", ConsistencyLevel.QUORUM));

        System.out.println("\n===== CONSISTENCY TEST COMPLETE =====\n");
    }
}
