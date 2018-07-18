package sk.intersoft.vicinity.agent.service.resource;

import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.clients.GatewayAPIClient;
import sk.intersoft.vicinity.agent.thing.ThingDescription;

public class RemoteObjectActionResource extends AgentResource {
    final static Logger logger = LoggerFactory.getLogger(RemoteObjectActionResource.class.getName());

    private static String OBJECT_ID = "oid";
    private static String ACTION_ID = "aid";

    @Post()
    public String executeAction(Representation entity) {
        try {
            String oid = getAttribute(OBJECT_ID);
            String aid = getAttribute(ACTION_ID);

            logger.info("EXECUTE REMOTE ACTION FOR TARGET : ");
            logger.info("OID: " + oid);
            logger.info("AID: " + aid);

            if(entity == null) {
                throw new Exception("Empty payload!");
            }
            String rawPayload = entity.getText();

            logger.info("PAYLOAD: " + rawPayload);

            ThingDescription thing = getCallerObject();
            logger.info("CALLER THING: " + thing.toSimpleString());

            String endpoint = GatewayAPIClient.getInteractionEndpoint(GatewayAPIClient.OBJECT_ACTION_ENDPOINT, oid, aid);

            logger.info("GTW API ENDPOINT: "+endpoint);

            String gtwResponse = GatewayAPIClient.put(endpoint, rawPayload, thing.oid, thing.password);
            logger.info("GTW API RAW RESPONSE: \n"+gtwResponse);

            return gtwResponse;

        }
        catch (Exception e) {
            logger.error("EXECUTE REMOTE ACTION  FAILURE! ", e);
            return ResourceResponse.failure(e).toString();
        }
    }
}