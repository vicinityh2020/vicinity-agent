package sk.intersoft.vicinity.agent.service.resource;

import org.json.JSONObject;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.clients.AdapterClient;
import sk.intersoft.vicinity.agent.clients.AdapterEndpoint;
import sk.intersoft.vicinity.agent.clients.GatewayAPIClient;
import sk.intersoft.vicinity.agent.thing.InteractionPattern;
import sk.intersoft.vicinity.agent.thing.InteractionPatternEndpoint;
import sk.intersoft.vicinity.agent.thing.ThingDescription;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ObjectUpdateActionResource extends AgentResource {
    final static Logger logger = LoggerFactory.getLogger(ObjectUpdateActionResource.class.getName());

    private static String OBJECT_ID = "oid";
    private static String ACTION_ID = "aid";
    private static String STATUS_ID = "status";


    private static String STATUS_RUNNING = "running";
    private static String STATUS_FINISHED = "finished";
    private static String STATUS_FAILED = "failed";

    private static Set<String> statuses =
            new HashSet<>(
                    Arrays.asList(
                            new String[] {
                                    STATUS_RUNNING,
                                    STATUS_FINISHED,
                                    STATUS_FAILED
                            }
                    ));


    // UPDATES ACTION STATUS OF LOCAL OBJECT IN GATEWAY
    @Put()
    public String updateActionStatus(Representation entity) {
        try {
            String aid = getAttribute(ACTION_ID);

            logger.info("UPDATE ACTION STATUS OF LOCAL OBJECT: ");
            logger.info("AID: " + aid);

            if(entity == null) {
                throw new Exception("Empty payload!");
            }
            String rawPayload = entity.getText();

            logger.info("PAYLOAD: " + rawPayload);

            ThingDescription thing = getCallerObject();
            logger.info("CALLER THING: " + thing.toSimpleString());

            String statusParameter = getStatusHeader();
            logger.info("ACTION UPDATE STATUS: " + statusParameter);
            if(!statuses.contains(statusParameter.trim().toLowerCase())){
                throw new Exception("Incorrect status ["+statusParameter+"]! Allowed status: ["+statuses+"]");
            }

            String endpoint = GatewayAPIClient.getInteractionEndpoint(GatewayAPIClient.OBJECT_ACTION_ENDPOINT, thing.oid, aid)+"?"+STATUS_ID+"="+statusParameter;

            logger.info("GTW API ENDPOINT: "+endpoint);

            String gtwResponse = GatewayAPIClient.put(endpoint, rawPayload, thing.oid, thing.password);
            logger.info("GTW API RAW RESPONSE: \n"+gtwResponse);

            return gtwResponse;

        }
        catch (Exception e) {
            logger.error("UPDATE ACTION STATUS FAILURE! ", e);
            return gtwError(e).toString();
        }
    }

}