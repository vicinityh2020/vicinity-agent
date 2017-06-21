package sk.intersoft.vicinity.adapter.testing.service.resource;

import org.json.JSONObject;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

public class CustomActionResource extends ServerResource {

    @Post()
    public String postAction(Representation entity)  {
        try{

            System.out.println("exec action");

            String oid = getAttribute("oid");

            JSONObject input = new JSONObject(entity.getText());

            JSONObject out = new JSONObject();
            out.put("echo", "exec action on custom endpoint");
            out.put("oid", oid);
            out.put("payload", input);

            return out.toString();
        }
        catch(Exception e){
            return "{}";
        }
    }

}
