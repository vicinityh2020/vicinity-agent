package sk.intersoft.vicinity.agent;


import sk.intersoft.vicinity.agent.adapter.AgentAdapter;
import sk.intersoft.vicinity.agent.config.AgentConfig;

import java.io.File;

public class TestAdapter {
    public void test() throws Exception {
        AgentConfig.create(new File("").getAbsolutePath() + "/agent/bin/agent-sitewhere-config.json");
        System.out.println("CONFIG : ");
        AgentConfig.show();

        AgentAdapter adapter = new AgentAdapter(AgentConfig.adapterEndpoint);


//        String oid = "0d485748-cf2a-450c-bcf6-02ac1cb39a2d";
//        String iid = AgentConfig.objectInfrastructureId(oid);
//        adapter.get("/objects/"+iid+"/properties/PowerConsumption");
//        adapter.get("/objects/0D485748-CF2A-450C-BCF6-02AC1CB39A2D:6/properties/PowerConsumption");

        adapter.get("/objects/d6e5acc3-dc29-417f-aa10-ebad34bf9db3/properties/humidity");
        adapter.get("/objects/d6e5acc3-dc29-417f-aa10-ebad34bf9db3/properties/temperature");


        String postData = "{\"input\": [{\"parameterName\": \"switch\",\"parameterValue\": \"Off\"}]}";
        adapter.post("/objects/000D6F0005B10494/actions/switch", postData);

    }


    public static void main(String[] args) throws Exception {
        TestAdapter t = new TestAdapter();
        t.test();
    }


}
