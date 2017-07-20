package sk.intersoft.vicinity.agent.service.resource;

import org.apache.http.HttpEntity;
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
import sk.intersoft.vicinity.agent.adapter.AdapterEndpoint;
import sk.intersoft.vicinity.agent.adapter.AgentAdapter;
import sk.intersoft.vicinity.agent.config.AgentConfig;
import sk.intersoft.vicinity.agent.config.thing.InteractionPattern;
import sk.intersoft.vicinity.agent.gateway.GatewayAPIClient;
import sk.intersoft.vicinity.agent.service.response.ServiceResponse;

import java.util.UUID;

public class EventListenerResource extends ServerResource {

    @Post()
    public String readEvent(Representation entity)  {
        try{

            String id = getAttribute("oid");
            String eid = getAttribute("eid");

            System.out.println("\n\n");
            System.out.println("getting event: ");
            System.out.println("object id: "+id);
            System.out.println("eid: "+eid);

            String inputString = entity.getText();
            JSONObject input = new JSONObject(inputString);

            System.out.println("event payload: \n"+input);


            System.out.println("RESEND EVENT TO ADAPTER");

            String endpoint = AdapterEndpoint.getEventsEndpoint(id, eid);
            getLogger().info("EXEC PASS EVENT TO ADAPTER ENDPOINT: ["+endpoint+"]");

            AgentAdapter adapter = AgentAdapter.getInstance();

            String adapterResponse = adapter.post(endpoint, inputString);
            getLogger().info("GTW API RESPONSE: \n");
            JSONObject result = new JSONObject(adapterResponse);


            getLogger().info("GTW API RETURNS: \n"+result.toString(2));
            return ServiceResponse.success(result).toString();

        }
        catch(Exception e){
            e.printStackTrace();
            return ServiceResponse.failure(e).toString();
        }

    }

}
