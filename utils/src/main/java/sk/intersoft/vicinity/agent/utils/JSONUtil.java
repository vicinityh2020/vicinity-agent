package sk.intersoft.vicinity.agent.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JSONUtil {

    public static List<String> getStringArray(String key, JSONObject object) throws Exception {
        List<String> result = new ArrayList<String>();
        try{
            if(object.has(key)) {
                Object value = object.get(key);
                if(value instanceof String) {
                    result.add(object.getString(key).trim());
                    return result;
                }
                else if(value instanceof JSONArray) {
                    Iterator i = object.getJSONArray(key).iterator();
                    while(i.hasNext()){
                        Object item = i.next();
                        if(item instanceof String){
                            result.add(((String)item).trim());
                        }
                        else throw new Exception("Missing or incorrect ["+key+"] in: "+object.toString());
                    }
                    return result;
                }
                else throw new Exception("Missing or incorrect ["+key+"] in: "+object.toString());
            }
            else{
                return null;
            }

        }
        catch(Exception e){
            throw e;
        }
    }

    public static String getString(String key, JSONObject object) throws Exception {
        List<String> array = getStringArray(key, object);
        if(array != null && array.size() == 1 && array.get(0) != null) return array.get(0);
        else if(array == null) return null;
        else throw new Exception("Missing or incorrect ["+key+"] in: "+object.toString());
    }

    public static boolean getBoolean(String key, JSONObject object) throws Exception {
        if(object.has(key)){
            try{
                return object.getBoolean(key);
            }
            catch(Exception e){
                String value = object.getString(key);
                if(value != null){
                    if(value.trim().equalsIgnoreCase("true")) return true;
                    else if(value.trim().equalsIgnoreCase("false")) return false;
                    else throw new Exception("Key ["+key+"] should be boolean in: "+object.toString());
                }
                else throw new Exception("Missing or incorrect ["+key+"] in: "+object.toString());
            }
        }
        else throw new Exception("Missing or incorrect ["+key+"] in: "+object.toString());
    }

    public static List<JSONObject> getObjectArray(String key, JSONObject object) throws Exception {
        List<JSONObject> result = new ArrayList<JSONObject>();
        try{
            if(object.has(key)) {
                Object value = object.get(key);
                if(value instanceof JSONObject) {
                    result.add(object.getJSONObject(key));
                    return result;
                }
                else if(value instanceof JSONArray) {
                    Iterator i = object.getJSONArray(key).iterator();
                    while(i.hasNext()){
                        Object item = i.next();
                        if(item instanceof JSONObject){
                            result.add((JSONObject)item);
                        }
                        else throw new Exception("Missing or incorrect ["+key+"] in: "+object.toString());
                    }
                    return result;
                }
                else throw new Exception("Missing or incorrect ["+key+"] in: "+object.toString());
            }
            else{
                return null;
            }

        }
        catch(Exception e){
            throw e;
        }
    }

    public static JSONObject getObject(String key, JSONObject object) throws Exception {
        List<JSONObject> array = getObjectArray(key, object);
        if(array != null && array.size() == 1 && array.get(0) != null) return array.get(0);
        else if(array == null) return null;
        else throw new Exception("Missing or incorrect ["+key+"] in: "+object.toString());
    }
}
