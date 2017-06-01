package sk.intersoft.vicinity.agent.config;


import org.json.JSONObject;

public abstract class AuthConfig {

    public static final String AUTH_KEY = "auth";
    public static final String AUTH_TYPE_KEY = "type";
    public static final String BASIC_AUTH_KEY = "basic-auth";
    public static final String BASIC_AUTH_LOGIN_KEY = "login";
    public static final String BASIC_AUTH_PASSWORD_KEY = "password";

    public static AuthConfig create(JSONObject authConfig) throws Exception {
        String type = authConfig.getString(AUTH_TYPE_KEY).trim().toLowerCase();
        if(type.equals(BASIC_AUTH_KEY)){
            return new BasicAuthConfig(authConfig.getString(BASIC_AUTH_LOGIN_KEY), authConfig.getString(BASIC_AUTH_PASSWORD_KEY));
        }
        else {
            throw new Exception("Unknown auth type ["+type+"]");
        }
    }

    public abstract void show();

}
