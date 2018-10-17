package sk.intersoft.vicinity.agent;

import sk.intersoft.vicinity.agent.service.config.processor.ThingDescriptions;
import sk.intersoft.vicinity.agent.service.config.processor.ThingsDiff;
import sk.intersoft.vicinity.agent.thing.InteractionPattern;
import sk.intersoft.vicinity.agent.thing.ThingDescription;

import java.util.*;

class WP6DIffExpectation {
    Set<String> delete = new HashSet<String>();
    Set<String> create = new HashSet<String>();
    Set<String> update = new HashSet<String>();
    Set<String> unchange = new HashSet<String>();

    ThingDescriptions things = new ThingDescriptions();
}

public class WP6DIffGenerator {
    // THING:
    private static final String adapterId = "adapter-id";
    private static final String thingName = "t-name";
    private static final String thingType = "t-type";

    // PROP
    private static final String pid = "property-id";
    private static final String patternReference = "reffers-to";


    // DATE TIME UTILS: START
    public static long millis(){
        return System.currentTimeMillis();
    }


    public static long duration(long ms) {
        return (millis() - ms);
    }

    public static String hours(long ms) {
        return ((ms / (1000 * 60 * 60)) % 24)+"";
    }

    public static String minutes(long ms) {
        return ((ms / (1000 * 60)) % 60)+"";
    }

    public static String seconds(long ms) {
        return ((ms / 1000) % 60)+"";
    }

    public static String format(long time) {
        return time + "ms :: "+hours(time)+":"+minutes(time)+":"+seconds(time);
    }
    // DATE TIME UTILS: END


    private static Map<String, String> oid2iid =  new HashMap<String, String>();

    private ThingDescriptions init(int num) throws Exception {
        ThingDescriptions things = new ThingDescriptions();
        for(int i = 1; i <= num; i++){
            String oid = "oid-"+i;
            String iid = "iid-"+i;
            oid2iid.put(oid, iid);
            things.add(configThing(oid, iid));
        }
        return things;
    }

    private String oid2iid(String oid) throws Exception{
        String iid = oid2iid.get(oid);
        if(iid == null) throw new Exception("OID ["+oid+"] does not exist");

        return iid;
    }

    private InteractionPattern property(String pid){
        InteractionPattern p = new InteractionPattern();
        p.id = pid;
        p.refersTo = patternReference;
        return p;
    }

    private void addProperty(ThingDescription thing, InteractionPattern property){
        thing.properties.put(property.id, property);
    }

    private ThingDescription thing(){
        ThingDescription t = new ThingDescription();
        t.adapterId = adapterId;
        t.name = thingName;
        t.type = thingType;

        addProperty(t, property(pid));

        return t;
    }

    private ThingDescription configThing(String oid, String iid) throws Exception {
        ThingDescription t = thing();
        t.oid = oid;
        t.adapterInfrastructureID = ThingDescription.identifier(adapterId, iid);
        t.password = "...";

        return t;
    }

    private ThingDescription adapterThing(String iid) throws Exception {
        ThingDescription t = thing();
        t.infrastructureId = iid;
        t.adapterInfrastructureID = ThingDescription.identifier(adapterId, iid);

        return t;
    }
    private ThingDescription update(ThingDescription t) throws Exception {
        addProperty(t, property("new pid"));
        return t;
    }

