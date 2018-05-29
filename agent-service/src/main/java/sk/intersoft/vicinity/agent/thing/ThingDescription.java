package sk.intersoft.vicinity.agent.thing;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.db.PersistedThing;
import sk.intersoft.vicinity.agent.service.config.processor.ThingDescriptions;
import sk.intersoft.vicinity.agent.utils.Dump;
import sk.intersoft.vicinity.agent.utils.JSONUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThingDescription {
    final static Logger logger = LoggerFactory.getLogger(ThingDescription.class.getName());

    public String oid = null;
    public String infrastructureId = null;
    public String adapterId = null;
    public String adapterOID = null;
    public String adapterInfrastructureID = null;

    public String name = null;
    public String password = null;
    public String type;

    public Map<String, InteractionPattern> properties = new HashMap<String, InteractionPattern>();
    public Map<String, InteractionPattern> actions = new HashMap<String, InteractionPattern>();
    public Map<String, InteractionPattern> events = new HashMap<String, InteractionPattern>();

    public Map<String, String> jsonExtension = new HashMap<String, String>();


    // JSON keys
    public static String OID_KEY = "oid";
    public static String INFRASTRUCTURE_KEY = "infrastructure-id";
    public static String PASSWORD_KEY = "password";
    public static String ADAPTER_ID_KEY = "adapter-id";
    public static String NAME_KEY = "name";
    public static String TYPE_KEY = "type";
    public static String PROPERTIES_KEY = "properties";
    public static String ACTIONS_KEY = "actions";
    public static String EVENTS_KEY = "events";


    public InteractionPattern getInteractionPattern(String patternID, String patternType) throws Exception {
        if(patternID == null) throw new Exception("Missing Interaction pattern ID");


        InteractionPattern pattern = null;
        if(patternType.equals(InteractionPattern.PROPERTY)){
            pattern = properties.get(patternID);
        }
        else if(patternType.equals(InteractionPattern.ACTION)){
            pattern = actions.get(patternID);
        }
        else if(patternType.equals(InteractionPattern.EVENT)){
            pattern = events.get(patternID);
        }

        if(pattern == null) throw new Exception("Missing interaction pattern ["+patternType+"] for [OID: "+oid+"] [PATTERN-ID: "+patternID+"]");

        return pattern;

    }

    public static String identifier(String id, String adapterId) {
        return adapterId + "---!---"+id;
    }
    public void toInfrastructure(){
        infrastructureId = oid;
        oid = null;

        adapterInfrastructureID = adapterOID;
        adapterOID = null;
    }
    public void updateCredentials(ThingDescription configThing) {
        oid = configThing.oid;
        adapterOID = configThing.adapterOID;
        password = configThing.password;
    }
    public void updatePersistence(PersistedThing persisted) {
        adapterInfrastructureID = persisted.adapterInfrastructureId;
        password = persisted.password;
    }
    public void updateCreatedData(String oid, String password) {
        this.oid = oid;
        this.adapterOID = identifier(oid, adapterId);
        this.password = password;
    }


    public static ThingDescription create(JSONObject thingJSON, ThingValidator validator) throws Exception {
        logger.debug("PROCESSING THING DESCRIPTION FROM: \n"+thingJSON.toString(2));



        ThingDescription thing = new ThingDescription();

        try{

            boolean fail = false;

            thing.oid = JSONUtil.getString(OID_KEY, thingJSON);
            if(thing.oid == null || thing.oid.equals("")) fail = validator.error("Missing thing [oid].");

            thing.adapterId = JSONUtil.getString(ADAPTER_ID_KEY, thingJSON);
            if(thing.adapterId == null || thing.adapterId.equals("")) fail = validator.error("Missing thing [adapter-id].");

            thing.adapterOID = identifier(thing.oid, thing.adapterId);


            thing.type = JSONUtil.getString(TYPE_KEY, thingJSON);
            if(thing.type == null) fail = validator.error("Missing thing [type].");

            thing.name = JSONUtil.getString(NAME_KEY, thingJSON);
            if(thing.name == null) fail = validator.error("Missing thing [name].");


            List<JSONObject> properties = JSONUtil.getObjectArray(PROPERTIES_KEY, thingJSON);
            List<JSONObject> actions = JSONUtil.getObjectArray(ACTIONS_KEY, thingJSON);
            List<JSONObject> events = JSONUtil.getObjectArray(EVENTS_KEY, thingJSON);

            if(properties != null){
                for(JSONObject property : properties){
                    InteractionPattern pattern = InteractionPattern.createProperty(property, validator);
                    if(pattern == null)
                        fail = true;
                    else
                        thing.properties.put(pattern.id, pattern);
                }
            }
            if(actions != null){
                for(JSONObject action : actions){
                    InteractionPattern pattern = InteractionPattern.createAction(action, validator);
                    if(pattern == null)
                        fail = true;
                    else
                        thing.actions.put(pattern.id, pattern);
                }
            }

            if(events != null){
                for(JSONObject event : events){
                    InteractionPattern pattern = InteractionPattern.createEvent(event, validator);
                    if(pattern == null)
                        fail = true;
                    else
                        thing.events.put(pattern.id, pattern);
                }
            }

            if(fail){
                validator.error("Unable to process thing: "+validator.identify(thing.oid, thingJSON));
                return null;
            }
        }
        catch(Exception e) {
            validator.error("Unable to process thing: "+validator.identify(thing.oid, thingJSON));
            return null;
        }

        return thing;
    }

    public static void addExtension(Map<String, String> extension, JSONObject object){
        for (Map.Entry<String, String> entry : extension.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            object.put(key, value);
        }

    }

    public static JSONObject toJSON(ThingDescription thing) throws Exception {
        JSONObject object = new JSONObject();
        JSONArray jsonProperties = new JSONArray();
        JSONArray jsonActions = new JSONArray();
        JSONArray jsonEvents = new JSONArray();

        object.put(PROPERTIES_KEY, jsonProperties);
        object.put(ACTIONS_KEY, jsonActions);
        object.put(EVENTS_KEY, jsonEvents);

        object.put(OID_KEY, thing.oid);
        object.put(TYPE_KEY, thing.type);
        object.put(ADAPTER_ID_KEY, thing.adapterId);
        object.put(NAME_KEY, thing.name);

        for (Map.Entry<String, InteractionPattern> entry : thing.properties.entrySet()) {
            InteractionPattern pattern = entry.getValue();
            jsonProperties.put(InteractionPattern.propertyJSON(pattern));
        }
        for (Map.Entry<String, InteractionPattern> entry : thing.actions.entrySet()) {
            InteractionPattern pattern = entry.getValue();
            jsonActions.put(InteractionPattern.actionJSON(pattern));
        }
        for (Map.Entry<String, InteractionPattern> entry : thing.events.entrySet()) {
            InteractionPattern pattern = entry.getValue();
            jsonEvents.put(InteractionPattern.eventJSON(pattern));
        }

        addExtension(thing.jsonExtension, object);

        return object;
    }

    public String toString(int indent){
        Dump dump = new Dump();

        dump.add("THING :", indent);
        dump.add("oid: "+oid, (indent + 1));
        dump.add("infrastructure-id: "+infrastructureId, (indent + 1));
        dump.add("adapter-id: "+adapterId, (indent + 1));
        dump.add("adapter-oid: "+adapterOID, (indent + 1));
        dump.add("adapter-infrastructure-id: "+adapterInfrastructureID, (indent + 1));
        dump.add("type: "+type, (indent + 1));
        dump.add("name: "+name, (indent + 1));
        dump.add("password: "+password, (indent + 1));
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
        return "THING : [OID: "+oid+"][INFRA-ID: "+ infrastructureId +"][ADAPTER-ID: "+adapterId+"][ADAPTER-INFRA-ID: "+adapterInfrastructureID+"][PWD: "+password+"] ";
    }

    public JSONObject toStatusJSON() {
        JSONObject object = new JSONObject();

        object.put("oid", oid);
        object.put("infra-id", infrastructureId);
        object.put("adapter-oid", adapterOID);
        object.put("adapter-infra-id", adapterInfrastructureID);
        object.put("password", password);

        return object;
    }

}
