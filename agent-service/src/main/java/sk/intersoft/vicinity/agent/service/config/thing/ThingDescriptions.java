package sk.intersoft.vicinity.agent.service.config.thing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.thing.ThingDescription;
import sk.intersoft.vicinity.agent.utils.Dump;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ThingDescriptions {
    final static Logger logger = LoggerFactory.getLogger(ThingDescriptions.class.getName());

    public Map<String, ThingDescription> byAdapterOID = new HashMap<String, ThingDescription>();
    public Map<String, ThingDescription> byAdapterInfrastructureID = new HashMap<String, ThingDescription>();

    public void add(ThingDescription thing) throws Exception {

        if(thing.adapterOID != null && !thing.adapterOID.equals("")){
            if(byAdapterOID.get(thing.adapterOID) != null){
                throw new Exception("Duplicate thing with identifier ["+thing.adapterOID+"] exists!");
            }
            byAdapterOID.put(thing.oid, thing);
        }
        if(thing.adapterInfrastructureID != null && !thing.adapterInfrastructureID.equals("")){
            if(byAdapterInfrastructureID.get(thing.adapterInfrastructureID) != null){
                throw new Exception("Duplicate thing with identifier ["+thing.adapterInfrastructureID+"] exists!");
            }
            byAdapterInfrastructureID.put(thing.adapterInfrastructureID, thing);
        }
    }

    public String toString(int indent){
        Dump dump = new Dump();

        dump.add("THING DESCRIPTIONS:", indent);

        dump.add("BY ADAPTER-OID: "+byAdapterOID.keySet().size(), (indent + 1));
        for (Map.Entry<String, ThingDescription> entry : byAdapterOID.entrySet()) {
            String id = entry.getKey();
            dump.add("mapped-id: "+id, (indent + 2));
            dump.add(entry.getValue().toSimpleString());
        }

        dump.add("BY ADAPTER-INFRASTRUCTURE ID: "+byAdapterInfrastructureID.keySet().size(), (indent + 1));
        for (Map.Entry<String, ThingDescription> entry : byAdapterInfrastructureID.entrySet()) {
            String id = entry.getKey();
            dump.add("mapped-id: "+id, (indent + 2));
            dump.add(entry.getValue().toSimpleString());
        }

        return dump.toString();
    }

}
