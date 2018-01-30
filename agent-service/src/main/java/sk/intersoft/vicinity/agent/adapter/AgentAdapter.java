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

public class AgentAdapter {
    final static Logger logger = LoggerFactory.getLogger(AgentAdapter.class.getName());

    public String endpoint = "";
    public static AgentAdapter adapter = null;

    public AgentAdapter(String endpoint){
        this.endpoint = endpoint;
    }


    public static void create(String endpoint){
        adapter = new AgentAdapter(endpoint);
    }

    public static AgentAdapter getInstance(){
        return adapter;
    }

    public String get(String path) throws Exception {
        try{
            String callEndpoint = endpoint + path;


            logger.info("agent call endpoint: " + callEndpoint);
            HttpClient client = HttpClientBuilder.create()
                    .build();

            HttpGet request = new HttpGet(callEndpoint);
            HttpResponse response = client.execute(request);

            int status = response.getStatusLine().getStatusCode();
            String content = EntityUtils.toString(response.getEntity());

            HttpEntity entity = response.getEntity();
            logger.info("agent get status: " + status);
            logger.info("agent get entity: " + content);

            return content;

        }
        catch(Exception e){
            e.printStackTrace();
            return "[]";
        }

    }


    public String post(String path, String payload) throws Exception {
        try{
            String callEndpoint = endpoint + path;



            logger.info("POST ENDPOINT: "+callEndpoint);
            logger.info("POST DATA: "+payload);
            HttpClient client = HttpClientBuilder.create()
                    .build();

            HttpPost request = new HttpPost(callEndpoint);

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
            e.printStackTrace();
            return null;
        }

    }

    public String put(String path, String payload) throws Exception {
        try{
            String callEndpoint = endpoint + path;



            logger.info("PUT ENDPOINT: "+callEndpoint);
            logger.info("PUT DATA: "+payload);
            HttpClient client = HttpClientBuilder.create()
                    .build();

            HttpPut request = new HttpPut(callEndpoint);

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
            e.printStackTrace();
            return null;
        }

    }


}
