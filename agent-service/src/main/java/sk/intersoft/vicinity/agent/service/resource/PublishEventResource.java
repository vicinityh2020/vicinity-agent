package sk.intersoft.vicinity.agent.service.resource;

import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.clients.GatewayAPIClient;
import sk.intersoft.vicinity.agent.thing.InteractionPattern;
import sk.intersoft.vicinity.agent.thing.ThingDescription;

public class PublishEventResource extends AgentResource {
    final static Logger logger = LoggerFactory.getLogger(PublishEventResource.class.getName());

    private static String EVENT_ID = "eid";

    @Put()
    public String publishEvent(Representation entity) {
        try {
            String eid = getAttribute(EVENT_ID);

            logger.info("PUBLISHING EVENT: ["+eid+"]");

            if(entity == null) {
                throw new Exception("Empty payload!");
            }
            String rawPayload = entity.getText();

            logger.info("PAYLOAD: " + rawPayload);

            ThingDescription thing = getCallerObject();
            logger.info("CALLER THING: " + thing.toSimpleString());

            try{
                InteractionPattern event = thing.getInteractionPattern(eid, InteractionPattern.EVENT);
            }
            catch(Exception e){
                throw new Exception("CALLER THING: " + thing.toSimpleString() + " DOES NOT HAVE EVENT ["+eid+"]!");
            }

            String endpoint = GatewayAPIClient.getPublishEventEndpoint(eid);

            String gtwResponse = GatewayAPIClient.put(endpoint, rawPayload, thing.oid, thing.password);
            logger.info("GTW API RAW RESPONSE: \n"+gtwResponse);

            return gtwResponse;

        }
        catch (Exception e) {
            logger.error("PUBLISH EVENT FAILURE! ", e);
            return ResourceResponse.failure(e).toString();
        }
    }
}