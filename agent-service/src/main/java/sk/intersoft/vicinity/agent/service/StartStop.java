package sk.intersoft.vicinity.agent.service;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.adapter.AgentAdapter;
import sk.intersoft.vicinity.agent.discovery.Discovery;
import sk.intersoft.vicinity.agent.gateway.GatewayAPIClient;
import sk.intersoft.vicinity.agent.service.config.AdapterConfig;
import sk.intersoft.vicinity.agent.service.config.AgentConfig;
import sk.intersoft.vicinity.agent.thing.ThingDescription;
import sk.intersoft.vicinity.agent.thing.ThingDescriptions;
import sk.intersoft.vicinity.agent.thing.ThingsProcessor;

import java.util.Map;

public class StartStop {
    final static Logger logger = LoggerFactory.getLogger(StartStop.class.getName());


    public static void start() {
        logger.info("Launching starting sequence!");
        try{
            // START SEQUENCE:

            // 1. INITIALIZE AGENT CONFIG
            AgentConfig.create(System.getProperty("config.file"));
            logger.info("AGENT CONFIGURED: \n" + AgentConfig.asString());

            // 2. LOGIN AGENT
            logger.info("agent log in ..");
            GatewayAPIClient.login(AgentConfig.agentId, AgentConfig.password);

            // 3. RUN DISCO
            Discovery.fire();

            // 4. LOGIN THINGS FROM REDISCOVERED CONFIG
            for (ThingDescription thing : AgentConfig.things.thingsByOID()) {
                logger.info("login: "+thing.toSimpleString());
                GatewayAPIClient.login(thing.oid, thing.password);
            }



        }
        catch(Exception e){
            logger.error("", e);
            logger.error("startup sequence failed!");
        }
    }

    public static void stop() {
        try{
            logger.info("Launching shutdown sequence!");

            // 1. LOGOUT THINGS
            for (ThingDescription thing : AgentConfig.things.thingsByOID()) {
                logger.info("logout: "+thing.toSimpleString());
                GatewayAPIClient.logout(thing.oid, thing.password);
            }

            // 2. LOGOUT AGENT
            logger.info("agent log out ..");
            GatewayAPIClient.logout(AgentConfig.agentId, AgentConfig.password);
        }
        catch(Exception e){
            logger.error("", e);
            logger.error("shutdown sequence failed!");
        }
    }

}
