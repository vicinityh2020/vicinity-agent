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
    public static final String ADID_KEY = "adid";
    public static final String THING_DESCRIPTIONS_KEY = "thingDescriptions";

    public static JSONObject CreateUpdatePayload(ThingDescriptions things, boolean create) {
        JSONObject payload = new JSONObject();
        JSONArray thingsArray = new JSONArray();

        payload.put(ADID_KEY, AgentConfig.login);
        payload.put(THING_DESCRIPTIONS_KEY, thingsArray);


        for (Map.Entry<String, ThingDescription> entry : things.byInfrastructureID.entrySet()) {
            ThingDescription thing = entry.getValue();
            JSONObject json = thing.toJSON();
            if(create){
                json.put(ThingDescription.INFRASTRUCTURE_ID_KEY, json.getString(ThingDescription.OID_KEY));
                json.remove(ThingDescription.OID_KEY);
            }
            thingsArray.put(json);
        }


        return payload;

    }

    public static void processCreate(String response,
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

                        JSONObject json = thing.toJSON();
                        json.put(ThingDescription.OID_KEY, oid);

                        thing.jsonString = json.toString();
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
//        String deleteData = GatewayAPIClient.getInstance().get(GatewayAPIClient.DELETE);
//        logger.info("DELETE response: "+deleteData);

        // 5.2 HANDLE CREATE
        logger.info("handling CREATE: ");
//        System.out.println(CreateUpdatePayload(diff.create, true).toString(2));
//        String createData = GatewayAPIClient.getInstance().post(GatewayAPIClient.CREATE, CreateUpdatePayload(diff.create, true).toString());
//        logger.info("CREATE response: "+createData);
        String createData = "{\"error\":false,\"message\":[{\"oid\":\"76ae8574-4be7-485c-9728-a6d5a7eac140\",\"password\":\"nbU38nc7Z1v3YhUHEYJTBUNUHDjto3+lABmoQpvifSI=\",\"infrastructure-id\":\"test-bulb1e\"},{\"oid\":\"7539cd28-f0b5-4251-916f-ba65ca19c09c\",\"password\":\"DehZ/eGSB4Q0LqrOvadlCim5pVYGVHSKwQUKxxU8Xag=\",\"infrastructure-id\":\"test-bulb2-to-create\"}]}";
        processCreate(createData, diff.create, config);

        // 5.3 HANDLE UPDATE
//        String updateData = GatewayAPIClient.getInstance().get(GatewayAPIClient.UPDATE);
//        logger.info("UPDATE response: "+updateData);

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

        logger.info("DISCOVERY : END");
    }
}
