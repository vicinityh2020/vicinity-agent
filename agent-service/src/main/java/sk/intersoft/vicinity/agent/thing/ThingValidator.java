package sk.intersoft.vicinity.agent.thing;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ThingValidator {
    public boolean failOnError;
    public ThingDescription thing = null;
    public List<String> errors = new ArrayList<String>();

    public ThingValidator(boolean failOnError){
        this.failOnError = failOnError;
    }

    public String identify(String id, JSONObject object) {
        if(id != null) return "["+id+"]";
        return object.toString();
    }

    public boolean error(String error) throws Exception {
        String encode = error.replaceAll("\"", "\'");
        errors.add(encode);
        if(failOnError) throw new Exception(encode);
        return true;
    }

    public boolean failed(){
        return (errors.size() > 0);
    }

    public JSONObject failure() {
        JSONObject out = new JSONObject();
        JSONArray list = new JSONArray();
        for(String error : errors) {
            list.put(error);
        }
        out.put("errors", list);
        return out;
    }

}
