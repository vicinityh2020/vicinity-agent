package sk.intersoft.vicinity.agent;

import org.json.JSONArray;
import org.json.JSONObject;
import sk.intersoft.vicinity.agent.thing.ThingDescription;
import sk.intersoft.vicinity.agent.thing.ThingsProcessor;

import java.io.File;
import java.util.List;
import java.util.Scanner;

public class TestThingDiff {

    public static String file2string(String path) {
        try{
            return new Scanner(new File(path)).useDelimiter("\\Z").next();
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public void process() throws Exception{
        JSONObject thing1JSON = new JSONObject(file2string(new File("").getAbsolutePath() + "/testing-adapter/src/test/resources/objects/thing1.json"));
        JSONObject thing2JSON = new JSONObject(file2string(new File("").getAbsolutePath() + "/testing-adapter/src/test/resources/objects/thing2.json"));

        ThingDescription thing1 = ThingDescription.create(thing1JSON, false);
        ThingDescription thing2 = ThingDescription.create(thing2JSON, false);
        boolean same = thing1.sameAs(thing2);
        System.out.println("SAME: "+same);
    }

    public static void main(String[] args) throws Exception {
        TestThingDiff p = new TestThingDiff();
        p.process();
    }

}
