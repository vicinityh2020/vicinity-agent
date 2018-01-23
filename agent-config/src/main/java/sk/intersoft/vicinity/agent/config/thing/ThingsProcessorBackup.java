package sk.intersoft.vicinity.agent.config.thing;

import org.json.JSONArray;
import org.json.JSONObject;
import sk.intersoft.vicinity.agent.config.AgentConfigBackup;
import sk.intersoft.vicinity.agent.config.BasicAuthConfig;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ThingsProcessorBackup {


    public static JSONObject prepareRegistration() {
        try{
            System.out.println("PREPARING REGISTRATION DATA");


            BasicAuthConfig auth = (BasicAuthConfig) AgentConfigBackup.auth;

            JSONObject data = new JSONObject();
            JSONArray objects = new JSONArray();
            data.put("adid", System.getProperty("agent.id"));
            data.put("thingDescriptions", objects);

            for (Map.Entry<String, ThingDescriptionBackup> entry : AgentConfigBackup.things.entrySet()) {
                ThingDescriptionBackup thing = entry.getValue();

                JSONObject json = new JSONObject(thing.json.toString());
                json.put("name", json.getString("oid"));
                json.remove("oid");
                JSONObject credentials = new JSONObject();
                credentials.put("oid", thing.login);
                credentials.put("password", thing.password);
                json.put("credentials", credentials);

                objects.put(json);
            }

            return data;
        }
        catch(Exception e){
            System.out.println("UNABLE TO PREPARE REGISTRATION DATA");
            e.printStackTrace();
            return null;
        }
    }

    public static List<ThingDescriptionBackup> process(JSONArray things) throws Exception {
        JSONArray mapping = new JSONArray();
        List<ThingDescriptionBackup> list = new ArrayList<ThingDescriptionBackup>();

        Iterator i = things.iterator();
        while(i.hasNext()){
            JSONObject thing = (JSONObject)i.next();
            list.add(ThingDescriptionBackup.create(thing));
        }


        return list;

    }

}
