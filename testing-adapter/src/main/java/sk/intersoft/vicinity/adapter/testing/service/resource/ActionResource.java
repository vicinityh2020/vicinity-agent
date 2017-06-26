package sk.intersoft.vicinity.adapter.testing.service.resource;

import org.json.JSONObject;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

public class ActionResource extends ServerResource {

    @Post()
    public String postAction(Representation entity)  {
        try{

            System.out.println("exec action");

            String oid = getAttribute("oid");
            String aid = getAttribute("aid");

            JSONObject input = new JSONObject(entity.getText());

            JSONObject out = new JSONObject();
            out.put("echo", "exec action");
            out.put("oid", oid);
            out.put("aid", aid);
            out.put("payload", input);

            return out.toString();
        }
        catch(Exception e){
            return "{}";
        }
    }


}
