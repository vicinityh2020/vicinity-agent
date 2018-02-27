package sk.intersoft.vicinity.agent.service;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.adapter.AgentAdapter;
import sk.intersoft.vicinity.agent.discovery.Discovery;
import sk.intersoft.vicinity.agent.service.config.AgentConfig;
import sk.intersoft.vicinity.agent.thing.ThingDescriptions;
import sk.intersoft.vicinity.agent.thing.ThingsProcessor;

public class StartStop {
    final static Logger logger = LoggerFactory.getLogger(StartStop.class.getName());


    public static void start() {
        logger.info("Launching starting sequence!");
        try{
            // START SEQUENCE:

            // 1. INITIALIZE AGENT CONFIG
            AgentConfig.create(System.getProperty("config.file"));
            logger.info("AGENT CONFIGURED: \n" + AgentConfig.asString());

            Discovery.fire();


        }
        catch(Exception e){
            logger.error("", e);
            logger.error("startup sequence failed!");
        }
    }

    public static void stop() {
        logger.info("Launching shutdown sequence!");
    }

}
