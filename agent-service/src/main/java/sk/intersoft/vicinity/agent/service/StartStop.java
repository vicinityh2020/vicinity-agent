package sk.intersoft.vicinity.agent.service;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.gateway.GatewayAPIClient;
import sk.intersoft.vicinity.agent.gateway.NeighbourhoodManager;
import sk.intersoft.vicinity.agent.service.config.AgentConfig;
import sk.intersoft.vicinity.agent.service.config.Configuration;
import sk.intersoft.vicinity.agent.service.config.thing.ThingProcessor;
import sk.intersoft.vicinity.agent.thing.ThingDescription;
import sk.intersoft.vicinity.agent.thing.ThingValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StartStop {
    final static Logger logger = LoggerFactory.getLogger(StartStop.class.getName());



    public static void start() {
        logger.info("Launching starting sequence!");
        try{
            // START SEQUENCE:

            logger.info("READING CONFIGURATION!");
            Configuration.create(System.getProperty("service.config"), System.getProperty("agents.config"));
            logger.info(Configuration.toString(0));

            logger.info("ACQUIRING ACTUAL AGENT CONFIGURATIONS FROM NeighbourhoodManager");
            List<AgentConfig> configuredAgents = new ArrayList<AgentConfig>();
            for (AgentConfig agent : Configuration.agents.values()) {
                boolean configured = agent.configure();
                if(configured){
                    configuredAgents.add(agent);
                }
            }

            logger.info("CONFIGURED AGENTS: "+configuredAgents.size());
            for(AgentConfig ac : configuredAgents){
                logger.info(ac.toSimpleString());

            }

            logger.info("STARTUP SEQUENCE COMPLETED!");

        }
        catch(Exception e){
            logger.error("", e);
            logger.error("STARTUP SEQUENCE FAILED!");
        }
    }

    public static void stop() {
        try{
            logger.info("Launching shutdown sequence!");

        }
        catch(Exception e){
            logger.error("", e);
            logger.error("shutdown sequence failed!");
        }
    }

}
