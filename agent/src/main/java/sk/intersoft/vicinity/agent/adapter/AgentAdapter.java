package sk.intersoft.vicinity.agent.adapter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.restlet.resource.ClientResource;

import java.util.logging.Logger;

public class AgentAdapter {
    private final static Logger LOGGER = Logger.getLogger(AgentAdapter.class.getName());

    String endpoint = "";
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
//            ClientResource resource = new ClientResource(callEndpoint);
//
//            LOGGER.info("ADAPTER CALL: [" + callEndpoint+"]");
//
//            resource.get();
//
//            LOGGER.info("> ADAPTER CALL STATUS: " + resource.getStatus());
//
//            String content = resource.getResponse().getEntity().getText();
//            LOGGER.info("> ADAPTER CALL RESPONSE: " + content);
//            return content;


            HttpClient client = HttpClientBuilder.create()
                    .build();

            HttpGet request = new HttpGet(callEndpoint);
            HttpResponse response = client.execute(request);

            int status = response.getStatusLine().getStatusCode();
            String content = EntityUtils.toString(response.getEntity());

            HttpEntity entity = response.getEntity();
            LOGGER.info("agent get status: " + status);
            LOGGER.info("agent get entity: " + content);

            return content;

        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }

    }


    public String post(String path, String payload) throws Exception {
        try{
            String callEndpoint = endpoint + path;



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

            HttpEntity entity = response.getEntity();
            LOGGER.info("agent action status: " + status);
            LOGGER.info("agent action entity: " + content);

            return content;

        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }

    }

}
