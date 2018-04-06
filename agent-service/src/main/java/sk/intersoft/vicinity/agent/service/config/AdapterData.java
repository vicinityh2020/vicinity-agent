package sk.intersoft.vicinity.agent.service.config;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.utils.Dump;
import sk.intersoft.vicinity.agent.utils.JSONUtil;

public class AdapterData {
    final static Logger logger = LoggerFactory.getLogger(AdapterData.class.getName());
    public static final String ADAPTER_ID_KEY = "adapter-id";
    public static final String THING_DESCRIPTIONS_KEY = "thing-descriptions";

    public String adapterId = "";
    public JSONArray things;
    public AdapterConfig config;

    public AdapterData(String adapterId, JSONArray data, AdapterConfig config){
        this.adapterId = adapterId;
        this.things = data;
        this.config = config;
    }

    public static AdapterData create(AdapterConfig adapterConfig, String data) throws Exception {
        logger.debug("CREATING ADAPTER DATA FROM: "+adapterConfig);
        try{
            JSONObject obj = new JSONObject(data);
            String adapterId = JSONUtil.getString(ADAPTER_ID_KEY, obj);
            JSONArray things = obj.getJSONArray(THING_DESCRIPTIONS_KEY);
            return new AdapterData(adapterId, things, adapterConfig);
        }
        catch (Exception e){
            try{
                JSONArray array = new JSONArray(data);
                return new AdapterData(null, array, adapterConfig);
            }
            catch(Exception ex){
                throw new Exception("unable to process adapter data from ["+adapterConfig.endpoint+"]");
            }
        }
    }

    public String asString(int indent) {
        Dump dump = new Dump();

        dump.add("ADAPTER DATA: ", indent);

        dump.add("adapter-id: [" + adapterId + "]", (indent + 1));

        return dump.toString();
    }

    public String toString() {
        return "["+adapterId+"]";
    }

}
