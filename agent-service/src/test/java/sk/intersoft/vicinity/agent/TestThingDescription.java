package sk.intersoft.vicinity.agent;

import org.json.JSONObject;
import sk.intersoft.vicinity.agent.thing.ThingDescription;
import sk.intersoft.vicinity.agent.thing.ThingValidator;

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

        ThingValidator validator = new ThingValidator(true);
        try{
            System.out.println(ThingDescription.create(in, validator).toString(0));
        }
        catch(Exception e){}
        System.out.println("FAILED: " +validator.failed());
        System.out.println(validator.failure().toString(2));

    }

    public static void main(String[] args) throws Exception {
        TestThingDescription p = new TestThingDescription();
        p.process();
    }

}
