package sk.intersoft.vicinity.agent.service.config;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.thing.ThingDescriptions;
import sk.intersoft.vicinity.agent.utils.Dump;

import java.io.File;
import java.util.*;

public class AgentConfig {
    final static Logger logger = LoggerFactory.getLogger(AgentConfig.class.getName());

    private static final String CREDENTIALS_KEY = "credentials";
    private static final String AGENT_ID_KEY = "agent-id";
    private static final String PASSWORD_KEY = "password";

    private static final String GATEWAY_API_ENDPOINT_KEY = "gateway-api-endpoint";
    private static final String ADAPTERS_KEY = "adapters";


    public static String agentId = "";
    public static String password = "";

    public static String gatewayAPIEndpoint = "";
    public static Map<String, AdapterConfig> adapters = new HashMap<String, AdapterConfig>();
    public static Map<String, String> x = new HashMap<String, String>();

    public static ThingDescriptions things = new ThingDescriptions();


    public static String file2string(String path) {
        try{
            return new Scanner(new File(path)).useDelimiter("\\Z").next();
        }
        catch(Exception e){
            logger.error("", e);
            return null;
        }
    }

    public static void create(String configPath) throws Exception {
        JSONObject config = new JSONObject(file2string(configPath));
        logger.info("CREATING CONFIG FILE FROM: \n"+config.toString(2));
        JSONObject credentials = config.getJSONObject(CREDENTIALS_KEY);
        agentId = credentials.getString(AGENT_ID_KEY);
        password = credentials.getString(PASSWORD_KEY);

        gatewayAPIEndpoint = config.getString(GATEWAY_API_ENDPOINT_KEY);

        JSONArray adaptersArray = config.getJSONArray(ADAPTERS_KEY);
        Iterator<Object> i = adaptersArray.iterator();
        while(i.hasNext()){
            JSONObject adapterConfig = (JSONObject)i.next();
            AdapterConfig ac = AdapterConfig.create(adapterConfig);
            adapters.put(ac.adapterId, ac);
        }
    }

    public static String asString(int indent) {
        Dump dump = new Dump();

        dump.add("AGENT CONFIG CREATED: ", indent);

        dump.add("Credentials: ", (indent + 1));
        dump.add("agent-id: [" + agentId + "]", (indent + 2));
        dump.add("password: [" + password + "]", (indent + 2));
        dump.add("GatewayAPI Endpoint: " + gatewayAPIEndpoint, (indent + 1));
        dump.add("Adapters: ", (indent + 1));
        for (Map.Entry<String, AdapterConfig> entry : adapters.entrySet()) {
            String id = entry.getKey();
            AdapterConfig ac = entry.getValue();
            dump.add("mapped key: "+id, (indent + 2));
            dump.add(ac.asString(indent + 3));
        }

        return dump.toString();
    }
    public static String asString() {
        return asString(0);
    }

}
