package sk.intersoft.vicinity.agent.thing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.utils.Dump;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ThingsDiff {
    final static Logger logger = LoggerFactory.getLogger(ThingsDiff.class.getName());

    public ThingDescriptions create = new ThingDescriptions();
    public ThingDescriptions update = new ThingDescriptions();
    public ThingDescriptions delete = new ThingDescriptions();
    public ThingDescriptions unchanged = new ThingDescriptions();

    public static ThingsDiff fire(ThingDescriptions config, ThingDescriptions adapter) {
        ThingsDiff diff = new ThingsDiff();

        // always look at infrastructure-id .. it was attached to config from database when thing was created

        // find things to create:
        // thing with infra-id from adapter does not exist in config
        for (Map.Entry<String, ThingDescription> entry : adapter.byInfrastructureID.entrySet()) {
            String infrastructureID = entry.getKey();
            ThingDescription adapterThing = entry.getValue();
            if(config.byInfrastructureID.get(infrastructureID) == null){
                diff.create.add(adapterThing);
            }
        }

        // find things to delete:
        // configuration thing with infrastructure-id is not presented in adapter
        for (Map.Entry<String, ThingDescription> entry : config.byInfrastructureID.entrySet()) {
            String infrastructureID = entry.getKey();
            ThingDescription configThing = entry.getValue();
            if(adapter.byInfrastructureID.get(infrastructureID) == null){
                diff.delete.add(configThing);
            }
        }
        // configuration thing without infrastructure-id .. does not have persistence, must be removed
        for (Map.Entry<String, ThingDescription> entry : config.byOID.entrySet()) {
            ThingDescription configThing = entry.getValue();
            if(configThing.infrastructureID == null){
                diff.delete.add(configThing);
            }
        }

        // find things to update:
        // configuration thing with infrastructure-id is presented in adapter
        // are same: unchanged
        // are different: update
        for (Map.Entry<String, ThingDescription> entry : config.byInfrastructureID.entrySet()) {
            String infrastructureID = entry.getKey();
            if(adapter.byInfrastructureID.get(infrastructureID) != null){
                ThingDescription configThing = entry.getValue();
                ThingDescription adapterThing = adapter.byInfrastructureID.get(infrastructureID);
                if(configThing.sameAs(adapterThing)){
                    diff.unchanged.add(configThing);
                }
                else{
                    diff.update.add(configThing);
                }
            }
        }

        return diff;

    }

    public String toString(int indent) {
        Dump dump = new Dump();

        dump.add("THINGS DIFF: ", indent);
        dump.add("DELETE: ", indent);
        dump.add(delete.toSimpleString(indent + 1));

        dump.add("CREATE: ", indent);
        dump.add(create.toSimpleString(indent + 1));

        dump.add("UPDATE: ", indent);
        dump.add(update.toSimpleString(indent + 1));

        dump.add("UNCHANGED: ", indent);
        dump.add(unchanged.toSimpleString(indent + 1));

        return dump.toString();
    }
}
