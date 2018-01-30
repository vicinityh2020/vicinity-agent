package sk.intersoft.vicinity.agent.thing;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.utils.Dump;
import sk.intersoft.vicinity.agent.utils.JSONUtil;

import java.util.*;

public class ThingDescriptions {
    final static Logger logger = LoggerFactory.getLogger(ThingDescriptions.class.getName());

    public Map<String, ThingDescription> byOID = new HashMap<String, ThingDescription>();
    public Map<String, ThingDescription> byInfrastructureID = new HashMap<String, ThingDescription>();


    public ThingDescription getThingByOID(String oid){
        return byOID.get(oid);
    }

    public List<ThingDescription> thingsToCreate(ThingDescriptions fromAdapter) {
        List<ThingDescription> toCreate = new ArrayList<ThingDescription>();
        for (Map.Entry<String, ThingDescription> entry : fromAdapter.byInfrastructureID.entrySet()) {
            String infrastructureID = entry.getKey();
            if(byInfrastructureID.get(infrastructureID) == null){
                toCreate.add(entry.getValue());
            }
        }
        return toCreate;
    }

    public List<ThingDescription> thingsToRemove(ThingDescriptions fromAdapter) {
        List<ThingDescription> toRemove = new ArrayList<ThingDescription>();
        for (Map.Entry<String, ThingDescription> entry : byInfrastructureID.entrySet()) {
            String infrastructureID = entry.getKey();
            if(fromAdapter.byInfrastructureID.get(infrastructureID) == null){
                toRemove.add(entry.getValue());
            }
        }
        return toRemove;
    }

    public List<ThingDescription> thingsToUpdate(ThingDescriptions fromAdapter) {
        List<ThingDescription> toUpdate = new ArrayList<ThingDescription>();
        for (Map.Entry<String, ThingDescription> entry : byInfrastructureID.entrySet()) {
            String infrastructureID = entry.getKey();
            if(fromAdapter.byInfrastructureID.get(infrastructureID) != null){
                ThingDescription configThing = entry.getValue();
                ThingDescription adapterThing = fromAdapter.byInfrastructureID.get(infrastructureID);
                if(!configThing.sameAs(adapterThing)){
                    toUpdate.add(configThing);
                }
            }
        }
        return toUpdate;
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
}
