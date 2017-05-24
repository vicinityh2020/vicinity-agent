package sk.intersoft.vicinity.agent.service.resource;

import org.json.JSONArray;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import sk.intersoft.vicinity.adapter.AgentAdapter;
import sk.intersoft.vicinity.agent.config.AgentConfig;
import sk.intersoft.vicinity.agent.service.response.ServiceResponse;

import java.util.logging.Logger;

public class ObjectPropertiesResource extends ServerResource {

    private static String OBJECT_ID = "oid";
    private static String PROPERTY_IDS = "pids";

    @Get("json")
    public String getPropertyValues()  {
        try{
            String oid = getAttribute(OBJECT_ID);
            String pids = getAttribute(PROPERTY_IDS);
            getLogger().info("GETTING PROPERTY VALUES FOR: ["+oid+"]["+pids+"]");

            String iid = AgentConfig.deviceUUID2Infrastructure.get(oid);
            if(iid != null) {
                AgentAdapter adapter = AgentConfig.getAdapter();
                JSONArray result = adapter.getPropertiesValue(iid, pids);

                getLogger().info("ADAPTER RETURNS: "+result.toString());
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
