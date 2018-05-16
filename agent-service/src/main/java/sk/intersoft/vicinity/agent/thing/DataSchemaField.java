package sk.intersoft.vicinity.agent.thing;

import org.json.JSONObject;
import sk.intersoft.vicinity.agent.utils.Dump;
import sk.intersoft.vicinity.agent.utils.JSONUtil;

public class DataSchemaField {
    public String name;
    public DataSchema schema;

    // JSON KEYS
    public static final String NAME_KEY = "name";
    public static final String SCHEMA_KEY = "schema";

    public static DataSchemaField create(JSONObject fieldJSON) throws Exception {
        DataSchemaField field = new DataSchemaField();
        field.name = JSONUtil.getString(NAME_KEY, fieldJSON);
        if (field.name == null) {
            throw new Exception("DataSchemaField: Missing [" + NAME_KEY + "] in: " + fieldJSON.toString());
        }

        JSONObject schema = JSONUtil.getObject(SCHEMA_KEY, fieldJSON);
        if(schema == null) {
            throw new Exception("DataSchemaField: Missing ["+SCHEMA_KEY+"] in: "+fieldJSON.toString());
        }
        field.schema = DataSchema.create(schema);

        return field;
    }

    public String toString(int indent) {
        Dump dump = new Dump();

        dump.add("field:", indent);
        dump.add("name: "+name, (indent + 2));
        dump.add("schema: ", (indent + 2));
        dump.add(schema.toString(indent + 3));
        return dump.toString();
    }

}
