package sk.intersoft.vicinity.agent.service.resource;

import org.json.JSONObject;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import sk.intersoft.vicinity.agent.adapter.AgentAdapter;
import sk.intersoft.vicinity.agent.config.AgentConfig;
import sk.intersoft.vicinity.agent.service.response.ServiceResponse;

public class ObjectActionResource extends ServerResource {

    private static String OBJECT_ID = "oid";
    private static String ACTION_ID = "aid";

    @Post()
    public String getPropertyValue(Representation entity)  {
        try{
            String oid = getAttribute(OBJECT_ID);
            String pid = getAttribute(ACTION_ID);
            String iid = AgentConfig.objectInfrastructureId(oid);

            JSONObject input = new JSONObject(entity.getText());

            getLogger().info("EXECUTING ACTION FOR: ["+oid+"]["+pid+"]["+iid+"]");
            getLogger().info("INPUT: \n"+input.toString(2));

            if(iid != null) {
                AgentAdapter adapter = AgentAdapter.getInstance();

                String endpoint = "/objects/"+iid+"/actions/"+pid;
                getLogger().info("ADAPTER ENDPOINT: \n"+endpoint);

                String adapterResponse = adapter.post(endpoint, input.toString());
                getLogger().info("ADAPTER RESPONSE: \n"+endpoint);
                JSONObject result = new JSONObject(adapterResponse);


                getLogger().info("ADAPTER RETURNS: \n"+result.toString(2));
                return ServiceResponse.success(result).toString();
            }

            else {
                throw new Exception("UNKNOWN InfrastuctureId for UUID=["+oid+"]");
            }

        }
        catch(Exception e){
            return ServiceResponse.failure(e).toString();
        }
    }

}
