package dkv;

import dkv.network.NodeServer;

import java.util.concurrent.ConcurrentHashMap;
import java.io.*;
import java.util.Map;
public class KeyValueStore {
    private final ConcurrentHashMap<String,String> store;
    private final File commitLog;

    public KeyValueStore(String logPath) throws IOException {
        this.store=new ConcurrentHashMap<>();
        this.commitLog=new File(logPath);

        if (!commitLog.exists()) commitLog.createNewFile();
        loadFromLog();
    }

    public synchronized void put(String key, String value) throws IOException {
        store.put(key,value);
        log("PUT",key,value);
    }
    public synchronized String get(String key){
        return store.getOrDefault(key, null);
    }
    public synchronized void remove(String key) throws IOException {
        store.remove(key);
        log("DEL",key,"");
    }
    public synchronized void clear(){
        store.clear();
    }
    private void log(String op, String key, String value) throws IOException {
        try(FileWriter fw=new FileWriter(commitLog,true)){
            fw.write(op+"\t"+key+"\t"+value+"\n");
        }
    }

    private void loadFromLog() throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(commitLog))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;   // skip blank lines

                String[] parts = line.split("\t", 3);  // allow at most 3 parts

                if (parts.length < 2) {
                    System.out.println("Skipping malformed log line: " + line);
                    continue;
                }

                String op = parts[0];
                String key = parts[1];
                String value = parts.length == 3 ? parts[2] : null;

                if ("PUT".equals(op) && value != null) {
                    store.put(key, value); // load directly to avoid recursive logging
                } else if ("DEL".equals(op)) {
                    store.remove(key);
                } else {
                    System.out.println("Skipping unrecognized log entry: " + line);
                }
            }
        }
    }






}
