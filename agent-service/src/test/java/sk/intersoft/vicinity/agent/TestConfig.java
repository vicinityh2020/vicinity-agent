package sk.intersoft.vicinity.agent;


import sk.intersoft.vicinity.agent.service.config.AgentConfig;

import java.io.File;
import java.util.Scanner;

public class TestConfig {

    public static String file2string(String path) {
        try{
            return new Scanner(new File(path)).useDelimiter("\\Z").next();
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }


    public void test() throws Exception {
        System.out.println("CONFIG BEFORE: \n"+AgentConfig.asString());

        AgentConfig.create(new File("").getAbsolutePath() + "/agent-service/bin/config/test-config.json");
        System.out.println("CONFIG AFTER: \n"+AgentConfig.asString());

    }
    public static void main(String[] args) throws Exception {
        TestConfig c = new TestConfig();
//        c.testLogin();
        c.test();
    }

}
