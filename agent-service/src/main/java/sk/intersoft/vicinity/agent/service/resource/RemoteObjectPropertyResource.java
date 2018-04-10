package sk.intersoft.vicinity.agent.service.resource;

import org.json.JSONObject;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.adapter.AdapterEndpoint;
import sk.intersoft.vicinity.agent.adapter.AgentAdapter;
import sk.intersoft.vicinity.agent.gateway.GatewayAPIClient;
import sk.intersoft.vicinity.agent.thing.InteractionPattern;
import sk.intersoft.vicinity.agent.thing.ThingDescription;

public class RemoteObjectPropertyResource extends AgentResource {
    final static Logger logger = LoggerFactory.getLogger(RemoteObjectPropertyResource.class.getName());

    private static String OBJECT_ID = "oid";
    private static String PROPERTY_ID = "pid";

    @Get("json")
    public String getPropertyValue()  {
        try{
            String oid = getAttribute(OBJECT_ID);
            String pid = getAttribute(PROPERTY_ID);

            logger.info("GETTING PROPERTY VALUE TARGET FOR: ");
            logger.info("OID: "+oid);
            logger.info("PID: " + pid);



            ThingDescription caller = getCallerObject();
            logger.info("CALLER: " + caller.toSimpleString());
            logger.info("CALLING GTW API WITH CALLER CREDENTIALS");

            String endpoint = GatewayAPIClient.getInteractionEndpoint(GatewayAPIClient.OBJECT_PROPERTY, oid, pid);

            logger.info("GTW API ENDPOINT: "+endpoint);

            String gtwResponse = GatewayAPIClient.get(endpoint, caller.oid, caller.password);
            logger.info("GTW API RAW RESPONSE: \n"+gtwResponse);

            return gtwResponse;


        }
        catch(Exception e){
            logger.error("", e);
            return ResourceResponse.failure(e).toString();
        }
    }

    @Put()
    public String setPropertyValue(Representation entity)  {

        try{
            String oid = getAttribute(OBJECT_ID);
            String pid = getAttribute(PROPERTY_ID);

            logger.info("SETTING PROPERTY VALUE FOR: ");
            logger.info("OID: "+oid);
            logger.info("PID: " + pid);

            if(entity == null) {
                throw new Exception("Empty payload!");
            }
            String rawPayload = entity.getText();

            logger.info("PAYLOAD: " + rawPayload);



            ThingDescription caller = getCallerObject();
            logger.info("CALLER: " + caller.toSimpleString());
            logger.info("CALLING GTW API WITH CALLER CREDENTIALS");

            String endpoint = GatewayAPIClient.getInteractionEndpoint(GatewayAPIClient.OBJECT_PROPERTY, oid, pid);

            logger.info("GTW API ENDPOINT: "+endpoint);

            String gtwResponse = GatewayAPIClient.put(endpoint, rawPayload, caller.oid, caller.password);
            logger.info("GTW API RAW RESPONSE: \n"+gtwResponse);

            return gtwResponse;


        }
        catch(Exception e){
            logger.error("", e);
            return ResourceResponse.failure(e).toString();
        }

    }



}
