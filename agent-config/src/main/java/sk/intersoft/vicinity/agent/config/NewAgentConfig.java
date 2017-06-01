package sk.intersoft.vicinity.agent.config;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;

public class NewAgentConfig {
    private final static Logger LOGGER = Logger.getLogger(NewAgentConfig.class.getName());

    private static final String GATEWAY_API_ENDPOINT_KEY = "gateway-api-endpoint";
    private static final String ADAPTER_ENDPOINT_KEY = "adapter-endpoint";
    private static final String OBJECTS_KEY = "objects";

    public static AuthConfig auth = null;

    public static String gatewayAPIEndpoint = "";
    public static String adapterEndpoint = "";
    public static Map<String, ObjectConfig> objects = new HashMap<String, ObjectConfig>();


    public static String objectInfrastructureId(String oid) {
        ObjectConfig object = objects.get(oid);
        if(object != null) return object.infrastructureId;
        else return null;
    }


    public static String file2string(String path) {
        try{
            return new Scanner(new File(path)).useDelimiter("\\Z").next();
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static void create(String configPath) throws Exception {
        JSONObject config = new JSONObject(file2string(configPath));
        LOGGER.info("CONFIG FILE: \n"+config.toString(2));
        if(config.has(AuthConfig.AUTH_KEY)){
            auth = AuthConfig.create(config.getJSONObject(AuthConfig.AUTH_KEY));
        }
        gatewayAPIEndpoint = config.getString(GATEWAY_API_ENDPOINT_KEY);
        adapterEndpoint = config.getString(ADAPTER_ENDPOINT_KEY);

        JSONArray deviceConfigs = config.getJSONArray(OBJECTS_KEY);
        Iterator<Object> i = deviceConfigs.iterator();
        while(i.hasNext()){
            ObjectConfig deviceConfig = ObjectConfig.create((JSONObject) i.next());
            objects.put(deviceConfig.oid, deviceConfig);
        }
    }

    public static void show() {
        if (auth != null) {
            auth.show();
        } else {
            LOGGER.info("no authentication info");
        }

        LOGGER.info("GatewayAPI Endpoint: " + gatewayAPIEndpoint);
        LOGGER.info("Adapter Endpoint: " + adapterEndpoint);
        LOGGER.info("Objects: ");
        for (Map.Entry<String, ObjectConfig> entry : objects.entrySet()) {
            LOGGER.info("[" + entry.getKey() + "]: ");
            entry.getValue().show();
        }
    }
}
