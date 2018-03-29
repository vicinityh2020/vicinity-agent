package sk.intersoft.vicinity.agent.gateway;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.service.config.AgentConfig;
import sk.intersoft.vicinity.agent.service.resource.ResourceResponse;

public class GatewayAPIClient {
    final static Logger logger = LoggerFactory.getLogger(GatewayAPIClient.class.getName());


    // ENDPOINTS:
    public static final String LOGIN = "/objects/login";
    public static final String LOGOUT = "/objects/logout";

    public static final String CONFIGURATION = "/agents/"+AgentConfig.agentId+"/objects";

    public static final String CREATE = "/agents/"+AgentConfig.agentId+"/objects";
    public static final String UPDATE = "/agents/"+AgentConfig.agentId+"/objects";
    public static final String DELETE = "/agents/"+AgentConfig.agentId+"/objects/delete";

    public static HttpClient getClient(String login, String password) {

        CredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials
                = new UsernamePasswordCredentials(login, password);
        provider.setCredentials(AuthScope.ANY, credentials);

        logger.info("GTW API CREDENTIALS:");
        logger.info("login: ["+login+"]");
        logger.info("password: ["+password+"]");

        return HttpClientBuilder.create()
                .setDefaultCredentialsProvider(provider)
                .build();

    }

    public static HttpClient getClient() {
        return getClient(AgentConfig.agentId, AgentConfig.password);
    }

    public static void login(String login, String password) throws Exception {
        logger.info("doing login: ["+login+"]["+password+"]");
        get(LOGIN, login, password);
    }
    public static void logout(String login, String password) throws Exception {
        logger.info("doing logout: ["+login+"]["+password+"]");
        get(LOGOUT, login, password);
    }

    public static String get(String path, String login, String password) throws Exception {
        try{


            String callEndpoint = AgentConfig.gatewayAPIEndpoint + path;

            logger.info("GTW API GET:");
            logger.info("path: " + path);
            logger.info("endpoint: " + callEndpoint);


            HttpClient client = getClient(login, password);

            HttpGet request = new HttpGet(callEndpoint);

            request.addHeader("Content-Type", "application/json");

            HttpResponse response = client.execute(request);

            int status = response.getStatusLine().getStatusCode();
            logger.info("GET status: " + status);

            String responseContent = EntityUtils.toString(response.getEntity());
            logger.info("GTW API response: " + responseContent);


            return responseContent;
        }
        catch(Exception e){
            logger.error("", e);
            throw e;
        }

    }

    public static String get(String path) throws Exception {
        return get(path, AgentConfig.agentId, AgentConfig.password);
    }

    public static String post(String path, String payload) throws Exception {
        try{


            String callEndpoint = AgentConfig.gatewayAPIEndpoint + path;

            logger.info("GTW API POST:");
            logger.info("path: " + path);
            logger.info("endpoint: " + callEndpoint);
            logger.info("payload: " + payload);




            HttpClient client = getClient();


            HttpPost request = new HttpPost(callEndpoint);

            request.addHeader("Content-Type", "application/json");

            StringEntity data = new StringEntity(payload);

            request.setEntity(data);

            HttpResponse response = client.execute(request);

            int status = response.getStatusLine().getStatusCode();
            logger.info("POST status: " + status);

            String responseContent = EntityUtils.toString(response.getEntity());
            logger.info("GTW API response: " + responseContent);


            return responseContent;
        }
        catch(Exception e){
            logger.error("", e);
            throw e;
        }

    }

    public static String put(String path, String payload) throws Exception {
        try{

            String callEndpoint = AgentConfig.gatewayAPIEndpoint + path;

            logger.info("GTW API PUT:");
            logger.info("path: " + path);
            logger.info("endpoint: " + callEndpoint);
            logger.info("payload: " + payload);


            HttpClient client = getClient();


            HttpPut request = new HttpPut(callEndpoint);

            request.addHeader("Content-Type", "application/json");

            StringEntity data = new StringEntity(payload);

            request.setEntity(data);

            HttpResponse response = client.execute(request);

            int status = response.getStatusLine().getStatusCode();
            logger.info("PUT status: " + status);

            String responseContent = EntityUtils.toString(response.getEntity());
            logger.info("GTW API response: " + responseContent);


            return responseContent;
        }
        catch(Exception e){
            logger.error("", e);
            throw e;
        }

    }

}