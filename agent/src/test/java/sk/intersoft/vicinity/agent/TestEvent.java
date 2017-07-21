package sk.intersoft.vicinity.agent;


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
import org.json.JSONObject;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.MediaType;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;
import sk.intersoft.vicinity.agent.adapter.AgentAdapter;
import sk.intersoft.vicinity.agent.config.AgentConfig;
import sk.intersoft.vicinity.agent.config.BasicAuthConfig;

import java.io.File;

public class TestEvent {

    public void hard1() throws Exception {
        try{
            String endpoint = "http://138.201.156.73:8181/api/objects/235ad597-008a-41eb-9d94-3efe646e37f2/events/test";

            String login = "test_vcnt0";
            String password = "0VicinityTestUser0";


            System.out.println("httpc endpoint: "+endpoint);

            CredentialsProvider provider = new BasicCredentialsProvider();
            UsernamePasswordCredentials credentials
                    = new UsernamePasswordCredentials(login, password);
            provider.setCredentials(AuthScope.ANY, credentials);

            HttpClient client = HttpClientBuilder.create()
                    .setDefaultCredentialsProvider(provider)
                    .build();


            HttpPost  request = new HttpPost(endpoint);

//            request.addHeader("Accept", "application/json");
//            request.addHeader("Content-Type", "application/json");

            StringEntity data = new StringEntity("{}");

            request.setEntity(data);

            HttpResponse response = client.execute(request);


            int status = response.getStatusLine().getStatusCode();
            String content = EntityUtils.toString(response.getEntity());


            System.out.println("> POST EVENT STATUS: "+status);
            System.out.println("> POST EVENT RESPONSE: "+content);

        }
        catch(Exception e){
            System.out.println("WTF");
            e.printStackTrace();
        }


    }


    public void hard() throws Exception {
        try{
            String endpoint = "http://138.201.156.73:8181/api/objects/235ad597-008a-41eb-9d94-3efe646e37f2/events/load";

            String login = "test_vcnt0";
            String password = "0VicinityTestUser0";


            System.out.println("endpoint: "+endpoint);



            ClientResource resource = new ClientResource(endpoint);
            resource.setChallengeResponse(ChallengeScheme.HTTP_BASIC, login, password);

            StringRepresentation content = new StringRepresentation("{\"ja\": \"ne!\"}", MediaType.APPLICATION_JSON);
            resource.post(content, MediaType.APPLICATION_JSON);
            System.out.println("> POST EVENT STATUS: "+resource.getStatus());
            System.out.println("> POST EVENT RESPONSE: "+resource.getResponse().getEntity().getText());

        }
        catch(Exception e){
            System.out.println("WTF");
            e.printStackTrace();
        }


    }

    public void test() throws Exception {

        AgentAdapter adapter = new AgentAdapter("http://localhost:9994/adapter");

        String postData = "<xml><a>xx</a></xml>";
        adapter.post("/objects/service/events/load", postData);

    }


    public void simulateAdapterEvent() throws Exception {
        try{
            String endpoint = "http://localhost:9994/adapter/objects/simulation/events/energy_reduction/publish";

            String login = "test_vcnt0";
            String password = "0VicinityTestUser0";


            System.out.println("endpoint: "+endpoint);



            ClientResource resource = new ClientResource(endpoint);
            resource.setChallengeResponse(ChallengeScheme.HTTP_BASIC, login, password);

            String aauString = "{\"Energy_reduction\":{\"value\":20,\"unit\":\"kW\"},\"Time_period\":{\"value\":1,\"unit\":\"h\"}}";
            JSONObject event = new JSONObject(aauString.toLowerCase());
            event.put("message", "and now look, how handsome Jason i am!");

            StringRepresentation content = new StringRepresentation(event.toString(), MediaType.APPLICATION_JSON);
            resource.post(content, MediaType.APPLICATION_JSON);
            System.out.println("> POST EVENT STATUS: "+resource.getStatus());
            System.out.println("> POST EVENT RESPONSE: "+resource.getResponse().getEntity().getText());

        }
        catch(Exception e){
            System.out.println("WTF");
            e.printStackTrace();
        }


    }

    public void simulateAgentEvent() throws Exception {
        try{
            String endpoint = "http://localhost:9997/agent/objects/servicex/events/load";

            String login = "test_vcnt0";
            String password = "0VicinityTestUser0";


            System.out.println("endpoint: "+endpoint);



            ClientResource resource = new ClientResource(endpoint);
            resource.setChallengeResponse(ChallengeScheme.HTTP_BASIC, login, password);

            StringRepresentation content = new StringRepresentation("{\"agent\": \"receive\"}", MediaType.APPLICATION_JSON);
            resource.post(content, MediaType.APPLICATION_JSON);
            System.out.println("> POST EVENT STATUS: "+resource.getStatus());
            System.out.println("> POST EVENT RESPONSE: "+resource.getResponse().getEntity().getText());

        }
        catch(Exception e){
            System.out.println("WTF");
            e.printStackTrace();
        }


    }

    public static void main(String[] args) throws Exception {
        TestEvent t = new TestEvent();
//        t.test();
//        t.hard();
//        t.hard1();
        t.simulateAdapterEvent();
//        t.simulateAgentEvent();
        // just for testing commit
        // just for testing commit again
    }


}
