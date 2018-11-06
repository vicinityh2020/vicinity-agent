package sk.intersoft.vicinity.agent.service.resource;

import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.clients.ClientResponse;
import sk.intersoft.vicinity.agent.clients.GatewayAPIClient;
import sk.intersoft.vicinity.agent.thing.InteractionPattern;
import sk.intersoft.vicinity.agent.thing.ThingDescription;

public class OpenPublishEventResource extends AgentResource {
    final static Logger logger = LoggerFactory.getLogger(OpenPublishEventResource.class.getName());

    private static String EVENT_ID = "eid";

    @Post()
    public String openEventChannel() {
        try {
            String eid = getAttribute(EVENT_ID);

            logger.info("OPENING EVENT CHANNEL: ["+eid+"]");


            ThingDescription thing = getCallerObject();
            logger.info("CALLER THING: " + thing.toSimpleString());

            try{
                InteractionPattern event = thing.getInteractionPattern(eid, InteractionPattern.EVENT);
            }
            catch(Exception e){
                throw new Exception("CALLER THING: " + thing.toSimpleString() + " DOES NOT HAVE EVENT ["+eid+"]!");
            }

            String endpoint = GatewayAPIClient.getOpenEventChannelEndpoint(eid);

            ClientResponse gtwResponse = GatewayAPIClient.post(endpoint, null, thing.oid, thing.password, null);
            logger.info("GTW API RAW RESPONSE: \n"+gtwResponse);

            return gtwSuccess(gtwResponse);


        }
        catch (Exception e) {
            logger.error("OPEN EVENT CHANNEL FAILURE! ", e);
            return gtwError(e).toString();
        }
    }

    @Put()
    public String publishEvent(Representation entity) {
        try {
            String eid = getAttribute(EVENT_ID);

            logger.info("PUBLISHING EVENT: ["+eid+"]");
            String query = getQueryString(getQuery());
            logger.info("QUERY: " + query);

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

            ClientResponse gtwResponse = GatewayAPIClient.put(endpoint, rawPayload, thing.oid, thing.password, query);
            logger.info("GTW API RAW RESPONSE: \n"+gtwResponse);

            return gtwSuccess(gtwResponse);


        }
        catch (Exception e) {
            logger.error("PUBLISH EVENT FAILURE! ", e);
            return gtwError(e).toString();
        }
    }

}