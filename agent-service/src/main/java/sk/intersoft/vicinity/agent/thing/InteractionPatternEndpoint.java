package sk.intersoft.vicinity.agent.thing;

import org.json.JSONObject;
import sk.intersoft.vicinity.agent.utils.Dump;
import sk.intersoft.vicinity.agent.utils.JSONUtil;

import java.util.List;

public class InteractionPatternEndpoint {
    public String href = null;
    public DataSchema output = null;
    public DataSchema input = null;

    // JSON keys
    public static final String HREF_KEY = "href";

    // link type
    public static final String READ = "read";
    public static final String WRITE = "write";



    public static InteractionPatternEndpoint create(JSONObject linkJSON,
                                                    String linkType,
                                                    ThingValidator validator) throws Exception {
        if(linkJSON != null){
            InteractionPatternEndpoint endpoint = new InteractionPatternEndpoint();
            try {
                endpoint.href = JSONUtil.getString(HREF_KEY, linkJSON);
                if (endpoint.href == null) validator.error("Missing [" + HREF_KEY + "] in ["+linkType+"] link: " + linkJSON.toString());

                JSONObject output = JSONUtil.getObject(DataSchema.OUTPUT_KEY, linkJSON);
                if (output == null)
                    validator.error("Missing [" + DataSchema.OUTPUT_KEY + "] in ["+linkType+"] link: " + linkJSON.toString());
                endpoint.output = DataSchema.create(output, validator);

                if (linkType.equals(InteractionPatternEndpoint.WRITE)) {
                    JSONObject input = JSONUtil.getObject(DataSchema.INPUT_KEY, linkJSON);
                    if (input == null)
                        validator.error("Missing [" + DataSchema.INPUT_KEY + "] in ["+linkType+"] link: " + linkJSON.toString());
                    endpoint.input = DataSchema.create(input, validator);
                }
            }
            catch(Exception e){
                validator.error("Unable to process link: "+linkJSON.toString());
            }
            return endpoint;
        }
        else return null;
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
