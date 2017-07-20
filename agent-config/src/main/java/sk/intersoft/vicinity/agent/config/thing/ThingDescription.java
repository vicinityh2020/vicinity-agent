package sk.intersoft.vicinity.agent.config.thing;

import org.json.JSONArray;
import org.json.JSONObject;
import sk.intersoft.vicinity.agent.utils.JSONUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThingDescription {
    public static final String OID = "oid";

    public String oid;
    public String infrastructureID;
    public String login;
    public String password;
    public JSONObject json;
    public Map<String, InteractionPattern> properties = new HashMap<String, InteractionPattern>();
    public Map<String, InteractionPattern> actions = new HashMap<String, InteractionPattern>();
    public Map<String, InteractionPattern> events = new HashMap<String, InteractionPattern>();

    public ThingDescription(String infrastructureID, JSONObject object) {
        this.infrastructureID = infrastructureID;
        this.json = object;
    }

    public static ThingDescription create(JSONObject object) throws Exception {
        String oid = JSONUtil.getString("oid", object);
        if(oid == null) throw new Exception("Missing oid in: "+object.toString());

        ThingDescription thing = new ThingDescription(oid, object);
        List<JSONObject> properties = JSONUtil.getObjectArray("properties", object);
        List<JSONObject> actions = JSONUtil.getObjectArray("actions", object);
        List<JSONObject> events = JSONUtil.getObjectArray("events", object);

        if(properties != null){
            for(JSONObject property : properties){
                InteractionPattern pattern = InteractionPattern.create(property, InteractionPattern.PID);
                thing.properties.put(pattern.id, pattern);
            }
        }
        if(actions != null){
            for(JSONObject action : actions){
                InteractionPattern pattern = InteractionPattern.create(action, InteractionPattern.AID);
                thing.actions.put(pattern.id, pattern);
            }
        }

        if(events != null){
            for(JSONObject event : events){
                InteractionPattern pattern = new InteractionPattern(JSONUtil.getString(InteractionPattern.EID, event), null, null);
                thing.events.put(pattern.id, pattern);
            }
        }

        return thing;
    }

    public void show(){
        System.out.println("  THING ["+oid+" / "+infrastructureID+"]:");
        System.out.println("    login/password: "+login+" / "+password);
        System.out.println("    json: \n"+json.toString(2));
        System.out.println("    properties:");
        for (Map.Entry<String, InteractionPattern> entry : properties.entrySet()) {
            String pid = entry.getKey();
            InteractionPattern pattern = entry.getValue();
            System.out.println("    pid: ["+pid+"]");
            System.out.println("      pattern pid: ["+pattern.id+"]");
            System.out.println("      read: ["+pattern.readEndpoint+"]");
            System.out.println("      write: ["+pattern.writeEndpoint+"]");
        }
        System.out.println("    actions:");
        for (Map.Entry<String, InteractionPattern> entry : actions.entrySet()) {
            String pid = entry.getKey();
            InteractionPattern pattern = entry.getValue();
            System.out.println("    aid: ["+pid+"]");
            System.out.println("      pattern aid: ["+pattern.id+"]");
            System.out.println("      read: ["+pattern.readEndpoint+"]");
            System.out.println("      write: [" + pattern.writeEndpoint + "]");
        }
        System.out.println("    events:");
        for (Map.Entry<String, InteractionPattern> entry : events.entrySet()) {
            String pid = entry.getKey();
            InteractionPattern pattern = entry.getValue();
            System.out.println("    eid: ["+pid+"]");
            System.out.println("      pattern eid: ["+pattern.id+"]");
        }

    }

}
