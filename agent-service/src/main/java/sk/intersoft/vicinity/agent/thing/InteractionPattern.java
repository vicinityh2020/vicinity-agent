package sk.intersoft.vicinity.agent.thing;

import org.json.JSONObject;
import sk.intersoft.vicinity.agent.utils.Dump;
import sk.intersoft.vicinity.agent.utils.JSONUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InteractionPattern {

    public String id = null;
    public String refersTo = null;
    public DataSchema output = null;
    public InteractionPatternEndpoint readEndpoint = null;
    public InteractionPatternEndpoint writeEndpoint = null;

    public Map<String, String> jsonExtension = new HashMap<String, String>();

    // PATTERN TYPES
    public static final String PROPERTY = "property";
    public static final String ACTION = "action";
    public static final String EVENT = "event";


    // JSON keys
    public static final String PID_KEY = "pid";
    public static final String AID_KEY = "aid";
    public static final String EID_KEY = "eid";

    public static final String MONITORS_KEY = "monitors";
    public static final String AFFECTS_KEY = "affects";

    public static final String READ_LINK_KEY = "read_link";
    public static final String WRITE_LINK_KEY = "write_link";




    public static boolean createLinks(InteractionPattern pattern,
                                   JSONObject patternJSON,
                                   ThingValidator validator) throws Exception {
        JSONObject read = JSONUtil.getObject(READ_LINK_KEY, patternJSON);
        JSONObject write = JSONUtil.getObject(WRITE_LINK_KEY, patternJSON);


        boolean fail = false;

        if(read != null || write != null){
            if(read != null){
                pattern.readEndpoint = InteractionPatternEndpoint.create(read, InteractionPatternEndpoint.READ, validator);
                if(pattern.readEndpoint == null) fail = true;
            }
            if(write != null){
                pattern.writeEndpoint = InteractionPatternEndpoint.create(write, InteractionPatternEndpoint.WRITE, validator);
                if(pattern.writeEndpoint == null) fail = true;
            }
        }
        else {
            fail = validator.error("At least one of read_link/write_link must be defined in: "+patternJSON.toString());
        }


        return fail;
    }
    public static InteractionPattern createProperty(JSONObject patternJSON, ThingValidator validator) throws Exception {
        InteractionPattern pattern = new InteractionPattern();

        boolean fail = false;
        try{
            pattern.id = JSONUtil.getString(PID_KEY, patternJSON);
            if(pattern.id == null) fail = validator.error("Missing ["+PID_KEY+"] in property: "+patternJSON.toString());

            pattern.refersTo = JSONUtil.getString(MONITORS_KEY, patternJSON);
            if(pattern.refersTo == null) fail = validator.error("Missing ["+MONITORS_KEY+"] in property: "+patternJSON.toString());

            fail = createLinks(pattern, patternJSON, validator);

            if(fail){
                validator.error("Unable to process property: "+validator.identify(pattern.id, patternJSON));
                return null;
            }
        }
        catch(Exception e){
            validator.error("Unable to process property: "+validator.identify(pattern.id, patternJSON));
        }
        return pattern;

    }

    public static InteractionPattern createAction(JSONObject patternJSON, ThingValidator validator) throws Exception {
        InteractionPattern pattern = new InteractionPattern();

        boolean fail = false;
        try{
            pattern.id = JSONUtil.getString(AID_KEY, patternJSON);
            if(pattern.id == null) fail = validator.error("Missing ["+AID_KEY+"] in action: "+patternJSON.toString());

            pattern.refersTo = JSONUtil.getString(AFFECTS_KEY, patternJSON);
            if(pattern.refersTo == null) fail = validator.error("Missing ["+AFFECTS_KEY+"] in action: "+patternJSON.toString());

            fail = createLinks(pattern, patternJSON, validator);

            if(fail){
                validator.error("Unable to process action: "+validator.identify(pattern.id, patternJSON));
                return null;
            }

        }
        catch(Exception e){
            validator.error("Unable to process action: "+validator.identify(pattern.id, patternJSON));
        }

        return pattern;

    }

    public static InteractionPattern createEvent(JSONObject patternJSON, ThingValidator validator) throws Exception {
        InteractionPattern pattern = new InteractionPattern();

        boolean fail = false;
        try{
            pattern.id = JSONUtil.getString(EID_KEY, patternJSON);
            if(pattern.id == null) fail = validator.error("Missing ["+EID_KEY+"] in event: "+patternJSON.toString());

            pattern.refersTo = JSONUtil.getString(MONITORS_KEY, patternJSON);
            if(pattern.refersTo == null) fail = validator.error("Missing ["+MONITORS_KEY+"] in event: "+patternJSON.toString());

            JSONObject output = JSONUtil.getObject(DataSchema.OUTPUT_KEY, patternJSON);
            if(output == null) fail = validator.error("Missing ["+DataSchema.OUTPUT_KEY+"] in event: "+patternJSON.toString());

            pattern.output = DataSchema.create(output, validator);
            if(pattern.output == null) fail = true;

            if(fail){
                validator.error("Unable to process event: "+validator.identify(pattern.id, patternJSON));
                return null;
            }

        }
        catch(Exception e){
            validator.error("Unable to process event: "+validator.identify(pattern.id, patternJSON));
        }

        return pattern;

    }

    public static void addLinks(InteractionPattern pattern, JSONObject object) throws Exception  {
        if(pattern.readEndpoint != null){
            object.put(READ_LINK_KEY, InteractionPatternEndpoint.readJSON(pattern.readEndpoint));
        }
        if(pattern.writeEndpoint != null){
            object.put(WRITE_LINK_KEY, InteractionPatternEndpoint.writeJSON(pattern.writeEndpoint));
        }
    }



    public static JSONObject propertyJSON(InteractionPattern pattern) throws Exception  {
        JSONObject object = new JSONObject();

        object.put(PID_KEY, pattern.id);
        object.put(MONITORS_KEY, pattern.refersTo);
        addLinks(pattern, object);

        ThingDescription.addExtension(pattern.jsonExtension, object);

        return object;
    }
    public static JSONObject actionJSON(InteractionPattern pattern) throws Exception  {
        JSONObject object = new JSONObject();

        object.put(AID_KEY, pattern.id);
        object.put(AFFECTS_KEY, pattern.refersTo);
        addLinks(pattern, object);

        ThingDescription.addExtension(pattern.jsonExtension, object);

        return object;
    }

    public static JSONObject eventJSON(InteractionPattern pattern) throws Exception  {
        JSONObject object = new JSONObject();

        object.put(EID_KEY, pattern.id);
        object.put(MONITORS_KEY, pattern.refersTo);
        object.put(DataSchema.OUTPUT_KEY, DataSchema.toJSON(pattern.output));

        ThingDescription.addExtension(pattern.jsonExtension, object);

        return object;
    }

    public String toString(int indent) {
        Dump dump = new Dump();

        dump.add("INTERACTION PATTERN:", indent);
        dump.add("id: "+id, (indent + 1));
        dump.add("refers-to (monitors/affects): "+refersTo, (indent + 1));


        if(output != null){
            dump.add("OUTPUT: ", (indent + 1));
            dump.add(output.toString(indent + 2));
        }

        dump.add("ENDPOINTS: ", (indent + 1));
        if(readEndpoint != null){
            dump.add("read endpoint: ", (indent + 2));
            dump.add(readEndpoint.toString(indent + 3));
        }
        else {
            dump.add("read endpoint: unset", (indent + 2));
        }

        if(writeEndpoint != null){
            dump.add("write endpoint: ", (indent + 2));
            dump.add(writeEndpoint.toString(indent + 3));
        }
        else {
            dump.add("write endpoint: unset", (indent + 2));
        }

        return dump.toString();
    }
}
