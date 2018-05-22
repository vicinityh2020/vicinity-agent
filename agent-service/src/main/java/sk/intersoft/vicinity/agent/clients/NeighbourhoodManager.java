package sk.intersoft.vicinity.agent.clients;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.service.config.AgentConfig;
import sk.intersoft.vicinity.agent.thing.ThingDescription;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NeighbourhoodManager {
    final static Logger logger = LoggerFactory.getLogger(NeighbourhoodManager.class.getName());

    public static final String AGID_KEY = "adid";
    public static final String THING_DESCRIPTIONS_KEY = "thingDescriptions";
    public static final String OIDS_KEY = "oids";


    public static JSONArray getConfigurationThings(String data) throws Exception {
        JSONObject root = new JSONObject(data);
        JSONArray results = root.getJSONArray("message");
        JSONArray extraction = new JSONArray();
        Iterator<Object> i = results.iterator();
        while(i.hasNext()){
            JSONObject item = (JSONObject)i.next();
            extraction.put(item.getJSONObject("id").getJSONObject("info"));
        }
        return extraction;
    }

    public static List<JSONObject> getCreateResults(String data) throws Exception {
        JSONObject root = new JSONObject(data);
        JSONArray results = root.getJSONArray("message");

        List<JSONObject> extraction = new ArrayList<JSONObject>();

        Iterator<Object> i = results.iterator();
        while(i.hasNext()){
            JSONObject item = (JSONObject)i.next();
            extraction.add(item);
        }
        return extraction;
    }


    public static JSONObject deletePayload(JSONArray oids, String agentId) {
        JSONObject payload = new JSONObject();

        payload.put(AGID_KEY, agentId);
        payload.put(OIDS_KEY, oids);

        return payload;

    }

    public static JSONObject deletePayload(List<String> oids, String agentId) {
        JSONArray array = new JSONArray();

        for (String oid : oids) {
            array.put(oid);
        }
        return deletePayload(array, agentId);
    }

    public static JSONObject deleteThingsPayload(List<ThingDescription> things, String agentId) {
        JSONArray array = new JSONArray();

        for (ThingDescription t : things) {
            array.put(t.oid);
        }
        return deletePayload(array, agentId);
    }

    public static JSONObject createUpdateThingsPayload(List<ThingDescription> things, String agentId, boolean create) throws Exception {
        JSONObject payload = new JSONObject();
        JSONArray thingsArray = new JSONArray();

        payload.put(AGID_KEY, agentId);
        payload.put(THING_DESCRIPTIONS_KEY, thingsArray);


        for (ThingDescription thing : things) {
            JSONObject json = ThingDescription.toJSON(thing);
            if(create){
                json.remove(ThingDescription.OID_KEY);
            }
            json.put(ThingDescription.INFRASTRUCTURE_KEY, thing.adapterInfrastructureID);
            thingsArray.put(json);
        }

        return payload;

    }

    public static JSONObject createThingsPayload(List<ThingDescription> things, String agentId) throws Exception {
        return createUpdateThingsPayload(things, agentId, true);
    }
    public static JSONObject updateThingsPayload(List<ThingDescription> things, String agentId) throws Exception {
        return createUpdateThingsPayload(things, agentId, false);
    }

    public static void delete(JSONObject payload, AgentConfig agent) throws Exception {
        logger.info("delete payload: \n" + payload.toString(2));

        String deleteResponse = GatewayAPIClient.post(GatewayAPIClient.deleteEndpoint(agent.agentId), payload.toString(), agent.agentId, agent.password);
        logger.info("delete raw response: \n" + deleteResponse);

    }


    public static String create(JSONObject payload, AgentConfig agent) throws Exception {
        logger.info("create payload: \n" + payload.toString(2));

        String createResponse = GatewayAPIClient.post(GatewayAPIClient.createEndpoint(agent.agentId), payload.toString(), agent.agentId, agent.password);
        logger.info("create raw response: \n" + createResponse);

        return createResponse;
    }


    public static String update(JSONObject payload, AgentConfig agent) throws Exception {
        logger.info("update payload: \n" + payload.toString(2));

        String updateResponse = GatewayAPIClient.put(GatewayAPIClient.updateEndpoint(agent.agentId), payload.toString(), agent.agentId, agent.password);
        logger.info("update raw response: \n" + updateResponse);

        return updateResponse;
    }
}