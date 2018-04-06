package sk.intersoft.vicinity.agent.service.resource;

import org.restlet.data.Header;
import org.restlet.resource.ServerResource;
import org.restlet.util.Series;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.service.config.AdapterConfig;
import sk.intersoft.vicinity.agent.service.config.AgentConfig;
import sk.intersoft.vicinity.agent.thing.ThingDescription;

import java.util.Iterator;

public class AgentResource extends ServerResource {
    final static Logger logger = LoggerFactory.getLogger(AgentResource.class.getName());

    String CALLER_OID_HEADER = "infrastructure-id";
    String ADAPTER_ID_HEADER = "adapter-id";

    private String getHeader(String name) {
        try{
            Series<Header> series = (Series<Header>) getRequestAttributes().get("org.restlet.http.headers");

            Iterator<Header> i = series.iterator();
            while (i.hasNext()) {
                Header h = i.next();
                if (h.getName().trim().equalsIgnoreCase(name.trim())) {
                    return h.getValue().trim();
                }
            }

        }
        catch(Exception e){
            e.printStackTrace();
        }


        return null;
    }



    public static ThingDescription getThingByInfrastructureID(String infrastructureId) throws Exception {
        ThingDescription thing = AgentConfig.things.getThingByInfrastructureID(infrastructureId);
        if(thing == null) throw new Exception("Unknown thing for infrastructure id: ["+infrastructureId+"]");
        return thing;
    }

    protected ThingDescription getThing(String oid) throws Exception {
        ThingDescription thing = AgentConfig.things.getThingByOID(oid);
        if(thing == null) throw new Exception("Unknown thing for OID: ["+oid+"]");
        return thing;
    }

    private String getAdapterId() throws Exception {

        boolean multi = AgentConfig.hasMultiAdapters();

        String adapterId = getHeader(ADAPTER_ID_HEADER);

        if(adapterId != null) {
            return adapterId;
        }
        else{
            if(multi){
                throw new Exception("When using multiple adapters, [adapter-id] header must be presented!");
            }
            else {
                return AdapterConfig.DEFAULT_ADAPTER_ID;
            }
        }

    }

    protected ThingDescription getCallerObject() throws Exception {
        String oid = getHeader(CALLER_OID_HEADER);
        logger.debug("getting caller header ["+CALLER_OID_HEADER+"]: ["+oid+"]");

        if(oid == null) return null;
        else {

            String adapterId = getAdapterId();

            logger.debug("adapter id to be used: ["+adapterId+"]");

            String iid = ThingDescription.makeAdapterInfrastructureId(adapterId, oid);
            logger.debug("infrastructure id to look for: ["+iid+"]");

            ThingDescription caller = AgentConfig.things.getThingByInfrastructureID(iid);
            logger.debug("thing by infra-id: "+caller);
            return caller;
        }
    }


}
