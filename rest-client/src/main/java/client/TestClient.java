package client;


import org.json.JSONObject;

import java.io.File;
import java.util.Scanner;

public class TestClient {

    public String ENDPOINT = "http://localhost:9997/agent/";

    public static String file2string(String path) {
        try{
            return new Scanner(new File(path)).useDelimiter("\\Z").next();
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }


    public static String path(String path) {
        return new File("").getAbsolutePath() + path;
    }

    public void test() {
        RESTClient client = new RESTClient();

        System.out.println("TEST GET and so: ");
        System.out.println("simple get: "+client.get(ENDPOINT+"alive/kokot"));


    }

    public static void main(String[] args) {
        TestClient c = new TestClient();
        c.test();
    }

}
