package sk.intersoft.vicinity.agent;


import sk.intersoft.vicinity.agent.adapter.AgentAdapter;
import sk.intersoft.vicinity.agent.config.AgentConfig;

import java.io.File;

public class TestAdapter {
    public void test() throws Exception {
        AgentConfig.create(new File("").getAbsolutePath() + "/agent/bin/unikl-agent-config.json");
//        AgentConfig.create(new File("").getAbsolutePath() + "/agent/bin/agent-sitewhere-config.json");
        System.out.println("CONFIG : ");
        AgentConfig.show();

        AgentAdapter adapter = new AgentAdapter(AgentConfig.adapterEndpoint);


        String oid = "abc-51742-0123456789-xyz-54790";
//        String iid = AgentConfig.getInfrastructureId(oid);
//        adapter.get("/objects/"+iid+"/properties/PowerConsumption");
//        adapter.get("/objects/0D485748-CF2A-450C-BCF6-02AC1CB39A2D:6/properties/PowerConsumption");

//        adapter.get("/objects/d6e5acc3-dc29-417f-aa10-ebad34bf9db3/properties/humidity");
//        adapter.get("/objects/d6e5acc3-dc29-417f-aa10-ebad34bf9db3/properties/temperature");
//        adapter.get("/objects/x/properties/y");




        String postData = "{\"property\": \"color\",\"value\": \"ffff00\"}";
        String result = adapter.put("/objects/"+oid+"/properties/color", postData);

        System.out.println("WTF: "+result);

//        String postData = "{\"value\": 47}";
//        adapter.put("/objects/hvacs:HVAC_LG_02/properties/UCtrlTempSetPoint", postData);

//        adapter.get("/objects/");

//        String postData = "{\"test\": \"some\"}";
//        adapter.post("/objects/x/events/y", postData);

    }


    public static void main(String[] args) throws Exception {
        TestAdapter t = new TestAdapter();
        t.test();
    }


}
