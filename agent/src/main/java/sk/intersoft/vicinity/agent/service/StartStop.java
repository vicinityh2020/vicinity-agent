package sk.intersoft.vicinity.agent.service;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import sk.intersoft.vicinity.agent.adapter.AgentAdapter;
import sk.intersoft.vicinity.agent.config.BasicAuthConfig;
import sk.intersoft.vicinity.agent.config.AgentConfig;
import sk.intersoft.vicinity.agent.config.thing.ThingDescription;
import sk.intersoft.vicinity.agent.config.thing.ThingsProcessor;
import sk.intersoft.vicinity.agent.gateway.GatewayAPIClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class StartStop  {

//    public static void testRegister(String data){
//
//        try{
//            String endpoint = "http://vicinity.bavenir.eu:3000/commServer/registration";
//
//            System.out.println("testing registration baypass to: "+endpoint);
//
//            HttpClient client = HttpClientBuilder.create()
//                    .build();
//
//
//            HttpPost request = new HttpPost(endpoint);
//
//            request.addHeader("Accept", "application/json");
//            request.addHeader("Content-Type", "application/json");
//
//            StringEntity entity = new StringEntity(data);
//
//            request.setEntity(entity);
//
//            HttpResponse response = client.execute(request);
//
//            int status = response.getStatusLine().getStatusCode();
//            String responseContent = EntityUtils.toString(response.getEntity());
//            System.out.println("> REGISTER STATUS: "+status);
//            System.out.println("> REGISTER  RESPONSE: "+responseContent);
//
//
//        }
//        catch(Exception e){
//            e.printStackTrace();
//        }
//    }

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

            List<ThingDescription> things = new ArrayList<ThingDescription>();
            try{
                things = ThingsProcessor.process(new JSONArray(data));
            }
            catch(Exception e){
                System.out.println("COULD NOT PROCESS THING DESCRIPTIONS .. TREATING AS EMPTY");
            }


            // 4. ADD THINGS TO CONFIG + ASSIGN FAKE IDS
            AgentConfig.configureThings(things);

            System.out.println("Starting sequence config:");
            AgentConfig.show();


            // 5. LOGIN AGENT VIA GTW API
            System.out.println("Login agent");
            BasicAuthConfig auth = (BasicAuthConfig)AgentConfig.auth;
            GatewayAPIClient.login(auth.login, auth.password);


            // 6. DO DYNAMIC REGISTRATION IF CONFIGURED
            String register = System.getProperty("register.on.startup");
            if(register != null && register.trim().equalsIgnoreCase("true")){
                System.out.println("REGISTERING DEVICES TO SERVER WITH: ");
                JSONObject registration = ThingsProcessor.prepareRegistration();
                if(registration != null){
                    System.out.println(registration.toString(2));

                    GatewayAPIClient.register(registration.toString());
                }
                else{
                    System.out.println("EMPTY REGISTRATION STRING .. DO NOTHING");

                }
            }
            else{
                System.out.println("NOT REGISTERING DEVICES TO SERVER");
            }

            // 7. LOGIN DEVICES VIA GTW API
            System.out.println("Login things");
            for (Map.Entry<String, ThingDescription> entry : AgentConfig.things.entrySet()) {
                ThingDescription thing = entry.getValue();
                System.out.println("Login thing: ["+thing.login+" / "+thing.password+"]");
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
