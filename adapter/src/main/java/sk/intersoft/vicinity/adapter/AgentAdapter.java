package sk.intersoft.vicinity.adapter;

import org.json.JSONArray;

public interface AgentAdapter {
    public JSONArray getPropertiesValue(String thingID, String propertyIDs) throws Exception;
}
