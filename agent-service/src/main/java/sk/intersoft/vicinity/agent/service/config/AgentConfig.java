package sk.intersoft.vicinity.agent.service.config;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.clients.ClientResponse;
import sk.intersoft.vicinity.agent.clients.GatewayAPIClient;
import sk.intersoft.vicinity.agent.clients.NeighbourhoodManager;
import sk.intersoft.vicinity.agent.db.PersistedThing;
import sk.intersoft.vicinity.agent.db.Persistence;
import sk.intersoft.vicinity.agent.service.config.processor.ThingDescriptions;
import sk.intersoft.vicinity.agent.service.config.processor.ThingProcessor;
import sk.intersoft.vicinity.agent.thing.ThingDescription;
import sk.intersoft.vicinity.agent.thing.ThingValidator;
import sk.intersoft.vicinity.agent.utils.Dump;
import sk.intersoft.vicinity.agent.utils.FileUtil;
import sk.intersoft.vicinity.agent.utils.JSONUtil;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

public class AgentConfig {
    final static Logger logger = LoggerFactory.getLogger(AgentConfig.class.getName());

    private static final String CREDENTIALS_KEY = "credentials";
    private static final String AGENT_ID_KEY = "agent-id";
    private static final String PASSWORD_KEY = "password";

    private static final String ADAPTERS_KEY = "adapters";

    public String agentId = "";
    public String password = "";

    public Map<String, AdapterConfig> adapters = new ConcurrentHashMap<String, AdapterConfig>();

    public ThingDescriptions configurationThings = new ThingDescriptions();

    private boolean configurationRunning = false;

    private void start() {
        configurationRunning = true;
    }
    private void stop() {
        configurationRunning = false;
    }
    public boolean isRunning() {
        return configurationRunning;
    }

    public void login() {
        try{
            logger.info("LOG-IN AGENT ["+agentId+"]");
            GatewayAPIClient.login(agentId, password);
        }
        catch(Exception e){
            logger.error("UNABLE TO LOG-IN AGENT ["+agentId+"]", e);
        }
    }
    public void logout() {
        try{
            logger.info("LOG-OUT AGENT ["+agentId+"]");
            GatewayAPIClient.logout(agentId, password);
        }
        catch(Exception e){
            logger.error("UNABLE TO LOG-OUT AGENT ["+agentId+"]", e);
        }
    }

    public void clearMappings(){
        logger.info("CLEANUP FOR AGENT: ["+agentId+"]");

        for (Map.Entry<String, AdapterConfig> entry : adapters.entrySet()) {
            String id = entry.getKey();
            AdapterConfig ac = entry.getValue();
            ac.clearMappings();
        }

        if(Configuration.agents.get(agentId) != null){
            Configuration.agents.remove(agentId);
        }
        for (Map.Entry<String, AdapterConfig> entry : adapters.entrySet()) {
            AdapterConfig ac = entry.getValue();
            Configuration.adapters.remove(ac.adapterId);
        }
        logger.info("AGENT ["+agentId+"] removed from configuration");
        logout();
        logger.info("CLEANUP FOR AGENT ["+agentId+"]: DONE");
    }

    private void updateMappings() {
        logger.info("UPDATING CONFIGURATION MAPPINGS FOR AGENT: [" + agentId + "]");
        Configuration.agents.put(agentId, this);
        for (Map.Entry<String, AdapterConfig> entry : adapters.entrySet()) {
            AdapterConfig ac = entry.getValue();
            Configuration.adapters.put(ac.adapterId, ac);
        }
    }

    public ThingDescriptions configurationThingsForAdapter(String adapterId){
        ThingDescriptions ts = new ThingDescriptions();
        Set<ThingDescription> adapterThings = configurationThings.byAdapterID.get(adapterId);
        if(adapterThings != null){
            for(ThingDescription t : adapterThings){
                try{
                    ts.add(t);
                }
                catch(Exception e){
                    logger.error("", e);
                }
            }
        }

        return ts;
    }


    public boolean configure() {
        if (isRunning()) {
            logger.info("NOT CONFIGURING AGENT: [" + agentId + "].. another process is actually using this configuration!");
            return false;
        }

        start();
        boolean success = configureAgent();
        stop();

        logger.info("AGENT [" + agentId + "] CONFIGURED:\n"+toStatusString(0));

        return success;
    }

