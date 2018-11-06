package sk.intersoft.vicinity.agent.clients;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
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

    public static ClientResponse delete(String endpoint) throws Exception {
        try{
            logger.info("DELETE ENDPOINT: " + endpoint);
            HttpClient client = getClient();

            HttpDelete request = new HttpDelete(endpoint);
            HttpResponse response = client.execute(request);

            int status = response.getStatusLine().getStatusCode();
            String reason = response.getStatusLine().getReasonPhrase();
            String content = EntityUtils.toString(response.getEntity());

            logger.info("agent DELETE status: " + status);
            logger.info("agent DELETE response: " + content);

            return new ClientResponse(status, reason, content);

        }
        catch(Exception e){
            logger.error("", e);
            throw e;
        }

    }

    public static ClientResponse get(String endpoint) throws Exception {
        try{
            logger.info("GET ENDPOINT: " + endpoint);
            HttpClient client = getClient();

            HttpGet request = new HttpGet(endpoint);
            request.addHeader("Content-Type", "application/json; charset=utf-8");
            HttpResponse response = client.execute(request);

            int status = response.getStatusLine().getStatusCode();
            String reason = response.getStatusLine().getReasonPhrase();
            String content = EntityUtils.toString(response.getEntity());

            logger.info("agent GET status: " + status);
            logger.info("agent GET response: " + content);

            return new ClientResponse(status, reason, content);

        }
        catch(Exception e){
            logger.error("", e);
            throw e;
        }

    }

    public static ClientResponse post(String endpoint, String payload) throws Exception {
        try{
            logger.info("POST ENDPOINT: " + endpoint);
            logger.info("POST DATA: \n"+payload);

            HttpClient client = getClient();

            HttpPost request = new HttpPost(endpoint);

            request.addHeader("Accept", "application/json");
            request.addHeader("Content-Type", "application/json");

            StringEntity data = new StringEntity(payload, "utf-8");

            request.setEntity(data);

            HttpResponse response = client.execute(request);

            int status = response.getStatusLine().getStatusCode();
            String reason = response.getStatusLine().getReasonPhrase();
            String content = EntityUtils.toString(response.getEntity());

            logger.info("agent POST status: " + status);
            logger.info("agent POST response: " + content);

            return new ClientResponse(status, reason, content);

        }
        catch(Exception e){
            logger.error("", e);
            throw e;
        }

    }

    public static ClientResponse put(String endpoint, String payload) throws Exception {
        try{
            logger.info("PUT ENDPOINT: " + endpoint);
            logger.info("PUT DATA: \n"+payload);

            HttpClient client = getClient();

            HttpPut request = new HttpPut(endpoint);

            request.addHeader("Accept", "application/json");
            request.addHeader("Content-Type", "application/json");

            StringEntity data = new StringEntity(payload, "utf-8");

            request.setEntity(data);

            HttpResponse response = client.execute(request);

            int status = response.getStatusLine().getStatusCode();
            String reason = response.getStatusLine().getReasonPhrase();
            String content = EntityUtils.toString(response.getEntity());

            logger.info("agent PUT status: " + status);
            logger.info("agent PUT response: " + content);

            return new ClientResponse(status, reason, content);

        }
        catch(Exception e){
            logger.error("", e);
            throw e;
        }

    }

}