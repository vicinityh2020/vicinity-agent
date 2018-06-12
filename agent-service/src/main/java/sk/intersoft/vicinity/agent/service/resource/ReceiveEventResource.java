package sk.intersoft.vicinity.agent.service.resource;

import org.json.JSONObject;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.clients.AdapterClient;
import sk.intersoft.vicinity.agent.clients.AdapterEndpoint;
import sk.intersoft.vicinity.agent.service.config.AdapterConfig;
import sk.intersoft.vicinity.agent.service.config.Configuration;
import sk.intersoft.vicinity.agent.thing.InteractionPattern;
import sk.intersoft.vicinity.agent.thing.InteractionPatternEndpoint;
import sk.intersoft.vicinity.agent.thing.ThingDescription;

public class ReceiveEventResource extends AgentResource {
    final static Logger logger = LoggerFactory.getLogger(ReceiveEventResource.class.getName());

    private static String OBJECT_ID = "oid";
    private static String EVENT_ID = "eid";


    @Put()
    public String receiveEvent(Representation entity) {
        try {
            String oid = getAttribute(OBJECT_ID);
            String eid = getAttribute(EVENT_ID);

            logger.info("RECEIVING EVENT FOR: ");
            logger.info("SUBSCRIBER OID: " + oid);
            logger.info("EID: " + eid);

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

            String endpoint = adapter.endpoint + AdapterEndpoint.getReceiveEventEndpoint(thing.infrastructureId, eid);

            logger.info("PASS EVENT TO ADAPTER ENDPOINT: [" + endpoint + "]");

            String adapterResponse = AdapterClient.put(endpoint, rawPayload);
            logger.info("ADAPTER RAW RESPONSE: \n" + adapterResponse);

            return ResourceResponse.success(adapterResponse).toString();

        }
        catch (Exception e) {
            logger.error("SET OBJECT PROPERTY FAILURE! ", e);
            return ResourceResponse.failure(e).toString();
        }
    }
}