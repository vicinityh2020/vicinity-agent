package sk.intersoft.vicinity.agent.service.resource;

import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.gateway.GatewayAPIClient;
import sk.intersoft.vicinity.agent.thing.InteractionPattern;
import sk.intersoft.vicinity.agent.thing.ThingDescription;

public class PublishObjectEventResource extends AgentResource {
    final static Logger logger = LoggerFactory.getLogger(PublishObjectEventResource.class.getName());

    private static String INFRASTRUCTURE_ID = "infrastructure-id";
    private static String EVENT_ID = "eid";


    @Put()
    public String publishEvent(Representation entity)  {
        try{
            String infrastructureId = getAttribute(INFRASTRUCTURE_ID);
            String eid = getAttribute(EVENT_ID);

            ThingDescription thing = getThingByInfrastructureID(infrastructureId);

            logger.info("PUBLISHER: "+thing.toSimpleString());
            logger.info("EID: " +eid);

            if(entity == null) {
                throw new Exception("Empty payload!");
            }
            String rawPayload = entity.getText();

            logger.info("PAYLOAD: " + rawPayload);

            // retrieve event to check it exists .. if not, exception is thrown
            InteractionPattern event = thing.getInteractionPattern(eid, InteractionPattern.EVENT);
            logger.info("Thing has event ["+eid+"]");

            logger.info("CALLING GTW API WITH CALLER CREDENTIALS");

            String endpoint = GatewayAPIClient.getInteractionEndpoint(GatewayAPIClient.OBJECT_EVENT, thing.oid, eid);

            logger.info("GTW API ENDPOINT: "+endpoint);

            String gtwResponse = GatewayAPIClient.put(endpoint, rawPayload, thing.oid, thing.password);
            logger.info("GTW API RAW RESPONSE: \n"+gtwResponse);

            return gtwResponse;


        }
        catch(Exception e){
            logger.error("", e);
            return ResourceResponse.failure(e).toString();
        }

    }


}
