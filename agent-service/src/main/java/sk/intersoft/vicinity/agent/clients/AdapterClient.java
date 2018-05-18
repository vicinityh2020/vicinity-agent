package sk.intersoft.vicinity.agent.clients;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdapterClient {
    final static Logger logger = LoggerFactory.getLogger(AdapterClient.class.getName());

    // ENDPOINTS
    public static final String OBJECTS_ENDPOINT = "/objects";

    public static String objectsEndpoint(String endpoint){
        return endpoint + OBJECTS_ENDPOINT;
    }

    public static HttpClient getClient() throws Exception {
        return HttpClientBuilder.create().build();

    }

    public static String get(String endpoint) throws Exception {
        try{
            logger.info("GET ENDPOINT: " + endpoint);
            HttpClient client = getClient();

            HttpGet request = new HttpGet(endpoint);
            HttpResponse response = client.execute(request);

            int status = response.getStatusLine().getStatusCode();
            String content = EntityUtils.toString(response.getEntity());

            logger.info("agent GET status: " + status);
            logger.info("agent GET response: " + content);

            return content;

        }
        catch(Exception e){
            logger.error("", e);
            throw e;
        }

    }

}