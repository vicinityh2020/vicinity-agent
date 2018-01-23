package sk.intersoft.vicinity.agent.config.thing;

import org.json.JSONObject;
import sk.intersoft.vicinity.agent.utils.JSONUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThingDescriptionBackup {
    public static final String OID = "oid";

    public String oid;
    public String infrastructureID;
    public String login;
    public String password;
    public JSONObject json;
    public Map<String, InteractionPatternBackup> properties = new HashMap<String, InteractionPatternBackup>();
    public Map<String, InteractionPatternBackup> actions = new HashMap<String, InteractionPatternBackup>();
    public Map<String, InteractionPatternBackup> events = new HashMap<String, InteractionPatternBackup>();

    public ThingDescriptionBackup(String infrastructureID, JSONObject object) {
        this.infrastructureID = infrastructureID;
        this.json = object;
    }

    public static ThingDescriptionBackup create(JSONObject object) throws Exception {
        String oid = JSONUtil.getString("oid", object);
        if(oid == null) throw new Exception("Missing oid in: "+object.toString());

        ThingDescriptionBackup thing = new ThingDescriptionBackup(oid, object);
        List<JSONObject> properties = JSONUtil.getObjectArray("properties", object);
        List<JSONObject> actions = JSONUtil.getObjectArray("actions", object);
        List<JSONObject> events = JSONUtil.getObjectArray("events", object);

        if(properties != null){
            for(JSONObject property : properties){
                InteractionPatternBackup pattern = InteractionPatternBackup.create(property, InteractionPatternBackup.PID);
                thing.properties.put(pattern.id, pattern);
            }
        }
        if(actions != null){
            for(JSONObject action : actions){
                InteractionPatternBackup pattern = InteractionPatternBackup.create(action, InteractionPatternBackup.AID);
                thing.actions.put(pattern.id, pattern);
            }
        }

        if(events != null){
            for(JSONObject event : events){
                InteractionPatternBackup pattern = new InteractionPatternBackup(JSONUtil.getString(InteractionPatternBackup.EID, event), null, null);
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
        for (Map.Entry<String, InteractionPatternBackup> entry : properties.entrySet()) {
            String pid = entry.getKey();
            InteractionPatternBackup pattern = entry.getValue();
            System.out.println("    pid: ["+pid+"]");
            System.out.println("      pattern pid: ["+pattern.id+"]");
            System.out.println("      read: ["+pattern.readEndpoint+"]");
            System.out.println("      write: ["+pattern.writeEndpoint+"]");
        }
        System.out.println("    actions:");
        for (Map.Entry<String, InteractionPatternBackup> entry : actions.entrySet()) {
            String pid = entry.getKey();
            InteractionPatternBackup pattern = entry.getValue();
            System.out.println("    aid: ["+pid+"]");
            System.out.println("      pattern aid: ["+pattern.id+"]");
            System.out.println("      read: ["+pattern.readEndpoint+"]");
            System.out.println("      write: [" + pattern.writeEndpoint + "]");
        }
        System.out.println("    events:");
        for (Map.Entry<String, InteractionPatternBackup> entry : events.entrySet()) {
            String pid = entry.getKey();
            InteractionPatternBackup pattern = entry.getValue();
            System.out.println("    eid: ["+pid+"]");
            System.out.println("      pattern eid: ["+pattern.id+"]");
        }

    }

}
