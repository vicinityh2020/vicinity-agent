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

public class ObjectActionResource extends ServerResource {

    private static String OBJECT_ID = "oid";
    private static String ACTION_ID = "aid";


    @Post()
    public String executeAction(Representation entity)  {

        try{
            JSONObject payload = new JSONObject(entity.getText());

            String oid = getAttribute(OBJECT_ID);
            String aid = getAttribute(ACTION_ID);

            getLogger().info("EXEC ACTION FOR: ["+oid+"]["+aid+"]");

            String endpoint = AdapterEndpoint.getEndpoint(oid, aid, InteractionPattern.ACTION, false);

            getLogger().info("EXEC ACTION ENDPOINT: ["+endpoint+"]");

            AgentAdapter adapter = AgentAdapter.getInstance();

            String adapterResponse = adapter.post(endpoint, payload.toString());
            getLogger().info("ADAPTER RESPONSE: \n"+endpoint);
            JSONObject result = new JSONObject(adapterResponse);


            getLogger().info("ADAPTER RETURNS: \n"+result.toString(2));
            return ServiceResponse.success(result).toString();

        }
        catch(Exception e){
            return ServiceResponse.failure(e).toString();
        }

    }


    @Get()
    public String getActionStatus()  {

        try{

            String oid = getAttribute(OBJECT_ID);
            String aid = getAttribute(ACTION_ID);

            getLogger().info("ACTION SATTUS FOR: ["+oid+"]["+aid+"]");

            String endpoint = AdapterEndpoint.getEndpoint(oid, aid, InteractionPattern.ACTION, true);

            getLogger().info("ACTION STAUTS ENDPOINT: ["+endpoint+"]");

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

}
