package sk.intersoft.vicinity.agent.service.resource;

import org.restlet.resource.ServerResource;
import sk.intersoft.vicinity.agent.service.config.AgentConfig;
import sk.intersoft.vicinity.agent.thing.ThingDescription;

public class AgentResource extends ServerResource {
    protected ThingDescription getThing(String oid) throws Exception {
        ThingDescription thing = AgentConfig.things.getThingByOID(oid);
        if(thing == null) throw new Exception("Unknown thing for OID: ["+oid+"]");
        return thing;
    }
}
