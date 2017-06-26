package sk.intersoft.vicinity.agent;

import org.restlet.Context;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.MediaType;
import org.restlet.resource.ClientResource;
import sk.intersoft.vicinity.agent.config.AgentConfig;
import sk.intersoft.vicinity.agent.gateway.GatewayAPIClient;

import java.util.Map;

public class TestAgent {

//    public static final String AGENT_ENDPOINT = "http://160.40.206.250:9997/agent";
//    public static final String AGENT_ENDPOINT = "http://localhost:9997/agent";
//    public static final String AGENT_ENDPOINT = "http://localhost:9996/aau-adapter";
    public static final String AGENT_ENDPOINT = "http://localhost:9997/agent";
    public static final String LOGIN = "test_vcnt1";
    public static final String PASSWORD = "1VicinityTestUser1";


    public void callProperty() throws Exception {
        try{
//            String oid = "0D485748-CF2A-450C-BCF6-02AC1CB39A2D".toLowerCase();


//            String endpoint = AGENT_ENDPOINT+"/objects/"+oid+"/properties/DeviceStatus";
//            System.out.println("GET PROPERTY: "+endpoint);

//            String endpoint = AGENT_ENDPOINT+"/objects/"+oid+"/actions/Keket";
//            System.out.println("ACTION ENDPOINT: "+endpoint);

            String oid = "test_vcnt1";
            String pid = "device-1-pid-1";


            String endpoint = AGENT_ENDPOINT+"/objects/"+oid+"/properties/"+pid;
            System.out.println("GET PROPERTY: "+endpoint);


            ClientResource getResource = new ClientResource(endpoint);
            getResource.get();
            System.out.println("> STATUS: "+getResource.getStatus());
            System.out.println("> RESPONSE: " + getResource.getResponse().getEntity().getText());



            System.out.println("SET PROPERTY: "+endpoint);

            ClientResource setResource = new ClientResource(endpoint);
            setResource.put("{\"value\": true}");
            System.out.println("> STATUS: "+setResource.getStatus());
            System.out.println("> RESPONSE: " + setResource.getResponse().getEntity().getText());



            String aid = "switch1";
            endpoint = AGENT_ENDPOINT+"/objects/"+oid+"/actions/"+aid;
            System.out.println("EXEC ACTION: "+endpoint);

            ClientResource actionResource = new ClientResource(endpoint);
            actionResource.post("{\"x\": \"y\"}");
            System.out.println("> STATUS: "+actionResource.getStatus());
            System.out.println("> RESPONSE: " + actionResource.getResponse().getEntity().getText());


            endpoint = AGENT_ENDPOINT+"/objects/"+oid+"/actions/"+aid+"/tasks/x";
            ClientResource getTaskResource = new ClientResource(endpoint);
            getTaskResource.get();
            System.out.println("> STATUS: "+getTaskResource.getStatus());
            System.out.println("> RESPONSE: " + getTaskResource.getResponse().getEntity().getText());


        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        TestAgent c = new TestAgent();
        c.callProperty();

//        System.out.println("0D485748-CF2A-450C-BCF6-02AC1CB39A2D".toLowerCase());
//        System.out.println("0D485748-CF2A-450C-BCF6-02AC1CB39A2D:6");
//
//        System.out.println("D77EC6B0-F039-4734-925E-0A90CE7D1B5B".toLowerCase().toLowerCase());
//        System.out.println("D77EC6B0-F039-4734-925E-0A90CE7D1B5B:0184A96B:CO2");


    }

}
