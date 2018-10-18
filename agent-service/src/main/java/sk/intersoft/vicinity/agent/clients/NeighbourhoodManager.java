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

    public static final String AGID_KEY = "agid";
    public static final String THING_DESCRIPTIONS_KEY = "thingDescriptions";
    public static final String OIDS_KEY = "oids";


    public static JSONArray getConfigurationThings(String data) throws Exception {


        JSONObject root = new JSONObject(data);

        JSONArray extraction = new JSONArray();

        logger.debug("parsing configuration things..");

        try{
            JSONObject msgObj = root.getJSONObject("message");
            logger.debug("message is object .. no configuration case");
            return extraction;
        }
        catch(Exception ex){

        }


        if(!root.has("message")){
            logger.debug("[message] key does not exist?? .. return empty config");
            return extraction;
        }


        JSONArray results = root.getJSONArray("message");
        Iterator<Object> i = results.iterator();
        while(i.hasNext()){
            JSONObject item = (JSONObject)i.next();
            if(item.has("id") && item.getJSONObject("id").has("info")){
                extraction.put(item.getJSONObject("id").getJSONObject("info"));
            }
        }


        return extraction;
    }

    public static List<JSONObject> getCreateResults(String data) throws Exception {
        JSONObject root = new JSONObject(data);

        List<JSONObject> extraction = new ArrayList<JSONObject>();

        logger.debug("parsing configuration things..");

        try{
            JSONObject msgObj = root.getJSONObject("message");
            logger.debug("message is object .. no configuration case");
            return extraction;
        }
        catch(Exception ex){

        }


        if(!root.has("message")){
            logger.debug("[message] key does not exist?? .. return empty config");
            return extraction;
        }


        JSONArray results = root.getJSONArray("message");
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

    public static void delete(JSONObject payload, String agentId, String agentPassword) throws Exception {
        logger.info("delete payload: \n" + payload.toString(2));

        String deleteResponse = GatewayAPIClient.post(GatewayAPIClient.deleteEndpoint(agentId), payload.toString(), agentId, agentPassword);
        logger.info("delete raw response: \n" + deleteResponse);

    }
    public static void delete(JSONObject payload, AgentConfig agent) throws Exception {
        delete(payload, agent.agentId, agent.password);
    }


    public static String create(JSONObject payload, AgentConfig agent) throws Exception {
        logger.info("create payload: \n" + payload.toString(2));

        String createResponse = GatewayAPIClient.post(GatewayAPIClient.createEndpoint(agent.agentId), payload.toString(), agent.agentId, agent.password);
        logger.info("create raw response: \n" + createResponse);
//        String createResponse = GatewayAPIClient.fakepost("https://vicinity.bavenir.eu:3000/commServer/items/register", payload.toString(), agent.agentId, agent.password);
//        String test = GatewayAPIClient.fakepost("http://localhost:9995/adapter/objects/x/actions/y", payload.toString(), agent.agentId, agent.password);
//        logger.info("test response: \n" + test);

        return createResponse;
    }


    public static String update(JSONObject payload, AgentConfig agent) throws Exception {
        logger.info("update payload: \n" + payload.toString(2));

        String updateResponse = GatewayAPIClient.put(GatewayAPIClient.updateEndpoint(agent.agentId), payload.toString(), agent.agentId, agent.password);
        logger.info("update raw response: \n" + updateResponse);

        return updateResponse;
    }

    public static String updateContent(JSONObject payload, AgentConfig agent) throws Exception {
        logger.info("update things content payload: \n" + payload.toString(2));

        String updateResponse = GatewayAPIClient.put(GatewayAPIClient.updateContentEndpoint(agent.agentId), payload.toString(), agent.agentId, agent.password);
        logger.info("update things content raw response: \n" + updateResponse);

        return updateResponse;
    }

}
