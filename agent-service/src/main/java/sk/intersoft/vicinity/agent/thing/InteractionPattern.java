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

    public static final String READ_LINK_KEY = "read_link";
    public static final String WRITE_LINK_KEY = "write_link";





    public static void createLinks(InteractionPattern pattern, JSONObject patternJSON) throws Exception {
        JSONObject read = JSONUtil.getObject(READ_LINK_KEY, patternJSON);
        JSONObject write = JSONUtil.getObject(WRITE_LINK_KEY, patternJSON);


        if(read != null || write != null){
            pattern.readEndpoint = InteractionPatternEndpoint.create(read, InteractionPatternEndpoint.READ);
            pattern.writeEndpoint = InteractionPatternEndpoint.create(write, InteractionPatternEndpoint.WRITE);
        }
        else {
            throw new Exception("Missing or wrong configuration of read_link/write_link in: "+patternJSON.toString());
        }
    }
    public static InteractionPattern createProperty(JSONObject patternJSON) throws Exception {
        InteractionPattern pattern = new InteractionPattern();

        pattern.id = JSONUtil.getString(PID_KEY, patternJSON);
        if(pattern.id == null) throw new Exception("Missing ["+PID_KEY+"] in: "+patternJSON.toString());

        pattern.refersTo = JSONUtil.getString(MONITORS_KEY, patternJSON);
        if(pattern.refersTo == null) throw new Exception("Missing ["+MONITORS_KEY+"] in: "+patternJSON.toString());

        createLinks(pattern, patternJSON);
        return pattern;

    }

    public static InteractionPattern createAction(JSONObject patternJSON) throws Exception {
        InteractionPattern pattern = new InteractionPattern();

        pattern.id = JSONUtil.getString(AID_KEY, patternJSON);
        if(pattern.id == null) throw new Exception("Missing ["+AID_KEY+"] in: "+patternJSON.toString());

        pattern.refersTo = JSONUtil.getString(AFFECTS_KEY, patternJSON);
        if(pattern.refersTo == null) throw new Exception("Missing ["+AFFECTS_KEY+"] in: "+patternJSON.toString());

        createLinks(pattern, patternJSON);
        return pattern;

    }

    public static InteractionPattern createEvent(JSONObject patternJSON) throws Exception {
        InteractionPattern pattern = new InteractionPattern();


        pattern.id = JSONUtil.getString(EID_KEY, patternJSON);
        if(pattern.id == null) throw new Exception("Missing ["+EID_KEY+"] in: "+patternJSON.toString());

        pattern.refersTo = JSONUtil.getString(MONITORS_KEY, patternJSON);
        if(pattern.refersTo == null) throw new Exception("Missing ["+MONITORS_KEY+"] in: "+patternJSON.toString());

        JSONObject output = JSONUtil.getObject(InteractionPatternParameter.OUTPUT_KEY, patternJSON);
//        if(output == null) throw new Exception("Missing ["+InteractionPatternParameter.OUTPUT_KEY+"] in: "+patternJSON.toString());
        if(output == null) output =  new JSONObject();
        pattern.output = InteractionPatternParameter.create(output);

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
