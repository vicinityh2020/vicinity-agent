package sk.intersoft.vicinity.agent.service.resource;

import org.json.JSONObject;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;
import sk.intersoft.vicinity.agent.adapter.AdapterEndpoint;
import sk.intersoft.vicinity.agent.adapter.AgentAdapter;
import sk.intersoft.vicinity.agent.config.AgentConfig;
import sk.intersoft.vicinity.agent.config.thing.InteractionPattern;
import sk.intersoft.vicinity.agent.service.response.ServiceResponse;

public class ObjectGetSetPropertyResource extends ServerResource {

    private static String OBJECT_ID = "oid";
    private static String PROPERTY_ID = "pid";

    @Get("json")
    public String getPropertyValue()  {
        try{
            String oid = getAttribute(OBJECT_ID);
            String pid = getAttribute(PROPERTY_ID);

            getLogger().info("GETTING PROPERTY VALUES FOR: ["+oid+"]["+pid+"]");

            String endpoint = AdapterEndpoint.getEndpoint(oid, pid, InteractionPattern.PROPERTY, true);

            getLogger().info("GETTING PROPERTY ENDPOINT: ["+endpoint+"]");

            AgentAdapter adapter = AgentAdapter.getInstance();

            String adapterResponse = adapter.get(endpoint);
            getLogger().info("ADAPTER RESPONSE: \n"+endpoint);
            JSONObject result = new JSONObject(adapterResponse);


            getLogger().info("ADAPTER RETURNS: \n"+result.toString(2));
            return ServiceResponse.success(result).toString();

        }
        catch(Exception e){
            return ServiceResponse.failure(e).toString();
        }
    }


    @Put()
    public String setPropertyValue(Representation entity)  {

        try{
            JSONObject payload = new JSONObject(entity.getText());

            String oid = getAttribute(OBJECT_ID);
            String pid = getAttribute(PROPERTY_ID);

            getLogger().info("SETTING PROPERTY VALUES FOR: ["+oid+"]["+pid+"]");

            String endpoint = AdapterEndpoint.getEndpoint(oid, pid, InteractionPattern.PROPERTY, false);

            getLogger().info("SETTING PROPERTY ENDPOINT: ["+endpoint+"]");

            AgentAdapter adapter = AgentAdapter.getInstance();

            String adapterResponse = adapter.put(endpoint, payload.toString());
            getLogger().info("ADAPTER RESPONSE: \n"+endpoint);
            JSONObject result = new JSONObject(adapterResponse);


            getLogger().info("ADAPTER RETURNS: \n"+result.toString(2));
            return ServiceResponse.success(result).toString();

        }
        catch(Exception e){
            return ServiceResponse.failure(e).toString();
        }

    }


}
