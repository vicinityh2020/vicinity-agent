package sk.intersoft.vicinity.adapter.testing.service.resource;

import org.json.JSONObject;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class GetCustomPropertyResource extends ServerResource {

    @Get("json")
    public String getPropertyValue()  {
        try{

            System.out.println("getting custom property");

            String oid = getAttribute("oid");

            JSONObject out = new JSONObject();
            out.put("echo", "get property on custom endpoint");
            out.put("oid", oid);

            return out.toString();
        }
        catch(Exception e){
            return "{}";
        }
    }

}
