package sk.intersoft.vicinity.agent;

import org.restlet.Context;
import org.restlet.data.ChallengeScheme;
import org.restlet.resource.ClientResource;
import sk.intersoft.vicinity.agent.config.AgentConfig;
import sk.intersoft.vicinity.agent.gateway.GatewayAPIClient;

import java.util.Map;

public class TestAgent {

    public static final String AGENT_ENDPOINT = "http://160.40.206.250:9997/agent";
//    public static final String AGENT_ENDPOINT = "http://localhost:9997/agent";
    public static final String LOGIN = "test_vcnt1";
    public static final String PASSWORD = "1VicinityTestUser1";

    public void login() throws Exception {
        try{
            System.out.println("LOGIN");
            GatewayAPIClient.relogin(LOGIN, PASSWORD);
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

    public void callProperty() throws Exception {
        login();
        try{
            String oid = "0D485748-CF2A-450C-BCF6-02AC1CB39A2D".toLowerCase();
            String endpoint = AGENT_ENDPOINT+"/objects/"+oid+"/properties/PowerConsumption";
            System.out.println("GET PROPERTY: "+endpoint);

            ClientResource resource = new ClientResource(endpoint);
            resource.setChallengeResponse(ChallengeScheme.HTTP_BASIC, LOGIN, PASSWORD);
            resource.get();
            System.out.println("> STATUS: "+resource.getStatus());
            System.out.println("> RESPONSE: " + resource.getResponse().getEntity().getText());

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        TestAgent c = new TestAgent();
//        c.callProperty();

        System.out.println("0D485748-CF2A-450C-BCF6-02AC1CB39A2D".toLowerCase());
        System.out.println("0D485748-CF2A-450C-BCF6-02AC1CB39A2D:6");

        System.out.println("D77EC6B0-F039-4734-925E-0A90CE7D1B5B".toLowerCase().toLowerCase());
        System.out.println("D77EC6B0-F039-4734-925E-0A90CE7D1B5B:0184A96B:CO2");


    }

}
