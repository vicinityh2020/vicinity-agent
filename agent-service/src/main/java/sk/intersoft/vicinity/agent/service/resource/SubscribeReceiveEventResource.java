package sk.intersoft.vicinity.agent.service.resource;

import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.clients.AdapterClient;
import sk.intersoft.vicinity.agent.clients.AdapterEndpoint;
import sk.intersoft.vicinity.agent.clients.GatewayAPIClient;
import sk.intersoft.vicinity.agent.service.config.AdapterConfig;
import sk.intersoft.vicinity.agent.service.config.Configuration;
import sk.intersoft.vicinity.agent.thing.InteractionPattern;
import sk.intersoft.vicinity.agent.thing.ThingDescription;

public class SubscribeReceiveEventResource extends AgentResource {
    final static Logger logger = LoggerFactory.getLogger(SubscribeReceiveEventResource.class.getName());

    private static String OBJECT_ID = "oid";
    private static String EVENT_ID = "eid";
    private static String SOURCE_OID = "sourceOid";


    @Post()
    public String subscribeEventChannel() {
        try {
            String oid = getAttribute(OBJECT_ID);
            String eid = getAttribute(EVENT_ID);

            logger.info("SUBSCRIBING EVENT CHANNEL FOR: ");
            logger.info("PUBLISHER OID: " + oid);
            logger.info("EID: " + eid);

            ThingDescription thing = getCallerObject();
            logger.info("CALLER THING: " + thing.toSimpleString());

            AdapterConfig adapter = Configuration.adapters.get(thing.adapterId);
            logger.info("CALLER THING ADAPTER: " + adapter.toSimpleString());

            if(!adapter.hasEndpoint()) {
                throw new Exception("Adapter [" + adapter.adapterId + "] does not have endpoint.. unable to subscribe its thing to channels, events can not be received!");
            }


            String endpoint = GatewayAPIClient.getSubscribeEventChannelEndpoint(oid, eid);

            String gtwResponse = GatewayAPIClient.post(endpoint, null, thing.oid, thing.password);
            logger.info("GTW API RAW RESPONSE: \n"+gtwResponse);

            return gtwResponse;

        }
        catch (Exception e) {
            logger.error("SUBSCRIBE EVENT CHANNEL FAILURE! ", e);
            return ResourceResponse.failure(e).toString();
        }
    }

    @Put()
    public String receiveEvent(Representation entity) {
        try {
            String oid = getAttribute(OBJECT_ID);
            String eid = getAttribute(EVENT_ID);
            String sourceOid = getQueryValue(SOURCE_OID);

            logger.info("RECEIVING EVENT FOR: ");
            logger.info("SUBSCRIBER OID: " + oid);
            logger.info("EID: " + eid);
            logger.info("PUBLISHER (SOURCE) OID: " + sourceOid);


            if(sourceOid == null || sourceOid.trim().equals("")){
                throw new Exception("Unknown source OID!");
            }

            if(entity == null) {
                throw new Exception("Empty payload!");
            }
            String rawPayload = entity.getText();

            logger.info("PAYLOAD: " + rawPayload);

            ThingDescription thing = getThingByOID(oid);
            logger.info("THING FOR OID [" + oid + "]: " + thing.toSimpleString());

            AdapterConfig adapter = Configuration.adapters.get(thing.adapterId);
            if(adapter == null) {
                throw new Exception("MISSING EVENT RECEIVING ADAPTER FOR THING: "+thing.toSimpleString());
            }
            logger.info("ADAPTER FOR THING [" + oid + "]: " + thing.toSimpleString());
            logger.info("\n" + adapter.toSimpleString());

            if(!adapter.hasEndpoint()) {
                throw new Exception("Adapter [" + adapter.adapterId + "] does not have endpoint.. unable to pass the event into adapter!");
            }
            String endpoint = adapter.endpoint + AdapterEndpoint.getReceiveEventEndpoint(thing.infrastructureId, sourceOid.trim(), eid);

            logger.info("PASS EVENT TO ADAPTER ENDPOINT: [" + endpoint + "]");

            String adapterResponse = AdapterClient.put(endpoint, rawPayload);
            logger.info("ADAPTER RAW RESPONSE: \n" + adapterResponse);

            return ResourceResponse.success(adapterResponse).toString();

        }
        catch (Exception e) {
            logger.error("RECEIVE EVENT FAILURE! ", e);
            return ResourceResponse.failure(e).toString();
        }
    }
}