package sk.intersoft.vicinity.agent.thing;

import org.json.JSONObject;
import sk.intersoft.vicinity.agent.utils.Dump;
import sk.intersoft.vicinity.agent.utils.JSONUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InteractionPatternEndpoint {
    public String href = null;
    public DataSchema output = null;
    public DataSchema input = null;

    public String linkType = null;

    public Map<String, String> jsonExtension = new HashMap<String, String>();

    // JSON keys
    public static final String HREF_KEY = "href";

    // link type
    public static final String READ = "read";
    public static final String WRITE = "write";



    public static InteractionPatternEndpoint create(JSONObject linkJSON,
                                                    String linkType,
                                                    ThingValidator validator) throws Exception {
        if(linkJSON != null){
            boolean fail = false;

            InteractionPatternEndpoint endpoint = new InteractionPatternEndpoint();
            try {
                endpoint.href = JSONUtil.getString(HREF_KEY, linkJSON);
                if (endpoint.href == null) fail = validator.error("Missing [" + HREF_KEY + "] in ["+linkType+"] link: " + linkJSON.toString());

                JSONObject output = JSONUtil.getObject(DataSchema.OUTPUT_KEY, linkJSON);
                if (output == null){
                    fail = validator.error("Missing [" + DataSchema.OUTPUT_KEY + "] in ["+linkType+"] link: " + linkJSON.toString());
                }
                else {
                    endpoint.output = DataSchema.create(output, validator);
                    if(endpoint.output == null) fail = true;
                }

                if (linkType.equals(InteractionPatternEndpoint.WRITE)) {
                    JSONObject input = JSONUtil.getObject(DataSchema.INPUT_KEY, linkJSON);
                    if (input == null){
                        fail = validator.error("Missing [" + DataSchema.INPUT_KEY + "] in ["+linkType+"] link: " + linkJSON.toString());
                    }
                    else {
                        endpoint.input = DataSchema.create(input, validator);
                        if(endpoint.input == null) fail = true;
                    }
                }

                endpoint.linkType = linkType;
                if(fail){
                    validator.error("Unable to process ["+linkType+"] link: "+linkJSON.toString());
                    return null;
                }

            }
            catch(Exception e){
                validator.error("Unable to process link: " + linkJSON.toString());
                return null;
            }
            return endpoint;
        }
        else return null;
    }

    public static JSONObject readJSON(InteractionPatternEndpoint endpoint) throws Exception  {
        JSONObject object = new JSONObject();

        object.put(HREF_KEY, endpoint.href);
        object.put(DataSchema.OUTPUT_KEY, DataSchema.toJSON(endpoint.output));

        ThingDescription.addExtension(endpoint.jsonExtension, object);

        return object;
    }

    public static JSONObject writeJSON(InteractionPatternEndpoint endpoint) throws Exception  {
        JSONObject object = new JSONObject();

        object.put(HREF_KEY, endpoint.href);
        object.put(DataSchema.OUTPUT_KEY, DataSchema.toJSON(endpoint.output));
        object.put(DataSchema.INPUT_KEY, DataSchema.toJSON(endpoint.input));

        ThingDescription.addExtension(endpoint.jsonExtension, object);

        return object;
    }

    public String toString(int indent) {
        Dump dump = new Dump();

        dump.add("endpoint:", indent);
        dump.add("href: "+href, (indent + 1));
        if(input != null){
            dump.add("input: ", (indent + 1));
            dump.add(input.toString(indent + 2));
        }
        dump.add("output: ", (indent + 1));
        dump.add(output.toString(indent + 2));

        return dump.toString();
    }
}
