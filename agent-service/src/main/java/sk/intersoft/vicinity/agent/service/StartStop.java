package sk.intersoft.vicinity.agent.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.db.PersistedThing;
import sk.intersoft.vicinity.agent.service.config.AdapterConfig;
import sk.intersoft.vicinity.agent.service.config.AgentConfig;
import sk.intersoft.vicinity.agent.service.config.Configuration;
import sk.intersoft.vicinity.agent.service.config.ConfigurationMappings;

import java.io.File;
import java.util.Map;

public class StartStop {
    final static Logger logger = LoggerFactory.getLogger(StartStop.class.getName());


    public static void configureAgents() throws Exception {
        logger.info("ACQUIRING ACTUAL AGENT CONFIGURATIONS");
        String configFolder = System.getProperty("agents.config");
        logger.debug("CONFIGURING AGENTS FROM FOLDER: "+configFolder);
        File folder = new File(configFolder);
        File[] files = folder.listFiles();
        if(files.length == 0){
            throw new Exception("no agent config files found in ["+configFolder+"]!");
        }
        for(File f : files){
            boolean success = AgentConfig.configure(f);
            if(!success){
                logger.error("UNABLE TO CONFIGURE AGENT FROM: "+f.getAbsolutePath());
            }
        }


    }



    public static void start() {
        logger.info("Launching starting sequence!");
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
            configureAgents();


            // 4. list status of processed components
            logger.info("FINAL CONFIGURATION STATUS: \n"+Configuration.toStatusString(0));
            logger.info("FINAL CONFIGURATION PERSISTENCE:");
            PersistedThing.list();

            logger.info("STARTUP SEQUENCE COMPLETED!");

        }
        catch(Exception e){
            logger.error("STARTUP SEQUENCE FAILED!", e);
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
