package sk.intersoft.vicinity.agent.service.resource;

import org.restlet.data.Header;
import org.restlet.resource.ServerResource;
import org.restlet.util.Series;
import sk.intersoft.vicinity.agent.service.config.AgentConfig;
import sk.intersoft.vicinity.agent.thing.ThingDescription;

import java.util.Iterator;

public class AgentResource extends ServerResource {
    String CALLER_OID_HEADER = "oid";

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



    protected ThingDescription getThing(String oid) throws Exception {
        ThingDescription thing = AgentConfig.things.getThingByOID(oid);
        if(thing == null) throw new Exception("Unknown thing for OID: ["+oid+"]");
        return thing;
    }

    protected ThingDescription getCallerObject() throws Exception {
        String oid = getHeader(CALLER_OID_HEADER);
        if(oid == null) return null;
        else {
            ThingDescription thing = AgentConfig.things.getThingByOID(oid);
            if(thing == null) throw new Exception("Unknown thing for caller OID: ["+oid+"]");
            return thing;
        }
    }


}
