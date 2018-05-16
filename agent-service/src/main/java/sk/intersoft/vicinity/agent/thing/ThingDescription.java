package sk.intersoft.vicinity.agent.thing;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.utils.Dump;
import sk.intersoft.vicinity.agent.utils.JSONUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThingDescription {
    final static Logger logger = LoggerFactory.getLogger(ThingDescription.class.getName());

    public String oid = null;
    public String infrastructureId = null;
    public String name = null;
    public String adapterId = null;
    public String agentId = null;
    public String password = null;
    public String type;

    public Map<String, InteractionPattern> properties = new HashMap<String, InteractionPattern>();
    public Map<String, InteractionPattern> actions = new HashMap<String, InteractionPattern>();
    public Map<String, InteractionPattern> events = new HashMap<String, InteractionPattern>();

    // JSON keys
    public static String OID_KEY = "oid";
    public static String NAME_KEY = "name";
    public static String TYPE_KEY = "type";
    public static String PROPERTIES_KEY = "properties";
    public static String ACTIONS_KEY = "actions";
    public static String EVENTS_KEY = "events";



    public static ThingDescription create(JSONObject thingJSON) throws Exception {
        logger.debug("PROCESSING THING DESCRIPTION FROM: \n"+thingJSON.toString(2));



        ThingDescription thing = new ThingDescription();

        try{
            thing.oid = JSONUtil.getString(OID_KEY, thingJSON);
            if(thing.oid == null) throw new Exception("Missing thing [oid]");

            thing.type = JSONUtil.getString(TYPE_KEY, thingJSON);
            if(thing.type == null) throw new Exception("Missing thing [type]");

            thing.name = JSONUtil.getString(NAME_KEY, thingJSON);
            if(thing.name == null) throw new Exception("Missing thing [name]");

            List<JSONObject> properties = JSONUtil.getObjectArray(PROPERTIES_KEY, thingJSON);
            List<JSONObject> actions = JSONUtil.getObjectArray(ACTIONS_KEY, thingJSON);
            List<JSONObject> events = JSONUtil.getObjectArray(EVENTS_KEY, thingJSON);

            if(properties != null){
                for(JSONObject property : properties){
                    InteractionPattern pattern = InteractionPattern.createProperty(property);
                    thing.properties.put(pattern.id, pattern);
                }
            }
            if(actions != null){
                for(JSONObject action : actions){
                    InteractionPattern pattern = InteractionPattern.createAction(action);
                    thing.actions.put(pattern.id, pattern);
                }
            }

            if(events != null){
                for(JSONObject event : events){
                    InteractionPattern pattern = InteractionPattern.createEvent(event);
                    thing.events.put(pattern.id, pattern);
                }
            }

        }
        catch(Exception e) {
            logger.error("", e);
            throw new Exception(e.getMessage() + " ..unable to process thing: "+thingJSON.toString());
        }

        return thing;
    }

    public String toString(int indent){
        Dump dump = new Dump();

        dump.add("THING :", indent);
        dump.add("oid: "+oid, (indent + 1));
        dump.add("type: "+type, (indent + 1));
        dump.add("name: "+name, (indent + 1));
        dump.add("agent-id: "+ agentId, (indent + 1));
        dump.add("adapter-id: "+ adapterId, (indent + 1));
        dump.add("password: "+password, (indent + 1));
        dump.add("credentials: ", (indent + 1));
        dump.add("PROPERTIES: "+properties.size(), (indent + 1));
        for (Map.Entry<String, InteractionPattern> entry : properties.entrySet()) {
            String id = entry.getKey();
            dump.add("PROPERTY MAPPED KEY: "+id, (indent + 2));
            dump.add(entry.getValue().toString(indent + 2));
        }
        dump.add("ACTIONS: "+actions.size(), (indent + 1));
        for (Map.Entry<String, InteractionPattern> entry : actions.entrySet()) {
            String id = entry.getKey();
            dump.add("ACTION MAPPED KEY: "+id, (indent + 2));
            dump.add(entry.getValue().toString(indent + 2));
        }
        dump.add("EVENTS: "+events.size(), (indent + 1));
        for (Map.Entry<String, InteractionPattern> entry : events.entrySet()) {
            String id = entry.getKey();
            dump.add("EVENT MAPPED KEY: "+id, (indent + 2));
            dump.add(entry.getValue().toString(indent + 2));
        }

        return dump.toString();
    }

    public String toSimpleString(int indent){
        Dump dump = new Dump();

        dump.add(toSimpleString(), indent);

        return dump.toString();
    }

    public String toSimpleString(){
        return "THING : [OID: "+oid+"][INFRA-ID: "+ infrastructureId +"][PWD: "+password+"] ";
    }
}
