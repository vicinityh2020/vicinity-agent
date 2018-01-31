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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.service.config.AgentConfig;
import sk.intersoft.vicinity.agent.service.resource.ResourceResponse;

public class GatewayAPIClient {
    final static Logger logger = LoggerFactory.getLogger(GatewayAPIClient.class.getName());

    public String endpoint = "";
    public static GatewayAPIClient gtwAPI = null;

    public GatewayAPIClient(String endpoint) {
        this.endpoint = endpoint;
    }


    public static GatewayAPIClient getInstance() {
        if (gtwAPI == null) {
            gtwAPI = new GatewayAPIClient(AgentConfig.gatewayAPIEndpoint);
            logger.info("GATEWAY API CLIENT CONFIGURED TO ENDPOINT: " + gtwAPI.endpoint);
        }
        return gtwAPI;
    }


    public String post(String path, String payload){
        try{

            String login = AgentConfig.login;
            String password = AgentConfig.password;

            String callEndpoint = endpoint + path;

            logger.info("GTW API POST:");
            logger.info("path: " + path);
            logger.info("endpoint: " + path);
            logger.info("payload: " + payload);
            logger.info("login: " + login);
            logger.info("password: " + password);




            CredentialsProvider provider = new BasicCredentialsProvider();
            UsernamePasswordCredentials credentials
                    = new UsernamePasswordCredentials(login, password);
            provider.setCredentials(AuthScope.ANY, credentials);

            HttpClient client = HttpClientBuilder.create()
                    .setDefaultCredentialsProvider(provider)
                    .build();


            HttpPost request = new HttpPost(endpoint);

            request.addHeader("Content-Type", "application/json");

            StringEntity data = new StringEntity(payload);

            request.setEntity(data);

            HttpResponse response = client.execute(request);
            logger.info("post executed");

            int status = response.getStatusLine().getStatusCode();
            logger.info("post status: " + status);

            String responseContent = EntityUtils.toString(response.getEntity());
            logger.info("GTW API response: " + responseContent);


            return responseContent;
        }
        catch(Exception e){
            logger.error("", e);
            return ResourceResponse.failure(e).toString();
        }

    }

}