package sk.intersoft.vicinity.agent.service.resource;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class TestResource extends ServerResource {

    @Get("txt")
    public String doSomeGet() throws Exception {
        System.out.println("DO GET");
        System.out.println("REQ HEADERS: "+getRequest().getHeaders());
        return "AGENT to GET return: ["+getAttribute("x")+"]";
    }

}
