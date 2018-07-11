package sk.intersoft.vicinity.agent.service.config.processor;

import org.json.JSONArray;
import org.json.JSONObject;
import sk.intersoft.vicinity.agent.clients.NeighbourhoodManager;
import sk.intersoft.vicinity.agent.thing.ThingDescription;
import sk.intersoft.vicinity.agent.utils.JSONUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ThingProcessor {


    public static List<JSONObject> processConfiguration(String data) throws Exception {
        List<JSONObject> things = new ArrayList<JSONObject>();
        JSONArray array = NeighbourhoodManager.getConfigurationThings(data);
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
        if(adapterId == null && adapterId.equals("")){
            throw new Exception("Missing [adapter-id] in agent discovery data!");
        }

        if(adapterId != null && !adapterId.equals("")){

            if(!adapterId.equals(matchAdapterId)) {
                throw new Exception("ADAPTER-ID["+adapterId+"] IN /objects JSON DOES NOT MATCH ADAPTER-ID["+matchAdapterId+"] THAT INVOKED DISCOVERY! .. check endpoints in agent config!");
            }

            List<JSONObject> objects = JSONUtil.getObjectArray("thing-descriptions", descriptions);
            if(objects == null){
                throw new Exception("Missing [thing-descriptions] in agent discovery data!");
            }
            for(JSONObject o : objects) {
                o.put(ThingDescription.ADAPTER_ID_KEY, adapterId);
                things.add(o);
            }

        }
        return things;
    }

    public static List<JSONObject> processRecoveryData(String data, String matchAdapterId) throws Exception {
        List<JSONObject> things = new ArrayList<JSONObject>();

        JSONArray descriptions = new JSONArray(data);
        Iterator i = descriptions.iterator();
        while(i.hasNext()){
            Object item = i.next();
            things.add((JSONObject)item);
        }
        return things;
    }

}
