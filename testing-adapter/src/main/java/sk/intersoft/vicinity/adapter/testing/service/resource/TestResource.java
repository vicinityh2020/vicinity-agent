package sk.intersoft.vicinity.adapter.testing.service.resource;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class TestResource extends ServerResource {

    public static String test() {
        try{

            HttpClient client = HttpClientBuilder.create().build();

            String callEndpoint = System.getProperty("agent.endpoint") + "/alive";

            System.out.println("TEST GET:");
            System.out.println("endpoint: " + callEndpoint);


            HttpGet request = new HttpGet(callEndpoint);

            HttpResponse response = client.execute(request);

            int status = response.getStatusLine().getStatusCode();
            System.out.println("GET status: " + status);

            String responseContent = EntityUtils.toString(response.getEntity());
            System.out.println("response: " + responseContent);
            return responseContent;

        }
        catch(Exception e){
            e.printStackTrace();
        }

        return "failed";
    }


    @Get()
    public String disco()  {
        try{


            System.out.println("test");

            test();

            JSONObject out = new JSONObject();
            out.put("test", "executed");

            return out.toString(2);
        }
        catch(Exception e){
            return "{}";
        }
    }


}
