package sk.intersoft.vicinity.adapter.aau.service.resource;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import java.util.UUID;

public class EventPublisherResource extends ServerResource {

    public String pass2Agent(String oid, String eid, String payload) throws Exception {
        try{
            String callEndpoint = System.getProperty("agent.endpoint") + "/objects/"+oid+"/events/"+eid+"/publish";


            System.out.println("POST EVENT ENDPOINT: "+callEndpoint);
            System.out.println("POST DATA: "+payload);
            HttpClient client = HttpClientBuilder.create()
                    .build();

            HttpPost request = new HttpPost(callEndpoint);

//            request.addHeader("Accept", "application/json");
//            request.addHeader("Content-Type", "application/json");

            StringEntity data = new StringEntity(payload);

            request.setEntity(data);

            HttpResponse response = client.execute(request);


            int status = response.getStatusLine().getStatusCode();
            System.out.println("event resend status: " + status);

            String content = EntityUtils.toString(response.getEntity());

            System.out.println("event resend entity: " + content);

            return content;

        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }

    }



    @Post()
    public String readEvent(Representation entity)  {
        try{

            String oid = getAttribute("oid");
            String eid = getAttribute("eid");

            System.out.println("\n\n");
            System.out.println("publishing event: ");
            System.out.println("oid: "+oid);
            System.out.println("eid: "+eid);

//            JSONObject input = new JSONObject(entity.getText());

            String entityString = entity.getText();
            System.out.println("event payload: \n"+entityString);

            JSONObject out = new JSONObject(entityString);
            out.put("businessId", UUID.randomUUID().toString());


            out.put("oid", oid);
            out.put("eid", eid);

            return pass2Agent(oid, eid, out.toString());
        }
        catch(Exception e){
            e.printStackTrace();
            return "{}";
        }
    }

}
