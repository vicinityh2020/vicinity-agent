package sk.intersoft.vicinity.agent;


import sk.intersoft.vicinity.agent.adapter.Adapter;
import sk.intersoft.vicinity.agent.config.NewAgentConfig;

import java.io.File;

public class TestAdapter {
    public void test() throws Exception {
        NewAgentConfig.create(new File("").getAbsolutePath() + "/agent/bin/agent-config.json");
        System.out.println("CONFIG : ");
        NewAgentConfig.show();

        Adapter adapter = new Adapter(NewAgentConfig.adapterEndpoint);

        String oid = "0d485748-cf2a-450c-bcf6-02ac1cb39a2d";
        String iid = NewAgentConfig.objectInfrastructureId(oid);
        adapter.get("/objects/"+iid+"/properties/PowerConsumption");

    }


    public static void main(String[] args) throws Exception {
        TestAdapter t = new TestAdapter();
        t.test();
    }


}
