package sk.intersoft.vicinity.agent.service.config;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.utils.Dump;

public class EventChannel {
    final static Logger logger = LoggerFactory.getLogger(EventChannel.class.getName());
    public static final String INFRASTRUCTURE_ID_KEY = "infrastructure-id";
    public static final String EVENT_ID_KEY = "eid";

    public String infrastructureId = "";
    public String eventId = "";
    public AdapterConfig adapter = null;

    public EventChannel(String infrastructureId, String eid, AdapterConfig adapter){
        this.infrastructureId = infrastructureId;
        this.eventId = eid;
        this.adapter = adapter;
    }

    public static EventChannel create(JSONObject json, AdapterConfig adapter) throws Exception {
        logger.debug("CREATING EVENT CHANNEL: "+json.toString());
        String infrastructureId = json.getString(INFRASTRUCTURE_ID_KEY);
        String eventId = json.getString(EVENT_ID_KEY);


        return new EventChannel(infrastructureId, eventId, adapter);
    }

    public String toString() {
        return "["+adapter.adapterId+":"+infrastructureId+" :: event:"+eventId+"]";
    }

    public String toString(int indent) {
        Dump dump = new Dump();
        dump.add("EVENT CHANNEL: " + toString(), indent);
        return dump.toString();
    }

    public JSONObject toJSON() {
        JSONObject object = new JSONObject();
        object.put("infrastructure-id", infrastructureId);
        object.put("eid", eventId);
        return object;
    }

}
