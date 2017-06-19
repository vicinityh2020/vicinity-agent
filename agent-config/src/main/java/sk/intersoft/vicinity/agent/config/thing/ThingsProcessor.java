package sk.intersoft.vicinity.agent.config.thing;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ThingsProcessor {


    public static List<ThingDescription> process(JSONArray things) throws Exception {
        JSONArray mapping = new JSONArray();
        List<ThingDescription> list = new ArrayList<ThingDescription>();

        Iterator i = things.iterator();
        while(i.hasNext()){
            JSONObject thing = (JSONObject)i.next();
            list.add(ThingDescription.create(thing));
        }


        return list;

    }

}
