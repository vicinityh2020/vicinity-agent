package sk.intersoft.vicinity.agent.service.config;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.utils.Dump;


public class AdapterConfig {
    final static Logger logger = LoggerFactory.getLogger(AdapterConfig.class.getName());

    private static final String ADAPTER_ID_KEY = "adapter-id";
    private static final String ENDPOINT_KEY = "endpoint";
    private static final String ACTIVE_DISCOVERY_KEY = "active-discovery";


    public String adapterId = "";
    public String endpoint = null;
    public boolean activeDiscovery = false;


    public static AdapterConfig create(JSONObject json) throws Exception {
        logger.debug("CREATING ADAPTER CONFIG FROM: \n"+json.toString(2));

        AdapterConfig config = new AdapterConfig();

        config.adapterId = json.getString(ADAPTER_ID_KEY);
        if(json.has(ENDPOINT_KEY)){
            config.endpoint = json.getString(ENDPOINT_KEY);

            if(json.has(ACTIVE_DISCOVERY_KEY)){
                config.activeDiscovery = json.getBoolean(ACTIVE_DISCOVERY_KEY);
            }
        }
        else{
            logger.debug("no endpoint! setting active discovery to TRUE");
            config.activeDiscovery = true;
        }


        return config;
    }

    public String toString(int indent) {
        Dump dump = new Dump();

        dump.add("ADAPTER CONFIG: ", indent);

        dump.add("adapter-id: [" + adapterId + "]", (indent + 1));
        dump.add("endpoint: " + endpoint, (indent + 1));
        dump.add("active disco: " + activeDiscovery, (indent + 1));

        return dump.toString();
    }

    public String toString() {
        return "["+adapterId+" : "+endpoint+" : "+activeDiscovery+"]";
    }

}
