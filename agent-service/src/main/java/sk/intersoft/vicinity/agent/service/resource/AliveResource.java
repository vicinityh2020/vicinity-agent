package sk.intersoft.vicinity.agent.service.resource;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AliveResource extends ServerResource {
    final static Logger logger = LoggerFactory.getLogger(AliveResource.class.getName());

    @Get("txt")
    public String doSomeGet() throws Exception {
        logger.info("DO GET");
        return "AGENT IS ALIVE";
    }

}
