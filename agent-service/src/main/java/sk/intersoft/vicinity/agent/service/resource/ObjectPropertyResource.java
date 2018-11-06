package sk.intersoft.vicinity.agent.service.resource;

import org.json.JSONObject;
import org.restlet.data.ClientInfo;
import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.clients.AdapterClient;
import sk.intersoft.vicinity.agent.clients.AdapterEndpoint;
import sk.intersoft.vicinity.agent.clients.ClientResponse;
import sk.intersoft.vicinity.agent.thing.InteractionPattern;
import sk.intersoft.vicinity.agent.thing.InteractionPatternEndpoint;
import sk.intersoft.vicinity.agent.thing.ThingDescription;

public class ObjectPropertyResource extends AgentResource {
    final static Logger logger = LoggerFactory.getLogger(ObjectPropertyResource.class.getName());

    private static String OBJECT_ID = "oid";
    private static String PROPERTY_ID = "pid";

    @Get("json")
    public String getPropertyValue() {
        try {
            String oid = getAttribute(OBJECT_ID);
            String pid = getAttribute(PROPERTY_ID);

            logger.info("GETTING LOCAL PROPERTY VALUE TARGET FOR: ");
            logger.info("OID: " + oid);
            logger.info("PID: " + pid);

            String query = getQueryString(getQuery());
            logger.info("QUERY: " + query);

            ClientInfo info = getClientInfo();
            logger.info("client: "+info);
            if(info != null){
                logger.info("addr: "+info.getAddress());
                logger.info("addrs: "+info.getForwardedAddresses());
                logger.info("port: "+info.getPort());
            }

            ThingDescription thing = getThingByOID(oid);
            logger.info("ADAPTER THING FOR OID [" + oid + "]: " + thing.toSimpleString());

            String endpoint = AdapterEndpoint.getEndpoint(thing, pid, InteractionPattern.PROPERTY, InteractionPatternEndpoint.READ);


            logger.info("GET PROPERTY ADAPTER ENDPOINT: [" + endpoint + "]");

            ClientResponse adapterResponse = AdapterClient.get(endpoint, query);
            logger.info("ADAPTER RAW RESPONSE: \n" + adapterResponse);

            return adapterSuccess(adapterResponse);

        } catch (Exception e) {
            logger.error("GET OBJECT PROPERTY FAILURE! ", e);
            return adapterError(e).toString();
        }
    }

    @Put()
    public String setPropertyValue(Representation entity) {
        try {
            String oid = getAttribute(OBJECT_ID);
            String pid = getAttribute(PROPERTY_ID);

            logger.info("SETTING LOCAL PROPERTY VALUE TARGET FOR: ");
            logger.info("OID: " + oid);
            logger.info("PID: " + pid);
            String query = getQueryString(getQuery());
            logger.info("QUERY: " + query);


            ClientInfo info = getClientInfo();
            logger.info("client: "+info);
            if(info != null){
                logger.info("addr: "+info.getAddress());
                logger.info("addrs: "+info.getForwardedAddresses());
                logger.info("port: "+info.getPort());
            }

            if(entity == null) {
                throw new Exception("Empty payload!");
            }
            String rawPayload = entity.getText();

            logger.info("PAYLOAD: " + rawPayload);

            ThingDescription thing = getThingByOID(oid);
            logger.info("ADAPTER THING FOR OID [" + oid + "]: " + thing.toSimpleString());

            String endpoint = AdapterEndpoint.getEndpoint(thing, pid, InteractionPattern.PROPERTY, InteractionPatternEndpoint.WRITE);

            logger.info("SET PROPERTY ADAPTER ENDPOINT: [" + endpoint + "]");

            ClientResponse adapterResponse = AdapterClient.put(endpoint, rawPayload, query);
            logger.info("ADAPTER RAW RESPONSE: \n" + adapterResponse);

            return adapterSuccess(adapterResponse);

        }
        catch (Exception e) {
            logger.error("SET OBJECT PROPERTY FAILURE! ", e);
            return adapterError(e).toString();
        }
    }
}