    public static boolean configure(File configFile, boolean checkDuplicates)  {
        logger.info("CONFIGURING AGENT CONFIG FROM: " + configFile.getAbsolutePath());

        try{
            AgentConfig config = create(configFile, checkDuplicates);
            if(config != null){


                AgentConfig existing = Configuration.agents.get(config.agentId);
                if(existing != null) {
                    existing.clearMappings();
                }
                else{
                    logger.info("AGENT IS NEW .. NO CLEANUP");
                }

                return config.configure();
            }

        }
        catch(Exception e){
            logger.error("", e);
        }

        return false;
    }

    public void updateLastConfiguration() throws Exception {
        logger.info("ACQUIRE LAST CONFIGURATION");
        configurationThings = new ThingDescriptions();

        ClientResponse configData = GatewayAPIClient.get(GatewayAPIClient.configurationEndpoint(agentId), agentId, password, null);
        logger.debug("Configuration raw response: \n" + configData);
        if(!configData.isOK()){
            throw new Exception("wrong response .. fail!");
        }
        List<JSONObject> objects = ThingProcessor.processConfiguration(configData.data);

        List<JSONObject> unprocessed = new ArrayList<JSONObject>();

        logger.debug("parsing things ... ");
        for (JSONObject object : objects) {
            logger.debug(object.toString());
            ThingValidator validator = new ThingValidator(true);
            ThingDescription thing = validator.create(object);
            if (thing != null) {
                logger.debug("processed thing: " + thing.oid);
                try {
                    PersistedThing persisted = Persistence.getByOID(thing.oid);
                    if(persisted != null){
                        thing.updatePersistence(persisted);
                        configurationThings.add(thing);
                    }
                    else throw new Exception("credentials for thing ["+ thing.oid +"] were lost .. must remove it!");
                } catch (Exception e) {
                    logger.error("", e);
                    logger.debug("unprocessed thing [" + thing.oid + "]! remove!");
                    unprocessed.add(object);
                }
            } else {
                logger.debug("unprocessed thing! validator errors: \n" + validator.failureMessage().toString(2));
                unprocessed.add(object);
            }
        }

        if (unprocessed.size() > 0) {
            logger.info("removing unparsed things: " + unprocessed.size());

            List<String> unprocessedOIDs = new ArrayList<String>();
            logger.info("extracting OIDs ...");
            for (JSONObject o : unprocessed) {
                try {
                    String oid = JSONUtil.getString(ThingDescription.OID_KEY, o);
                    if (!oid.equals("")) {
                        logger.debug("OID marked for removal: [" + oid + "]");
                        unprocessedOIDs.add(oid);
                    }
                } catch (Exception e) {
                    logger.debug("unable event to get OID from thing! " + o.toString());
                }
            }


            logger.info("removing unparsed things: " + unprocessedOIDs);
            NeighbourhoodManager.delete(NeighbourhoodManager.deletePayload(unprocessedOIDs, agentId), this);

        }


        logger.info("CONFIGURATION THINGS: \n" + configurationThings.toString(0));
//        logger.info("FULL CONFIGURATION THINGS: \n" + configurationThings.toFullString(0));

    }

    private void discoverAdapters()  {
        logger.info("DISCOVERING ADAPTERS FOR AGENT: "+toSimpleString());
        for (Map.Entry<String, AdapterConfig> entry : adapters.entrySet()) {
            AdapterConfig adapter = entry.getValue();
            logger.info("discovery for adapter: ["+adapter.adapterId+"]");
            boolean success = adapter.discover();
            if(!success) {
                logger.error("DISCOVERY FAILED FOR ADAPTER ["+adapter.adapterId+"]!");
            }

        }
    }



    public void updatePersistence() throws Exception {
        Persistence.clearAgent(agentId);
        logger.debug("persistence cleared for agent: "+toSimpleString());

        Persistence.saveAgent(agentId, password);
        logger.debug("persistence updated for adapter "+toSimpleString());
        Persistence.list();

    }

