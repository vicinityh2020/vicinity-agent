package sk.intersoft.vicinity.agent;

import org.json.JSONArray;
import sk.intersoft.vicinity.agent.thing.ThingDescription;
import sk.intersoft.vicinity.agent.thing.ThingDescriptions;
import sk.intersoft.vicinity.agent.thing.ThingsDiff;
import sk.intersoft.vicinity.agent.thing.ThingsProcessor;

import java.io.File;
import java.util.List;
import java.util.Scanner;

public class TestRegistrationDiff {

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
        JSONArray adapter = new JSONArray(file2string(new File("").getAbsolutePath() + "/agent-service/src/test/resources/objects/adapter.json"));
        JSONArray config = new JSONArray(file2string(new File("").getAbsolutePath() + "/agent-service/src/test/resources/objects/configuration.json"));

        ThingDescriptions configuration = ThingsProcessor.process(config, true);
        ThingDescriptions fromAdapter = ThingsProcessor.process(adapter, false);

        ThingsDiff diff = ThingsDiff.fire(configuration, fromAdapter);
        System.out.println(diff.toString(0));
    }

    public static void main(String[] args) throws Exception {
        TestRegistrationDiff p = new TestRegistrationDiff();
        p.process();
    }

}
