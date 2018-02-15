package sk.intersoft.vicinity.agent.thing;

import org.json.JSONObject;
import sk.intersoft.vicinity.agent.utils.Dump;
import sk.intersoft.vicinity.agent.utils.JSONUtil;

import java.util.List;

public class InteractionPattern {

    public String id = null;
    public String refersTo = null;
    public InteractionPatternParameter output = null;
    public InteractionPatternEndpoint readEndpoint = null;
    public InteractionPatternEndpoint writeEndpoint = null;


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

    public static final String OUTPUT_KEY = "output";

    public static final String READ_LINKS_KEY = "read_links";
    public static final String WRITE_LINKS_KEY = "write_links";
    public static final String LINKS_KEY = "links";

    // JSON-LD keys
    public static final String OBSERVES_KEY = "observes";
    public static final String FOR_PROPERTY_KEY = "forProperty";



    public static InteractionPatternParameter createOutput(JSONObject patternJSON) throws Exception {
        List<JSONObject> outputs = JSONUtil.getObjectArray(OUTPUT_KEY, patternJSON);
        if(outputs != null && outputs.size() > 0) return InteractionPatternParameter.create(outputs.get(0));
        else return null;
    }

    public static void createLinks(InteractionPattern pattern, JSONObject patternJSON) throws Exception {
        List<JSONObject> links = JSONUtil.getObjectArray(LINKS_KEY, patternJSON);
        List<JSONObject> reads = JSONUtil.getObjectArray(READ_LINKS_KEY, patternJSON);
        List<JSONObject> writes = JSONUtil.getObjectArray(WRITE_LINKS_KEY, patternJSON);


        if(reads != null || writes != null){
            pattern.readEndpoint = InteractionPatternEndpoint.create(reads);
            pattern.writeEndpoint = InteractionPatternEndpoint.create(writes);
        }
        else if(links != null){
            pattern.readEndpoint = InteractionPatternEndpoint.create(links);
            pattern.writeEndpoint = InteractionPatternEndpoint.create(links);
        }
        else {
            throw new Exception("Missing or wrong configuration of links/read_links/write_links in: "+patternJSON.toString());
        }
    }
    public static InteractionPattern createProperty(JSONObject patternJSON, boolean isConfiguration) throws Exception {
        InteractionPattern pattern = new InteractionPattern();

        if(isConfiguration){
            String observes = JSONUtil.getString(OBSERVES_KEY, patternJSON);
            if(observes == null) throw new Exception("Missing ["+OBSERVES_KEY+"] in: "+patternJSON.toString());

            patternJSON.put(MONITORS_KEY, ThingDescription.prefixed2value(observes));
        }

        pattern.id = JSONUtil.getString(PID_KEY, patternJSON);
        if(pattern.id == null) throw new Exception("Missing ["+PID_KEY+"] in: "+patternJSON.toString());

        pattern.refersTo = JSONUtil.getString(MONITORS_KEY, patternJSON);
        if(pattern.refersTo == null) throw new Exception("Missing ["+MONITORS_KEY+"] in: "+patternJSON.toString());

        pattern.output = createOutput(patternJSON);
        createLinks(pattern, patternJSON);
        return pattern;

    }

    public static InteractionPattern createAction(JSONObject patternJSON, boolean isConfiguration) throws Exception {
        InteractionPattern pattern = new InteractionPattern();

        if(isConfiguration){
            String forProperty = JSONUtil.getString(FOR_PROPERTY_KEY, patternJSON);
            if(forProperty == null) throw new Exception("Missing ["+FOR_PROPERTY_KEY+"] in: "+patternJSON.toString());

            patternJSON.put(AFFECTS_KEY, ThingDescription.prefixed2value(forProperty));
        }

        pattern.id = JSONUtil.getString(AID_KEY, patternJSON);
        if(pattern.id == null) throw new Exception("Missing ["+AID_KEY+"] in: "+patternJSON.toString());

        pattern.refersTo = JSONUtil.getString(AFFECTS_KEY, patternJSON);
        if(pattern.refersTo == null) throw new Exception("Missing ["+AFFECTS_KEY+"] in: "+patternJSON.toString());

        pattern.output = createOutput(patternJSON);
        createLinks(pattern, patternJSON);
        return pattern;

    }

    public static InteractionPattern createEvent(JSONObject patternJSON, boolean isConfiguration) throws Exception {
        InteractionPattern pattern = new InteractionPattern();

        pattern.id = JSONUtil.getString(EID_KEY, patternJSON);
        if(pattern.id == null) throw new Exception("Missing ["+PID_KEY+"] in: "+patternJSON.toString());

        pattern.refersTo = JSONUtil.getString(MONITORS_KEY, patternJSON);
        if(pattern.refersTo == null) throw new Exception("Missing ["+MONITORS_KEY+"] in: "+patternJSON.toString());

        try{
            createLinks(pattern, patternJSON);
        }
        catch (Exception e){
        }
        return pattern;

    }

    public String toString(int indent) {
        Dump dump = new Dump();

        dump.add("INTERACTION PATTERN:", indent);
        dump.add("id: "+id, (indent + 1));
        dump.add("refers-to: "+refersTo, (indent + 1));


        if(output != null){
            dump.add("OUTPUT: ", (indent + 1));
            dump.add(output.toString(indent + 2));
        }
        else {
            dump.add("OUTPUT: unset", (indent + 1));
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
