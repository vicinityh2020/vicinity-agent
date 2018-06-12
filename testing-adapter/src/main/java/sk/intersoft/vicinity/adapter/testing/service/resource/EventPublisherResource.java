package sk.intersoft.vicinity.adapter.testing.service.resource;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

public class EventPublisherResource extends ServerResource {

    public String execute(String iid, String eid, JSONObject payload) throws Exception {
        HttpClient client = HttpClientBuilder.create().build();

        String callEndpoint = System.getProperty("agent.endpoint") + "/events/"+eid;

        System.out.println("EXECUTING EVENT PUBLISH:");
        System.out.println("endpoint: " + callEndpoint);
        System.out.println("payload: " + payload.toString());


        HttpPut request = new HttpPut(callEndpoint);

        request.addHeader("Content-Type", "application/json");
        request.addHeader("infrastructure-id", iid);
        request.addHeader("adapter-id", System.getProperty("adapter.id"));

        StringEntity data = new StringEntity(payload.toString());
        request.setEntity(data);


        HttpResponse response = client.execute(request);

        int status = response.getStatusLine().getStatusCode();
        System.out.println("PUT status: " + status);

        String responseContent = EntityUtils.toString(response.getEntity());
        System.out.println("response: " + responseContent);
        return responseContent;


    }

    @Put()
    public String publish(Representation entity)  {
        try{

            System.out.println("publish event");

            String iid = getAttribute("iid");
            String eid = getAttribute("eid");

            JSONObject input = new JSONObject(entity.getText());

            JSONObject out = new JSONObject();
            out.put("echo", "publish event");
            out.put("iid", iid);
            out.put("eid", eid);
            out.put("payload", input);

            return execute(iid, eid, out);
        }
        catch(Exception e){
            return "{\"something\": \"went ape\"}";
        }
    }

}
