package sk.intersoft.vicinity.adapter.testing.service.resource;

import org.json.JSONObject;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

public class EventListenerResource extends ServerResource {

    @Post()
    public String readEvent(Representation entity)  {
        try{

            String oid = getAttribute("oid");
            String pid = getAttribute("pid");

            System.out.println("\n\n");
            System.out.println("getting event: ");
            System.out.println("oid: "+oid);
            System.out.println("pid: "+pid);

            JSONObject input = new JSONObject(entity.getText());

            System.out.println("event payload: \n"+input.toString(2));
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return "{\"event\": \"received\"}";
    }

}
