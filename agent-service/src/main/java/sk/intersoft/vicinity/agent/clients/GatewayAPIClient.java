package sk.intersoft.vicinity.agent.clients;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.service.config.Configuration;

public class GatewayAPIClient {
    final static Logger logger = LoggerFactory.getLogger(GatewayAPIClient.class.getName());


    // ENDPOINTS:
    public static final String LOGIN_ENDPOINT = "/objects/login";
    public static final String LOGOUT_ENDPOINT = "/objects/logout";

    // interactions:
    public static final String OBJECT_PROPERTY_ENDPOINT = "/objects/{oid}/properties/{pid}";
    public static final String OBJECT_ACTION_ENDPOINT = "/objects/{oid}/actions/{aid}";
    public static final String OBJECT_ACTION_TASK_ENDPOINT = "/objects/{oid}/actions/{aid}/tasks/{tid}";


    // configuration:
    public static String configurationEndpoint(String agentId) {
      return "/agents/"+agentId+"/objects";
    }

    public static String createEndpoint(String agentId) {
        return "/agents/"+agentId+"/objects";
    }

    public static String updateEndpoint(String agentId) {
        return "/agents/"+agentId+"/objects";
    }

    public static String deleteEndpoint(String agentId) {
        return "/agents/"+agentId+"/objects/delete";
    }

    public static String getInteractionEndpoint(String endpoint, String oid, String patternId, String taskId) {
        return endpoint.replaceAll("\\{oid\\}", oid).replaceAll("\\{pid\\}", patternId).replaceAll("\\{aid\\}", patternId).replaceAll("\\{eid\\}", patternId).replaceAll("\\{tid\\}", taskId);
    }

    public static String getInteractionEndpoint(String endpoint, String oid, String patternId) {
        return getInteractionEndpoint(endpoint, oid, patternId, "");
    }

    public static String getOpenEventChannelEndpoint(String eventId) {
        return "/events/"+eventId;
    }
    public static String getSubscribeEventChannelEndpoint(String oid, String eventId) {
        return "/objects/"+oid+"/events/"+eventId;
    }
    public static String getPublishEventEndpoint(String eventId) {
        return "/events/"+eventId;
    }

    public static HttpClient getClient(String login, String password) {

        CredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials
                = new UsernamePasswordCredentials(login, password);
        provider.setCredentials(AuthScope.ANY, credentials);

//        logger.info("GTW API CALL CREDENTIALS:");
//        logger.info("login: ["+login+"]");
//        logger.info("password: ["+password+"]");

        return HttpClientBuilder.create()
                .setDefaultCredentialsProvider(provider)
                .build();

    }

    public static void login(String login, String password) throws Exception {
        logger.info("doing login: ["+login+"]["+password+"]");
        get(LOGIN_ENDPOINT, login, password);
    }

    public static void logout(String login, String password) throws Exception {
        logger.info("doing logout: ["+login+"]["+password+"]");
        get(LOGOUT_ENDPOINT, login, password);
    }

    public static String get(String path, String login, String password) throws Exception {
        try{


            String callEndpoint = Configuration.gatewayAPIEndpoint + path;

            logger.info("GTW API GET:");
            logger.info("path: " + path);
            logger.info("endpoint: " + callEndpoint);
            logger.info("credentials: ");
            logger.info("login: " + login);
            logger.info("password: " + password);


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

    public static String delete(String path, String login, String password) throws Exception {
        try{


            String callEndpoint = Configuration.gatewayAPIEndpoint + path;

            logger.info("GTW API DELETE:");
            logger.info("path: " + path);
            logger.info("endpoint: " + callEndpoint);
            logger.info("credentials: ");
            logger.info("login: " + login);
            logger.info("password: " + password);


            HttpClient client = getClient(login, password);

            HttpDelete request = new HttpDelete(callEndpoint);

            request.addHeader("Content-Type", "application/json");

            HttpResponse response = client.execute(request);

            int status = response.getStatusLine().getStatusCode();
            logger.info("DELETE status: " + status);

            String responseContent = EntityUtils.toString(response.getEntity());
            logger.info("GTW API response: " + responseContent);


            return responseContent;
        }
        catch(Exception e){
            logger.error("", e);
            throw e;
        }

    }

    public static String post(String path, String payload, String login, String password) throws Exception {
        try{


            String callEndpoint = Configuration.gatewayAPIEndpoint + path;

            logger.info("GTW API POST:");
            logger.info("path: " + path);
            logger.info("endpoint: " + callEndpoint);
            logger.info("payload: " + payload);
            logger.info("credentials: ");
            logger.info("login: " + login);
            logger.info("password: " + password);




            HttpClient client = getClient(login, password);


            HttpPost request = new HttpPost(callEndpoint);

            request.addHeader("Content-Type", "application/json");

            if(payload != null){
                StringEntity data = new StringEntity(payload);
                request.setEntity(data);
            }


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

    public static String put(String path, String payload, String login, String password) throws Exception {
        try{


            String callEndpoint = Configuration.gatewayAPIEndpoint + path;

            logger.info("GTW API PUT:");
            logger.info("path: " + path);
            logger.info("endpoint: " + callEndpoint);
            logger.info("payload: " + payload);
            logger.info("credentials: ");
            logger.info("login: " + login);
            logger.info("password: " + password);




            HttpClient client = getClient(login, password);


            HttpPut request = new HttpPut(callEndpoint);

            request.addHeader("Content-Type", "application/json");

            if(payload != null){
                StringEntity data = new StringEntity(payload);
                request.setEntity(data);
            }


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