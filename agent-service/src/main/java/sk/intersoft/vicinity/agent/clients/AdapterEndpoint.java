package sk.intersoft.vicinity.agent.clients;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.service.config.AdapterConfig;
import sk.intersoft.vicinity.agent.service.config.AgentConfig;
import sk.intersoft.vicinity.agent.service.config.Configuration;
import sk.intersoft.vicinity.agent.thing.InteractionPattern;
import sk.intersoft.vicinity.agent.thing.InteractionPatternEndpoint;
import sk.intersoft.vicinity.agent.thing.ThingDescription;

public class AdapterEndpoint {
    final static Logger logger = LoggerFactory.getLogger(AdapterEndpoint.class.getName());

    public static String process(String href, String oid, String patternId) throws Exception {
        return href.replaceAll("\\{oid\\}", oid).replaceAll("\\{pid\\}", patternId).replaceAll("\\{aid\\}", patternId).replaceAll("\\{eid\\}", patternId);
    }

    private static String getAdapterEndpoint(ThingDescription thing) throws Exception {
        logger.debug("Getting read endpoint for thing: " + thing.toSimpleString());

        AdapterConfig adapter = Configuration.adapters.get(thing.adapterId);
        if (adapter == null) {
            throw new Exception("Adapter [" + thing.adapterId + "] is NOT CONFIGURED!!?!??");
        }

        String adapterEndpoint = adapter.endpoint;
        if (adapterEndpoint == null || adapterEndpoint.trim().equals("")) {
            throw new Exception("Adapter [" + adapter.adapterId + "] does not have endpoint.. unable to read from thing!");
        }

        return adapterEndpoint;
    }

    public static String getEndpoint(ThingDescription thing,
                                     String patternId,
                                     String patternType,
                                     String operation) throws Exception {
        String adapterEndpoint = getAdapterEndpoint(thing);

        InteractionPattern pattern = thing.getInteractionPattern(patternId, patternType);

        InteractionPatternEndpoint endpoint = null;
        if(operation.equals(InteractionPatternEndpoint.READ)){
            endpoint = pattern.readEndpoint;
        }
        else if(operation.equals(InteractionPatternEndpoint.WRITE)){
            endpoint = pattern.writeEndpoint;
        }
        if(endpoint == null){
            throw new Exception("Thing ["+thing.oid+"] does not have endpoint for ["+patternType+"]["+patternId+"]["+operation+"]");
        }

        String link = endpoint.href;
        if(link == null || link.trim().equals("")){
            throw new Exception("Thing ["+thing.oid+"] does not have endpoint for ["+patternType+"]["+patternId+"]["+operation+"] .. empty href!");
        }

        String thingEndpoint = process(link, thing.oid, patternId);
        return adapterEndpoint + thingEndpoint;

    }

}
