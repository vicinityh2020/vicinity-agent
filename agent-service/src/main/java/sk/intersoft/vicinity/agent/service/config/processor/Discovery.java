package sk.intersoft.vicinity.agent.service.config.processor;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.clients.NeighbourhoodManager;
import sk.intersoft.vicinity.agent.service.config.AdapterConfig;
import sk.intersoft.vicinity.agent.thing.ThingDescription;
import sk.intersoft.vicinity.agent.utils.JSONUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Discovery {
    final static Logger logger = LoggerFactory.getLogger(Discovery.class.getName());

    public static List<ThingDescription> processCreatedThings(String data,
                                                              Map<String, ThingDescription> lookup) throws Exception {
        List<ThingDescription> result = new ArrayList<ThingDescription>();

        List<JSONObject> pairs = NeighbourhoodManager.getCreateResults(data);
        logger.debug("PROCESSING CREATED OID->INFRA-ID PAIRS");
        for(JSONObject pair : pairs) {
            String oid = JSONUtil.getString(ThingDescription.OID_KEY, pair);
            if(oid == null) throw new Exception("Missing ["+ThingDescription.OID_KEY+"] in: "+pair.toString());

            String infrastructureId = JSONUtil.getString(ThingDescription.INFRASTRUCTURE_KEY, pair);
            if(infrastructureId == null) throw new Exception("Missing ["+ThingDescription.INFRASTRUCTURE_KEY+"] in: "+pair.toString());

            String password = JSONUtil.getString(ThingDescription.PASSWORD_KEY, pair);
            if(password == null) throw new Exception("Missing ["+ThingDescription.PASSWORD_KEY+"] in: "+pair.toString());

            logger.debug("PAIR: [oid: "+oid+"][infra-id: "+infrastructureId+"][password: "+password+"]");

            ThingDescription thing = lookup.get(infrastructureId);
            if(thing != null){
                thing.updateCreatedData(oid, password);
                logger.debug("RELATED THING UPDATE: \n"+thing.toSimpleString());

                result.add(thing);
            }
            else throw new Exception("UNABLE TO FIND THING FOR INFRA-ID ["+infrastructureId+"] IN DIFF DATA TO CREATE!");

        }

        return result;
    }

    public static ThingDescriptions execute(ThingDescriptions configThings,
                                            ThingDescriptions adapterThings,
                                            AdapterConfig adapter) throws Exception {
        logger.debug("EXECUTING DISCO: ");
        logger.debug("CONFIG: \n");
        logger.debug(configThings.toString(0));
        logger.debug("ADAPTER: \n");
        logger.debug(adapterThings.toString(0));

        ThingDescriptions result = new ThingDescriptions();

        ThingsDiff diff = ThingsDiff.fire(configThings, adapterThings);
        logger.info(diff.toString(0));

        // HANDLE DELETE
        logger.info("HANDLING DELETE: ");
        List<ThingDescription> toDelete = ThingDescriptions.toList(diff.delete.byAdapterOID);
        if(toDelete.size() > 0){
            NeighbourhoodManager.delete(NeighbourhoodManager.deleteThingsPayload(toDelete, adapter.agent.agentId), adapter.agent);
        }
        else{
            logger.info("..nothing to delete");
        }

        // HANDLE CREATE
        logger.info("HANDLING CREATE: ");
        List<ThingDescription> toCreateList = ThingDescriptions.toList(diff.create.byAdapterInfrastructureID);
        if(toCreateList.size() > 0){
            result.add(
                    processCreatedThings(
                            NeighbourhoodManager.create(
                                    NeighbourhoodManager.createThingsPayload(
                                            toCreateList,
                                            adapter.agent.agentId),
                                    adapter.agent),
                            diff.create.byAdapterInfrastructureID));
        }
        else{
            logger.info("..nothing to create");
        }


        // HANDLE UPDATE
        logger.info("HANDLING UPDATE: ");
        List<ThingDescription> toUpdateList = ThingDescriptions.toList(diff.update.byAdapterOID);
        if(toUpdateList.size() > 0){
            result.add(
                    processCreatedThings(
                            NeighbourhoodManager.update(
                                    NeighbourhoodManager.updateThingsPayload(
                                            toUpdateList,
                                            adapter.agent.agentId),
                                    adapter.agent),
                            diff.update.byAdapterInfrastructureID));
        }
        else{
            logger.info("..nothing to update");
        }

        // HANDLE UNCHANGED
        logger.info("HANDLING UNCHANGED: ");
        List<ThingDescription> toUnchangedList = ThingDescriptions.toList(diff.unchanged.byAdapterOID);
        if(toUnchangedList.size() > 0){
            result.add(toUnchangedList);
        }
        else{
            logger.info("..nothing unchanged to handle");
        }


        logger.info("FINAL DISCOVERED THINGS FOR: "+adapter.toSimpleString()+"\n"+result.toString(0));
        List<ThingDescription> ts = ThingDescriptions.toList(result.byAdapterOID);
        for (ThingDescription t : ts){
            logger.info("\n"+t.toString(0));
        }


        return result;
    }

}
