package sk.intersoft.vicinity.agent.service.resource;

import org.json.JSONObject;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.clients.AdapterClient;
import sk.intersoft.vicinity.agent.clients.AdapterEndpoint;
import sk.intersoft.vicinity.agent.clients.ClientResponse;
import sk.intersoft.vicinity.agent.clients.GatewayAPIClient;
import sk.intersoft.vicinity.agent.thing.InteractionPattern;
import sk.intersoft.vicinity.agent.thing.InteractionPatternEndpoint;
import sk.intersoft.vicinity.agent.thing.ThingDescription;

public class RemoteObjectPropertyResource extends AgentResource {
    final static Logger logger = LoggerFactory.getLogger(RemoteObjectPropertyResource.class.getName());

    private static String OBJECT_ID = "oid";
    private static String PROPERTY_ID = "pid";

    @Get("json")
    public String getPropertyValue() {
        try {
            String oid = getAttribute(OBJECT_ID);
            String pid = getAttribute(PROPERTY_ID);

            logger.info("GETTING REMOTE PROPERTY VALUE TARGET FOR: ");
            logger.info("OID: " + oid);
            logger.info("PID: " + pid);

            ThingDescription thing = getCallerObject();
            logger.info("CALLER THING: " + thing.toSimpleString());

            String endpoint = GatewayAPIClient.getInteractionEndpoint(GatewayAPIClient.OBJECT_PROPERTY_ENDPOINT, oid, pid);

            logger.info("GTW API ENDPOINT: "+endpoint);

            ClientResponse gtwResponse = GatewayAPIClient.get(endpoint, thing.oid, thing.password);
            logger.info("GTW API RAW RESPONSE: \n"+gtwResponse);

            return gtwSuccess(gtwResponse);


        } catch (Exception e) {
            logger.error("GET OBJECT PROPERTY FAILURE! ", e);
            return gtwError(e).toString();
        }
    }

    @Put()
    public String setPropertyValue(Representation entity) {
        try {
            String oid = getAttribute(OBJECT_ID);
            String pid = getAttribute(PROPERTY_ID);

            logger.info("SETTING REMOTE PROPERTY VALUE TARGET FOR: ");
            logger.info("OID: " + oid);
            logger.info("PID: " + pid);

            if(entity == null) {
                throw new Exception("Empty payload!");
            }
            String rawPayload = entity.getText();

            logger.info("PAYLOAD: " + rawPayload);

            ThingDescription thing = getCallerObject();
            logger.info("CALLER THING: " + thing.toSimpleString());

            String endpoint = GatewayAPIClient.getInteractionEndpoint(GatewayAPIClient.OBJECT_PROPERTY_ENDPOINT, oid, pid);

            logger.info("GTW API ENDPOINT: "+endpoint);

            ClientResponse gtwResponse = GatewayAPIClient.put(endpoint, rawPayload, thing.oid, thing.password);
            logger.info("GTW API RAW RESPONSE: \n"+gtwResponse);

            return gtwSuccess(gtwResponse);


        }
        catch (Exception e) {
            logger.error("SET OBJECT PROPERTY FAILURE! ", e);
            return gtwError(e).toString();
        }
    }
}