package sk.intersoft.vicinity.agent.discovery;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.adapter.AdapterEndpoint;
import sk.intersoft.vicinity.agent.adapter.AgentAdapter;
import sk.intersoft.vicinity.agent.gateway.GatewayAPIClient;
import sk.intersoft.vicinity.agent.service.config.AdapterConfig;
import sk.intersoft.vicinity.agent.service.config.AgentConfig;
import sk.intersoft.vicinity.agent.thing.ThingDescription;
import sk.intersoft.vicinity.agent.thing.ThingDescriptions;
import sk.intersoft.vicinity.agent.thing.ThingsDiff;
import sk.intersoft.vicinity.agent.thing.ThingsProcessor;
import sk.intersoft.vicinity.agent.thing.persistence.PersistedThing;
import sk.intersoft.vicinity.agent.utils.JSONUtil;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Discovery {
    final static Logger logger = LoggerFactory.getLogger(Discovery.class.getName());

    // PAYLOAD KEYS
    public static final String AGID_KEY = "agid";
    public static final String THING_DESCRIPTIONS_KEY = "thingDescriptions";
    public static final String OIDS_KEY = "oids";

    public static JSONObject DeletePayload(List<ThingDescription> things) {
        JSONObject payload = new JSONObject();
        JSONArray oids = new JSONArray();

        payload.put(AGID_KEY, AgentConfig.agentId);
        payload.put(OIDS_KEY, oids);


        for (ThingDescription thing : things) {
            oids.put(thing.oid);
        }


        return payload;

    }

    public static JSONObject CreateUpdatePayload(List<ThingDescription> things, boolean create) {
        JSONObject payload = new JSONObject();
        JSONArray thingsArray = new JSONArray();

        payload.put(AGID_KEY, AgentConfig.agentId);
        payload.put(THING_DESCRIPTIONS_KEY, thingsArray);


        for (ThingDescription thing : things) {
            JSONObject json = thing.toJSON();
            if(create){
                json.remove(ThingDescription.OID_KEY);
            }
            thingsArray.put(json);
        }


        return payload;

    }

    public static void processCreated(String response,
                                      ThingDescriptions toCreate,
                                      ThingDescriptions config) {
        try{
            logger.debug("PROCESSING CREATE DATA: ");

            JSONObject data = new JSONObject(response);
            JSONArray results = data.getJSONArray("message");

            Iterator<Object> i = results.iterator();
            while(i.hasNext()){
                JSONObject result = (JSONObject)i.next();

                String oid = JSONUtil.getString(ThingDescription.OID_KEY, result);
                if(oid == null) throw new Exception("Missing ["+ThingDescription.OID_KEY+"] in: "+result.toString());

                String infrastructureId = JSONUtil.getString(ThingDescription.AGENT_INFRASTRUCTURE_ID_KEY, result);
                if(infrastructureId == null) throw new Exception("Missing ["+ThingDescription.AGENT_INFRASTRUCTURE_ID_KEY +"] in: "+result.toString());

                String password = JSONUtil.getString(ThingDescription.PASSWORD_KEY, result);
                if(password == null) throw new Exception("Missing ["+ThingDescription.PASSWORD_KEY+"] in: "+result.toString());

                ThingDescription thing = toCreate.byInfrastructureID.get(infrastructureId);

                if(thing != null){
                    logger.debug("create base: "+thing.toSimpleString());

                    thing.oid = oid;
                    thing.password = password;

                    logger.debug("create updated: "+thing.toSimpleString());

                    if(PersistedThing.save(thing)){
                        logger.debug("thing persisted!");
                        logger.debug("adding to agent config");

                        config.add(thing);

                    }
                    else {
                        logger.debug("thing NOT persisted!");
                    }
                }
                else {
                    logger.debug("NOT creating thing for not found infra-id: "+infrastructureId);
                }

            }

        }
        catch(Exception e){
            logger.error("", e);
        }

    }

    public static ThingDescriptions addAdapterThings(AdapterConfig adapterConfig) throws Exception {
        String data = AgentAdapter.get(adapterConfig.endpoint + AdapterEndpoint.OBJECTS);
        ThingDescriptions things = ThingsProcessor.process(data, adapterConfig);
        return things;
    }

    public static ThingDescriptions getAllAdapterThings() throws Exception {
        logger.info("FETCHING THINGS FROM ALL ADAPTERS:");
        ThingDescriptions things = new ThingDescriptions();
        try{
            for (Map.Entry<String, AdapterConfig> entry : AgentConfig.adapters.entrySet()) {
                String id = entry.getKey();
                AdapterConfig config = entry.getValue();
                logger.info("fetching things from: ["+config.adapterId+"]");
                things.add(addAdapterThings(config));
            }
        }
        catch(Exception e){
            logger.error("", e);
            throw e;
        }
        return things;
    }

    public static void fire() throws Exception {
        logger.info("DISCOVERY : START");

        // 1. INITIALIZE PERSISTENCE
        PersistedThing.createTable();
        logger.info("Initialized persistence");
        PersistedThing.list();


        // 2. READ CONFIGURATION FROM NM
        String configData = GatewayAPIClient.get(GatewayAPIClient.CONFIGURATION);
        logger.info("Configuration response: "+configData);
        ThingDescriptions configuredThings = ThingsProcessor.process(configData, null);
        logger.info("Configured things: \n"+configuredThings.toString(0));


        // 3. READ ADAPTER OBJECTS
        ThingDescriptions adapterThings = getAllAdapterThings();
        logger.info("Adapter things: \n"+adapterThings.toString(0));

        // 4. MAKE DIFF
        ThingsDiff diff = ThingsDiff.fire(configuredThings, adapterThings);
        logger.debug(diff.toString(0));

        // 5. CREATE ACTUAL AGENT CONFIG: HANDLE DIFF

        ThingDescriptions config = new ThingDescriptions();

        // 5.0 HANDLE UNCHANGED
        // completely reset persistence .. delete everything and persist unchanged
        // we are sure here, that persistence list is still fresh
        logger.info("handling UNCHANGED: "+diff.unchanged.byOID.keySet().size());
        PersistedThing.clear();
        logger.info("persistence cleared");
        for (Map.Entry<String, ThingDescription> entry : diff.unchanged.byOID.entrySet()) {
            ThingDescription thing = entry.getValue();
            config.add(thing);
            PersistedThing.save(thing);
        }

        // 5.1 HANDLE DELETE
        // fire DELETE as first for case, when configuration was not assigned to infrastructure-id
        // that means, the thing is not persisted anymore from whatever reason (deleted database?)
        // must be deleted
        logger.info("handling DELETE: "+diff.delete.byOID.keySet().size());
        if(diff.delete.byOID.keySet().size() > 0){
            List<ThingDescription> things = diff.delete.thingsByOID();
            JSONObject payload = DeletePayload(things);
            System.out.println("DELETE PAYLOAD: "+payload.toString(2));
            String deleteData = GatewayAPIClient.post(GatewayAPIClient.DELETE, payload.toString());
            logger.info("DELETE response: " + deleteData);
        }
        else {
            logger.info("Nothing to CREATE");
        }

        // 5.2 HANDLE CREATE
        logger.info("handling CREATE: "+diff.create.byInfrastructureID.keySet().size());
        if(diff.create.byInfrastructureID.keySet().size() > 0){
            List<ThingDescription> things = diff.create.thingsByInfrastructureId();
            JSONObject payload = CreateUpdatePayload(things, true);
            System.out.println("CREATE PAYLOAD: "+payload.toString(2));
            String createData = GatewayAPIClient.post(GatewayAPIClient.CREATE, payload.toString());
            logger.info("CREATE response: " + createData);
            processCreated(createData, diff.create, config);
        }
        else {
            logger.info("Nothing to CREATE");
        }

        // 5.3 HANDLE UPDATE
        logger.info("handling UPDATE: "+diff.update.byOID.keySet().size());
        if(diff.update.byOID.keySet().size() > 0){
            List<ThingDescription> things = diff.update.thingsByInfrastructureId();
            JSONObject payload = CreateUpdatePayload(things, false);
            System.out.println("UPDATE PAYLOAD: "+payload.toString(2));
            String updateData = GatewayAPIClient.put(GatewayAPIClient.UPDATE, payload.toString());
            logger.info("UPDATE response: "+updateData);
            processCreated(updateData, diff.update, config);
        }
        else {
            logger.info("Nothing to UPDATE");
        }


        // 6. LOG IN ENABLED THINGS!
        // TODO

        AgentConfig.things = config;
        logger.info("ACTUALIZED AGENT CONFIG: ");
        logger.info(AgentConfig.things.toString(0));

        logger.info("ACTUAL THING PERSISTENCE: ");
        PersistedThing.list();

        logger.info("DISCOVERY : END");
    }
}
