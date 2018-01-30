package sk.intersoft.vicinity.agent.service.resource;

import org.json.JSONObject;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.adapter.AdapterEndpoint;
import sk.intersoft.vicinity.agent.adapter.AgentAdapter;
import sk.intersoft.vicinity.agent.thing.InteractionPattern;

/**
 * Resource listening to events from adapter and resenting them to GTW API
 */
public class EventPublisherResource extends ServerResource {
    final static Logger logger = LoggerFactory.getLogger(EventPublisherResource.class.getName());

    private static String OBJECT_ID = "oid";
    private static String EVENT_ID = "eid";

    @Post()
    public String passEventToGtwAPI(Representation entity)  {
        try{
            String oid = getAttribute(OBJECT_ID);
            String eid = getAttribute(EVENT_ID);

            logger.info("EVENT PUBLISHER - PASS THE EVENT TO GTW API FOR: ");
            logger.info("OID: "+oid);
            logger.info("EID: " + eid);

            if(entity == null) {
                throw new Exception("Empty payload!");
            }
            String rawPayload = entity.getText();

            logger.info("PAYLOAD: " + rawPayload);

            JSONObject payload = new JSONObject(rawPayload);

//            String endpoint = AdapterEndpoint.getEndpoint(oid, eid, InteractionPattern.EVENT, false);
//
//            logger.info("EVENT LISTENER ADAPTER ENDPOINT: [" + endpoint + "]");
//
//            AgentAdapter adapter = AgentAdapter.getInstance();
//
//            String adapterResponse = adapter.post(endpoint, payload.toString());
//            logger.info("ADAPTER RAW RESPONSE: \n"+adapterResponse);
//
//            JSONObject result = new JSONObject(adapterResponse);
//
//
//            logger.info("ADAPTER RESPONSE: \n"+result.toString(2));
//            return ResourceResponse.success(result).toString();
            return "implement me, when GWT API implemented";
        }
        catch(Exception e){
            logger.error("", e);
            return ResourceResponse.failure(e).toString();
        }
    }


}
