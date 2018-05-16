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
    public String type;
    public String jsonString;

    public Map<String, InteractionPattern> properties = new HashMap<String, InteractionPattern>();
    public Map<String, InteractionPattern> actions = new HashMap<String, InteractionPattern>();
    public Map<String, InteractionPattern> events = new HashMap<String, InteractionPattern>();

    // JSON keys
    public static String OID_KEY = "oid";
    public static String AGENT_INFRASTRUCTURE_ID_KEY = "infrastructure-id";
    public static String PASSWORD_KEY = "password";
    public static String TYPE_KEY = "type";
    public static String PROPERTIES_KEY = "properties";
    public static String ACTIONS_KEY = "actions";
    public static String EVENTS_KEY = "events";



    public static ThingDescription create(JSONObject thingJSON, AdapterConfig adapterConfig) throws Exception {

        ThingDescription thing = new ThingDescription();

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
        dump.add("type: "+type, (indent + 1));
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
        return "THING : [OID: "+oid+"][INFRA-ID: "+ AgentInfrastructureID +"][PWD: "+password+"] ";
    }
}
