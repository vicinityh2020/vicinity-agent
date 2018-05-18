package sk.intersoft.vicinity.agent.service.config;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.clients.AdapterClient;
import sk.intersoft.vicinity.agent.clients.GatewayAPIClient;
import sk.intersoft.vicinity.agent.service.config.thing.ThingDescriptions;
import sk.intersoft.vicinity.agent.service.config.thing.ThingProcessor;
import sk.intersoft.vicinity.agent.thing.ThingDescription;
import sk.intersoft.vicinity.agent.thing.ThingValidator;
import sk.intersoft.vicinity.agent.utils.Dump;

import java.util.List;


public class AdapterConfig {
    final static Logger logger = LoggerFactory.getLogger(AdapterConfig.class.getName());

    public AgentConfig agent = null;

    public AdapterConfig(AgentConfig agent){
        this.agent = agent;
    }

    private static final String ADAPTER_ID_KEY = "adapter-id";
    private static final String ENDPOINT_KEY = "endpoint";
    private static final String ACTIVE_DISCOVERY_KEY = "active-discovery";


    public String adapterId = "";
    public String endpoint = null;
    public boolean activeDiscovery = false;

    ThingDescriptions things = new ThingDescriptions();

    public void login(){
        logger.debug("log-out all things");
        for(ThingDescription thing : things.byAdapterOID.values()){
            logger.debug("log-out: ["+thing.oid+"]");
            try{
                GatewayAPIClient.logout(thing.oid, thing.password);
            }
            catch(Exception e) {
                logger.error("log-out error for ["+thing.oid+"]!", e);
            }

        }
    }

    public void logout(){
        logger.debug("log-out all things");
        for(ThingDescription thing : things.byAdapterOID.values()){
            logger.debug("log-out: ["+thing.oid+"]");
            try{
                GatewayAPIClient.logout(thing.oid, thing.password);
            }
            catch(Exception e) {
                logger.error("log-out error for [" + thing.oid + "]!", e);
            }

        }
    }

    public boolean discover() {
        logger.debug("DISCOVERY FOR ADAPTER ["+adapterId+"] .. agent ["+agent.agentId+"]");
        logout();
        things = new ThingDescriptions();
        logger.debug("things cleared!");

        try{
            String data = AdapterClient.get(AdapterClient.objectsEndpoint(endpoint));
            List<JSONObject> objects = ThingProcessor.processAdapter(data, adapterId);
            logger.debug("parsing things ... ");
            for (JSONObject object : objects) {
                logger.debug(object.toString());
                ThingValidator validator = new ThingValidator(true);
                ThingDescription thing = validator.create(object);
                if (thing != null) {
                    logger.debug("processed thing: " + thing.oid);

                    // TRANSFORM OID -> INFRASTRUCTURE
                    thing.toInfrastructure();

                    things.add(thing);
                }
                else {
                    logger.debug("unprocessed thing! validator errors: \n" + validator.failureMessage().toString(2));
                    throw new Exception("unprocessed thing adapter: "+toSimpleString());
                }
            }


            logger.debug("DISCOVERED THINGS: "+things.byAdapterInfrastructureID.keySet().size());
            logger.debug("\n"+things.toString(0));

            return true;
        }
        catch(Exception e){
            logger.error("DISCOVERY FAILED FOR: "+toSimpleString(), e);
        }
        return false;
    }

    public static AdapterConfig create(JSONObject json, AgentConfig agentConfig) throws Exception {
        logger.debug("CREATING ADAPTER CONFIG FROM: \n"+json.toString(2));

        AdapterConfig config = new AdapterConfig(agentConfig);

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

    public String toSimpleString() {
        return "[ADAPTER: " + adapterId + " [agent-id: "+agent.agentId+"] [active-disco: "+activeDiscovery+"]]";
    }


}
