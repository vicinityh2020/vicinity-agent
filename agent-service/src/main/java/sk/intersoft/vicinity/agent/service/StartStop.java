package sk.intersoft.vicinity.agent.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.db.PersistedThing;
import sk.intersoft.vicinity.agent.service.config.AdapterConfig;
import sk.intersoft.vicinity.agent.service.config.AgentConfig;
import sk.intersoft.vicinity.agent.service.config.Configuration;

import java.io.File;
import java.util.Map;

public class StartStop {
    final static Logger logger = LoggerFactory.getLogger(StartStop.class.getName());





    public static void start() {
        logger.info("LAUNCHING START SEQUENCE!");
        try{
            // START SEQUENCE:

            // 0. init persistence for case that it does not exist yet
            // 1. INITIALIZE PERSISTENCE
            PersistedThing.createTable();
            logger.info("Initialized persistence");
            PersistedThing.list();


            // 1. read config mappings
            logger.info("READING CONFIGURATION!");
            Configuration.create();
            logger.info(Configuration.toString(0));

            // 2. configure agents config by config, getting last configuration from NM
            Configuration.configureAgents();


            // 4. list status of processed components
            logger.info("FINAL CONFIGURATION STATUS: \n"+Configuration.toStatusString(0));
            logger.info("FINAL CONFIGURATION PERSISTENCE:");
            PersistedThing.list();

            logger.info("STARTUP SEQUENCE COMPLETED!");

        }
        catch(Exception e){
            logger.error("START SEQUENCE FAILED!", e);
        }
    }

    public static void stop() {
        try{
            logger.info("LAUNCHING SHUTDOWN SEQUENCE!");
            logger.info("LOGOUT ALL THINGS:");
            for (Map.Entry<String, AdapterConfig> entry : Configuration.adapters.entrySet()) {
                AdapterConfig a = entry.getValue();
                a.logout();
            }

            logger.info("LOGOUT AGENTS:");
            for (Map.Entry<String, AgentConfig> entry : Configuration.agents.entrySet()) {
                AgentConfig a = entry.getValue();
                a.logout();
            }
            logger.info("SHUTDOWN SEQUENCE COMPLETED!");
        }
        catch(Exception e){
            logger.error("SHUTDOWN SEQUENCE FAILED", e);
        }
    }

}
