package sk.intersoft.vicinity.agent;


import sk.intersoft.vicinity.agent.discovery.Discovery;
import sk.intersoft.vicinity.agent.service.config.AgentConfig;
import sk.intersoft.vicinity.agent.thing.ThingDescriptions;

import java.io.File;
import java.util.Scanner;

public class TestDiscovery {


    public void testAdapters() throws Exception {
        ThingDescriptions things = Discovery.getAllAdapterThings();
        System.out.println(things.toString(0));
    }

    public void test() throws Exception {
        Discovery.fire();

    }
    public static void main(String[] args) throws Exception {
        AgentConfig.create(new File("").getAbsolutePath() + "/agent-service/bin/config/test-config.json");

        TestDiscovery test = new TestDiscovery();
        test.test();
//        test.testAdapters();
    }

}
