package sk.intersoft.vicinity.agent.service.resource;

import org.json.JSONObject;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import sk.intersoft.vicinity.agent.adapter.AgentAdapter;
import sk.intersoft.vicinity.agent.config.AgentConfig;
import sk.intersoft.vicinity.agent.service.response.ServiceResponse;

public class ObjectGetPropertyResource extends ServerResource {

    private static String OBJECT_ID = "oid";
    private static String PROPERTY_ID = "pid";

    @Get("json")
    public String getPropertyValue()  {
        try{
            String oid = getAttribute(OBJECT_ID);
            String pid = getAttribute(PROPERTY_ID);
            String iid = AgentConfig.objectInfrastructureId(oid);

            getLogger().info("GETTING PROPERTY VALUES FOR: ["+oid+"]["+pid+"]["+iid+"]");

            if(iid != null) {
                AgentAdapter adapter = AgentAdapter.getInstance();

                String endpoint = "/objects/"+iid+"/properties/"+pid;
                getLogger().info("ADAPTER ENDPOINT: \n"+endpoint);

                String adapterResponse = adapter.get(endpoint);
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
