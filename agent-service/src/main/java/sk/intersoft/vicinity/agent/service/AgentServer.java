package sk.intersoft.vicinity.agent.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.service.config.PrepareLogging;

public class AgentServer {
    final static Logger logger = LoggerFactory.getLogger(AgentServer.class.getName());

    public static void main(String [] args) throws Exception {


        AgentComponent component = new AgentComponent();
        component.start();

        logger.info("starting");

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                logger.info("run shutdown");
                StartStop.stop();
                component.shutdown();
            }
        });


        StartStop.start();
    }
}
