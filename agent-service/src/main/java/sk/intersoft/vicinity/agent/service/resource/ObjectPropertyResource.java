package sk.intersoft.vicinity.agent.service.resource;

import org.json.JSONObject;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.adapter.AdapterEndpoint;
import sk.intersoft.vicinity.agent.adapter.AgentAdapter;
import sk.intersoft.vicinity.agent.thing.InteractionPattern;
import sk.intersoft.vicinity.agent.thing.ThingDescription;

public class ObjectPropertyResource extends AgentResource {
    final static Logger logger = LoggerFactory.getLogger(ObjectPropertyResource.class.getName());

    private static String OBJECT_ID = "oid";
    private static String PROPERTY_ID = "pid";

   @Get("json")
    public String getPropertyValue()  {
        try{
            String oid = getAttribute(OBJECT_ID);
            String pid = getAttribute(PROPERTY_ID);

            logger.info("GETTING PROPERTY VALUE FOR: ");
            logger.info("OID: "+oid);
            logger.info("PID: " + pid);

            String endpoint = AdapterEndpoint.getEndpoint(getThing(oid), pid, InteractionPattern.PROPERTY, true);

            logger.info("GET PROPERTY ADAPTER ENDPOINT: [" + endpoint + "]");

            String adapterResponse = AgentAdapter.get(endpoint);
            logger.info("ADAPTER RAW RESPONSE: \n"+adapterResponse);

            JSONObject result = new JSONObject(adapterResponse);


            logger.info("ADAPTER RESPONSE: \n"+result.toString(2));
            return ResourceResponse.success(result).toString();

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

            JSONObject payload = new JSONObject(rawPayload);

            String endpoint = AdapterEndpoint.getEndpoint(getThing(oid), pid, InteractionPattern.PROPERTY, false);

            logger.info("SET PROPERTY ADAPTER ENDPOINT: [" + endpoint + "]");

            String adapterResponse = AgentAdapter.put(endpoint, payload.toString());
            logger.info("ADAPTER RAW RESPONSE: \n"+adapterResponse);

            JSONObject result = new JSONObject(adapterResponse);

            logger.info("ADAPTER JSON RESPONSE: \n"+result.toString(2));
            return ResourceResponse.success(result).toString();

        }
        catch(Exception e){
            logger.error("", e);
            return ResourceResponse.failure(e).toString();
        }

    }



}
