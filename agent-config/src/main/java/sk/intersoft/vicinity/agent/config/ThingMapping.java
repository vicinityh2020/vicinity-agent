package sk.intersoft.vicinity.agent.config;


import org.json.JSONObject;

import java.util.logging.Logger;

public class ThingMapping {
    private final static Logger LOGGER = Logger.getLogger(ThingMapping.class.getName());

    public String oid;
    public String infrastructureId;
    public String login;
    public String password;

    public ThingMapping(String oid, String infrastructureId, String login, String password){
        this.oid = oid;
        this.infrastructureId = infrastructureId;
        this.login = login;
        this.password = password;
    }

    public static ThingMapping create(JSONObject config) throws Exception {
        return new ThingMapping(
                config.getString("oid").trim(),
                config.getString("infrastructure-id").trim(),
                config.getString("login").trim(),
                config.getString("password").trim());
    }

    public void show(){
        System.out.println("  ThingMapping");
        System.out.println("    oid: "+oid);
        System.out.println("    infrastructure-id: "+infrastructureId);
        System.out.println("    login: "+login);
        System.out.println("    password: "+password);
    }

}
