package sk.intersoft.vicinity.agent.service.resource;

import org.restlet.resource.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.gateway.GatewayAPIClient;
import sk.intersoft.vicinity.agent.thing.InteractionPattern;
import sk.intersoft.vicinity.agent.thing.ThingDescription;

public class SubscribeObjectEventResource extends AgentResource {
    final static Logger logger = LoggerFactory.getLogger(SubscribeObjectEventResource.class.getName());

    private static String OBJECT_ID = "oid";
    private static String EVENT_ID = "eid";

    public static String subscribeChannel(ThingDescription subscriber, String oid, String eid) throws Exception {
        logger.info("SUBSCRIBING EVENT CHANNEL FOR: ");
        logger.info("OID: "+oid);
        logger.info("EID: " +eid);
        logger.info("SUBSCRIBER: " +subscriber.toSimpleString());

        logger.info("CALLING GTW API WITH SUBSCRIBER CREDENTIALS");

        String endpoint = GatewayAPIClient.getInteractionEndpoint(GatewayAPIClient.OBJECT_EVENT, oid, eid);

        logger.info("GTW API ENDPOINT: "+endpoint);

        String gtwResponse = GatewayAPIClient.post(endpoint, null, subscriber.oid, subscriber.password);
        logger.info("GTW API RAW RESPONSE: \n"+gtwResponse);

        return gtwResponse;

    }


    @Post()
    public String subscribeToEventChannel()  {
        try{
            String oid = getAttribute(OBJECT_ID);
            String eid = getAttribute(EVENT_ID);

            ThingDescription caller = getCallerObject();
            logger.info("CALLER: " + caller.toSimpleString());

            return subscribeChannel(caller, oid, eid);

        }
        catch(Exception e){
            logger.error("", e);
            return ResourceResponse.failure(e).toString();
        }

    }


}
