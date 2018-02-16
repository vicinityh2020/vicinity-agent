package sk.intersoft.vicinity.agent.discovery;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.adapter.AdapterEndpoint;
import sk.intersoft.vicinity.agent.adapter.AgentAdapter;
import sk.intersoft.vicinity.agent.gateway.GatewayAPIClient;
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

        payload.put(AGID_KEY, AgentConfig.login);
        payload.put(OIDS_KEY, oids);


        for (ThingDescription thing : things) {
            oids.put(thing.oid);
        }


        return payload;

    }

    public static JSONObject CreateUpdatePayload(List<ThingDescription> things, boolean create) {
        JSONObject payload = new JSONObject();
        JSONArray thingsArray = new JSONArray();

        payload.put(AGID_KEY, AgentConfig.login);
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

                String infrastructureId = JSONUtil.getString(ThingDescription.INFRASTRUCTURE_ID_KEY, result);
                if(infrastructureId == null) throw new Exception("Missing ["+ThingDescription.INFRASTRUCTURE_ID_KEY+"] in: "+result.toString());

                String password = JSONUtil.getString(ThingDescription.PASSWORD_KEY, result);
                if(password == null) throw new Exception("Missing ["+ThingDescription.PASSWORD_KEY+"] in: "+result.toString());

                ThingDescription thing = toCreate.byInfrastructureID.get(infrastructureId);

                if(thing != null){
                    logger.debug("creating: "+thing.toSimpleString());
                    PersistedThing persisted = new PersistedThing(oid, infrastructureId, password);
                    if(persisted.persist()){
                        logger.debug("thing persisted!");
                        logger.debug("assigning infra-id and adding to agent config");

                        thing.oid = oid;

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

    public static void fire() throws Exception {
        logger.info("DISCOVERY : START");

        // 1. INITIALIZE PERSISTENCE
        PersistedThing.createTable();
        logger.info("Initialized persistence");
        PersistedThing.list();


        // 2. READ CONFIGURATION FROM NM
        String configData = GatewayAPIClient.getInstance().get(GatewayAPIClient.CONFIGURATION);
        logger.info("Configuration response: "+configData);
        ThingDescriptions configuredThings = ThingsProcessor.process(configData, true);
        logger.info("Configured things: \n"+configuredThings.toString(0));


        // 3. READ ADAPTER OBJECTS
        String adaterData = AgentAdapter.getInstance().get(AdapterEndpoint.OBJECTS);
        logger.info("Adapter objects response: "+adaterData);

        ThingDescriptions adapterThings = ThingsProcessor.process(adaterData, false);
        logger.info("Adapter things: \n"+adapterThings.toString(0));

        // 4. MAKE DIFF
        ThingsDiff diff = ThingsDiff.fire(configuredThings, adapterThings);
        logger.debug(diff.toString(0));

        // 5. CREATE ACTUAL AGENT CONFIG: HANDLE DIFF

        ThingDescriptions config = new ThingDescriptions();

        // 5.1 HANDLE DELETE
        // fire DELETE as first for case, when configuration was not assigned to infrastructure-id
        // that means, the thing is not persisted anymore from whatever reason (deleted database?)
        // must be deleted
        logger.info("handling DELETE: "+diff.delete.byOID.keySet().size());
        if(diff.delete.byOID.keySet().size() > 0){
            List<ThingDescription> things = diff.delete.thingsByOID();
            JSONObject payload = DeletePayload(things);
            System.out.println("DELETE PAYLOAD: "+payload.toString(2));
            String deleteData = GatewayAPIClient.getInstance().post(GatewayAPIClient.DELETE, payload.toString());
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
            String createData = GatewayAPIClient.getInstance().post(GatewayAPIClient.CREATE, payload.toString());
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
            String updateData = GatewayAPIClient.getInstance().put(GatewayAPIClient.UPDATE, payload.toString());
            logger.info("UPDATE response: "+updateData);
            processCreated(updateData, diff.update, config);
        }
        else {
            logger.info("Nothing to UPDATE");
        }

        // 5.4 HANDLE UNCHANGED
        logger.info("adding UNCHANGED: ");
        for (Map.Entry<String, ThingDescription> entry : diff.unchanged.byOID.entrySet()) {
            ThingDescription thing = entry.getValue();
            config.add(thing);
        }

        // 6. LOG IN ENABLED THINGS!
        // TODO

        logger.info("ACTUALIZED AGENT CONFIG: ");
        logger.info(config.toString(0));

        logger.info("ACTUAL THING PERSISTENCE: ");
        PersistedThing.list();

        logger.info("DISCOVERY : END");
    }
}
