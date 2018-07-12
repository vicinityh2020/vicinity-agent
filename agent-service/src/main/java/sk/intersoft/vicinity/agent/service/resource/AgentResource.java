package sk.intersoft.vicinity.agent.service.resource;

import org.restlet.Context;
import org.restlet.Response;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.Header;
import org.restlet.data.Reference;
import org.restlet.resource.Resource;
import org.restlet.resource.ServerResource;
import org.restlet.util.Series;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.service.config.AdapterConfig;
import sk.intersoft.vicinity.agent.service.config.AgentConfig;
import sk.intersoft.vicinity.agent.service.config.Configuration;
import sk.intersoft.vicinity.agent.service.config.processor.ThingDescriptions;
import sk.intersoft.vicinity.agent.thing.ThingDescription;

import java.util.Iterator;

public class AgentResource extends ServerResource {
    final static Logger logger = LoggerFactory.getLogger(AgentResource.class.getName());

    String CALLER_ID_HEADER = "infrastructure-id";
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


    protected ThingDescription getThingByOID(String oid) throws Exception {
        ThingDescription thing = Configuration.thingsByOID.get(oid);
        if(thing == null) throw new Exception("Unknown thing for OID: ["+oid+"]");
        return thing;
    }

    protected ThingDescription getCallerObject() throws Exception {
        String infrastructureId = getHeader(CALLER_ID_HEADER);
        String adapterId = getHeader(ADAPTER_ID_HEADER);
        logger.debug("caller headers: [adapter-id: "+adapterId+"][infrastructure-id: "+infrastructureId+"]");

        if(infrastructureId == null) throw new Exception("Missing ["+CALLER_ID_HEADER+"] header of caller object!");
        if(adapterId == null) throw new Exception("Missing ["+ADAPTER_ID_HEADER+"] header of caller object!");

        ThingDescriptions adapterThings = Configuration.things.get(adapterId);
        if(adapterThings == null){
            throw new Exception("Adapter ["+adapterId+"] does not exist in configuration!");
        }

        String id = ThingDescription.identifier(infrastructureId, adapterId);
        ThingDescription thing = adapterThings.byAdapterInfrastructureID.get(id);
        if(thing == null) {
            throw new Exception("Thing [adapter-id: "+adapterId+"][infrastructure-id: "+infrastructureId+"] does not exist in configuration!");
        }

        return thing;
    }

    private boolean isLocalhost(String d){
        return (d.equals("localhost") || d.equals("127.0.0.1"));
    }

    private boolean sameDomain(String d1, String d2){
        if(isLocalhost(d1) && isLocalhost(d2)){
            return true;
        }
        else if(d1.equals(d2)){
            return true;
        }
        else return false;
    }

    protected boolean isGatewayRequest() {
        logger.debug("CHECKING IF THIS IS REQUEST FROM GATEWAY");
        try{
            Reference gtw = new Reference(Configuration.gatewayAPIEndpoint);
            String gtwDomain = gtw.getHostDomain();
            int gtwPort = gtw.getHostPort();
            String gtwScheme = gtw.getScheme();

            Reference requestRef = getRequest().getHostRef();
            String rDomain = requestRef.getHostDomain();
            int rPort = requestRef.getHostPort();
            String rScheme = requestRef.getScheme();


            logger.debug("domain: "+rDomain);
            logger.debug("port: "+rPort);
            logger.debug("scheme: "+rScheme);

            logger.debug("GATEWAY: ");
            logger.debug("domain: "+gtwDomain);
            logger.debug("port: "+gtwPort);
            logger.debug("scheme: "+gtwScheme);

            return false;


//            if(sameDomain(gtwDomain, rDomain)){
//                logger.debug("same domain");
//                if(gtwPort == rPort){
//                    logger.debug("same port");
//                    if(gtwScheme.equals(rScheme)){
//                        logger.debug("same scheme");
//                        logger.debug("IS GATEWAY REQUEST!!");
//                        return true;
//                    }
//                    else {
//                        logger.debug("different scheme ...fail");
//                        return false;
//                    }
//                }
//                else {
//                    logger.debug("different port ...fail");
//                    return false;
//                }
//            }
//            else {
//                logger.debug("different domain ...fail");
//                return false;
//            }

        }
        catch(Exception e){
            logger.error("", e);
        }
        return false;
    }

}
