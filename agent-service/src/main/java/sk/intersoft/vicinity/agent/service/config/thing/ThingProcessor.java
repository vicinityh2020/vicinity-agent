package sk.intersoft.vicinity.agent.service.config.thing;

import org.json.JSONArray;
import org.json.JSONObject;
import sk.intersoft.vicinity.agent.thing.ThingDescription;
import sk.intersoft.vicinity.agent.utils.JSONUtil;

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

    public static List<JSONObject> processAdapter(String data, String matchAdapterId) throws Exception {
        List<JSONObject> things = new ArrayList<JSONObject>();

        JSONObject descriptions = new JSONObject(data);
        String adapterId = JSONUtil.getString("adapter-id", descriptions);
        if(adapterId != null && !adapterId.equals("")){

            if(!adapterId.equals(matchAdapterId)) {
                throw new Exception("ADAPTER-ID["+adapterId+"] IN /objects JSON DOES NOT MATCH ADAPTER-ID["+matchAdapterId+"] THAT INVOKED DISCOVERY! .. check endpoints in agent config!");
            }

            List<JSONObject> objects = JSONUtil.getObjectArray("thing-descriptions", descriptions);
            for(JSONObject o : objects) {
                o.put(ThingDescription.ADAPTER_ID_KEY, adapterId);
                things.add(o);
            }

        }
        return things;
    }

}
