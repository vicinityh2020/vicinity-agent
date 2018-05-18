package sk.intersoft.vicinity.agent.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.db.PersistedThing;
import sk.intersoft.vicinity.agent.service.config.AdapterConfig;
import sk.intersoft.vicinity.agent.service.config.AgentConfig;
import sk.intersoft.vicinity.agent.service.config.Configuration;
import sk.intersoft.vicinity.agent.service.config.ConfigurationMappings;

import java.util.Map;

public class StartStop {
    final static Logger logger = LoggerFactory.getLogger(StartStop.class.getName());


    public static void configureAgents() {
        logger.info("ACQUIRING ACTUAL AGENT CONFIGURATIONS FROM NeighbourhoodManager");
        for (AgentConfig agent : Configuration.mappings.agents.values()) {
            boolean configured = agent.configure();
            if(configured){
                Configuration.agents.put(agent.agentId, agent);
                for (Map.Entry<String, AdapterConfig> entry : agent.adapters.entrySet()) {
                    AdapterConfig adapter = entry.getValue();
                    Configuration.adapters.put(adapter.adapterId, adapter);
                }

            }
        }

        logger.info("CONFIGURED AGENTS: " + Configuration.agents.size());
        for(AgentConfig ac : Configuration.agents.values()){
            logger.info(ac.toSimpleString());

        }
        logger.info("CONFIGURED ADAPTERS: " + Configuration.agents.size());
        for(AdapterConfig ac : Configuration.adapters.values()){
            logger.info(ac.toSimpleString());

        }

    }

    private static void discoverPassiveAdapters() throws Exception {
        logger.info("DISCOVERING PASSIVE ADAPTERS");
        for (Map.Entry<String, AdapterConfig> entry : Configuration.adapters.entrySet()) {
            AdapterConfig adapter = entry.getValue();
            if(!adapter.activeDiscovery){
                logger.info("passive discovery for: ["+adapter.adapterId+"]");
                boolean success = adapter.discover();
                if(!success) {
                    throw new Exception("PASSIVE DISCOVERY FAILED!");
                }
            }
            else {
                logger.info("active passive discovery for: ["+adapter.adapterId+"] .. skipping");
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

            // 2. configure agents from NM
            configureAgents();

            // 3. discover passive adapters
            discoverPassiveAdapters();

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
