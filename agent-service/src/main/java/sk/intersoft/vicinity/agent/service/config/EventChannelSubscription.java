package sk.intersoft.vicinity.agent.service.config;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.utils.Dump;

public class EventChannelSubscription extends EventChannel {
    final static Logger logger = LoggerFactory.getLogger(EventChannelSubscription.class.getName());
    public static final String OBJECT_ID_KEY = "oid";

    public String oid = "";

    public EventChannelSubscription(String oid, EventChannel channel){
        super(channel.infrastructureId, channel.adapterId, channel.eventId);
        this.oid = oid;
    }

    public static EventChannelSubscription create(JSONObject json) throws Exception {
        logger.debug("CREATING EVENT CHANNEL SUBSCRIPTION: "+json.toString());
        return new EventChannelSubscription(json.getString(OBJECT_ID_KEY), EventChannel.create(json));
    }

    public String toString() {
        return "[SUBSCRIBE TO [oid:"+oid+" / event: "+eventId+"], SUBSCRIBER: ["+adapterId+":"+infrastructureId+"]";
    }

    public String asString(int indent) {
        Dump dump = new Dump();
        dump.add(toString(), indent);
        return dump.toString();
    }

}
