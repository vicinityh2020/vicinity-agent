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

    public static String post(String agentEndpoint, String objects) {
        try{

            HttpClient client = HttpClientBuilder.create().build();

            String callEndpoint = agentEndpoint + "/objects";
            
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

            String agentEndpoint = System.getProperty("agent.endpoint");

            System.out.println("active disco");
            System.out.println("posting objects to: "+agentEndpoint);

            String response = post(agentEndpoint, ObjectsResource.getObjects());

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


    @Get()
    public String getActionStatus(Representation entity)  {
        try{

            System.out.println("get action status");

            String oid = getAttribute("oid");
            String aid = getAttribute("aid");


            JSONObject out = new JSONObject();
            out.put("echo", "action status");
            out.put("oid", oid);
            out.put("aid", aid);

            return out.toString();
        }
        catch(Exception e){
            return "{}";
        }
    }

}
