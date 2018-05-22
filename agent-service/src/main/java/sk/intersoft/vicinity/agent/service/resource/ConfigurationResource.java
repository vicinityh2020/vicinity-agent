package sk.intersoft.vicinity.agent.service.resource;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.service.config.Configuration;

public class ConfigurationResource extends ServerResource {
    final static Logger logger = LoggerFactory.getLogger(ConfigurationResource.class.getName());

    @Get("json")
    public String getConfig() throws Exception {
        logger.info("GET CONFIG");
        return Configuration.toJSON().toString(2);
    }

}
