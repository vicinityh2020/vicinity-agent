package sk.intersoft.vicinity.agent.config;

import org.json.JSONArray;
import org.json.JSONObject;
import sk.intersoft.vicinity.agent.config.thing.InteractionPatternBackup;
import sk.intersoft.vicinity.agent.config.thing.ThingDescriptionBackup;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;

public class AgentConfigBackup {
    private final static Logger LOGGER = Logger.getLogger(AgentConfigBackup.class.getName());

    private static final String GATEWAY_API_ENDPOINT_KEY = "gateway-api-endpoint";
    private static final String ADAPTER_ENDPOINT_KEY = "adapter-endpoint";
    private static final String THINGS = "things";
    private static final String THING_MAPPING_KEY = "thing-mapping";

    public static AuthConfig auth = null;

    public static String gatewayAPIEndpoint = "";
    public static String adapterEndpoint = "";
    public static Map<String, ThingDescriptionBackup> things = new HashMap<String, ThingDescriptionBackup>();
    public static Map<String, ThingMapping> thingMapping = new HashMap<String, ThingMapping>();



    public static String getInfrastructureId(String oid) {
        ThingMapping object = thingMapping.get(oid);
        if(object != null) return object.infrastructureId;
        else return null;
    }

    public static String getOid(String infrastructureId) {
        for (Map.Entry<String, ThingMapping> entry : thingMapping.entrySet()) {
            ThingMapping mapping = entry.getValue();
            if(mapping.infrastructureId.equals(infrastructureId)){
                return mapping.oid;
            }
        }
        return null;
    }

    public static ThingMapping getMapping(String infrastructureId) {
        for (Map.Entry<String, ThingMapping> entry : thingMapping.entrySet()) {
            ThingMapping mapping = entry.getValue();
            if(mapping.infrastructureId.equals(infrastructureId)){
                return mapping;
            }
        }
        return null;
    }

    public static InteractionPatternBackup getInteractionPattern(String oid, String pid, String patternType) throws Exception {
        if(oid == null) throw new Exception("Missing OID");
        if(pid == null) throw new Exception("Missing Interaction pattern ID");

        ThingDescriptionBackup thing = things.get(oid);
        if(thing == null) throw new Exception("Missing thing for OID: "+oid);

        InteractionPatternBackup pattern = null;
        if(patternType.equals(InteractionPatternBackup.PROPERTY)){
            pattern = thing.properties.get(pid);
        }
        else if(patternType.equals(InteractionPatternBackup.ACTION)){
            pattern = thing.actions.get(pid);
        }
        else if(patternType.equals(InteractionPatternBackup.EVENT)){
            pattern = thing.events.get(pid);
        }

        if(pattern == null) throw new Exception("Missing interaction pattern ["+patternType+"] thing for OID: "+oid+" / pattern id: "+pid);

        return pattern;

    }

    public static String getReadHref(String oid, String pid, String patternType) throws Exception {
        return getInteractionPattern(oid, pid, patternType).readEndpoint;

    }
    public static String getWriteHref(String oid, String pid, String patternType) throws Exception {
        return getInteractionPattern(oid, pid, patternType).writeEndpoint;

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

        // CREATE DEMO FAKE OBJECT ID MAP
        if(config.has(THING_MAPPING_KEY)){
            JSONArray fakeMapping = config.getJSONArray(THING_MAPPING_KEY);
            Iterator<Object> i = fakeMapping.iterator();
            while(i.hasNext()){
                ThingMapping mapping = ThingMapping.create((JSONObject) i.next());
                thingMapping.put(mapping.oid, mapping);
            }
        }
    }


    public static void configureThings(List<ThingDescriptionBackup> list) throws Exception {
        for (ThingDescriptionBackup thing : list) {
            ThingMapping mapping = getMapping(thing.infrastructureID);
            if(mapping != null){
                thing.oid = mapping.oid;
                thing.login = mapping.login;
                thing.password = mapping.password;
                things.put(thing.oid, thing);
            }
        }
    }

    public static void show() {
        System.out.println("AGENT CONFIG CREATED: ");
        if (auth != null) {
            auth.show();
        } else {
            System.out.println("no authentication info");
        }

        System.out.println("GatewayAPI Endpoint: " + gatewayAPIEndpoint);
        System.out.println("Adapter Endpoint: " + adapterEndpoint);
        System.out.println("Thing mapping: ");
        for (Map.Entry<String, ThingMapping> entry : thingMapping.entrySet()) {
            System.out.println("[" + entry.getKey() + "]: ");
            entry.getValue().show();
        }

        System.out.println("Things: ");
        for (Map.Entry<String, ThingDescriptionBackup> entry : things.entrySet()) {
            System.out.println("  [" + entry.getKey() + "]: ");
            entry.getValue().show();
        }

    }
}
