package sk.intersoft.vicinity.agent.adapter;

import org.restlet.resource.ClientResource;

import java.util.logging.Logger;

public class Adapter {
    private final static Logger LOGGER = Logger.getLogger(Adapter.class.getName());

    String endpoint = "";

    public Adapter(String endpoint){
        this.endpoint = endpoint;
    }

    public void get(String path) throws Exception {
        ClientResource resource = new ClientResource(endpoint + path);

        LOGGER.info("CALL: "+endpoint + path);

        resource.get();
        LOGGER.info("> STATUS: "+resource.getStatus());
        LOGGER.info("> RESPONSE: "+resource.getResponse().getEntity().getText());

    }

}
