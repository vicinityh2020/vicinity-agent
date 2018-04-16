package sk.intersoft.vicinity.agent.thing;

import org.json.JSONObject;
import sk.intersoft.vicinity.agent.utils.Dump;
import sk.intersoft.vicinity.agent.utils.JSONUtil;

public class InteractionPatternParameter {
    public static final String INPUT_KEY = "input";
    public static final String OUTPUT_KEY = "output";


    public String json;



    public static InteractionPatternParameter create(JSONObject parameterJSON) throws Exception {
        InteractionPatternParameter parameter = new InteractionPatternParameter();

        parameter.json = parameterJSON.toString();

        return parameter;


    }


    public String toString(int indent) {
        Dump dump = new Dump();

        dump.add("parameter:", indent);
        dump.add("json: "+json.toString(), (indent + 1));

        return dump.toString();
    }

}

