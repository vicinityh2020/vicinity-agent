package sk.intersoft.vicinity.agent;

import org.json.JSONObject;
import sk.intersoft.vicinity.agent.thing.DataSchema;
import sk.intersoft.vicinity.agent.thing.ThingDescription;

import java.io.File;
import java.util.Scanner;

public class TestThingDescription {

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
        JSONObject in = new JSONObject(file2string(new File("").getAbsolutePath() + "/agent-service/src/test/resources/objects/adapter-thing.json"));

        System.out.println(ThingDescription.create(in).toString(0));

    }

    public static void main(String[] args) throws Exception {
        TestThingDescription p = new TestThingDescription();
        p.process();
    }

}
