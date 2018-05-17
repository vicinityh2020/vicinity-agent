package sk.intersoft.vicinity.agent.thing;

import org.json.JSONArray;
import org.json.JSONObject;
import sk.intersoft.vicinity.agent.utils.Dump;
import sk.intersoft.vicinity.agent.utils.JSONUtil;

import java.util.*;

public class DataSchema {
    public String type;
    public String description;
    public List<DataSchemaField> field= new ArrayList<DataSchemaField>();
    public DataSchema item;

    public static final String OBJECT = "object";
    public static final String ARRAY = "array";

    public static final String BOOLEAN = "boolean";
    public static final String INTEGER = "integer";
    public static final String DOUBLE = "double";
    public static final String STRING = "string";
    public static final Set<String> SIMPLE_TYPES =
            new HashSet<String>(
                    Arrays.asList(BOOLEAN, INTEGER, DOUBLE, STRING));

    // JSON KEYS
    public static final String INPUT_KEY = "input";
    public static final String OUTPUT_KEY = "output";


    public static final String TYPE_KEY = "type";
    public static final String DESCRIPTION_KEY = "description";
    public static final String FIELD_KEY = "field";
    public static final String ITEM_KEY = "item";

    public static boolean isObject(String type){
        return type.equals(DataSchema.OBJECT);
    }
    public boolean isObject(){
        return isObject(type);
    }

    public static boolean isArray(String type){
        return type.equals(DataSchema.ARRAY);
    }
    public boolean isArray(){
        return isArray(type);
    }

    public static boolean isSimpleType(String type){
        return DataSchema.SIMPLE_TYPES.contains(type);
    }
    public boolean isSimpleType(){
        return isSimpleType(type);
    }

    public static boolean isCorrectType(String type){
        return isObject(type) || isArray(type) || isSimpleType(type);
    }

    public static DataSchema create(JSONObject schemaJSON,
                                    ThingValidator validator) throws Exception {
        DataSchema schema = new DataSchema();
        try{
            boolean fail = false;

            schema.type = JSONUtil.getString(TYPE_KEY, schemaJSON);
            if(schema.type == null) {
                fail = validator.error("Missing ["+TYPE_KEY+"] in data-schema: "+schemaJSON.toString());
            }

            if(!isCorrectType(schema.type)){
                fail = validator.error("Unknown ["+TYPE_KEY+" : "+schema.type+"] in  data-schema: "+schemaJSON.toString());
            }

            schema.description = JSONUtil.getString(DESCRIPTION_KEY, schemaJSON);


            if(schema.isObject()){
                List<JSONObject> field = JSONUtil.getObjectArray(FIELD_KEY, schemaJSON);
                if(field == null) {
                    fail = validator.error("Missing ["+FIELD_KEY+"] in data-schema: "+schemaJSON.toString());
                }
                else {
                    if(field.isEmpty()) {
                        fail = validator.error("Empty ["+FIELD_KEY+"] array in data-schema: "+schemaJSON.toString());
                    }
                    else {
                        for(JSONObject f : field){
                            DataSchemaField processed = DataSchemaField.create(f, validator);
                            if(processed == null) fail = true;
                            else {
                                schema.field.add(processed);
                            }
                        }

                    }

                }

            }
            else if(schema.isArray()){
                JSONObject item = JSONUtil.getObject(ITEM_KEY, schemaJSON);
                if(item == null) {
                    fail = validator.error("Missing ["+ITEM_KEY+"] in data-schema: "+schemaJSON.toString());
                }
                else {
                    schema.item = DataSchema.create(item, validator);
                    if(schema.item == null) fail = true;
                }
            }

            if(fail){
                validator.error("Unable to process data-schema: "+schemaJSON.toString());
                return null;
            }

        }
        catch(Exception e){
            validator.error("Unable to process data-schema " + schemaJSON.toString());
            return null;
        }

        return schema;
    }

    public static JSONObject toJSON(DataSchema schema) {
        JSONObject object = new JSONObject();

        object.put(TYPE_KEY, schema.type);
        if(schema.description != null) {
            object.put(DESCRIPTION_KEY, schema.description);
        }

        if(schema.isObject()){
            JSONArray fields = new JSONArray();

            for(DataSchemaField f : schema.field){
                fields.put(DataSchemaField.toJSON(f));
            }
            object.put(FIELD_KEY, fields);

        }
        else if (schema.isArray()){
            object.put(ITEM_KEY, DataSchema.toJSON(schema.item));
        }

        return object;
    }

    public String toString(int indent) {
        Dump dump = new Dump();

        dump.add("data-schema:", indent);
        dump.add("type: "+type, (indent + 1));
        dump.add("description: "+description, (indent + 1));
        if(isObject()){
            dump.add("field: "+field.size(), (indent + 1));
            for(DataSchemaField f : field){
                dump.add(f.toString(indent + 2));
            }
        }
        if(isArray()){
            dump.add("item: ", (indent + 1));
            dump.add(item.toString(indent + 2));

        }

        return dump.toString();
    }

}
