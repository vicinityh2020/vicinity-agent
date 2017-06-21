package sk.intersoft.vicinity.adapter.testing.service.resource;

import org.json.JSONObject;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

public class SetCustomPropertyResource extends ServerResource {

    @Put()
    public String setPropertyValue(Representation entity)  {
        try{

            System.out.println("setting custom property");

            String oid = getAttribute("oid");

            JSONObject input = new JSONObject(entity.getText());

            JSONObject out = new JSONObject();
            out.put("echo", "set property on custom endpoint");
            out.put("oid", oid);
            out.put("payload", input);

            return out.toString();
        }
        catch(Exception e){
            return "{}";
        }
    }

}
