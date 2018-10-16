package sk.intersoft.vicinity.adapter.testing.service.resource;

import org.json.JSONObject;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

public class EventListenerResource extends ServerResource {

    @Put()
    public String readEvent(Representation entity)  {
        try{

            String iid = getAttribute("iid");
            String oid = getAttribute("oid");
            String eid = getAttribute("eid");

            System.out.println("\n\n");
            System.out.println("getting event: ");
            System.out.println("receiver oid: "+iid);
            System.out.println("source oid: "+oid);
            System.out.println("eid: "+eid);

            JSONObject input = new JSONObject(entity.getText());

            System.out.println("event payload: \n"+input.toString(2));


            return "{\"event\": \"received [from: "+oid+"][to: "+iid+"][event: "+eid+"]\"}";

        }
        catch(Exception e){
            e.printStackTrace();
            return "{\"event\": \"not received\"}";
        }
    }

}
