package sk.intersoft.vicinity.agent.service.config.processor;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.thing.ThingDescription;
import sk.intersoft.vicinity.agent.utils.Dump;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ThingDescriptions {
    final static Logger logger = LoggerFactory.getLogger(ThingDescriptions.class.getName());

    public Map<String, ThingDescription> byAdapterOID = new ConcurrentHashMap<String, ThingDescription>();
    public Map<String, ThingDescription> byAdapterInfrastructureID = new ConcurrentHashMap<String, ThingDescription>();
    public Map<String, Set<ThingDescription>> byAdapterID = new ConcurrentHashMap<String, Set<ThingDescription>>();

    public static List<ThingDescription> toList(Map<String, ThingDescription> map) {
        List<ThingDescription> things = new ArrayList<ThingDescription>();
        for (Map.Entry<String, ThingDescription> entry : map.entrySet()) {
            things.add(entry.getValue());
        }
        return things;
    }

    public void add(ThingDescription thing) throws Exception {

        if (thing.adapterOID != null && !thing.adapterOID.equals("")) {
            if (byAdapterOID.get(thing.adapterOID) != null) {
                throw new Exception("Duplicate thing with identifier [" + thing.adapterOID + "] exists!");
            }
            byAdapterOID.put(thing.oid, thing);
        }
        if (thing.adapterInfrastructureID != null && !thing.adapterInfrastructureID.equals("")) {
            if (byAdapterInfrastructureID.get(thing.adapterInfrastructureID) != null) {
                throw new Exception("Duplicate thing with identifier [" + thing.adapterInfrastructureID + "] exists!");
            }
            byAdapterInfrastructureID.put(thing.adapterInfrastructureID, thing);
        }
        Set<ThingDescription> adapterThings = byAdapterID.get(thing.adapterId);
        if (adapterThings == null) adapterThings = new HashSet<ThingDescription>();
        adapterThings.add(thing);
        byAdapterID.put(thing.adapterId, adapterThings);
    }

    public void add(List<ThingDescription> things) throws Exception {
        for (ThingDescription t : things) {
            add(t);
        }
    }

    public String toString(int indent) {
        Dump dump = new Dump();

        dump.add("THING DESCRIPTIONS:", indent);

        dump.add("BY ADAPTER-OID: " + byAdapterOID.keySet().size(), (indent + 1));
        for (Map.Entry<String, ThingDescription> entry : byAdapterOID.entrySet()) {
            String id = entry.getKey();
            dump.add("mapped-id: " + id, (indent + 2));
            dump.add(entry.getValue().toSimpleString(), (indent + 3));
        }

        dump.add("BY ADAPTER-INFRASTRUCTURE ID: " + byAdapterInfrastructureID.keySet().size(), (indent + 1));
        for (Map.Entry<String, ThingDescription> entry : byAdapterInfrastructureID.entrySet()) {
            String id = entry.getKey();
            dump.add("mapped-id: " + id, (indent + 2));
            dump.add(entry.getValue().toSimpleString(), (indent + 3));
        }

        dump.add("BY ADAPTER-ID: " + byAdapterID.keySet().size(), (indent + 1));
        for (Map.Entry<String, Set<ThingDescription>> entry : byAdapterID.entrySet()) {
            String id = entry.getKey();
            Set<ThingDescription> things = entry.getValue();
            dump.add("adapter-id: " + id, (indent + 2));
            dump.add("things: " + things.size(), (indent + 3));
            for (ThingDescription t : things) {
                dump.add(t.toSimpleString(), (indent + 3));
            }
        }

        return dump.toString();
    }

    public String toFullString(int indent) {
        Dump dump = new Dump();

        dump.add("THING DESCRIPTIONS:", indent);

        dump.add("BY ADAPTER-OID: " + byAdapterOID.keySet().size(), (indent + 1));
        for (Map.Entry<String, ThingDescription> entry : byAdapterOID.entrySet()) {
            String id = entry.getKey();
            dump.add("mapped-id: " + id, (indent + 2));
            dump.add(entry.getValue().toString(indent + 3), 0);
        }

        dump.add("BY ADAPTER-INFRASTRUCTURE ID: " + byAdapterInfrastructureID.keySet().size(), (indent + 1));
        for (Map.Entry<String, ThingDescription> entry : byAdapterInfrastructureID.entrySet()) {
            String id = entry.getKey();
            dump.add("mapped-id: " + id, (indent + 2));
            dump.add(entry.getValue().toString(indent + 3), 0);
        }

        dump.add("BY ADAPTER-ID: " + byAdapterID.keySet().size(), (indent + 1));
        for (Map.Entry<String, Set<ThingDescription>> entry : byAdapterID.entrySet()) {
            String id = entry.getKey();
            Set<ThingDescription> things = entry.getValue();
            dump.add("adapter-id: " + id, (indent + 2));
            dump.add("things: " + things.size(), (indent + 3));
            for (ThingDescription t : things) {
                dump.add(t.toSimpleString(), (indent + 3));
            }
        }

        return dump.toString();
    }

    public String toStatusString(int indent) {
        Dump dump = new Dump();

        dump.add("THING DESCRIPTIONS: " + byAdapterOID.keySet().size(), indent);

        for (Map.Entry<String, ThingDescription> entry : byAdapterOID.entrySet()) {
            String id = entry.getKey();
            dump.add(entry.getValue().toSimpleString(), (indent + 1));
        }

        return dump.toString();
    }

    public JSONArray toStatusJSON() {
        JSONArray array = new JSONArray();

        for (Map.Entry<String, ThingDescription> entry : byAdapterOID.entrySet()) {
            array.put(entry.getValue().toStatusJSON());
        }

        return array;
    }

    public JSONArray toJSON() throws Exception {
        JSONArray array = new JSONArray();

        for (Map.Entry<String, ThingDescription> entry : byAdapterOID.entrySet()) {
            array.put(ThingDescription.toJSON(entry.getValue()));
        }

        return array;
    }

}