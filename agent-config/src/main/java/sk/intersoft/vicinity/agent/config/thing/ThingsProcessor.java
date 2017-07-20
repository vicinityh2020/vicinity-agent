package sk.intersoft.vicinity.agent.config.thing;

import org.json.JSONArray;
import org.json.JSONObject;
import sk.intersoft.vicinity.agent.config.AgentConfig;
import sk.intersoft.vicinity.agent.config.BasicAuthConfig;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ThingsProcessor {


    public static JSONObject prepareRegistration() {
        try{
            System.out.println("PREPARING REGISTRATION DATA");


            BasicAuthConfig auth = (BasicAuthConfig)AgentConfig.auth;

            JSONObject data = new JSONObject();
            JSONArray objects = new JSONArray();
            data.put("adid", auth.login);
            data.put("thingDescription", objects);

            for (Map.Entry<String, ThingDescription> entry : AgentConfig.things.entrySet()) {
                ThingDescription thing = entry.getValue();

                JSONObject json = new JSONObject(thing.json.toString());
                JSONObject credentials = new JSONObject();
                credentials.put("name", thing.login);
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

    public static List<ThingDescription> process(JSONArray things) throws Exception {
        JSONArray mapping = new JSONArray();
        List<ThingDescription> list = new ArrayList<ThingDescription>();

        Iterator i = things.iterator();
        while(i.hasNext()){
            JSONObject thing = (JSONObject)i.next();
            list.add(ThingDescription.create(thing));
        }


        return list;

    }

}
