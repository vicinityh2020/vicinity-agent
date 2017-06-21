package sk.intersoft.vicinity.agent.service;

import org.json.JSONArray;
import sk.intersoft.vicinity.agent.adapter.AgentAdapter;
import sk.intersoft.vicinity.agent.config.BasicAuthConfig;
import sk.intersoft.vicinity.agent.config.AgentConfig;
import sk.intersoft.vicinity.agent.config.thing.ThingDescription;
import sk.intersoft.vicinity.agent.config.thing.ThingsProcessor;
import sk.intersoft.vicinity.agent.gateway.GatewayAPIClient;

import java.util.List;
import java.util.Map;


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

            System.out.println("Starting sequence config:");
            AgentConfig.show();


            // 5. LOGIN AGENT VIA GTW API
            System.out.println("Login agent");
            BasicAuthConfig auth = (BasicAuthConfig)AgentConfig.auth;
            GatewayAPIClient.login(auth.login, auth.password);


            // 6. LOGIN DEVICES VIA GTW API
            System.out.println("Login things");
            for (Map.Entry<String, ThingDescription> entry : AgentConfig.things.entrySet()) {
                ThingDescription thing = entry.getValue();
                GatewayAPIClient.login(thing.login, thing.password);
            }

            System.out.println("Starting sequence completed!");

        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println("SOMETHING WENT APE IN STARTUP SEQUENCE!");
        }
    }

    public static void stop() {
        try{

            System.out.println("Launching shutting down sequence!");

            // 1. LOGOUT DEVICES VIA GTW API
            System.out.println("Logout things");
            for (Map.Entry<String, ThingDescription> entry : AgentConfig.things.entrySet()) {
                ThingDescription thing = entry.getValue();
                GatewayAPIClient.logout(thing.login, thing.password);
            }

            // 2. LOGIN AGENT VIA GTW API
            System.out.println("Logout agent");
            BasicAuthConfig auth = (BasicAuthConfig)AgentConfig.auth;
            GatewayAPIClient.logout(auth.login, auth.password);


            System.out.println("Shutdown sequence completed!");

        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println("SOMETHING WENT APE IN SHUTDOWN SEQUENCE!");
        }

    }
}
