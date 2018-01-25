package sk.intersoft.vicinity.agent.thing;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ThingsProcessor {
    final static Logger logger = LoggerFactory.getLogger(ThingsProcessor.class.getName());

    public static List<ThingDescription> process(JSONArray things) throws Exception {
        List<ThingDescription> list = new ArrayList<ThingDescription>();

        Iterator i = things.iterator();
        while (i.hasNext()) {
            JSONObject thing = (JSONObject) i.next();
            list.add(ThingDescription.create(thing));
        }


        return list;

    }

    public static ThingDescription process(JSONObject thing) throws Exception {
        return ThingDescription.create(thing);
    }
}