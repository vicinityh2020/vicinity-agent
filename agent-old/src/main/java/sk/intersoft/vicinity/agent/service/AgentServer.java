package sk.intersoft.vicinity.agent.service;

import org.restlet.Component;

public class AgentServer {
    public static void main(String [] args) throws Exception {
        AgentComponent component = new AgentComponent();
        component.start();

        System.out.println("starting");

        Runtime.getRuntime().addShutdownHook(new Thread() {
	            @Override
	            public void run() {
                    System.out.println("run shutdown");
                    StartStop.stop();
	                component.shutdown();
	            }
	        });


        StartStop.start();
    }
}
