package sk.intersoft.vicinity.agent.service.resource;

import org.json.JSONObject;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import sk.intersoft.vicinity.agent.adapter.AdapterEndpoint;
import sk.intersoft.vicinity.agent.adapter.AgentAdapter;
import sk.intersoft.vicinity.agent.config.thing.InteractionPattern;
import sk.intersoft.vicinity.agent.service.response.ServiceResponse;

public class ObjectActionTaskStubResource extends ServerResource {

    private static String OBJECT_ID = "oid";
    private static String ACTION_ID = "aid";
    private static String TASK_ID = "tid";


    @Get()
    public String doStub()  {

        try{

            String oid = getAttribute(OBJECT_ID);
            String aid = getAttribute(ACTION_ID);
            String tid = getAttribute(TASK_ID);

            getLogger().info("GET ACTION TASK FOR: ["+oid+"]["+aid+"]["+tid+"]");

            JSONObject result = new JSONObject();
            result.put("echo", "action task status");
            result.put("oid", oid);
            result.put("aid", aid);
            result.put("tid", tid);

            getLogger().info("AGENT RETURNS STUB: \n"+result.toString(2));
            return ServiceResponse.success(result).toString();

        }
        catch(Exception e){
            return ServiceResponse.failure(e).toString();
        }

    }

}
