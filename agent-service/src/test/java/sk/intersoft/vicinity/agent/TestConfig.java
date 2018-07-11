package sk.intersoft.vicinity.agent;


import sk.intersoft.vicinity.agent.db.PersistedThing;
import sk.intersoft.vicinity.agent.service.config.AgentConfig;
import sk.intersoft.vicinity.agent.service.config.Configuration;

import java.io.File;
import java.util.Scanner;

public class TestConfig {
    public class AgentConfigThread implements Runnable {

        private String id;
        private String agentId;

        public AgentConfigThread(String id, String agentId) {
            this.id = id;
            this.agentId = agentId;
        }

        public void run() {
            System.out.println("["+id+"] CONFIGURING ["+agentId+"] START");
//            Configuration.configureAgent(agentId);
            System.out.println("["+id+"]CONFIGURING ["+agentId+"] STOP");
        }
    }

    public static void sleep(long millis){
        try{
            Thread.sleep(millis);
        }
        catch(Exception e){}
    }


    public void configure() throws Exception {
        Configuration.create();
        System.out.println(Configuration.toString(0));
    }

    public void multiConfigureAgent() throws Exception {
        System.out.println("MULTI CONFIGURE AGENT");

        String agentId = "4aaf6042-9888-4cd1-bc56-f42a84204101";

        Thread t1 = new Thread(new AgentConfigThread("1", agentId));
        Thread  t2 = new Thread (new AgentConfigThread("2", agentId));
        t1.start();
        t2.start();

//        sleep(10000);
//        Configuration.configureAgent(agentId);

    }

    public void addAgent() throws Exception {
        System.out.println("ADDING AGENT");

        String agentId = "chuj";

        sleep(10000);
//        Configuration.configureAgent(agentId);

        System.out.println(Configuration.toString(0));

    }


    public static void main(String[] args) throws Exception {
        TestConfig c = new TestConfig();
//        c.testLogin();
//        c.configure();
//        c.multiConfigureAgent();
//        c.addAgent();
    }

}