    private WP6DIffExpectation stub(ThingDescriptions config,
                                    int create,
                                    int update,
                                    int delete) throws Exception {
        WP6DIffExpectation expectation = new WP6DIffExpectation();

        int size = config.byAdapterInfrastructureID.keySet().size();

        System.out.println("GENERATE ADAPTER STUB");
        System.out.println("config: "+size);
        System.out.println("EXPECTATION: ");
        System.out.println("create: "+create);
        System.out.println("update: "+update);
        System.out.println("delete: "+delete);
        if((update + delete) > size) throw new Exception("update + delete > things in config");

        int unchange = size - (update + delete);
        System.out.println("unchanged: "+unchange);

        int finalSize = create + update + unchange;
        System.out.println("final things in adapter: "+finalSize);

        List<ThingDescription> things = ThingDescriptions.toList(config.byAdapterInfrastructureID);
        Collections.shuffle(things);
        things.sort(Comparator.comparing(ThingDescription::getOID));
        System.out.println("things as sorted list: "+things.size());
        for(ThingDescription t : things) {
            System.out.println("  "+t.toSimpleString());
        }


        System.out.println("LETS FUCK!");


        int dstart = 0;
        int dend = delete;
        System.out.println("DELETE : "+delete+ " :: interval: " +dstart+ " -> "+dend);
        for(int i = dstart; i < dend; i++){
            ThingDescription t = things.get(i);
            System.out.println("  deleting (not adding to adapter things): "+t.toSimpleString());

            expectation.delete.add(t.oid);
        }

        System.out.println("CREATE: "+create);
        for(int i = 0; i < create; i++){
            String iid = UUID.randomUUID().toString();
            ThingDescription c = adapterThing(iid);
            System.out.println("  creating: "+iid + " :: "+c.toSimpleString());

            expectation.create.add(iid);
            expectation.things.add(c);
        }

        int upstart = dend;
        int upend = dend + update;
        System.out.println("UPDATE: "+update+ " :: interval: 0 -> "+update);
        for(int i = upstart; i < upend; i++){
            ThingDescription t = things.get(i);

            String iid = oid2iid(t.oid);
            ThingDescription u = update(adapterThing(iid));
            System.out.println("  updating: "+iid + " :: "+u.toSimpleString());

            expectation.update.add(t.oid);
            expectation.things.add(u);
        }


        int unstart = upend;
        int unend = upend + unchange;
        System.out.println("UNCHANGED : "+unchange+ " :: interval: " +unstart+ " -> "+unend);
        for(int i = unstart; i < unend; i++){
            ThingDescription t = things.get(i);
            String iid = oid2iid(t.oid);
            ThingDescription u = adapterThing(iid);
            System.out.println("  unchanging: "+iid + " :: "+u.toSimpleString());

            expectation.unchange.add(t.oid);
            expectation.things.add(u);
        }


        return expectation;
    }

    private Set<String> oids(ThingDescriptions desc) {
        List<ThingDescription> things =  ThingDescriptions.toList(desc.byAdapterInfrastructureID);
        Set<String> oids = new HashSet<String>();
        for(ThingDescription t : things) {
            oids.add(t.oid);
        }
        return oids;
    }
    private Set<String> iids(ThingDescriptions desc) {
        List<ThingDescription> things =  ThingDescriptions.toList(desc.byAdapterInfrastructureID);
        Set<String> iids = new HashSet<String>();
        for(ThingDescription t : things) {
            iids.add(t.infrastructureId);
        }
        return iids;
    }


    public static void main(String[] args) throws Exception {

        WP6DIffGenerator g = new WP6DIffGenerator();

        ThingDescriptions config = g.init(300);
        WP6DIffExpectation expectation = g.stub(config, 2, 1, 1);


//        System.out.println("CONFIG: \n"+config.toFullString(0));
//        System.out.println("ADAPTER: \n"+expectation.things.toFullString(0));


        long start = millis();
        ThingsDiff diff = ThingsDiff.fire(config, expectation.things);
        long end = duration(start);
        System.out.println("DIFF TOOK: "+format(end));



        System.out.println("DIFF: ");
        System.out.println(diff.toString(0));

        System.out.println("COMPARING EXPECTATIONS: ");
        System.out.println("DELETE: ");
        Set<String> delete = g.oids(diff.delete);
        System.out.println("  expected: "+expectation.delete);
        System.out.println("  real: "+delete);
        System.out.println("  match: "+delete.equals(expectation.delete));

        System.out.println("CREATE: ");
        Set<String> create = g.iids(diff.create);
        System.out.println("  expected: "+expectation.create);
        System.out.println("  real: "+create);
        System.out.println("  match: "+create.equals(expectation.create));

        System.out.println("UPDATE: ");
        Set<String> update = g.oids(diff.update);
        System.out.println("  expected: "+expectation.update);
        System.out.println("  real: "+update);
        System.out.println("  match: "+update.equals(expectation.update));

        System.out.println("UNCHANGE: ");
        Set<String> unchange = g.oids(diff.unchanged);
        System.out.println("  expected: "+expectation.unchange);
        System.out.println("  real: "+unchange);
        System.out.println("  match: "+unchange.equals(expectation.unchange));


        System.out.println("DIFF TOOK: "+format(end));

    }
}
