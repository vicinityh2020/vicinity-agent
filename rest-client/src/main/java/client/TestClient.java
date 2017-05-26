package client;


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


    public void test() throws Exception {
        RESTClient client = new RESTClient();

        System.out.println("TEST GET and so: ");
        System.out.println("simple get: "+client.get(ENDPOINT+"alive/kokot"));

        System.out.println("TEST GET OBJ PROPS: ");
        System.out.println("CNT: "+client.get(ENDPOINT + "objects/002/properties/018BD53D:Brightness"));


    }

    public static void main(String[] args) throws Exception {
        TestClient c = new TestClient();
        c.test();
    }

}
