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

    public static ThingDescriptions process(JSONArray thingsJSON, boolean isConfiguration) throws Exception {
        ThingDescriptions things = new ThingDescriptions();

        Iterator i = thingsJSON.iterator();
        while (i.hasNext()) {
            JSONObject thingJSON = (JSONObject) i.next();
            try{
                ThingDescription thing = ThingDescription.create(thingJSON, isConfiguration);
                if(thing.oid != null) {
                    things.byOID.put(thing.oid, thing);
                }
                if(thing.infrastructureID != null) {
                    things.byInfrastructureID.put(thing.infrastructureID, thing);
                }
            }
            catch(Exception e){
                logger.error("thing not created!", e);
            }
        }


        return things;

    }

    public static ThingDescription process(JSONObject thing, boolean isConfiguration) throws Exception {
        return ThingDescription.create(thing, isConfiguration);
    }
}