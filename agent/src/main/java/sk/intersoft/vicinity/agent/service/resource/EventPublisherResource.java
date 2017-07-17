package sk.intersoft.vicinity.agent.service.resource;

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

public class EventPublisherResource extends ServerResource {

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


            String oid = AgentConfig.getOid(id);


            System.out.println("VICINITY OID FROM OBJECT ID: \n"+oid);
            if(oid != null) {
                System.out.println("received infra-id mapped to oid .. send to gtw");
                InteractionPattern pattern = AgentConfig.getInteractionPattern(oid, eid, InteractionPattern.EVENT);
                System.out.println("INTERACTION PATTERN: \n"+pattern);


                String gtwEndpoint = "/objects/"+oid+"/events/"+eid;

                System.out.println("GTW API ENDPOINT: \n"+gtwEndpoint);

                JSONObject out = new JSONObject(inputString);
                out.put("oid", oid);
                String gtwResponse = GatewayAPIClient.post(gtwEndpoint, out.toString());

                return gtwResponse;
            }
            else throw new Exception("Unknown OID for event object: "+id);

        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println("EVENT PUBLISHER EXCEPTION...");
            return ServiceResponse.failure(e).toString();
        }

    }

}
