package sk.intersoft.vicinity.agent.service.config;

import org.json.JSONObject;

import java.io.File;
import java.util.Scanner;
import java.util.logging.Logger;

public class AgentConfig {
    private final static Logger LOGGER = Logger.getLogger(AgentConfig.class.getName());

    private static final String CREDENTIALS_KEY = "credentials";
    private static final String LOGIN_KEY = "login";
    private static final String PASSWORD_KEY = "password";

    private static final String GATEWAY_API_ENDPOINT_KEY = "gateway-api-endpoint";
    private static final String ADAPTER_ENDPOINT_KEY = "adapter-endpoint";


    public static String login = "";
    public static String password = "";

    public static String gatewayAPIEndpoint = "";
    public static String adapterEndpoint = "";


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
        JSONObject credentials = config.getJSONObject(CREDENTIALS_KEY);
        login = credentials.getString(LOGIN_KEY);
        password = credentials.getString(PASSWORD_KEY);

        gatewayAPIEndpoint = config.getString(GATEWAY_API_ENDPOINT_KEY);
        adapterEndpoint = config.getString(ADAPTER_ENDPOINT_KEY);
    }

    public static void show() {
        System.out.println("AGENT CONFIG CREATED: ");

        System.out.println("Credentials: ");
        System.out.println("> login: [" + login + "]");
        System.out.println("> password: [" + password + "]");
        System.out.println("GatewayAPI Endpoint: " + gatewayAPIEndpoint);
        System.out.println("Adapter Endpoint: " + adapterEndpoint);
    }

}
