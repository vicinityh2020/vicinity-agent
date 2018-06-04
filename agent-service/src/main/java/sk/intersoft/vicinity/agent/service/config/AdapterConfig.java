package sk.intersoft.vicinity.agent.service.config;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.clients.AdapterClient;
import sk.intersoft.vicinity.agent.clients.GatewayAPIClient;
import sk.intersoft.vicinity.agent.db.PersistedThing;
import sk.intersoft.vicinity.agent.service.config.processor.Discovery;
import sk.intersoft.vicinity.agent.service.config.processor.ThingDescriptions;
import sk.intersoft.vicinity.agent.service.config.processor.ThingProcessor;
import sk.intersoft.vicinity.agent.service.resource.AgentResource;
import sk.intersoft.vicinity.agent.thing.InteractionPattern;
import sk.intersoft.vicinity.agent.thing.ThingDescription;
import sk.intersoft.vicinity.agent.thing.ThingValidator;
import sk.intersoft.vicinity.agent.utils.Dump;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class AdapterConfig {
    final static Logger logger = LoggerFactory.getLogger(AdapterConfig.class.getName());

    public AgentConfig agent = null;

    public AdapterConfig(AgentConfig agent){
        this.agent = agent;
    }

    private static final String ADAPTER_ID_KEY = "adapter-id";
    private static final String ENDPOINT_KEY = "endpoint";
    private static final String ACTIVE_DISCOVERY_KEY = "active-discovery";
    private static final String EVENTS_KEY = "events";
    private static final String OPEN_EVENT_CHANNELS_KEY = "channels";
    private static final String SUBSCRIBE_EVENT_CHANNELS_KEY = "subscriptions";


    public String adapterId = "";
    public String endpoint = null;
    public boolean activeDiscovery = false;

    ThingDescriptions things = new ThingDescriptions();

    public static List<EventChannel> eventChannels = new ArrayList<EventChannel>();
    public static List<EventChannelSubscription> eventSubscriptions = new ArrayList<EventChannelSubscription>();

    private boolean configurationRunning = false;


    public void login(){
        logger.debug("log-in all things of adapter "+toSimpleString());
        for(ThingDescription thing : things.byAdapterOID.values()){
            logger.debug("log-in: ["+thing.oid+"]");
            try{
                GatewayAPIClient.login(thing.oid, thing.password);
            }
            catch(Exception e) {
                logger.error("log-out error for ["+thing.oid+"]!", e);
            }

        }
    }

    public void logout(){
        logger.debug("log-out all things of adapter "+toSimpleString());
        for(ThingDescription thing : things.byAdapterOID.values()){
            logger.debug("log-out: ["+thing.oid+"]");
            try{
                GatewayAPIClient.logout(thing.oid, thing.password);
            }
            catch(Exception e) {
                logger.error("log-out error for [" + thing.oid + "]!", e);
            }

        }
    }


    public void updatePersistence(ThingDescriptions things) throws Exception {
        PersistedThing.clearAdapter(adapterId);
        logger.debug("persistence cleared for adapter : "+toSimpleString());
        for (Map.Entry<String, ThingDescription> entry : things.byAdapterOID.entrySet()) {
            ThingDescription thing = entry.getValue();
            PersistedThing.save(thing);
        }
        logger.debug("persistence updated for adapter "+toSimpleString());
        PersistedThing.list();

    }

    public void clearMappings(){
        logger.debug("CLEANUP FOR ADAPTER "+toSimpleString());
        logout();

        ThingDescriptions toRemove = Configuration.things.get(adapterId);
        if(toRemove != null){
            Configuration.things.remove(adapterId);
            for (Map.Entry<String, ThingDescription> entry : toRemove.byAdapterOID.entrySet()) {
                ThingDescription t = entry.getValue();
                if(Configuration.thingsByOID.get(t.oid) != null){
                    Configuration.thingsByOID.remove(t.oid);
                }
            }

            logger.debug("ADAPTER ["+adapterId+"] things removed from configuration");
        }
        logger.debug("CLEANUP FOR ADAPTER "+toSimpleString()+ ": DONE");
    }

    public void updateMappings(ThingDescriptions discoveredThings){
        logger.debug("UPDATING MAPPINGS FOR ADAPTER "+toSimpleString());
        things = discoveredThings;
        Configuration.things.put(adapterId, discoveredThings);
        for (Map.Entry<String, ThingDescription> entry : discoveredThings.byAdapterOID.entrySet()) {
            ThingDescription t = entry.getValue();
            Configuration.thingsByOID.put(t.oid, t);
        }

    }


    public static void openEventChannel(ThingDescription thing, String eventId) throws Exception {
        InteractionPattern event = thing.getInteractionPattern(eventId, InteractionPattern.EVENT);
        GatewayAPIClient.post(GatewayAPIClient.getOpenEventChannelEndpoint(eventId), null, thing.oid, thing.password);

    }

    private void openEventChannels(){
        logger.debug("OPENING EVENT CHANNELS FOR ADAPTER "+toSimpleString());

        for(EventChannel e : eventChannels){
            logger.debug("OPENING EVENT CHANNEL: "+e.toString());
            try{
                ThingDescription thing = things.byAdapterInfrastructureID.get(ThingDescription.identifier(e.infrastructureId, adapterId));
                if(thing != null){
                    logger.debug("PUBLISHER THING: "+thing.toSimpleString());
                    openEventChannel(thing, e.eventId);
                }
                else throw new Exception("thing with [infrastructure-id:"+e.infrastructureId+"] does not exist!");
            }
            catch (Exception ex){
                logger.error("unable to open channel: "+e.toString(), ex);
            }
        }

    }

    public static void subscribeEventChannel(ThingDescription thing, String oid, String eventId) throws Exception {
        GatewayAPIClient.post(GatewayAPIClient.getSubscribeEventChannelEndpoint(oid, eventId), null, thing.oid, thing.password);

    }

    private void subscribeEventChannels(){
        logger.debug("SUBSCRIBING EVENT CHANNELS FOR ADAPTER "+toSimpleString());

        for(EventChannelSubscription e : eventSubscriptions){
            logger.debug("SUBSCRIBING TO EVENT CHANNEL: "+e.toString());
            try{
                ThingDescription thing = things.byAdapterInfrastructureID.get(ThingDescription.identifier(e.infrastructureId, adapterId));
                if(thing != null){
                    logger.debug("SUBSCRIBING THING: "+thing.toSimpleString());
                    subscribeEventChannel(thing, e.oid, e.eventId);
                }
                else throw new Exception("thing with [infrastructure-id:"+e.infrastructureId+"] does not exist!");
            }
            catch (Exception ex){
                logger.error("unable to subscribe to channel: "+e.toString(), ex);
            }
        }

    }


    private void start() {
        configurationRunning = true;
    }
    private void stop() {
        configurationRunning = false;
    }
    private boolean isRunning() {
        return configurationRunning;
    }

    public boolean discover(String data) {
        if (isRunning()) {
            logger.info("NOT DISCOVERING ADAPTER: [" + adapterId + "] .. another process is actually using this configuration!");
            return false;
        }

        start();
        boolean success = discoverAdapter(data);
        stop();

        return success;
    }

    public boolean discover() {
        try{
            logger.debug("DISCOVERY FOR ADAPTER ["+adapterId+"] .. getting data from adapter");
            String data = AdapterClient.get(AdapterClient.objectsEndpoint(endpoint));
            return discover(data);
        }
        catch(Exception e){
            logger.error("DISCOVERY FAILED FOR: "+toSimpleString(), e);
        }
        return false;
    }

    public ThingDescriptions getAdapterThings(String data) throws Exception {
        ThingDescriptions adapterThings = new ThingDescriptions();
        List<JSONObject> objects = ThingProcessor.processAdapter(data, adapterId);
        logger.debug("parsing adapter things ... ");
        for (JSONObject object : objects) {
            logger.debug(object.toString());
            ThingValidator validator = new ThingValidator(true);
            ThingDescription thing = validator.create(object);
            if (thing != null) {
                logger.debug("processed thing: " + thing.oid);

                // TRANSFORM OID -> INFRASTRUCTURE
                thing.toInfrastructure();

                adapterThings.add(thing);
            }
            else {
                logger.debug("unprocessed thing! validator errors: \n" + validator.failureMessage().toString(2));
                throw new Exception("unprocessed adapter thing: "+toSimpleString());
            }
        }
        return adapterThings;
    }

    public boolean discoverAdapter(String data) {
        logger.debug("DISCOVERY FOR ADAPTER ["+adapterId+"] .. agent ["+agent.agentId+"]");

        clearMappings();



        try{
            ThingDescriptions adapterThings = getAdapterThings(data);

            logger.debug("PROCESSED ADAPTER THINGS: "+adapterThings.byAdapterInfrastructureID.keySet().size());
            logger.debug("\n" + adapterThings.toString(0));

            agent.updateLastConfiguration();
            ThingDescriptions configurationThings = agent.configurationThingsForAdapter(adapterId);

            ThingDescriptions discoveredThings =  Discovery.execute(configurationThings, adapterThings, this);

            updatePersistence(discoveredThings);

            updateMappings(discoveredThings);


            login();

            openEventChannels();
            subscribeEventChannels();

            return true;

        }
        catch(Exception e){
            logger.error("DISCOVERY FAILED FOR: "+toSimpleString(), e);
        }
        return false;
    }

    public static AdapterConfig create(JSONObject json, AgentConfig agentConfig) throws Exception {
        logger.debug("CREATING ADAPTER CONFIG FROM: \n"+json.toString(2));

        AdapterConfig config = new AdapterConfig(agentConfig);

        config.adapterId = json.getString(ADAPTER_ID_KEY);
        if(json.has(ENDPOINT_KEY)){
            config.endpoint = json.getString(ENDPOINT_KEY);

            if(json.has(ACTIVE_DISCOVERY_KEY)){
                config.activeDiscovery = json.getBoolean(ACTIVE_DISCOVERY_KEY);
            }
        }
        else{
            logger.debug("no endpoint! setting active discovery to TRUE");
            config.activeDiscovery = true;
        }

        if(json.has(EVENTS_KEY)){
            JSONObject events = json.getJSONObject(EVENTS_KEY);
            if(events.has(OPEN_EVENT_CHANNELS_KEY)){
                JSONArray channelsArray = events.getJSONArray(OPEN_EVENT_CHANNELS_KEY);
                Iterator<Object> it = channelsArray.iterator();
                while(it.hasNext()){
                    JSONObject obj = (JSONObject)it.next();
                    EventChannel c = EventChannel.create(obj, config);
                    eventChannels.add(c);
                }
            }

            if(events.has(SUBSCRIBE_EVENT_CHANNELS_KEY)){
                JSONArray channelsArray = events.getJSONArray(SUBSCRIBE_EVENT_CHANNELS_KEY);
                Iterator<Object> it = channelsArray.iterator();
                while(it.hasNext()){
                    JSONObject obj = (JSONObject)it.next();
                    EventChannelSubscription c = EventChannelSubscription.create(obj, config);
                    eventSubscriptions.add(c);
                }
            }

        }


        return config;
    }

    public String toString(int indent) {
        Dump dump = new Dump();

        dump.add("ADAPTER CONFIG: ", indent);

        dump.add("adapter-id: [" + adapterId + "]", (indent + 1));
        dump.add("endpoint: " + endpoint, (indent + 1));
        dump.add("active disco: " + activeDiscovery, (indent + 1));

        return dump.toString();
    }

    public String toStatusString(int indent) {
        Dump dump = new Dump();

        dump.add("ADAPTER CONFIG: ["+adapterId+"]", indent);

        dump.add("ADAPTER THINGS: ", (indent + 1));
        List<ThingDescription> list = ThingDescriptions.toList(things.byAdapterOID);
        for(ThingDescription t : list){
            dump.add(t.toSimpleString(), (indent + 2));
        }

        dump.add("OPEN CHANNELS: ", (indent + 1));
        for(EventChannel e : eventChannels){
            dump.add(e.toString(), (indent + 2));
        }
        dump.add("SUBSCRIBE CHANNELS: ", (indent + 1));
        for(EventChannelSubscription e : eventSubscriptions){
            dump.add(e.toString(), (indent + 2));
        }

        return dump.toString();
    }

    public JSONObject toJSON() {
        JSONObject object = new JSONObject();
        JSONArray thingsArray = new JSONArray();
        JSONArray channelsArray = new JSONArray();
        JSONArray subscriptionsArray = new JSONArray();

        object.put("adapter-id", adapterId);
        object.put("things", thingsArray);
        object.put("open-channels", channelsArray);
        object.put("subscribe-channels", subscriptionsArray);


        List<ThingDescription> list = ThingDescriptions.toList(things.byAdapterOID);
        for(ThingDescription t : list){
            thingsArray.put(t.toStatusJSON());
        }

        for(EventChannel e : eventChannels){
            channelsArray.put(e.toJSON());
        }
        for(EventChannelSubscription e : eventSubscriptions){
            subscriptionsArray.put(e.toJSON());
        }

        return object;
    }

    public String toSimpleString() {
        return "[ADAPTER: " + adapterId + " [agent-id: "+agent.agentId+"] [active-disco: "+activeDiscovery+"]]";
    }


}
