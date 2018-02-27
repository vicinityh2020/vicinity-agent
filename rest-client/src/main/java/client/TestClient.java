package client;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.restlet.data.ChallengeScheme;
import org.restlet.resource.ClientResource;

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
        System.out.println("simple get: "+client.get(ENDPOINT+"alive/"));

//        System.out.println("simple get: "+client.get("http://160.40.206.121/VICINITY/VICINITY.svc/objects/123/properties/234"));

//        System.out.println("TEST GET OBJ PROPS: ");
//        System.out.println("CNT: "+client.get(ENDPOINT + "objects/D77EC6B0-F039-4734-925E-0A90CE7D1B5B:0184A96B:CO2/properties/0184A96B:CO2Level"));
//        System.out.println("CNT: "+client.get(ENDPOINT + "objects/0D485748-CF2A-450C-BCF6-02AC1CB39A2D:6/properties/PowerConsumption"));


    }

    public void testLogin() throws Exception {

//        CredentialsProvider provider = new BasicCredentialsProvider();
//        UsernamePasswordCredentials credentials
//                = new UsernamePasswordCredentials("test_vcnt0", "0VicinityTestUser0");
//        provider.setCredentials(AuthScope.ANY, credentials);
//
//        HttpClient client = HttpClientBuilder.create()
//                .setDefaultCredentialsProvider(provider)
//                .build();
//
//        HttpGet request = new HttpGet("http://138.201.156.73:8181/api/objects/");
//        HttpResponse response = client.execute(request);
//
//        int status = response.getStatusLine().getStatusCode();
//        System.out.println("status: " + status);
//
//        HttpEntity entity = response.getEntity();
//        System.out.println("entity: " + entity);


        ClientResource resource = new ClientResource("http://138.201.156.73:8181/api/objects");
//        ClientResource resource = new ClientResource(ENDPOINT+"alive/kokot");


        // Send an authenticated request using the Basic authentication scheme.
        resource.setChallengeResponse(ChallengeScheme.HTTP_BASIC, "test_vcnt0", "0VicinityTestUser0");

        // Send the request
        resource.get();
        // Should be 200
        System.out.println(resource.getStatus());
        System.out.println(resource.getResponse().getEntity().getText());

    }

    public static void main(String[] args) throws Exception {
        TestClient c = new TestClient();
//        c.testLogin();
        c.test();
    }

}
