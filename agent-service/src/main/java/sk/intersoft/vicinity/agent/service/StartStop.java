package sk.intersoft.vicinity.agent.service;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.adapter.AgentAdapter;
import sk.intersoft.vicinity.agent.service.config.AgentConfig;
import sk.intersoft.vicinity.agent.thing.ThingDescriptions;
import sk.intersoft.vicinity.agent.thing.ThingsProcessor;

public class StartStop {
    final static Logger logger = LoggerFactory.getLogger(StartStop.class.getName());

    public static void fakeConfig() throws Exception {
        JSONArray json = new JSONArray(AgentConfig.file2string("/home/kostelni/work/eu-projekty/vicinity/unikl-workspace/vicinity-agent/agent-service/src/test/resources/objects/fake-configuration.json"));
        ThingDescriptions config = ThingsProcessor.process(json, true);
        AgentConfig.things = config;
    }

    public static void start() {
        logger.info("Launching starting sequence!");
        try{
            // START SEQUENCE:

            // 1. READ AGENT CONFIG
            AgentConfig.create(System.getProperty("config.file"));
            logger.info("AGENT CONFIGURED: \n"+AgentConfig.asString());

            fakeConfig();

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
