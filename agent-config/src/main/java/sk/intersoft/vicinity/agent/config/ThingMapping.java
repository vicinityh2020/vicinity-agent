package sk.intersoft.vicinity.agent.config;


import org.json.JSONObject;

import java.util.logging.Logger;

public class ThingMapping {
    private final static Logger LOGGER = Logger.getLogger(ThingMapping.class.getName());

    public final static String OID_KEY = "oid";
    public final static String INFRASTRUCTURE_ID_KEY = "infrastructure-id";

    public String oid = "";
    public String infrastructureId = "";

    public ThingMapping(String oid, String infrastructureId){
        this.oid = oid;
        this.infrastructureId = infrastructureId;
    }

    public static ThingMapping create(JSONObject config) throws Exception {
        return new ThingMapping(config.getString(OID_KEY).trim(), config.getString(INFRASTRUCTURE_ID_KEY).trim());
    }

    public void show(){
        System.out.println("  ThingMapping");
        System.out.println("    oid: "+oid);
        System.out.println("    infrastructure-id: "+infrastructureId);
    }

}
