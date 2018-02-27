package sk.intersoft.vicinity.agent.thing;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.service.config.AdapterConfig;
import sk.intersoft.vicinity.agent.thing.persistence.PersistedThing;
import sk.intersoft.vicinity.agent.utils.Dump;
import sk.intersoft.vicinity.agent.utils.JSONUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThingDescription {
    final static Logger logger = LoggerFactory.getLogger(ThingDescription.class.getName());

    public String oid = null;
    public String AgentInfrastructureID = null;
    public String adapterId = null;
    public String adapterThingId = null;
    public String password = null;
    public boolean enabled = true;
    public String thingType;
    public String jsonString;

    public Map<String, InteractionPattern> properties = new HashMap<String, InteractionPattern>();
    public Map<String, InteractionPattern> actions = new HashMap<String, InteractionPattern>();
    public Map<String, InteractionPattern> events = new HashMap<String, InteractionPattern>();

    // JSON keys
    public static String OID_KEY = "oid";
    public static String AGENT_INFRASTRUCTURE_ID_KEY = "infrastructure-id";
    public static String PASSWORD_KEY = "password";
    public static String ENABLED_KEY = "enabled";
    public static String TYPE_KEY = "type";
    public static String PROPERTIES_KEY = "properties";
    public static String ACTIONS_KEY = "actions";
    public static String EVENTS_KEY = "events";

    // JSON-LD keys
    public static String LD_TYPE_KEY = "@type";


    public JSONObject toJSON(){
        JSONObject json = new JSONObject(jsonString);
        if(oid != null) {
            json.put(OID_KEY, oid);
        }
        else {
            json.remove(OID_KEY);
        }
        if(AgentInfrastructureID != null) {
            json.put(AGENT_INFRASTRUCTURE_ID_KEY, AgentInfrastructureID);
        }
        else {
            json.remove(AGENT_INFRASTRUCTURE_ID_KEY);
        }
        return json;
    }

    public void update(ThingDescription configThing) {
        oid = configThing.oid;
        password = configThing.password;
    }

    public static String prefixed2value(String content) {
        String[] parts = content.split(":");
        if(parts.length == 2) {
            return parts[1];
        }
        return content;
    }

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

    public String getReadHref(String patternID, String patternType) throws Exception {
        InteractionPatternEndpoint endpoint = getInteractionPattern(patternID, patternType).readEndpoint;
        if(endpoint != null) {
            return endpoint.href;
        }
        else {
            throw new Exception("Not existing READ interaction pattern ["+patternType+"] for [OID: "+oid+"] [PATTERN-ID: "+patternID+"]");
        }

    }
    public String getWriteHref(String patternID, String patternType) throws Exception {
        InteractionPatternEndpoint endpoint = getInteractionPattern(patternID, patternType).writeEndpoint;
        if(endpoint != null) {
            return endpoint.href;
        }
        else {
            throw new Exception("Not existing WRITE interaction pattern ["+patternType+"] for [OID: "+oid+"] [PATTERN-ID: "+patternID+"]");
        }

    }



    public boolean sameAs(ThingDescription other) {
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


    public static ThingDescription create(JSONObject thingJSON, AdapterConfig adapterConfig) throws Exception {

        ThingDescription thing = new ThingDescription();


        if(adapterConfig == null){
            logger.debug("processing thing configuration");

            String ldType = JSONUtil.getString(LD_TYPE_KEY, thingJSON);
            if(ldType == null) throw new Exception("Missing ["+LD_TYPE_KEY+"] in: "+thingJSON.toString());

            thingJSON.put(TYPE_KEY, prefixed2value(ldType));


            String oid = JSONUtil.getString(OID_KEY, thingJSON);
            if(oid == null) throw new Exception("Missing [oid] in: "+thingJSON.toString());
            thing.oid = oid;

            PersistedThing persisted = PersistedThing.getByOID(thing.oid);
            if(persisted != null){
                logger.debug("persisted thing: "+persisted.toString());

                thing.AgentInfrastructureID = persisted.infrastructureId;
                thing.password = persisted.password;
            }
            else {
                logger.debug("NO persisted thing");
            }


//            try{
//                boolean enabled = JSONUtil.getBoolean(ENABLED_KEY, thingJSON);
//                thing.enabled = enabled;
//            }
//            catch(Exception e){
//                logger.debug("WRONG OR MISSING ENABLED PROPERTY! setting enabled to TRUE!");
//                thing.enabled = true;
//            }
        }
        else{
            logger.debug("processing thing from adapter");
            String oid = JSONUtil.getString(OID_KEY, thingJSON);
            if(oid == null) throw new Exception("Missing [oid] in: "+thingJSON.toString());
            thing.AgentInfrastructureID = adapterConfig.adapterId + "_" +oid;
            thing.adapterId = adapterConfig.adapterId;
            thing.adapterThingId = oid;
        }

        boolean isConfiguration = (adapterConfig == null);

        String thingType = JSONUtil.getString(TYPE_KEY, thingJSON);
        if(thingType == null) throw new Exception("Missing [type] in: "+thingJSON.toString());
        thing.thingType = thingType;

        List<JSONObject> properties = JSONUtil.getObjectArray(PROPERTIES_KEY, thingJSON);
        List<JSONObject> actions = JSONUtil.getObjectArray(ACTIONS_KEY, thingJSON);
        List<JSONObject> events = JSONUtil.getObjectArray(EVENTS_KEY, thingJSON);

        if(properties != null){
            for(JSONObject property : properties){
                InteractionPattern pattern = InteractionPattern.createProperty(property, isConfiguration);
                thing.properties.put(pattern.id, pattern);
            }
        }
        if(actions != null){
            for(JSONObject action : actions){
                InteractionPattern pattern = InteractionPattern.createAction(action, isConfiguration);
                thing.actions.put(pattern.id, pattern);
            }
        }

        if(events != null){
            for(JSONObject event : events){
                InteractionPattern pattern = InteractionPattern.createEvent(event, isConfiguration);
                thing.events.put(pattern.id, pattern);
            }
        }

        // save JSON string with all changes done on the way
        thing.jsonString = thingJSON.toString();

        return thing;
    }

    public String toString(int indent){
        Dump dump = new Dump();

        dump.add("THING :", indent);
        dump.add("oid: "+oid, (indent + 1));
        dump.add("agent-infrastructure-id: "+ AgentInfrastructureID, (indent + 1));
        dump.add("adapter-thing-id: "+ adapterThingId, (indent + 1));
        dump.add("adapter-id: "+ adapterId, (indent + 1));
        dump.add("password: "+password, (indent + 1));
        dump.add("enabled: "+enabled, (indent + 1));
        dump.add("type: "+thingType, (indent + 1));
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
        dump.add("JSON: \n"+toJSON().toString(2), (indent + 1));

        return dump.toString();
    }

    public String toSimpleString(int indent){
        Dump dump = new Dump();

        dump.add(toSimpleString(), indent);

        return dump.toString();
    }

    public String toSimpleString(){
        return "THING : [OID: "+oid+"][INFRA-ID: "+ AgentInfrastructureID +"][PWD: "+password+"] ";
    }
}
