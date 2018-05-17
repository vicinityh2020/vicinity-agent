package sk.intersoft.vicinity.agent.service.config.thing;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ThingProcessor {

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

    public static List<JSONObject> processConfiguration(String data) throws Exception {
        List<JSONObject> things = new ArrayList<JSONObject>();
        JSONArray array = getConfigurationThings(data);
        Iterator i = array.iterator();
        while (i.hasNext()) {
            JSONObject object = (JSONObject) i.next();
            things.add(object);
        }
        return things;
    }
}
