package sk.intersoft.vicinity.adapter.testing.service.resource;

import org.json.JSONObject;
import org.restlet.representation.Representation;
import org.restlet.resource.*;

public class ActionResource extends ServerResource {

    @Post()
    public String postAction(Representation entity)  {
        try{

            String txt = entity.getText();
            System.out.println("exec action");
            System.out.println("received e: "+entity);
            System.out.println("received cs: "+entity.getCharacterSet());
            System.out.println("received txt: "+txt);

            String oid = getAttribute("oid");
            String aid = getAttribute("aid");

            JSONObject input = new JSONObject(txt);

            JSONObject out = new JSONObject();
            out.put("echo", "exec action");
            out.put("oid", oid);
            out.put("aid", aid);
            out.put("payload", input);

            return out.toString();
        }
        catch(Exception e){
            e.printStackTrace();
            return "{}";
        }
    }

    @Delete()
    public String cancelAction()  {
        try{

            System.out.println("cancel action");

            String oid = getAttribute("oid");
            String aid = getAttribute("aid");

            JSONObject out = new JSONObject();
            out.put("echo", "cancel action");
            out.put("oid", oid);
            out.put("aid", aid);

            return out.toString();
        }
        catch(Exception e){
            return "{}";
        }
    }

    @Get()
    public String getActionStatus(Representation entity)  {
        try{

            System.out.println("get action status");

            String oid = getAttribute("oid");
            String aid = getAttribute("aid");


            JSONObject out = new JSONObject();
            out.put("echo", "action status");
            out.put("oid", oid);
            out.put("aid", aid);

            return out.toString();
        }
        catch(Exception e){
            return "{}";
        }
    }

}
