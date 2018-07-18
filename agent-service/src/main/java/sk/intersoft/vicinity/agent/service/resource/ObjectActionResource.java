package sk.intersoft.vicinity.agent.service.resource;

import org.json.JSONObject;
import org.restlet.data.ClientInfo;
import org.restlet.representation.Representation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
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

public class ObjectActionResource extends AgentResource {
    final static Logger logger = LoggerFactory.getLogger(ObjectActionResource.class.getName());

    private static String OBJECT_ID = "oid";
    private static String ACTION_ID = "aid";


    // EXECUTE ACTION ON LOCAL OBJECT
    @Post()
    public String executeAction(Representation entity) {
        try {
            String oid = getAttribute(OBJECT_ID);
            String aid = getAttribute(ACTION_ID);

            logger.info("EXECUTING LOCAL ACTION FOR TARGET: ");
            logger.info("OID: " + oid);
            logger.info("AID: " + aid);


            if(entity == null) {
                throw new Exception("Empty payload!");
            }
            String rawPayload = entity.getText();

            logger.info("PAYLOAD: " + rawPayload);

            ThingDescription thing = getThingByOID(oid);
            logger.info("ADAPTER THING FOR OID [" + oid + "]: " + thing.toSimpleString());

            String endpoint = AdapterEndpoint.getEndpoint(thing, aid, InteractionPattern.ACTION, InteractionPatternEndpoint.WRITE);

            logger.info("EXECUTE ACTION ADAPTER ENDPOINT: [" + endpoint + "]");

            String adapterResponse = AdapterClient.post(endpoint, rawPayload);
            logger.info("EXECUTE ACTION ADAPTER RAW RESPONSE: \n" + adapterResponse);

            JSONObject result = new JSONObject(adapterResponse);

            logger.info("EXECUTE ACTION RESPONSE: " + result.toString());

            return ResourceResponse.success(result).toString();

        }
        catch (Exception e) {
            logger.error("EXECUTE ACTION FAILURE! ", e);
            return ResourceResponse.failure(e).toString();
        }
    }

    // CANCEL ACTION
    @Delete
    public String cancelAction() {
        try {
            String oid = getAttribute(OBJECT_ID);
            String aid = getAttribute(ACTION_ID);

            logger.info("CANCEL LOCAL ACTION FOR TARGET: ");
            logger.info("OID: " + oid);
            logger.info("AID: " + aid);

            ThingDescription thing = getThingByOID(oid);
            logger.info("ADAPTER THING FOR OID [" + oid + "]: " + thing.toSimpleString());

            String endpoint = AdapterEndpoint.getEndpoint(thing, aid, InteractionPattern.ACTION, InteractionPatternEndpoint.WRITE);

            logger.info("CANCEL ACTION ADAPTER ENDPOINT: [" + endpoint + "]");

            String adapterResponse = AdapterClient.delete(endpoint);
            logger.info("CANCEL ACTION ADAPTER RAW RESPONSE: \n" + adapterResponse);

            JSONObject result = new JSONObject(adapterResponse);

            logger.info("EXECUTE ACTION RESPONSE: " + result.toString());

            return ResourceResponse.success(result).toString();

        }
        catch (Exception e) {
            logger.error("CANCEL ACTION FAILURE! ", e);
            return ResourceResponse.failure(e).toString();
        }
    }

}