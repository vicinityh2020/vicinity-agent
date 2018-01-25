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

    public String dbId = null;
    public String oid = null;
    public String infrastructureID = null;
    public String thingType;
    public String login;
    public String password;
    public JSONObject json;

    public Map<String, InteractionPattern> properties = new HashMap<String, InteractionPattern>();
    public Map<String, InteractionPattern> actions = new HashMap<String, InteractionPattern>();
    public Map<String, InteractionPattern> events = new HashMap<String, InteractionPattern>();

    // JSON keys
    public static String OID_KEY = "oid";
    public static String INFRASTRUCTURE_ID_KEY = "infrastructure-id";
    public static String TYPE_KEY = "type";
    public static String PROPERTIES_KEY = "properties";
    public static String ACTIONS_KEY = "actions";
    public static String EVENTS_KEY = "events";

    public boolean sameAs(ThingDescription other) {
        logger.info("DOING DOMETHING");
        logger.debug(Dump.indent("DOING DIFF", 0));
        if(!this.thingType.equalsIgnoreCase(other.thingType)){
            logger.debug(Dump.indent("Thing [type] diff: ["+thingType+"] -> ["+other.thingType+"]", 1));
            return false;
        }

        logger.debug(Dump.indent("Thing properties check", 1));
        boolean propertiesAreSame = ThingDescriptionDiff.samePatterns(properties, other.properties, 2);
        if(!propertiesAreSame) {
            logger.debug(Dump.indent("Thing properties are different", 2));
            return false;
        }

        logger.debug(Dump.indent("Thing actions check", 1));
        boolean actionsAreSame = ThingDescriptionDiff.samePatterns(actions, other.actions, 2);
        if(!actionsAreSame) {
            logger.debug(Dump.indent("Thing actions are different", 2));
            return false;
        }

        logger.debug(Dump.indent("Thing events check", 1));
        boolean eventsAreSame = ThingDescriptionDiff.samePatterns(events, other.events, 2);
        if(!eventsAreSame) {
            logger.debug(Dump.indent("Thing events are different", 2));
            return false;
        }

        return true;
    }


    public static ThingDescription create(JSONObject thingJSON) throws Exception {

        ThingDescription thing = new ThingDescription();
        thing.json = thingJSON;


        String oid = JSONUtil.getString(OID_KEY, thingJSON);
        if(oid == null) throw new Exception("Missing [oid] in: "+thingJSON.toString());
        thing.infrastructureID = oid;

        String thingType = JSONUtil.getString(TYPE_KEY, thingJSON);
        if(thingType == null) throw new Exception("Missing [type] in: "+thingJSON.toString());
        thing.thingType = thingType;

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

        return thing;
    }

    public String toString(int indent){
        Dump dump = new Dump();

        dump.add("THING :", indent);
        dump.add("oid: "+oid, (indent + 1));
        dump.add("infrastructure-id: "+infrastructureID, (indent + 1));
        dump.add("type: "+thingType, (indent + 1));
        dump.add("credentials: ", (indent + 1));
        dump.add("login: "+login, (indent + 2));
        dump.add("password: "+password, (indent + 2));
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
}
