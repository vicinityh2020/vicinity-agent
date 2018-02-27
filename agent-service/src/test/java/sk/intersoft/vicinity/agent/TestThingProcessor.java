package sk.intersoft.vicinity.agent;

import org.json.JSONArray;
import sk.intersoft.vicinity.agent.service.config.AdapterConfig;
import sk.intersoft.vicinity.agent.thing.ThingDescription;
import sk.intersoft.vicinity.agent.thing.ThingDescriptions;
import sk.intersoft.vicinity.agent.thing.ThingsProcessor;

import java.io.File;
import java.util.List;
import java.util.Scanner;

public class TestThingProcessor {

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
        JSONArray json = new JSONArray(file2string(new File("").getAbsolutePath() + "/agent-service/src/test/resources/objects/config-test.json"));

        ThingDescriptions things = ThingsProcessor.process(json, new AdapterConfig("test-id", "test-endpoint"));
        System.out.println(things.toString(0));

    }

    public static void main(String[] args) throws Exception {
        TestThingProcessor p = new TestThingProcessor();
        p.process();
    }

}
