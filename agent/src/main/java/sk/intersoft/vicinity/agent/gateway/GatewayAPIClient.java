package sk.intersoft.vicinity.agent.gateway;

import org.restlet.data.ChallengeScheme;
import org.restlet.resource.ClientResource;
import sk.intersoft.vicinity.agent.config.AgentConfig;
import sk.intersoft.vicinity.agent.config.ObjectConfig;

import java.util.Map;

public class GatewayAPIClient {
    static String ENDPOINT =  "http://160.40.206.1:8181/api";

    public static void objects(){
        try{
            System.out.println("GETTING OBJECTS");
            ClientResource resource = new ClientResource(ENDPOINT+"/objects");
            resource.setChallengeResponse(ChallengeScheme.HTTP_BASIC, "test_vcnt0", "0VicinityTestUser0");

            resource.get();
            System.out.println("> STATUS: "+resource.getStatus());
            System.out.println("> RESPONSE: "+resource.getResponse().getEntity().getText());
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

    public static void login(String service){
        try{
            System.out.println("LOGGING DEVICES");
            for (Map.Entry<String, ObjectConfig> entry : AgentConfig.objects.entrySet()){
                String oid = entry.getKey();
                System.out.println("LOGGING : "+oid);

                ClientResource resource = new ClientResource(ENDPOINT+"/objects/"+service);
                resource.setChallengeResponse(ChallengeScheme.HTTP_BASIC, oid, oid);

                resource.get();
                System.out.println("> STATUS: "+resource.getStatus());
                System.out.println("> RESPONSE: "+resource.getResponse().getEntity().getText());
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

    public static void relogin(String username, String password){
        try{
            System.out.println("LOGGING AGENT");
            System.out.println("LOGGING AS: "+username + " / "+password);

            System.out.println("LOGOUT FIRST: ");
            ClientResource logoutResource = new ClientResource(ENDPOINT+"/objects/logout");
            logoutResource.setChallengeResponse(ChallengeScheme.HTTP_BASIC, username, password);
            logoutResource.get();
            System.out.println("> STATUS: "+logoutResource.getStatus());
            System.out.println("> RESPONSE: "+logoutResource.getResponse().getEntity().getText());

            System.out.println("THEN LOGIN: ");
            ClientResource loginResource = new ClientResource(ENDPOINT+"/objects/login");
            loginResource.setChallengeResponse(ChallengeScheme.HTTP_BASIC, username, password);
            loginResource.get();
            System.out.println("> STATUS: "+loginResource.getStatus());
            System.out.println("> RESPONSE: "+loginResource.getResponse().getEntity().getText());
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

    public static void logout(String username, String password){
        try{
            System.out.println("LOGGING OFF ");
            System.out.println("AS: "+username + " / "+password);

            System.out.println("LOGOUT FIRST: ");
            ClientResource logoutResource = new ClientResource(ENDPOINT+"/objects/logout");
            logoutResource.setChallengeResponse(ChallengeScheme.HTTP_BASIC, username, password);
            logoutResource.get();
            System.out.println("> STATUS: "+logoutResource.getStatus());
            System.out.println("> RESPONSE: "+logoutResource.getResponse().getEntity().getText());

        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

    public static void login(){
        try{
            login("login");
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

    public static void logout(){
        try{
            login("logout");
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

}
