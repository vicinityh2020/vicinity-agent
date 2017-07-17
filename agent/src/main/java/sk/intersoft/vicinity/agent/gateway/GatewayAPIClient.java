package sk.intersoft.vicinity.agent.gateway;

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
import org.restlet.data.ChallengeScheme;
import org.restlet.data.MediaType;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;
import sk.intersoft.vicinity.agent.config.AgentConfig;
import sk.intersoft.vicinity.agent.config.BasicAuthConfig;
import sk.intersoft.vicinity.agent.config.ThingMapping;
import sk.intersoft.vicinity.agent.service.response.ServiceResponse;

import java.util.Map;

public class GatewayAPIClient {
    public static final String loginEndpoint = AgentConfig.gatewayAPIEndpoint+"/objects/login";
    public static final String logoutEndpoint = AgentConfig.gatewayAPIEndpoint+"/objects/logout";


    public static String post(String path, String payload){
        try{

            BasicAuthConfig auth = (BasicAuthConfig)AgentConfig.auth;
            String login = auth.login;
            String password = auth.password;

            System.out.println("GTW API POST:");
            System.out.println("endpoint: "+path);
            System.out.println("payload: "+payload);
            System.out.println("login/password: "+login+" / "+password);


            String endpoint = AgentConfig.gatewayAPIEndpoint + path;
            System.out.println("POST EVENT TO endpoint: "+endpoint);
            System.out.println("POST DATA:  "+payload);

            CredentialsProvider provider = new BasicCredentialsProvider();
            UsernamePasswordCredentials credentials
                    = new UsernamePasswordCredentials(login, password);
            provider.setCredentials(AuthScope.ANY, credentials);

            HttpClient client = HttpClientBuilder.create()
                    .setDefaultCredentialsProvider(provider)
                    .build();


            HttpPost request = new HttpPost(endpoint);

//            request.addHeader("Accept", "application/json");
            request.addHeader("Content-Type", "application/json");

            StringEntity data = new StringEntity("{\"test\": \"test x\"}");

            request.setEntity(data);

            HttpResponse response = client.execute(request);

            int status = response.getStatusLine().getStatusCode();
            String responseContent = EntityUtils.toString(response.getEntity());
            System.out.println("> POST EVENT STATUS: "+status);
            System.out.println("> POST EVENT RESPONSE: "+responseContent);



            return responseContent;
        }
        catch(Exception e){
            e.printStackTrace();
            return ServiceResponse.failure(e).toString();
        }

    }

    public static void logInOut(String login, String password, boolean in){
        try{
            System.out.println("LOG IN/OUT [login/password: "+login+" / "+password+"] :: [in: "+in+"][out: "+(!in)+"] ");

            String endpoint = loginEndpoint;
            if(!in) endpoint = logoutEndpoint;

            System.out.println("endpoint: "+endpoint);

            ClientResource resource = new ClientResource(endpoint);
            resource.setChallengeResponse(ChallengeScheme.HTTP_BASIC, login, password);
            resource.get();
            System.out.println("> LOG IN/OUT STATUS: "+resource.getStatus());
            System.out.println("> LOG IN/OUT RESPONSE: "+resource.getResponse().getEntity().getText());
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
