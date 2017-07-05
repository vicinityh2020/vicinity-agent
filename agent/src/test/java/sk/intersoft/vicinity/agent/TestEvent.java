package sk.intersoft.vicinity.agent;


import org.restlet.data.ChallengeScheme;
import org.restlet.data.MediaType;
import org.restlet.resource.ClientResource;
import sk.intersoft.vicinity.agent.adapter.AgentAdapter;
import sk.intersoft.vicinity.agent.config.AgentConfig;
import sk.intersoft.vicinity.agent.config.BasicAuthConfig;

import java.io.File;

public class TestEvent {
    public void hard() throws Exception {
        String endpoint = "http://3bedffc6.ngrok.io/api/objects/235ad597-008a-41eb-9d94-3efe646e37f2/events/load";

        String login = "test_vcnt0";
        String password = "0VicinityTestUser0";


        System.out.println("endpoint: "+endpoint);

        ClientResource resource = new ClientResource(endpoint);
        resource.setChallengeResponse(ChallengeScheme.HTTP_BASIC, login, password);
        resource.post("{}", MediaType.APPLICATION_JSON);
        System.out.println("> POST EVENT STATUS: "+resource.getStatus());
        System.out.println("> POST EVENT RESPONSE: "+resource.getResponse().getEntity().getText());

    }

    public void test() throws Exception {

        AgentAdapter adapter = new AgentAdapter("http://localhost:9994/adapter");

        String postData = "<xml><a>xx</a></xml>";
        adapter.post("/objects/service/events/load", postData);

    }


    public static void main(String[] args) throws Exception {
        TestEvent t = new TestEvent();
//        t.test();
        t.hard();
    }


}
