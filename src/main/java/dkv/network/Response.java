package dkv.network;

public class Response {
    public String status;
    public String value;
    public Response(String status, String value) {
        this.status = status;
        this.value = value;
    }
    public Response() {
        // Default constructor for Gson
    }
}