    public void removeUnusedAdapters()  {
        logger.info("REMOVING UNUSED ADAPTERS FOR AGENT ["+agentId+"]");
        try{
            Set<String> all = Persistence.getAdapterIds(agentId);
            logger.info("ALL KNOWN ADAPTERS FOR AGENT ["+agentId+"]: "+all.size());
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


    private boolean configureAgent(){
        try{
            logger.info("CONFIGURING AGENT: ["+agentId+"] ");
            login();

            updateLastConfiguration();
            discoverAdapters();
            updatePersistence();
            updateMappings();
            removeUnusedAdapters();
            logger.info("DONE CONFIGURING AGENT: ["+agentId+"]");

            return true;
        }
        catch(Exception e) {
            logger.info("ERROR DURING AGENT CONFIGURATION : ["+agentId+"]");
            logger.error("", e);
        }
        return false;
    }



    public static AgentConfig create(File configFile, boolean checkDuplicates)  {
        AgentConfig config = new AgentConfig();
        logger.info("READING AGENT CONFIG FROM: " + configFile.getAbsolutePath());

        try{
            String source = FileUtil.file2string(configFile);

            JSONObject json = new JSONObject(source);
            JSONObject credentials = JSONUtil.getObject(CREDENTIALS_KEY, json);
            if(credentials == null){
                throw new Exception("Missing ["+CREDENTIALS_KEY+"] in agent config!");
            }

            config.agentId = JSONUtil.getString(AGENT_ID_KEY, credentials);
            if(config.agentId == null){
                throw new Exception("Missing ["+AGENT_ID_KEY+"] in agent config!");
            }

            if(checkDuplicates && Configuration.agents.get(config.agentId) != null){
                throw new Exception("Duplicate ["+AGENT_ID_KEY+"] .. agent already exists!");
            }

            config.password = JSONUtil.getString(PASSWORD_KEY, credentials);
            if(config.password == null){
                throw new Exception("Missing ["+PASSWORD_KEY+"] in agent config!");
            }

            if(json.has(ADAPTERS_KEY)){
                List<JSONObject> adaptersArray = JSONUtil.getObjectArray(ADAPTERS_KEY, json);
                if(adaptersArray == null){
                    throw new Exception("Missing ["+ADAPTERS_KEY+"] in agent config!");
                }
                if (adaptersArray.size() == 0) {
                    throw new Exception("no adapters in agent config [" + config.agentId + "]");
                }
                for(JSONObject adapterConfig: adaptersArray) {
                    AdapterConfig ac = AdapterConfig.create(adapterConfig, config);

                    if(config.adapters.get(ac.adapterId) != null){
                        throw new Exception("duplicate adapter-id [" + ac.adapterId + "] in agent [" + config.agentId + "]!");
                    }

                    AdapterConfig existing = Configuration.adapters.get(ac.adapterId);
                    if(existing != null && !existing.agent.agentId.equals(config.agentId)){
                        throw new Exception("duplicate adapter-id [" + ac.adapterId + "] in agent [" + config.agentId + "] .. adapter id is already used in another agent ["+existing.agent.agentId+"]!");
                    }
                    config.adapters.put(ac.adapterId, ac);

                    logger.debug("ADAPTER CONFIG ADDED TO AGENT: \n"+config.toStatusString(0));
                    logger.debug("WHOLE CONFIG: \n"+Configuration.toStatusString(0));

                }

            }

            return config;

        }
        catch(Exception e){
            logger.error("UNABLE TO READ AGENT CONFIG FROM: "+configFile.getAbsolutePath(), e);
        }
        return null;
    }

    public String toString(int indent) {
        Dump dump = new Dump();

        dump.add("AGENT CONFIG: ", indent);

        dump.add("Credentials: ", (indent + 1));
        dump.add("agent-id: [" + agentId + "]", (indent + 2));
        dump.add("password: [" + password + "]", (indent + 2));
        dump.add("Adapters: " + adapters.keySet().size(), (indent + 1));
        for (Map.Entry<String, AdapterConfig> entry : adapters.entrySet()) {
            String id = entry.getKey();
            AdapterConfig ac = entry.getValue();
            dump.add("adapter-id: " + id, (indent + 3));
            dump.add(ac.toString(indent + 4));
        }

        return dump.toString();
    }

    public String toSimpleString() {
        return "[AGENT: " + agentId + "]";
    }

    public String toStatusString(int indent) {
        Dump dump = new Dump();

        dump.add("AGENT CONFIG: ["+agentId+"]", indent);
        dump.add("ACTIVE ADAPTERS: "+adapters.values().size(), (indent + 1));

        for (Map.Entry<String, AdapterConfig> entry : adapters.entrySet()) {
            dump.add(entry.getValue().toStatusString(indent + 1));
        }

        return dump.toString();
    }

    public JSONObject toJSON() {
        JSONObject object = new JSONObject();
        JSONArray adaptersArray = new JSONArray();

        object.put("agent-id", agentId);
        object.put("adapters", adaptersArray);


        for (Map.Entry<String, AdapterConfig> entry : adapters.entrySet()) {
            adaptersArray.put(entry.getValue().toJSON());
        }

        return object;
    }

}