package sk.intersoft.vicinity.agent.service.resource;

import org.json.JSONObject;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.adapter.AdapterEndpoint;
import sk.intersoft.vicinity.agent.adapter.AgentAdapter;
import sk.intersoft.vicinity.agent.thing.InteractionPattern;

public class ObjectActionResource extends AgentResource {
    final static Logger logger = LoggerFactory.getLogger(ObjectActionResource.class.getName());

    private static String OBJECT_ID = "oid";
    private static String ACTION_ID = "aid";

   @Get("json")
    public String getActionStatus()  {
        try{
            String oid = getAttribute(OBJECT_ID);
            String aid = getAttribute(ACTION_ID);

            logger.info("GETTING ACTION STATUS FOR: ");
            logger.info("OID: "+oid);
            logger.info("AID: " + aid);

            String endpoint = AdapterEndpoint.getEndpoint(getThing(oid), aid, InteractionPattern.ACTION, true);

            logger.info("GET ACTION STATUS ADAPTER ENDPOINT: [" + endpoint + "]");

            String adapterResponse = AgentAdapter.get(endpoint);
            logger.info("ADAPTER RAW RESPONSE: \n"+adapterResponse);

            JSONObject result = new JSONObject(adapterResponse);


            return ResourceResponse.success(result).toString();

        }
        catch(Exception e){
            logger.error("", e);
            return ResourceResponse.failure(e).toString();
        }
    }

   @Post()
    public String executeAction(Representation entity)  {

        try{
            String oid = getAttribute(OBJECT_ID);
            String aid = getAttribute(ACTION_ID);

            logger.info("EXECUTING ACTION FOR: ");
            logger.info("OID: "+oid);
            logger.info("AID: " + aid);

            if(entity == null) {
                throw new Exception("Empty payload!");
            }
            String rawPayload = entity.getText();

            logger.info("PAYLOAD: " + rawPayload);

            JSONObject payload = new JSONObject(rawPayload);

            String endpoint = AdapterEndpoint.getEndpoint(getThing(oid), aid, InteractionPattern.ACTION, false);

            logger.info("EXECUTE ACTION ADAPTER ENDPOINT: [" + endpoint + "]");

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
