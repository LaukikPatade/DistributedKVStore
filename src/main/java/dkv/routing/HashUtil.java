package dkv.routing;

import java.security.MessageDigest;

public class HashUtil {
    public static int hash(String key){
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest=md.digest(key.getBytes());

            // convert first 4 bytes to a 32 bit integer
            int h=0;
            for (int i=0;i<4;i++){
                h=(h<<8)|(digest[i]&0xff);
            }
            return h&0x7ffffff;

        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }
}
