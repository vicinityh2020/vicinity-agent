package sk.intersoft.vicinity.agent;

import org.json.JSONArray;
import sk.intersoft.vicinity.agent.thing.ThingDescription;
import sk.intersoft.vicinity.agent.thing.ThingDescriptions;
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

        List<ThingDescription> toCreate = configuration.thingsToCreate(fromAdapter);
        System.out.println("TO CREATE: "+toCreate.size());
        for(ThingDescription t : toCreate){
            System.out.println("> infrastructure-id: ["+t.infrastructureID +"] -> oid: ["+t.oid+"]");
        }

        List<ThingDescription> toRemove = configuration.thingsToRemove(fromAdapter);
        System.out.println("TO REMOVE: "+toRemove.size());
        for(ThingDescription t : toRemove){
            System.out.println("> infrastructure-id: ["+t.infrastructureID +"] -> oid: ["+t.oid+"]");
        }

        List<ThingDescription> toUpdate = configuration.thingsToUpdate(fromAdapter);
        System.out.println("TO UPDATE: "+toUpdate.size());
        for(ThingDescription t : toUpdate){
            System.out.println("> infrastructure-id: ["+t.infrastructureID +"] -> oid: ["+t.oid+"]");
        }
    }

    public static void main(String[] args) throws Exception {
        TestRegistrationDiff p = new TestRegistrationDiff();
        p.process();
    }

}
