package sk.intersoft.vicinity.agent;


import sk.intersoft.vicinity.agent.discovery.Discovery;
import sk.intersoft.vicinity.agent.service.config.AgentConfig;

import java.io.File;
import java.util.Scanner;

public class TestDiscovery {



    public void test() throws Exception {
        Discovery.fire();

    }
    public static void main(String[] args) throws Exception {
        AgentConfig.create(new File("").getAbsolutePath() + "/agent-service/bin/config/test-config.json");

        TestDiscovery test = new TestDiscovery();
        test.test();
    }

}
