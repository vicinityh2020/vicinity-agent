package sk.intersoft.vicinity.agent.service;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.adapter.AgentAdapter;
import sk.intersoft.vicinity.agent.discovery.Discovery;
import sk.intersoft.vicinity.agent.gateway.GatewayAPIClient;
import sk.intersoft.vicinity.agent.service.config.AdapterConfig;
import sk.intersoft.vicinity.agent.service.config.AgentConfig;
import sk.intersoft.vicinity.agent.service.config.EventChannel;
import sk.intersoft.vicinity.agent.service.config.EventChannelSubscription;
import sk.intersoft.vicinity.agent.service.resource.OpenObjectEventResource;
import sk.intersoft.vicinity.agent.service.resource.SubscribeObjectEventResource;
import sk.intersoft.vicinity.agent.thing.ThingDescription;
import sk.intersoft.vicinity.agent.thing.ThingDescriptions;
import sk.intersoft.vicinity.agent.thing.ThingsProcessor;

import java.util.List;
import java.util.Map;

public class StartStop {
    final static Logger logger = LoggerFactory.getLogger(StartStop.class.getName());

    public static void openEventChannels(List<EventChannel> channels) throws Exception {
        logger.info("Opening event channels: "+channels.size());
        for(EventChannel c : channels) {
            logger.info("Opening event channel: "+c.toString());

            ThingDescription thing = AgentConfig.things.getThingByInfrastructureID(ThingDescription.makeAdapterInfrastructureId(c.adapterId, c.infrastructureId));
            if(thing == null) throw new Exception("Thing for channel does not exist!");

            logger.info("Thing to open channel: "+thing.toSimpleString());

            OpenObjectEventResource.openChannel(thing, c.eventId);

        }

    }

    public static void subscribeToEventChannels(List<EventChannelSubscription> channels) throws Exception {
        logger.info("Subscribing to event channels: "+channels.size());
        for(EventChannelSubscription c : channels) {
            logger.info("Subscribing to event channel: "+c.toString());

            ThingDescription thing = AgentConfig.things.getThingByInfrastructureID(ThingDescription.makeAdapterInfrastructureId(c.adapterId, c.infrastructureId));
            if(thing == null) throw new Exception("Subscriber thing does not exist!");

            logger.info("Subscriber thing: "+thing.toSimpleString());

            SubscribeObjectEventResource.subscribeChannel(thing, c.oid, c.eventId);

        }

    }

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

            // 5. OPEN EVENT CHANNELS
            openEventChannels(AgentConfig.eventChannels);

            // 6. MAKE EVENT CHANNEL SUBSCRIPTIONS
            subscribeToEventChannels(AgentConfig.eventSubscriptions);

            logger.error("STARTUP SEQUENCE COMPLETED!");

        }
        catch(Exception e){
            logger.error("", e);
            logger.error("STARTUP SEQUENCE FAILED!");
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
