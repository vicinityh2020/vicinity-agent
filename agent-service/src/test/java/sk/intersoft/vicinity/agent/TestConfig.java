package sk.intersoft.vicinity.agent;


import sk.intersoft.vicinity.agent.service.config.AgentConfig;
import sk.intersoft.vicinity.agent.service.config.Configuration;

import java.io.File;
import java.util.Scanner;

public class TestConfig {

    public void test() throws Exception {
        String file = new File("").getAbsolutePath() + "/agent-service/src/test/resources/config/service.json";
        String folder = new File("").getAbsolutePath() + "/agent-service/src/test/resources/config/agents";
        Configuration.create(file, folder);
        System.out.println(Configuration.toString(0));


    }
    public static void main(String[] args) throws Exception {
        TestConfig c = new TestConfig();
//        c.testLogin();
        c.test();
    }

}
