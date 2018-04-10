package sk.intersoft.vicinity.agent.service.resource;

import org.json.JSONObject;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.adapter.AdapterEndpoint;
import sk.intersoft.vicinity.agent.adapter.AgentAdapter;
import sk.intersoft.vicinity.agent.gateway.GatewayAPIClient;
import sk.intersoft.vicinity.agent.thing.InteractionPattern;
import sk.intersoft.vicinity.agent.thing.ThingDescription;

public class ObjectEventResource extends AgentResource {
    final static Logger logger = LoggerFactory.getLogger(ObjectEventResource.class.getName());

    private static String OBJECT_ID = "oid";
    private static String EVENT_ID = "eid";

    @Put()
    public String publishEvent(Representation entity)  {
        try{
            String oid = getAttribute(OBJECT_ID);
            String eid = getAttribute(EVENT_ID);

            logger.info("CONSUMING EVENT FOR: ");
            logger.info("OID: "+oid);
            logger.info("EID: " +eid);


            ThingDescription thing = getThing(oid);

            logger.info("CONSUMER (subscriber) THING IN ADAPTER: " + thing.toSimpleString());

            if(entity == null) {
                throw new Exception("Empty payload!");
            }
            String rawPayload = entity.getText();

            logger.info("PAYLOAD: " + rawPayload);

            logger.info("PASSING EVENT TO ADAPTER ADAPTER");

            String endpoint = AdapterEndpoint.getEventEndpoint(thing, eid);

            logger.info("ADAPTER API ENDPOINT: "+endpoint);

            String adapterResponse = AgentAdapter.put(endpoint, rawPayload.toString());
            logger.info("ADAPTER RAW RESPONSE: \n"+adapterResponse);

            JSONObject result = new JSONObject(adapterResponse);

            return ResourceResponse.success(result).toString();

        }
        catch(Exception e){
            logger.error("", e);
            return ResourceResponse.failure(e).toString();
        }

    }


}
