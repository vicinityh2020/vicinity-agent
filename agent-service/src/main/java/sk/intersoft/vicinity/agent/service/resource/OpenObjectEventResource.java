package sk.intersoft.vicinity.agent.service.resource;

import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.gateway.GatewayAPIClient;
import sk.intersoft.vicinity.agent.thing.InteractionPattern;
import sk.intersoft.vicinity.agent.thing.ThingDescription;

public class OpenObjectEventResource extends AgentResource {
    final static Logger logger = LoggerFactory.getLogger(OpenObjectEventResource.class.getName());

    private static String INFRASTRUCTURE_ID = "infrastructure-id";
    private static String EVENT_ID = "eid";

    public static String openChannel(ThingDescription thing, String eid) throws Exception {
        logger.info("CREATING EVENT CHANNEL FOR: ");
        logger.info("PUBLISHER: "+thing.toSimpleString());
        logger.info("EID: " +eid);



        // retrieve event to check it exists .. if not, exception is thrown
        InteractionPattern event = thing.getInteractionPattern(eid, InteractionPattern.EVENT);
        logger.info("Thing has event ["+eid+"]");

        logger.info("CALLING GTW API WITH CALLER CREDENTIALS");

        String endpoint = GatewayAPIClient.getInteractionEndpoint(GatewayAPIClient.OBJECT_EVENT, thing.oid, eid);

        logger.info("GTW API ENDPOINT: "+endpoint);

        String gtwResponse = GatewayAPIClient.post(endpoint, null, thing.oid, thing.password);
        logger.info("GTW API RAW RESPONSE: \n"+gtwResponse);

        return gtwResponse;

    }


    @Post()
    public String openEventChannel()  {
        try{
            String infrastructureId = getAttribute(INFRASTRUCTURE_ID);
            String eid = getAttribute(EVENT_ID);

            ThingDescription thing = getThingByInfrastructureID(infrastructureId);

            return openChannel(thing, eid);

        }
        catch(Exception e){
            logger.error("", e);
            return ResourceResponse.failure(e).toString();
        }

    }


}
