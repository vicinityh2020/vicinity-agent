package sk.intersoft.vicinity.agent.gateway;

import org.restlet.data.ChallengeScheme;
import org.restlet.resource.ClientResource;
import sk.intersoft.vicinity.agent.config.AgentConfig;
import sk.intersoft.vicinity.agent.config.BasicAuthConfig;
import sk.intersoft.vicinity.agent.config.ThingMapping;

import java.util.Map;

public class GatewayAPIClient {
    public static final String loginEndpoint = AgentConfig.gatewayAPIEndpoint+"/objects/login";
    public static final String logoutEndpoint = AgentConfig.gatewayAPIEndpoint+"/objects/logout";

    public static void logInOut(String login, String password, boolean in){
        try{
            System.out.println("LOG IN/OUT [login/password: "+login+" / "+password+"] :: [in: "+in+"][out: "+(!in)+"] ");

            String endpoint = loginEndpoint;
            if(!in) endpoint = logoutEndpoint;

            System.out.println("endpoint: "+endpoint);

//            ClientResource resource = new ClientResource(endpoint);
//            resource.setChallengeResponse(ChallengeScheme.HTTP_BASIC, login, password);
//            resource.get();
//            System.out.println("> LOG IN/OUT STATUS: "+resource.getStatus());
//            System.out.println("> LOG IN/OUT RESPONSE: "+resource.getResponse().getEntity().getText());
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

    public static void login(String login, String password) throws Exception {
        logInOut(login, password, true);
    }
    public static void logout(String login, String password) throws Exception {
        logInOut(login, password, false);
    }

}
