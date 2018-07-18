package sk.intersoft.vicinity.agent.service.resource;

import org.restlet.representation.Representation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.clients.GatewayAPIClient;
import sk.intersoft.vicinity.agent.thing.ThingDescription;

public class RemoteObjectActionTaskResource extends AgentResource {
    final static Logger logger = LoggerFactory.getLogger(RemoteObjectActionTaskResource.class.getName());

    private static String OBJECT_ID = "oid";
    private static String ACTION_ID = "aid";
    private static String TASK_ID = "tid";

    @Get()
    public String getActionStatus() {
        try {
            String oid = getAttribute(OBJECT_ID);
            String aid = getAttribute(ACTION_ID);
            String tid = getAttribute(TASK_ID);

            logger.info("GET REMOTE ACTION STATUS FOR TARGET : ");
            logger.info("OID: " + oid);
            logger.info("AID: " + aid);
            logger.info("TASK-ID: " + tid);

            ThingDescription thing = getCallerObject();
            logger.info("CALLER THING: " + thing.toSimpleString());

            String endpoint = GatewayAPIClient.getInteractionEndpoint(GatewayAPIClient.OBJECT_ACTION_TASK_ENDPOINT, oid, aid, tid);

            logger.info("GTW API ENDPOINT: "+endpoint);

            String gtwResponse = GatewayAPIClient.get(endpoint, thing.oid, thing.password);
            logger.info("GTW API RAW RESPONSE: \n"+gtwResponse);

            return gtwResponse;

        }
        catch (Exception e) {
            logger.error("GET REMOTE ACTION STATUS FAILURE! ", e);
            return ResourceResponse.failure(e).toString();
        }
    }


    @Delete()
    public String cancelAction() {
        try {
            String oid = getAttribute(OBJECT_ID);
            String aid = getAttribute(ACTION_ID);
            String tid = getAttribute(TASK_ID);

            logger.info("CANCEL REMOTE ACTION FOR TARGET : ");
            logger.info("OID: " + oid);
            logger.info("AID: " + aid);
            logger.info("TASK-ID: " + tid);

            ThingDescription thing = getCallerObject();
            logger.info("CALLER THING: " + thing.toSimpleString());

            String endpoint = GatewayAPIClient.getInteractionEndpoint(GatewayAPIClient.OBJECT_ACTION_TASK_ENDPOINT, oid, aid, tid);

            logger.info("GTW API ENDPOINT: "+endpoint);

            String gtwResponse = GatewayAPIClient.delete(endpoint, thing.oid, thing.password);
            logger.info("GTW API RAW RESPONSE: \n"+gtwResponse);

            return gtwResponse;

        }
        catch (Exception e) {
            logger.error("CANCEL REMOTE ACTION FAILURE! ", e);
            return ResourceResponse.failure(e).toString();
        }
    }

}