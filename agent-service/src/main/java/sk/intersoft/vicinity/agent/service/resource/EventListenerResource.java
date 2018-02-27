package sk.intersoft.vicinity.agent.service.resource;

import org.json.JSONObject;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.adapter.AdapterEndpoint;
import sk.intersoft.vicinity.agent.adapter.AgentAdapter;
import sk.intersoft.vicinity.agent.thing.InteractionPattern;

/**
 * Resource listening to events from GTW API and resending them back to adapter
 */
public class EventListenerResource extends AgentResource {
    final static Logger logger = LoggerFactory.getLogger(EventListenerResource.class.getName());

    private static String OBJECT_ID = "oid";
    private static String EVENT_ID = "eid";

    @Post()
    public String passEventToAdapter(Representation entity)  {
        try{
            String oid = getAttribute(OBJECT_ID);
            String eid = getAttribute(EVENT_ID);

            logger.info("EVENT LISTENER - PASS THE EVENT TO ADAPTER FOR: ");
            logger.info("OID: "+oid);
            logger.info("EID: " + eid);

            if(entity == null) {
                throw new Exception("Empty payload!");
            }
            String rawPayload = entity.getText();

            logger.info("PAYLOAD: " + rawPayload);

            JSONObject payload = new JSONObject(rawPayload);

            String endpoint = AdapterEndpoint.getEndpoint(getThing(oid), eid, InteractionPattern.EVENT, false);

            logger.info("EVENT LISTENER ADAPTER ENDPOINT: [" + endpoint + "]");

            String adapterResponse = AgentAdapter.post(endpoint, payload.toString());
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
