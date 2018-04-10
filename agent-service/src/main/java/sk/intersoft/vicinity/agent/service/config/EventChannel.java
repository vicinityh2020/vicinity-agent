package sk.intersoft.vicinity.agent.service.config;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.utils.Dump;
import sk.intersoft.vicinity.agent.utils.JSONUtil;

public class EventChannel {
    final static Logger logger = LoggerFactory.getLogger(EventChannel.class.getName());
    public static final String INFRASTRUCTURE_ID_KEY = "infrastructure-id";
    public static final String ADAPTER_ID_KEY = "adapter-id";
    public static final String EVENT_ID_KEY = "eid";

    public String infrastructureId = "";
    public String adapterId = "";
    public String eventId = "";

    public EventChannel(String infrastructureId, String adapterId, String eid){
        this.infrastructureId = infrastructureId;
        this.adapterId = adapterId;
        this.eventId = eid;
    }

    public static EventChannel create(JSONObject json) throws Exception {
        logger.debug("CREATING EVENT CHANNEL: "+json.toString());
        String infrastructureId = json.getString(INFRASTRUCTURE_ID_KEY);
        String eventId = json.getString(EVENT_ID_KEY);

        String adapterId = AdapterConfig.DEFAULT_ADAPTER_ID;
        if(json.has(ADAPTER_ID_KEY)){
            adapterId = json.getString(ADAPTER_ID_KEY);
        }

        return new EventChannel(infrastructureId, adapterId, eventId);
    }

    public String toString() {
        return "["+adapterId+":"+infrastructureId+" :: "+eventId+"]";
    }

    public String asString(int indent) {
        Dump dump = new Dump();
        dump.add("EVENT CHANNEL: " + toString(), indent);
        return dump.toString();
    }

}
