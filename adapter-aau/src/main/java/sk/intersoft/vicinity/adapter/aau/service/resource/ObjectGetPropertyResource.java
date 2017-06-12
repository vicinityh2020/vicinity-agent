package sk.intersoft.vicinity.adapter.aau.service.resource;

import org.json.JSONObject;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import sk.intersoft.vicinity.adapter.aau.service.response.ServiceResponse;

public class ObjectGetPropertyResource extends ServerResource {

    private static String OBJECT_ID = "oid";
    private static String PROPERTY_ID = "pid";

    @Get("json")
    public String getPropertyValue()  {
        try{
            String oid = getAttribute(OBJECT_ID);
            String pid = getAttribute(PROPERTY_ID);

            getLogger().info("GETTING PROPERTY VALUES FOR: ["+oid+"]["+pid+"]");

            return "{\"test\": \"is up\"}";

        }
        catch(Exception e){
            return ServiceResponse.failure(e).toString();
        }
    }

}
