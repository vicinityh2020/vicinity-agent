package sk.intersoft.vicinity.adapter.aau.service.resource;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import java.util.UUID;

public class EventListenerResource extends ServerResource {




    @Post()
    public String readEvent(Representation entity)  {
        try{

            String oid = getAttribute("oid");
            String eid = getAttribute("eid");

            System.out.println("\n\n");
            System.out.println("getting event: ");
            System.out.println("oid: "+oid);
            System.out.println("eid: "+eid);

//            JSONObject input = new JSONObject(entity.getText());

            String entityString = entity.getText();
            System.out.println("received event payload: \n"+entityString);


            return entityString;
        }
        catch(Exception e){
            e.printStackTrace();
            return "{}";
        }
    }

}
