package sk.intersoft.vicinity.agent.service.config;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.clients.GatewayAPIClient;
import sk.intersoft.vicinity.agent.clients.NeighbourhoodManager;
import sk.intersoft.vicinity.agent.db.PersistedThing;
import sk.intersoft.vicinity.agent.service.config.processor.ThingDescriptions;
import sk.intersoft.vicinity.agent.service.config.processor.ThingProcessor;
import sk.intersoft.vicinity.agent.thing.ThingDescription;
import sk.intersoft.vicinity.agent.thing.ThingValidator;
import sk.intersoft.vicinity.agent.utils.Dump;
import sk.intersoft.vicinity.agent.utils.FileUtil;
import sk.intersoft.vicinity.agent.utils.JSONUtil;

import java.io.File;
import java.util.*;

public class AgentConfig {
    final static Logger logger = LoggerFactory.getLogger(AgentConfig.class.getName());

    private static final String CREDENTIALS_KEY = "credentials";
    private static final String AGENT_ID_KEY = "agent-id";
    private static final String PASSWORD_KEY = "password";

    private static final String ADAPTERS_KEY = "adapters";

    public String agentId = "";
    public String password = "";

    public Map<String, AdapterConfig> adapters = new HashMap<String, AdapterConfig>();

    public ThingDescriptions configurationThings = new ThingDescriptions();

    private boolean configurationRunning = false;

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
            logger.info("NOT CONFIGURING AGENT: [" + agentId + "] .. another process is actually using this configuration!");
            return false;
        }

        start();
        boolean success = configureAgent();
        stop();

        return success;
    }

    private void updateLastConfiguration() throws Exception {
        logger.info("ACQUIRE LAST CONFIGURATION");

        String configData = GatewayAPIClient.get(GatewayAPIClient.configurationEndpoint(agentId), agentId, password);
        logger.debug("Configuration raw response: \n" + configData);
        List<JSONObject> objects = ThingProcessor.processConfiguration(configData);

        List<JSONObject> unprocessed = new ArrayList<JSONObject>();

        logger.debug("parsing things ... ");
        for (JSONObject object : objects) {
            logger.debug(object.toString());
            ThingValidator validator = new ThingValidator(true);
            ThingDescription thing = validator.create(object);
            if (thing != null) {
                logger.debug("processed thing: " + thing.oid);
                try {
                    PersistedThing persisted = PersistedThing.getByOID(thing.oid);
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

    }



    private boolean configureAgent(){
        try{
            logger.info("CONFIGURING AGENT: ["+agentId+"]");
            logger.info("LOG IN P2P");
            GatewayAPIClient.login(agentId, password);

            updateLastConfiguration();

            logger.info("DONE CONFIGURING AGENT: ["+agentId+"]");

            return true;
        }
        catch(Exception e) {
            logger.info("ERROR DURING AGENT CONFIGURATION : ["+agentId+"]");
            logger.error("", e);
        }
        return false;
    }

    private void start() {
        configurationRunning = true;
    }
    private void stop() {
        configurationRunning = false;
    }
    private boolean isRunning() {
        return configurationRunning;
    }

    public static AgentConfig create(File configFile) throws Exception {
        AgentConfig config = new AgentConfig();

        String source = FileUtil.file2string(configFile);

        JSONObject json = new JSONObject(source);
        logger.info("CREATING AGENT CONFIG FROM: " + json.toString(2));
        JSONObject credentials = json.getJSONObject(CREDENTIALS_KEY);
        config.agentId = credentials.getString(AGENT_ID_KEY);
        config.password = credentials.getString(PASSWORD_KEY);


        JSONArray adaptersArray = json.getJSONArray(ADAPTERS_KEY);
        if (adaptersArray.length() == 0) {
            throw new Exception("no adapters in agent config [" + config.agentId + "]");
        }

        Iterator<Object> i = adaptersArray.iterator();
        while (i.hasNext()) {
            JSONObject adapterConfig = (JSONObject) i.next();
            AdapterConfig ac = AdapterConfig.create(adapterConfig, config);
            if (config.adapters.get(ac.adapterId) != null) {
                throw new Exception("duplicate adapter-id [" + ac.adapterId + "] in agent [" + config.agentId + "]!");
            }
            config.adapters.put(ac.adapterId, ac);
        }
        return config;
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
}