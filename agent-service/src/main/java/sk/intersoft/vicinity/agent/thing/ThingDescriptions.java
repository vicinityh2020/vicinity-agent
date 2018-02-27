package sk.intersoft.vicinity.agent.thing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.utils.Dump;

import java.util.*;

public class ThingDescriptions {
    final static Logger logger = LoggerFactory.getLogger(ThingDescriptions.class.getName());

    public Map<String, ThingDescription> byOID = new HashMap<String, ThingDescription>();
    public Map<String, ThingDescription> byInfrastructureID = new HashMap<String, ThingDescription>();

    public int size() {
        return byInfrastructureID.keySet().size();
    }

    public ThingDescription getThingByOID(String oid){
        return byOID.get(oid);
    }


    public List<ThingDescription> things(Map<String, ThingDescription> map){
        List<ThingDescription> things = new ArrayList<ThingDescription>();
        for (Map.Entry<String, ThingDescription> entry : map.entrySet()) {
            things.add(entry.getValue());
        }
        return things;
    }

    public List<ThingDescription> thingsByInfrastructureId(){
        return things(byInfrastructureID);
    }
    public List<ThingDescription> thingsByOID(){
        return things(byOID);
    }

    public void add(ThingDescription thing) {
        if(thing.oid != null) {
            byOID.put(thing.oid, thing);
        }
        if(thing.AgentInfrastructureID != null) {
            byInfrastructureID.put(thing.AgentInfrastructureID, thing);
        }
    }

    public void add(ThingDescriptions things) {
        byOID.putAll(things.byOID);
        byInfrastructureID.putAll(things.byInfrastructureID);
    }

    public String toString(int indent){
        Dump dump = new Dump();

        dump.add("THING DESCRIPTIONS:", indent);

        dump.add("BY OID: "+byOID.keySet().size(), (indent + 1));
        for (Map.Entry<String, ThingDescription> entry : byOID.entrySet()) {
            String id = entry.getKey();
            dump.add("THING MAPPED OID: "+id, (indent + 2));
            dump.add(entry.getValue().toString(indent + 2));
        }

        dump.add("BY INFRASTRUCTURE ID: "+byInfrastructureID.keySet().size(), (indent + 1));
        for (Map.Entry<String, ThingDescription> entry : byInfrastructureID.entrySet()) {
            String id = entry.getKey();
            dump.add("THING MAPPED INFRASTRUCTURE OID: "+id, (indent + 2));
            dump.add(entry.getValue().toString(indent + 2));
        }

        return dump.toString();
    }

    public String toSimpleString(int indent){
        Dump dump = new Dump();

        dump.add("THING DESCRIPTIONS:", indent);

        dump.add("BY OID: "+byOID.keySet().size(), (indent + 1));
        for (Map.Entry<String, ThingDescription> entry : byOID.entrySet()) {
            String id = entry.getKey();
            dump.add(entry.getValue().toSimpleString(indent + 2));
        }

        dump.add("BY INFRASTRUCTURE ID: "+byInfrastructureID.keySet().size(), (indent + 1));
        for (Map.Entry<String, ThingDescription> entry : byInfrastructureID.entrySet()) {
            String id = entry.getKey();
            dump.add(entry.getValue().toSimpleString(indent + 2));
        }

        return dump.toString();
    }
}
