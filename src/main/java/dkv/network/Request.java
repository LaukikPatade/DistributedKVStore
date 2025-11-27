package dkv.network;

public class Request {
    public String type;
    public String key;
    public String value;
    
    public Request(String type, String key, String value) {
        // Default constructor for Gson
        this.type = type;
        this.key = key;
        this.value = value;
    }
}
