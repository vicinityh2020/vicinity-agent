package sk.intersoft.vicinity.agent.config;


import org.json.JSONObject;

import java.util.logging.Logger;

public class ObjectConfig {
    private final static Logger LOGGER = Logger.getLogger(ObjectConfig.class.getName());

    public final static String OID_KEY = "oid";
    public final static String INFRASTRUCTURE_ID_KEY = "infrastructure-id";

    public String oid = "";
    public String infrastructureId = "";

    public ObjectConfig(String oid, String infrastructureId){
        this.oid = oid;
        this.infrastructureId = infrastructureId;
    }

    public static ObjectConfig create(JSONObject config) throws Exception {
        return new ObjectConfig(config.getString(OID_KEY).trim(), config.getString(INFRASTRUCTURE_ID_KEY).trim());
    }

    public void show(){
        LOGGER.info("ObjectConfig");
        LOGGER.info("oid: "+oid);
        LOGGER.info("infrastructure-id: "+infrastructureId);
    }

}
