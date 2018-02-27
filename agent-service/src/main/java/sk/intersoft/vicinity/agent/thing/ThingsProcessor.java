package sk.intersoft.vicinity.agent.thing;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.service.config.AdapterConfig;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ThingsProcessor {
    final static Logger logger = LoggerFactory.getLogger(ThingsProcessor.class.getName());



    public static ThingDescriptions process(JSONArray thingsJSON, AdapterConfig adapterConfig) throws Exception {
        ThingDescriptions things = new ThingDescriptions();

        Iterator i = thingsJSON.iterator();
        while (i.hasNext()) {
            JSONObject thingJSON = (JSONObject) i.next();
            try{
                ThingDescription thing = ThingDescription.create(thingJSON, adapterConfig);
                things.add(thing);
            }
            catch(Exception e){
                logger.error("thing not created!", e);
                throw e;
            }
        }


        return things;

    }

    public static ThingDescriptions process(String data, AdapterConfig adapterConfig) throws Exception {
        if(adapterConfig == null){
            JSONObject root = new JSONObject(data);
            JSONArray results = root.getJSONArray("message");
            JSONArray extraction = new JSONArray();
            Iterator<Object> i = results.iterator();
            while(i.hasNext()){
                JSONObject item = (JSONObject)i.next();
                extraction.put(item.getJSONObject("id").getJSONObject("info"));
            }
            return process(extraction, adapterConfig);
        }
        else {
            return process(new JSONArray(data), adapterConfig);
        }
    }

}