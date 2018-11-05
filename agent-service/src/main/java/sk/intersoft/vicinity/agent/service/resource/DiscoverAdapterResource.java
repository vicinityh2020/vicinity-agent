package sk.intersoft.vicinity.agent.service.resource;

import org.json.JSONObject;
import org.restlet.data.ClientInfo;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.service.config.AdapterConfig;
import sk.intersoft.vicinity.agent.service.config.Configuration;
import sk.intersoft.vicinity.agent.thing.InteractionPattern;
import sk.intersoft.vicinity.agent.thing.ThingDescription;
import sk.intersoft.vicinity.agent.utils.JSONUtil;

public class DiscoverAdapterResource extends  AgentResource {
    final static Logger logger = LoggerFactory.getLogger(DiscoverAdapterResource.class.getName());

    private static String ADAPTER_ID = "adapter-id";

    @Post()
    public String discoverAdapter(Representation entity)  {

        try{

            logger.info("DISCOVER ADAPTER SERVICE: ");


            if(entity == null) {
                throw new Exception("Empty payload!");
            }
            String rawPayload = entity.getText();
            logger.info("PAYLOAD: " + rawPayload);
            logger.info("PAYLOAD encoding: " + entity.getCharacterSet());

            JSONObject object = new JSONObject(rawPayload);
            String adapterId = JSONUtil.getString(ADAPTER_ID, object);
            if(adapterId == null){
                throw new Exception("Unable to read adapter id from data!");
            }



            AdapterConfig adapter = Configuration.adapters.get(adapterId);
            if(adapter != null){
                boolean success = adapter.discover(rawPayload);
                if(success){
                    return gtwSuccess("Discovery for adapter ["+adapterId+"] successfully done!").toString();
                }
                else {
                    throw new Exception("Discovery for adapter ["+adapterId+"] failed! See agent logs!");
                }

            }
            else {
                throw new Exception("Adapter ["+adapterId+"] does not exist! Unable to discover!");
            }



        }
        catch(Exception e){
            logger.error("", e);
            return gtwError(e).toString();
        }

    }

}
