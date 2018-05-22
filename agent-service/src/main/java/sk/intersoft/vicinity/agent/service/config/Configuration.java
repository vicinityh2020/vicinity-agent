package sk.intersoft.vicinity.agent.service.config;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.service.config.processor.ThingDescriptions;
import sk.intersoft.vicinity.agent.thing.ThingDescription;
import sk.intersoft.vicinity.agent.utils.Dump;
import sk.intersoft.vicinity.agent.utils.FileUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Configuration {
    final static Logger logger = LoggerFactory.getLogger(Configuration.class.getName());

    private static final String GATEWAY_API_ENDPOINT_KEY = "gateway-api-endpoint";

    public static String gatewayAPIEndpoint = "";

    public static Map<String, AgentConfig> agents = new HashMap<String, AgentConfig>();
    public static Map<String, AdapterConfig> adapters = new HashMap<String, AdapterConfig>();
    public static Map<String, ThingDescriptions> things = new HashMap<String, ThingDescriptions>();

    public static void create() throws Exception {

        String configFile = System.getProperty("service.config");

        logger.debug("CREATING CONFIG FROM FILE : "+configFile);
        JSONObject configSource = new JSONObject(FileUtil.file2string(new File(configFile)));
        gatewayAPIEndpoint = configSource.getString(GATEWAY_API_ENDPOINT_KEY);

    }

    public static String toString(int indent) {
        Dump dump = new Dump();

        dump.add("AGENT-SERVICE CONFIGURATION STATUS: ", indent);

        dump.add("GatewayAPI Endpoint: " + gatewayAPIEndpoint, indent);

        return dump.toString();
    }

    public static String toStatusString(int indent) {
        Dump dump = new Dump();

        dump.add("AGENT-SERVICE CONFIGURATION SUMMARY (all maps): ", indent);

        dump.add("EXPOSED AGENTS: " + agents.values().size(), indent);
        for (Map.Entry<String, AgentConfig> entry : agents.entrySet()) {
            dump.add(entry.getValue().toStatusString(indent + 1));
        }

        dump.add("ALL EXPOSED ADAPTERS: " + adapters.values().size(), indent);
        for (Map.Entry<String, AdapterConfig> entry : adapters.entrySet()) {
            dump.add(entry.getValue().toStatusString(indent + 1));
        }

        dump.add("ALL EXPOSED THINGS: " + things.values().size(), indent);
        for (Map.Entry<String, ThingDescriptions> entry : things.entrySet()) {
            dump.add("for adapter: " + adapters.get(entry.getKey()).adapterId, (indent + 1));
            dump.add(entry.getValue().toStatusString(indent + 2));
        }

        return dump.toString();
    }

    public static JSONObject toJSON() {
        JSONObject object = new JSONObject();
        JSONArray agentsArray = new JSONArray();
        JSONArray adaptersArray = new JSONArray();
        JSONArray thingsArray = new JSONArray();

        object.put("agents", agentsArray);
        object.put("adapters", adaptersArray);
        object.put("things", thingsArray);

        for (Map.Entry<String, AgentConfig> entry : agents.entrySet()) {
            agentsArray.put(entry.getValue().toJSON());
        }


        for (Map.Entry<String, AdapterConfig> entry : adapters.entrySet()) {
            adaptersArray.put(entry.getValue().toJSON());
        }

        for (Map.Entry<String, ThingDescriptions> entry : things.entrySet()) {
            JSONObject at = new JSONObject();
            at.put("for-adapter", adapters.get(entry.getKey()).adapterId);
            at.put("adapter-things", entry.getValue().toStatusJSON());
            thingsArray.put(at);
        }

        return object;
    }

}
