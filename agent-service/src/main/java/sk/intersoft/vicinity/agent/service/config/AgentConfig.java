package sk.intersoft.vicinity.agent.service.config;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.gateway.GatewayAPIClient;
import sk.intersoft.vicinity.agent.gateway.NeighbourhoodManager;
import sk.intersoft.vicinity.agent.service.config.thing.ThingDescriptions;
import sk.intersoft.vicinity.agent.service.config.thing.ThingProcessor;
import sk.intersoft.vicinity.agent.thing.ThingDescription;
import sk.intersoft.vicinity.agent.thing.ThingValidator;
import sk.intersoft.vicinity.agent.utils.Dump;

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

    public boolean configure(){
        try{
            logger.info("CONFIGURING AGENT: ["+agentId+"]");
            logger.info("LOG IN P2P");
            GatewayAPIClient.login(agentId, password);

            logger.info("ACQUIRE LAST CONFIGURATION");

            String configData = GatewayAPIClient.get(GatewayAPIClient.configurationEndpoint(agentId), agentId, password);
            logger.debug("Configuration raw response: \n" + configData);
            List<JSONObject> objects = ThingProcessor.processConfiguration(configData);

            List<String> unprocessed = new ArrayList<String>();

            logger.debug("parsing things ... ");
            for(JSONObject object : objects){
                logger.debug(object.toString());
                ThingValidator validator = new ThingValidator(true);
                ThingDescription thing = validator.create(object);
                if(thing != null){
                    logger.debug("processed thing: "+thing.oid);
                    configurationThings.add(thing);
                }
                else {
                    logger.debug("unprocessed! validator errors: \n"+validator.failureMessage().toString(2));
                    try{
                        String oid = object.getString(ThingDescription.OID_KEY);
                        logger.debug("OID marked for removal: ["+oid+"]");
                        unprocessed.add(oid);
                    }
                    catch(Exception e){
                        logger.debug("unable event to get OID from that thing!");
                    }
                }
            }

            if(unprocessed.size() > 0){
                logger.info("removing unparsed things: "+unprocessed);
                JSONObject payload = NeighbourhoodManager.deletePayload(unprocessed, agentId);
                logger.info("delete payload: \n"+payload);

                String deleteResponse = GatewayAPIClient.post(GatewayAPIClient.deleteEndpoint(agentId), payload.toString(), agentId, password);
                logger.info("delete raw response: \n"+deleteResponse);
            }


            logger.info("CONFIGURATION THINGS: \n" + configurationThings.toString(0));


            logger.info("DONE CONFIGURING AGENT: ["+agentId+"]");

            return true;
        }
        catch(Exception e) {
            logger.info("ERROR DURING AGENT CONFIGURATION : ["+agentId+"]");
            logger.error("", e);
        }

        return false;
    }


    public static AgentConfig create(String source) throws Exception {
        AgentConfig config = new AgentConfig();

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
            AdapterConfig ac = AdapterConfig.create(adapterConfig);
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
}