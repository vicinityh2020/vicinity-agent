package sk.intersoft.vicinity.agent.service.config.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.thing.ThingDescription;
import sk.intersoft.vicinity.agent.utils.Dump;

import java.util.Map;

public class ThingsDiff {
    final static Logger logger = LoggerFactory.getLogger(ThingsDiff.class.getName());

    public ThingDescriptions create = new ThingDescriptions();
    public ThingDescriptions delete = new ThingDescriptions();
    public ThingDescriptions update = new ThingDescriptions();
    public ThingDescriptions unchanged = new ThingDescriptions();

    public static ThingsDiff fire(ThingDescriptions config, ThingDescriptions adapter) {
        ThingsDiff diff = new ThingsDiff();

        logger.debug("MAKING DIFF ... ");

        // always look at infrastructure-id .. it was attached to config from database when thing was created

        // find things to create:
        // config infra-id is set from database, when thing is created
        // create adapter thing with infra-id that does not exist in config
        for (Map.Entry<String, ThingDescription> entry : adapter.byAdapterInfrastructureID.entrySet()) {
            String infrastructureID = entry.getKey();
            ThingDescription adapterThing = entry.getValue();
            if(config.byAdapterInfrastructureID.get(infrastructureID) == null){
                try {
                    diff.create.add(adapterThing);
                }
                catch(Exception e){
                    logger.error("", e);
                }
            }
        }

        // find things to delete:
        // configuration thing with infrastructure-id is not presented in adapter
        for (Map.Entry<String, ThingDescription> entry : config.byAdapterInfrastructureID.entrySet()) {
            String infrastructureID = entry.getKey();
            ThingDescription configThing = entry.getValue();
            if(adapter.byAdapterInfrastructureID.get(infrastructureID) == null){
                try{
                    diff.delete.add(configThing);
                }
                catch(Exception e){
                    logger.error("", e);
                }
            }
        }
        // configuration thing without infrastructure-id .. does not have persistence, must be removed
        for (Map.Entry<String, ThingDescription> entry : config.byAdapterOID.entrySet()) {
            ThingDescription configThing = entry.getValue();
            if(configThing.adapterInfrastructureID == null){
                try{
                    diff.delete.add(configThing);
                }
                catch(Exception e){
                    logger.error("", e);
                }
            }
        }

        // find things to update:
        // configuration thing with infrastructure-id is presented in adapter
        // are same: unchanged
        // are different: update
        for (Map.Entry<String, ThingDescription> entry : config.byAdapterInfrastructureID.entrySet()) {
            String infrastructureID = entry.getKey();
            if(adapter.byAdapterInfrastructureID.get(infrastructureID) != null){
                ThingDescription configThing = entry.getValue();
                ThingDescription adapterThing = adapter.byAdapterInfrastructureID.get(infrastructureID);
                adapterThing.updateCredentials(configThing);
                try{
                    if(ThingDiff.isSame(configThing, adapterThing)){
                        diff.unchanged.add(adapterThing);
                    }
                    else{
                        diff.update.add(adapterThing);
                    }
                }
                catch(Exception e){
                    logger.error("", e);
                }
            }
        }

        return diff;

    }

    public String toString(int indent) {
        Dump dump = new Dump();

        dump.add("THINGS DIFF: ", indent);
        dump.add("DELETE: ", indent);
        dump.add(delete.toString(indent + 1));

        dump.add("CREATE: ", indent);
        dump.add(create.toString(indent + 1));

        dump.add("UPDATE: ", indent);
        dump.add(update.toString(indent + 1));

        dump.add("UNCHANGED: ", indent);
        dump.add(unchanged.toString(indent + 1));

        return dump.toString();
    }
}
