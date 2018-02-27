package sk.intersoft.vicinity.agent.service.config;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.thing.ThingDescriptions;
import sk.intersoft.vicinity.agent.utils.Dump;

import java.io.File;
import java.util.Scanner;

public class AdapterConfig {
    final static Logger logger = LoggerFactory.getLogger(AdapterConfig.class.getName());

    private static final String ADAPTER_ID_KEY = "adapter-id";
    private static final String ADAPTER_ENDPOINT_KEY = "endpoint";


    public String adapterId = "";
    public String endpoint = "";

    public AdapterConfig(String adapterId, String endpoint){
        this.adapterId = adapterId;
        this.endpoint = endpoint;
    }

    public static AdapterConfig create(JSONObject config) throws Exception {
        logger.info("CREATING ADAPTER CONFIG: "+config.toString());
        String adapterId = config.getString(ADAPTER_ID_KEY);
        String endpoint = config.getString(ADAPTER_ENDPOINT_KEY);

        return new AdapterConfig(adapterId, endpoint);
    }

    public String asString(int indent) {
        Dump dump = new Dump();

        dump.add("ADAPTER CONFIG CREATED: ", indent);

        dump.add("adapter-id: [" + adapterId + "]", (indent + 1));
        dump.add("endpoint: " + endpoint, (indent + 1));

        return dump.toString();
    }

    public String toString() {
        return "["+adapterId+" : "+endpoint+"]";
    }

}
