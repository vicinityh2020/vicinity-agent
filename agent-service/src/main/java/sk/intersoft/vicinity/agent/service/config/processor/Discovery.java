package sk.intersoft.vicinity.agent.service.config.processor;

import org.json.JSONArray;
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
                                                              ThingDescriptions lookup) throws Exception {
        List<ThingDescription> result = new ArrayList<ThingDescription>();

        List<JSONObject> pairs = NeighbourhoodManager.getCreateResults(data);
        logger.debug("PROCESSING CREATED OID->INFRA-ID PAIRS");
        for(JSONObject pair : pairs) {
            String infrastructureId = JSONUtil.getString(ThingDescription.INFRASTRUCTURE_KEY, pair);
            if(infrastructureId == null) throw new Exception("Missing ["+ThingDescription.INFRASTRUCTURE_KEY+"] in: "+pair.toString());

            if(pair.has("error")){
                Object err = pair.get("error");
                if(err instanceof JSONArray){
                    throw new Exception("SEMANTIC VALIDATION ERRORS FOR THING ["+infrastructureId+"]: "+((JSONArray)err).toString()) ;
                }
            }


            String oid = JSONUtil.getString(ThingDescription.OID_KEY, pair);
            if(oid == null) throw new Exception("Missing ["+ThingDescription.OID_KEY+"] in: "+pair.toString());


            String password = JSONUtil.getString(ThingDescription.PASSWORD_KEY, pair);
            if(password == null) throw new Exception("Missing ["+ThingDescription.PASSWORD_KEY+"] in: "+pair.toString());

            logger.debug("PAIR: [oid: "+oid+"][infra-id: "+infrastructureId+"][password: "+password+"]");

            ThingDescription thing = lookup.byAdapterInfrastructureID.get(infrastructureId);
            if(thing != null){
                thing.updateCreatedData(oid, password);
                logger.debug("RELATED THING UPDATE: \n"+thing.toSimpleString());

                result.add(thing);
            }
            else throw new Exception("UNABLE TO FIND THING FOR INFRA-ID ["+infrastructureId+"] IN DIFF DATA TO CREATE!");

        }

        return result;
    }

    public static List<ThingDescription> processUpdatedThings(String data,
                                                              ThingDescriptions lookup) throws Exception {
        List<ThingDescription> result = new ArrayList<ThingDescription>();

        List<JSONObject> objects = NeighbourhoodManager.getCreateResults(data);
        logger.debug("PROCESSING UPDATED DATA");
        for(JSONObject o : objects) {

            String oid = JSONUtil.getString(ThingDescription.OID_KEY, o);
            if(oid == null) throw new Exception("Missing ["+ThingDescription.OID_KEY+"] in: "+o.toString());

            if(o.has("error")){
                Object err = o.get("error");
                if(err instanceof JSONArray){
                    throw new Exception("SEMANTIC VALIDATION ERRORS FOR THING ["+oid+"]: "+((JSONArray)err).toString()) ;
                }
            }


            logger.debug("THING DATA: [oid: "+oid+"]");

            ThingDescription thing = lookup.byAdapterOID.get(oid);
            if(thing != null){
                logger.debug("RELATED THING UPDATE: \n"+thing.toSimpleString());
                result.add(thing);
            }
            else throw new Exception("UNABLE TO FIND THING FOR OID ["+oid+"] IN DIFF DATA TO UPDATE!");

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
//        logger.debug(adapterThings.toString(0));
        logger.debug(adapterThings.toFullString(0));

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
                            diff.create));
        }
        else{
            logger.info("..nothing to create");
        }


        // HANDLE CREATE
        logger.info("HANDLING UPDATE: ");
        // HANDLE UPDATE UNHANGED CONTENT
        logger.info("HANDLING UPDATE of content of unchanged things: ");
        List<ThingDescription> toUpdateList = ThingDescriptions.toList(diff.update.byAdapterOID);
        if(toUpdateList.size() > 0){
            result.add(
                    processUpdatedThings(
                            NeighbourhoodManager.update(
                                    NeighbourhoodManager.updateThingsPayload(
                                            toUpdateList,
                                            adapter.agent.agentId),
                                    adapter.agent),
                            diff.update));
        }
        else{
            logger.info("..nothing to update");
        }

        // HANDLE UPDATE UNHANGED CONTENT
        logger.info("HANDLING UPDATE of content of unchanged things: ");
        List<ThingDescription> toUpdateContentList = ThingDescriptions.toList(diff.unchanged.byAdapterOID);
        if(toUpdateContentList.size() > 0){
            result.add(
                    processUpdatedThings(
                            NeighbourhoodManager.updateContent(
                                    NeighbourhoodManager.updateThingsPayload(
                                            toUpdateContentList,
                                            adapter.agent.agentId),
                                    adapter.agent),
                            diff.unchanged));
        }
        else{
            logger.info("..nothing to update content (for unchanged)");
        }



        logger.info("FINAL DISCOVERED THINGS FOR: "+adapter.toSimpleString()+"\n"+result.toString(0));
        List<ThingDescription> ts = ThingDescriptions.toList(result.byAdapterOID);
        for (ThingDescription t : ts){
            logger.info("\n"+t.toString(0));
        }


        return result;
    }

}
