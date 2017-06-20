package sk.intersoft.vicinity.agent.adapter;

import sk.intersoft.vicinity.agent.config.AgentConfig;
import sk.intersoft.vicinity.agent.config.thing.InteractionPattern;

public class AdapterEndpoint {

    public static String process(String iid, String pid, String href) {
        String endpoint = href.replaceAll("\\{oid\\}", iid).replaceAll("\\{pid\\}", pid).replaceAll("\\{aid\\}", pid);
        return endpoint;
    }


    public static String getEndpoint(String oid, String pid, String patternType, boolean read) throws Exception {
        String iid = AgentConfig.getInfrastructureId(oid);
        if(iid == null) throw new Exception("Unknown thing for oid:["+oid+"]");

        String href = null;
        if(read){
            href = AgentConfig.getReadHref(oid, pid, patternType);
        }
        else{
            href = AgentConfig.getWriteHref(oid, pid, patternType);
        }

        if(href == null) throw new Exception("Unable to process interaction pattern endpoint for ["+oid+"/"+pid+"]");

        return AdapterEndpoint.process(iid, pid, href);

    }



}
