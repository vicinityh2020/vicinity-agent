package sk.intersoft.vicinity.agent.adapter;

import sk.intersoft.vicinity.agent.service.config.AdapterConfig;
import sk.intersoft.vicinity.agent.service.config.AgentConfig;
import sk.intersoft.vicinity.agent.thing.ThingDescription;

public class AdapterEndpoint {
    public static final String OBJECTS = "/objects";


    public static String getEndpoint(ThingDescription thing) throws Exception {
        AdapterConfig config = AgentConfig.adapters.get(thing.adapterId);
        if(config == null) throw new Exception("Unable to find config for adapter: ["+thing.adapterId+"]");
        return config.endpoint;
    }

    public static String process(ThingDescription thing, String patternID, String href) throws Exception {
        String endpoint = href.replaceAll("\\{oid\\}", thing.adapterThingId).replaceAll("\\{pid\\}", patternID).replaceAll("\\{aid\\}", patternID).replaceAll("\\{eid\\}", patternID);
        return getEndpoint(thing) + endpoint;
    }


    public static String getEndpoint(ThingDescription thing,
                                     String patternID,
                                     String patternType,
                                     boolean read) throws Exception {

        String readWriteString = "";
        String href = null;
        if(read){
            readWriteString = "read";
            href = thing.getReadHref(patternID, patternType);
        }
        else{
            readWriteString = "write";
            href = thing.getWriteHref(patternID, patternType);
        }

        if(href == null) throw new Exception("Unable to process endpoint for ["+readWriteString+":"+patternType+"] pattern : [OID: "+thing.oid+"/ PATTERN-ID: "+patternID+"]");

        return AdapterEndpoint.process(thing, patternID, href);

    }

}
