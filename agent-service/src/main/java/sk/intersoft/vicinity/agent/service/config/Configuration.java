package sk.intersoft.vicinity.agent.service.config;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.db.PersistedThing;
import sk.intersoft.vicinity.agent.db.Persistence;
import sk.intersoft.vicinity.agent.service.config.processor.ThingDescriptions;
import sk.intersoft.vicinity.agent.thing.ThingDescription;
import sk.intersoft.vicinity.agent.utils.Dump;
import sk.intersoft.vicinity.agent.utils.FileUtil;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Configuration {
    final static Logger logger = LoggerFactory.getLogger(Configuration.class.getName());

    private static final String GATEWAY_API_ENDPOINT_KEY = "gateway-api-endpoint";

    public static String gatewayAPIEndpoint = "";

    public static Map<String, AgentConfig> agents = new ConcurrentHashMap<String, AgentConfig>();
    public static Map<String, AdapterConfig> adapters = new ConcurrentHashMap<String, AdapterConfig>();
    public static Map<String, ThingDescriptions> things = new ConcurrentHashMap<String, ThingDescriptions>();
    public static Map<String, ThingDescription> thingsByOID = new ConcurrentHashMap<String, ThingDescription>();

    public static void create() throws Exception {

        String configFile = System.getProperty("service.config");

        logger.debug("CREATING CONFIG FROM FILE : "+configFile);
        JSONObject configSource = new JSONObject(FileUtil.file2string(new File(configFile)));
        gatewayAPIEndpoint = configSource.getString(GATEWAY_API_ENDPOINT_KEY);

    }

    public static File[] getAgentConfigFiles() throws Exception {
        String configFolder = System.getProperty("agents.config");
        File folder = new File(configFolder);
        File[] files = folder.listFiles();
        if(files.length == 0){
            throw new Exception("no agent config files found in ["+configFolder+"]!");
        }
        return files;
    }

    public static File findAgentConfigFile(String agentId){
        try{
            File[] files = getAgentConfigFiles();
            for(File f : files){
                AgentConfig ac = AgentConfig.create(f, false);
                if(ac.agentId.equals(agentId)){
                    return f;
                }
            }
        }
        catch(Exception e) {
            logger.error("", e);
        }
        return null;
    }

    public static void configureAgents() throws Exception {
        logger.info("ACQUIRING ACTUAL AGENT CONFIGURATIONS");

        logger.debug("CONFIGURING AGENTS FROM FOLDER: "+System.getProperty("agents.config"));
        File[] files = getAgentConfigFiles();
        for(File f : files){
            boolean success = AgentConfig.configure(f, true);

            logger.debug("CONFIGURATION AFTER PARTIAL AGENT: "+f.getAbsolutePath());
            logger.debug("\n"+toString(0));
            logger.debug("\n"+toStatusString(0));

            if(!success){
                logger.error("UNABLE TO CONFIGURE AGENT FROM: "+f.getAbsolutePath());
            }
        }


    }

    public static void removeUnusedAdapters()  {
        logger.info("REMOVING UNUSED ADAPTERS");
        try{
            Set<String> all = Persistence.getAdapterIds();
            logger.info("ALL KNOWN ADAPTERS: "+all.size());
            for(String aid : all){
                AdapterConfig c = Configuration.adapters.get(aid);
                boolean inConfig = (c != null);
                logger.info("> "+aid+" .. exists in config: "+inConfig);
                if(!inConfig){
                    logger.info("  > permanently removing missing adapter: "+aid);
                    AdapterConfig.remove(aid);
                }
            }

        }
        catch(Exception e){
            logger.error("", e);
        }

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

        dump.add("ALL EXPOSED THINGS BY ADAPTER: " + things.values().size(), indent);
        for (Map.Entry<String, ThingDescriptions> entry : things.entrySet()) {
            dump.add("for adapter: " + adapters.get(entry.getKey()).adapterId, (indent + 1));
            dump.add(entry.getValue().toStatusString(indent + 2));
        }

        dump.add("ALL EXPOSED THINGS BY OID: " + thingsByOID.values().size(), indent);
        for (Map.Entry<String, ThingDescription> entry : thingsByOID.entrySet()) {
            dump.add(entry.getValue().toSimpleString());
        }

        return dump.toString();
    }

    public static JSONObject toJSON() {
        JSONObject object = new JSONObject();
        JSONArray agentsArray = new JSONArray();
        JSONArray adaptersArray = new JSONArray();
        JSONArray thingsArray = new JSONArray();
        JSONArray thingsOIDArray = new JSONArray();

        object.put("agents", agentsArray);
        object.put("adapters", adaptersArray);
        object.put("things-by-adapter", thingsArray);
        object.put("things-by-oid", thingsOIDArray);

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

        for (Map.Entry<String, ThingDescription> entry : thingsByOID.entrySet()) {
            thingsOIDArray.put(entry.getValue().toStatusJSON());
        }

        return object;
    }

}
