package sk.intersoft.vicinity.agent.service.resource;

import org.json.JSONObject;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.adapter.AdapterEndpoint;
import sk.intersoft.vicinity.agent.adapter.AgentAdapter;
import sk.intersoft.vicinity.agent.gateway.GatewayAPIClient;
import sk.intersoft.vicinity.agent.thing.InteractionPattern;
import sk.intersoft.vicinity.agent.thing.ThingDescription;

public class ObjectEventResource extends AgentResource {
    final static Logger logger = LoggerFactory.getLogger(ObjectEventResource.class.getName());

    private static String OBJECT_ID = "oid";
    private static String EVENT_ID = "eid";

    @Post()
    public String createEventChannel()  {
        // request OID = event publisher OID
        try{
            String oid = getAttribute(OBJECT_ID);
            String eid = getAttribute(EVENT_ID);

            logger.info("CREATING EVENT CHANNEL FOR: ");
            logger.info("OID: "+oid);
            logger.info("EID: " +eid);


            ThingDescription caller = getThing(oid);

            logger.info("CALLER: " + caller.toSimpleString());

            // retrieve event to check it exists .. if not, exception is thrown
            InteractionPattern event = caller.getInteractionPattern(eid, InteractionPattern.EVENT);
            logger.info("Object ["+oid+"] has Event ["+eid+"]");

            logger.info("CALLING GTW API WITH CALLER CREDENTIALS");

            String endpoint = GatewayAPIClient.getInteractionEndpoint(GatewayAPIClient.OBJECT_EVENT, oid, eid);

            logger.info("GTW API ENDPOINT: "+endpoint);

            String gtwResponse = GatewayAPIClient.post(endpoint, null, caller.oid, caller.password);
            logger.info("GTW API RAW RESPONSE: \n"+gtwResponse);

            return gtwResponse;


        }
        catch(Exception e){
            logger.error("", e);
            return ResourceResponse.failure(e).toString();
        }

    }

    @Put()
    public String publishEvent(Representation entity)  {
        // request OID = event publisher OID
        try{
            String oid = getAttribute(OBJECT_ID);
            String eid = getAttribute(EVENT_ID);

            logger.info("PUBLISHING EVENT FOR: ");
            logger.info("OID: "+oid);
            logger.info("EID: " +eid);


            ThingDescription caller = getThing(oid);

            logger.info("CALLER: " + caller.toSimpleString());

            // retrieve event to check it exists .. if not, exception is thrown
            InteractionPattern event = caller.getInteractionPattern(eid, InteractionPattern.EVENT);
            logger.info("Object ["+oid+"] has Event ["+eid+"]");

            if(entity == null) {
                throw new Exception("Empty payload!");
            }
            String rawPayload = entity.getText();

            logger.info("PAYLOAD: " + rawPayload);

            logger.info("CALLING GTW API WITH CALLER CREDENTIALS");

            String endpoint = GatewayAPIClient.getInteractionEndpoint(GatewayAPIClient.OBJECT_EVENT, oid, eid);

            logger.info("GTW API ENDPOINT: "+endpoint);

            String gtwResponse = GatewayAPIClient.put(endpoint, rawPayload, caller.oid, caller.password);
            logger.info("GTW API RAW RESPONSE: \n"+gtwResponse);

            return gtwResponse;


        }
        catch(Exception e){
            logger.error("", e);
            return ResourceResponse.failure(e).toString();
        }

    }


}
