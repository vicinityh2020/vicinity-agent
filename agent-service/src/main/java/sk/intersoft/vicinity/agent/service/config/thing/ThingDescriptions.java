package sk.intersoft.vicinity.agent.service.config.thing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.thing.ThingDescription;
import sk.intersoft.vicinity.agent.utils.Dump;

import java.util.HashMap;
import java.util.Map;

public class ThingDescriptions {
    final static Logger logger = LoggerFactory.getLogger(ThingDescriptions.class.getName());

    public Map<String, ThingDescription> byOID = new HashMap<String, ThingDescription>();
    public Map<String, ThingDescription> byInfrastructureID = new HashMap<String, ThingDescription>();

    public void add(ThingDescription thing) {
        if(thing.oid != null) {
            byOID.put(thing.oid, thing);
        }
        if(thing.adapterInfrastructureID != null) {
            byInfrastructureID.put(thing.adapterInfrastructureID, thing);
        }
    }

    public String toString(int indent){
        Dump dump = new Dump();

        dump.add("THING DESCRIPTIONS:", indent);

        dump.add("BY OID: "+byOID.keySet().size(), (indent + 1));
        for (Map.Entry<String, ThingDescription> entry : byOID.entrySet()) {
            String id = entry.getKey();
            dump.add("mapped-id: "+id, (indent + 2));
            dump.add(entry.getValue().toSimpleString());
        }

        dump.add("BY INFRASTRUCTURE ID: "+byInfrastructureID.keySet().size(), (indent + 1));
        for (Map.Entry<String, ThingDescription> entry : byInfrastructureID.entrySet()) {
            String id = entry.getKey();
            dump.add("mapped-id: "+id, (indent + 2));
            dump.add(entry.getValue().toSimpleString());
        }

        return dump.toString();
    }

}
