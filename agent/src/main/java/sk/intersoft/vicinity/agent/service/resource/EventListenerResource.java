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
import sk.intersoft.vicinity.agent.config.AgentConfig;
import sk.intersoft.vicinity.agent.config.thing.InteractionPattern;
import sk.intersoft.vicinity.agent.gateway.GatewayAPIClient;

import java.util.UUID;

public class EventListenerResource extends ServerResource {

    @Post()
    public String readEvent(Representation entity)  {
//        try{
//
//            String oid = getAttribute("oid");
//            String eid = getAttribute("eid");
//
//            System.out.println("\n\n");
//            System.out.println("getting event: ");
//            System.out.println("oid: "+oid);
//            System.out.println("eid: "+eid);
//
//            String inputString = entity.getText();
//            JSONObject input = new JSONObject(inputString);
//
//            System.out.println("event payload: \n"+input);
//
//
//            String vOid = AgentConfig.getOid(oid);
//
//            System.out.println("VICINITY OID: \n"+vOid);
//            if(vOid == null) throw new Exception("OID not found for "+oid);
//
//            InteractionPattern pattern = AgentConfig.getInteractionPattern(vOid, eid, InteractionPattern.EVENT);
//            System.out.println("INTERACTION PATTERN: \n"+pattern);
//
//
//            String gtwEndpoint = "/objects/"+vOid+"/events/"+eid;
//
//            System.out.println("GTW API ENDPOINT: \n"+gtwEndpoint);
//
//            JSONObject out = new JSONObject(inputString);
//            out.put("oid", vOid);
//            GatewayAPIClient.post(gtwEndpoint, out.toString());
//
//            return inputString;
//        }
//        catch(Exception e){
//            e.printStackTrace();
//            return "{}";
//        }

        return "{}";
    }

}
