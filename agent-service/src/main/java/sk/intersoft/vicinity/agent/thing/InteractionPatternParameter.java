package sk.intersoft.vicinity.agent.thing;

import org.json.JSONObject;
import sk.intersoft.vicinity.agent.utils.Dump;
import sk.intersoft.vicinity.agent.utils.JSONUtil;

public class InteractionPatternParameter {
    public static final String UNITS_KEY = "units";
    public static final String DATATYPE_KEY = "datatype";


    public String units;
    public String datatype;



    public static InteractionPatternParameter create(JSONObject parameterJSON) throws Exception {
        InteractionPatternParameter parameter = new InteractionPatternParameter();

        parameter.units = JSONUtil.getString(UNITS_KEY, parameterJSON);
        if(parameter.units == null) throw new Exception("Missing ["+UNITS_KEY+"] in: "+parameterJSON.toString());

        parameter.datatype = JSONUtil.getString(DATATYPE_KEY, parameterJSON);

        return parameter;


    }


    public String toString(int indent) {
        Dump dump = new Dump();

        dump.add("parameter:", indent);
        dump.add("units: "+units, (indent + 1));
        dump.add("data-type: "+datatype, (indent + 1));

        return dump.toString();
    }

}

