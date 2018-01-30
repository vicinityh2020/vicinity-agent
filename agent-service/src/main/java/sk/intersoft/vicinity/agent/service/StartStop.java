package sk.intersoft.vicinity.agent.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.service.config.AgentConfig;

public class StartStop {
    final static Logger logger = LoggerFactory.getLogger(StartStop.class.getName());

    public static void start() {
        logger.info("Launching starting sequence!");
    }

    public static void stop() {
        logger.info("Launching shutdown sequence!");
    }

}
