package sk.intersoft.vicinity.adapter.testing.service.resource;

import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

public class SleepResource extends ServerResource {

    @Post()
    public String sleep(Representation entity) throws Exception {
        System.out.println("TEST POST SLEEP");
        String rawPayload = entity.getText();

        System.out.println("PAYLOAD: " + rawPayload);

        try{
            Thread.sleep(80000);

            System.out.println("done .. returning value");

            return "sleep result";

        }
        catch(Exception e){
            System.out.println("EXCEPTION HERE"+e);
            getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);


            return "error";

        }
    }

}
