package sk.intersoft.vicinity.agent.service.config;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.utils.Dump;

import java.io.File;
import java.util.Scanner;

public class AgentConfig {
    final static Logger logger = LoggerFactory.getLogger(AgentConfig.class.getName());

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
            logger.error("", e);
            return null;
        }
    }

    public static void create(String configPath) throws Exception {
        JSONObject config = new JSONObject(file2string(configPath));
        logger.info("CREATING CONFIG FILE FROM: \n"+config.toString(2));
        JSONObject credentials = config.getJSONObject(CREDENTIALS_KEY);
        login = credentials.getString(LOGIN_KEY);
        password = credentials.getString(PASSWORD_KEY);

        gatewayAPIEndpoint = config.getString(GATEWAY_API_ENDPOINT_KEY);
        adapterEndpoint = config.getString(ADAPTER_ENDPOINT_KEY);
    }

    public static String asString() {
        Dump dump = new Dump();

        dump.add("AGENT CONFIG CREATED: ", 0);

        dump.add("Credentials: ", 1);
        dump.add("login: [" + login + "]", 2);
        dump.add("password: [" + password + "]", 2);
        dump.add("GatewayAPI Endpoint: " + gatewayAPIEndpoint, 1);
        dump.add("Adapter Endpoint: " + adapterEndpoint, 1);

        return dump.toString();
    }

}
