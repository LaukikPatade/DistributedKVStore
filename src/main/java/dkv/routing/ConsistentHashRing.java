package dkv.routing;
import java.util.*;

public class ConsistentHashRing {
    private final TreeMap<Integer,String> ring = new TreeMap<>();
    private final int VIRTUAL_NODES=100;

    public void addNode(String nodeId){
        for(int i=0;i<VIRTUAL_NODES;i++){
            int h=HashUtil.hash(nodeId+"#"+i);
            ring.put(h,nodeId);
        }
        System.out.println("Added node "+nodeId);
    }

    public void removeNode(String nodeId){
        for (int i=0;i<VIRTUAL_NODES;i++){
            int h=HashUtil.hash(nodeId+"#"+i);
            ring.remove(h);
        }
        System.out.println("Removed node "+nodeId);
    }
    public String getNodeByKey(String key){
        int h=HashUtil.hash(key);
        Map.Entry<Integer, String> entry = ring.ceilingEntry(h);
        if (entry==null){
            return ring.firstEntry().getValue();
        }
        return entry.getValue();
    }
    public void printRing(){
        System.out.println("Hash Ring:");
        for (var e:ring.entrySet()){
            System.out.println(e.getKey()+"->"+e.getValue());
        }
    }

    public List<String> getReplicasForKey(String key,int rf) {
        List<String> replicas = new ArrayList<>();
        if (ring.isEmpty()) return replicas;
        int h = HashUtil.hash(key);

        NavigableMap<Integer, String> tail = ring.tailMap(h, true);

        for (String nodeId : tail.values()) {
            if (!replicas.contains(nodeId)) {
                replicas.add(nodeId);
                if (replicas.size() == rf) return replicas;
            }
        }

        for (String nodeId : ring.values()) {
            if (!replicas.contains(nodeId)) {
                replicas.add(nodeId);
                if (replicas.size() == rf) break;
            }
        }
        return replicas;
    }
}
