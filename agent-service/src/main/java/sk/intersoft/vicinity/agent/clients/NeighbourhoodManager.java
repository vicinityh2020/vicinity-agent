package sk.intersoft.vicinity.agent.clients;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class NeighbourhoodManager {
    public static final String AGID_KEY = "agid";
    public static final String OIDS_KEY = "oids";

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
}
