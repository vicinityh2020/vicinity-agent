package sk.intersoft.vicinity.adapter.testing.service.resource;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

public class DiscoResource extends ServerResource {

    public static String post() {
        try{

            HttpClient client = HttpClientBuilder.create().build();

            String callEndpoint = System.getProperty("agent.endpoint") + "/objects";
            String objects = ObjectsResource.file2string(System.getProperty("active.objects.file"));

            System.out.println("DISCO POST:");
            System.out.println("endpoint: " + callEndpoint);
            System.out.println("payload: " + objects);


            HttpPost request = new HttpPost(callEndpoint);

            request.addHeader("Content-Type", "application/json");

            if(objects != null){
                StringEntity data = new StringEntity(objects);
                request.setEntity(data);
            }


            HttpResponse response = client.execute(request);

            int status = response.getStatusLine().getStatusCode();
            System.out.println("POST status: " + status);

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
    public String disco(Representation entity)  {
        try{


            System.out.println("active disco");

            String response = post();

            JSONObject out = new JSONObject();
            out.put("disco", "executed");

            try{
                out.put("response", new JSONObject(response));
            }
            catch(Exception e){
                out.put("response", response);
            }
            return out.toString(2);
        }
        catch(Exception e){
            return "{}";
        }
    }


}
