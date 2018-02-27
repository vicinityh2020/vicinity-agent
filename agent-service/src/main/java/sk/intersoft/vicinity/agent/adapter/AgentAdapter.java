package sk.intersoft.vicinity.agent.adapter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.service.config.AdapterConfig;
import sk.intersoft.vicinity.agent.service.config.AgentConfig;

public class AgentAdapter {
    final static Logger logger = LoggerFactory.getLogger(AgentAdapter.class.getName());

    public static String get(String endpoint) throws Exception {
        try{


            logger.info("agent call endpoint: " + endpoint);
            HttpClient client = HttpClientBuilder.create()
                    .build();

            HttpGet request = new HttpGet(endpoint);
            HttpResponse response = client.execute(request);

            int status = response.getStatusLine().getStatusCode();
            String content = EntityUtils.toString(response.getEntity());

            HttpEntity entity = response.getEntity();
            logger.info("agent get status: " + status);
            logger.info("agent get entity: " + content);

            return content;

        }
        catch(Exception e){
            logger.error("", e);
            throw e;
        }

    }

    public static String post(String endpoint, String payload) throws Exception {
        try{

            logger.info("POST ENDPOINT: "+endpoint);
            logger.info("POST DATA: "+payload);
            HttpClient client = HttpClientBuilder.create()
                    .build();

            HttpPost request = new HttpPost(endpoint);

            request.addHeader("Accept", "application/json");
            request.addHeader("Content-Type", "application/json");

            StringEntity data = new StringEntity(payload);

            request.setEntity(data);

            HttpResponse response = client.execute(request);


            int status = response.getStatusLine().getStatusCode();
            String content = EntityUtils.toString(response.getEntity());

            logger.info("agent action status: " + status);
            logger.info("agent action entity content: " + content);

            return content;

        }
        catch(Exception e){
            logger.error("", e);
            throw e;
        }

    }

    public static String put(String endpoint, String payload) throws Exception {
        try{

            logger.info("PUT ENDPOINT: "+endpoint);
            logger.info("PUT DATA: "+payload);
            HttpClient client = HttpClientBuilder.create()
                    .build();

            HttpPut request = new HttpPut(endpoint);

            request.addHeader("Accept", "application/json");
            request.addHeader("Content-Type", "application/json");

            StringEntity data = new StringEntity(payload);

            request.setEntity(data);

            HttpResponse response = client.execute(request);


            int status = response.getStatusLine().getStatusCode();
            String content = EntityUtils.toString(response.getEntity());

            HttpEntity entity = response.getEntity();
            logger.info("agent action status: " + status);
            logger.info("agent action entity: " + content);

            return content;

        }
        catch(Exception e){
            logger.error("", e);
            throw e;
        }

    }


}
