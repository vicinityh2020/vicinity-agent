package sk.intersoft.vicinity.agent.adapter;

import sk.intersoft.vicinity.agent.service.config.AgentConfig;
import sk.intersoft.vicinity.agent.thing.ThingDescription;

public class AdapterEndpoint {
    public static final String OBJECTS = "/objects";


    public static String process(ThingDescription thing, String patternID, String href) {
        String endpoint = href.replaceAll("\\{oid\\}", thing.infrastructureID).replaceAll("\\{pid\\}", patternID).replaceAll("\\{aid\\}", patternID).replaceAll("\\{eid\\}", patternID);
        return endpoint;
    }


    public static String getEndpoint(String oid, String patternID, String patternType, boolean read) throws Exception {
        ThingDescription thing = AgentConfig.things.getThingByOID(oid);
        if(thing == null) throw new Exception("Unknown thing for OID: ["+oid+"]");

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

        if(href == null) throw new Exception("Unable to process endpoint for ["+readWriteString+":"+patternType+"] pattern : [OID: "+oid+"/ PATTERN-ID: "+patternID+"]");

        return AdapterEndpoint.process(thing, patternID, href);

    }

}
