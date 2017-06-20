package sk.intersoft.vicinity.agent.service;

import org.json.JSONArray;
import sk.intersoft.vicinity.agent.adapter.AgentAdapter;
import sk.intersoft.vicinity.agent.config.BasicAuthConfig;
import sk.intersoft.vicinity.agent.config.AgentConfig;
import sk.intersoft.vicinity.agent.config.thing.ThingDescription;
import sk.intersoft.vicinity.agent.config.thing.ThingsProcessor;
import sk.intersoft.vicinity.agent.gateway.GatewayAPIClient;

import java.util.List;


public class StartStop  {
    public static void start() {
        System.out.println("Launching starting sequence!");
        try{
            // START SEQUENCE:
            // 1. READ AGENT CONFIG
            AgentConfig.create(System.getProperty("config.file"));

            // 2. CREATE ADAPTER CLIENT
            AgentAdapter.create(AgentConfig.adapterEndpoint);

            // 3. READ ADAPTER OBJECTS
            String data = AgentAdapter.getInstance().get("/objects");
            List<ThingDescription> things = ThingsProcessor.process(new JSONArray(data));


            // 4. ADD THINGS TO CONFIG + ASSIGN FAKE IDS
            AgentConfig.configureThings(things);


//            GatewayAPIClient.loginAgent();
//            BasicAuthConfig auth = (BasicAuthConfig) AgentConfig.auth;
//            GatewayAPIClient.relogin(auth.login, auth.password);

            System.out.println("Starting sequence complete with config:");
            AgentConfig.show();

        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println("SOMETHING WENT APE BY INITIALIZATION!");
        }
    }

    public static void stop() {
        System.out.println("Launching shutting down sequence!");
//        GatewayAPIClient.logout();
//        BasicAuthConfig auth = (BasicAuthConfig) AgentConfig.auth;
//        GatewayAPIClient.logout(auth.login, auth.password);
    }
}
