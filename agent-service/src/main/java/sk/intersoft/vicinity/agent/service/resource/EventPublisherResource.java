package sk.intersoft.vicinity.agent.service.resource;

import org.json.JSONObject;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.adapter.AdapterEndpoint;
import sk.intersoft.vicinity.agent.adapter.AgentAdapter;
import sk.intersoft.vicinity.agent.gateway.GatewayAPIClient;
import sk.intersoft.vicinity.agent.service.config.AgentConfig;
import sk.intersoft.vicinity.agent.thing.InteractionPattern;
import sk.intersoft.vicinity.agent.thing.ThingDescription;

/**
 * Resource listening to events from adapter and resenting them to GTW API
 */
public class EventPublisherResource extends ServerResource {
    final static Logger logger = LoggerFactory.getLogger(EventPublisherResource.class.getName());

    private static String OBJECT_ID = "oid";
    private static String EVENT_ID = "eid";

    @Post()
    public String passEventToGtwAPI(Representation entity)  {
        try{
            String oid = getAttribute(OBJECT_ID);
            String eid = getAttribute(EVENT_ID);

            logger.info("EVENT PUBLISHER - PASS THE EVENT TO GTW API FOR: ");
            logger.info("OID: "+oid);
            logger.info("EID: " + eid);

            if(entity == null) {
                throw new Exception("Empty payload!");
            }
            String rawPayload = entity.getText();
            logger.info("RAW PAYLOAD: " + rawPayload);

            JSONObject payload = new JSONObject(rawPayload);
            logger.info("PAYLOAD: " + payload.toString(2));

            ThingDescription thing = AgentConfig.things.getThingByOID(oid);
            if(thing == null) throw new Exception ("Missing thing with OID: "+oid);

            InteractionPattern event = thing.events.get(eid);
            if(event == null) throw new Exception ("Missing requested event ["+eid+"] in OID: "+oid);


            String gtwEndpoint = "/objects/"+oid+"/events/"+eid;
            logger.info("GTW API endpoint: " + gtwEndpoint);

            JSONObject data = new JSONObject(rawPayload);
            data.put(OBJECT_ID, oid);

            String gtwResponse = GatewayAPIClient.getInstance().post(gtwEndpoint, data.toString());
            logger.info("GTW API response: " + gtwResponse);

            return gtwResponse;

        }
        catch(Exception e){
            logger.error("", e);
            return ResourceResponse.failure(e).toString();
        }
    }


}
