package sk.intersoft.vicinity.agent.thing;

import org.json.JSONObject;
import sk.intersoft.vicinity.agent.utils.Dump;
import sk.intersoft.vicinity.agent.utils.JSONUtil;

public class ThingLocation {
    public String id;
    public String className;
    public String label;

    public ThingLocation(String id, String className, String label) {
        this.id = id;
        this.className = className;
        this.label = label;
    }

    private static String TYPE_KEY = "location_type";
    private static String LABEL_KEY = "label";
    private static String ID_KEY = "location_id";

    public static ThingLocation create(JSONObject locationJSON, ThingValidator validator) throws Exception {

        boolean fail = false;
        try {
            String className = JSONUtil.getString(TYPE_KEY, locationJSON);
            if (className == null)
                fail = validator.error("Missing [" + TYPE_KEY + "] in location: " + locationJSON.toString());

            String label = JSONUtil.getString(LABEL_KEY, locationJSON);
            if (label == null)
                fail = validator.error("Missing [" + LABEL_KEY + "] in location: " + locationJSON.toString());

            String id = JSONUtil.getString(ID_KEY, locationJSON);

            if (fail) {
                validator.error("Unable to process location: " + locationJSON.toString());
                return null;
            }

            return new ThingLocation(id, className, label);


        } catch (Exception e) {
            validator.error("Unable to process location: " + locationJSON.toString());
            return null;
        }


    }

    public static JSONObject toJSON(ThingLocation location) throws Exception  {
        JSONObject object = new JSONObject();

        if(location.id != null){
            object.put(ID_KEY, location.id);
        }
        object.put(TYPE_KEY, location.className);
        object.put(LABEL_KEY, location.label);

        return object;
    }

    public String toString(int indent) {
        Dump dump = new Dump();

        dump.add("LOCATION:", indent);
        dump.add("id: " + id, (indent + 1));
        dump.add("type: " + className, (indent + 1));
        dump.add("label: " + label, (indent + 1));
        return dump.toString();
    }
}