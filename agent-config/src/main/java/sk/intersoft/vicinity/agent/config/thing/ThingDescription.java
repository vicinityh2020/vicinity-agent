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
    public Map<String, InteractionPattern> properties = new HashMap<String, InteractionPattern>();
    public Map<String, InteractionPattern> actions = new HashMap<String, InteractionPattern>();

    public ThingDescription(String infrastructureID) {
        this.infrastructureID = infrastructureID;
    }

    public static ThingDescription create(JSONObject object) throws Exception {
        String oid = JSONUtil.getString("oid", object);
        if(oid == null) throw new Exception("Missing oid in: "+object.toString());

        ThingDescription thing = new ThingDescription(oid);
        List<JSONObject> properties = JSONUtil.getObjectArray("properties", object);
        List<JSONObject> actions = JSONUtil.getObjectArray("actions", object);

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

        return thing;
    }

    public void show(){
        System.out.println("THING ["+oid+" / "+infrastructureID+"]:");
        System.out.println("properties:");
        for (Map.Entry<String, InteractionPattern> entry : properties.entrySet()) {
            String pid = entry.getKey();
            InteractionPattern pattern = entry.getValue();
            System.out.println("  pid: ["+pid+"]");
            System.out.println("    pattern pid: ["+pattern.id+"]");
            System.out.println("    read: ["+pattern.readEndpoint+"]");
            System.out.println("    write: ["+pattern.writeEndpoint+"]");
        }
        System.out.println("actions:");
        for (Map.Entry<String, InteractionPattern> entry : actions.entrySet()) {
            String pid = entry.getKey();
            InteractionPattern pattern = entry.getValue();
            System.out.println("  aid: ["+pid+"]");
            System.out.println("    pattern aid: ["+pattern.id+"]");
            System.out.println("    read: ["+pattern.readEndpoint+"]");
            System.out.println("    write: [" + pattern.writeEndpoint + "]");
        }

    }

}
