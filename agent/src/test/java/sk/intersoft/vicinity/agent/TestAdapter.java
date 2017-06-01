package sk.intersoft.vicinity.agent;


import sk.intersoft.vicinity.agent.adapter.AgentAdapter;
import sk.intersoft.vicinity.agent.config.NewAgentConfig;

import java.io.File;

public class TestAdapter {
    public void test() throws Exception {
        NewAgentConfig.create(new File("").getAbsolutePath() + "/agent/bin/agent-config.json");
        System.out.println("CONFIG : ");
        NewAgentConfig.show();

        AgentAdapter adapter = new AgentAdapter(NewAgentConfig.adapterEndpoint);


        String oid = "0d485748-cf2a-450c-bcf6-02ac1cb39a2d";
        String iid = NewAgentConfig.objectInfrastructureId(oid);
        adapter.get("/objects/"+iid+"/properties/PowerConsumption");
        adapter.get("/objects/0D485748-CF2A-450C-BCF6-02AC1CB39A2D:6/properties/PowerConsumption");

    }


    public static void main(String[] args) throws Exception {
        TestAdapter t = new TestAdapter();
        t.test();
    }


}
