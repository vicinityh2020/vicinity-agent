package sk.intersoft.vicinity.agent.service;

import org.restlet.service.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.service.config.logging.ConfigureLogging;

public class AgentServer {
    static {
        ConfigureLogging c = new ConfigureLogging();
        c.configure(System.getProperty("logs.folder"));
    }


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
