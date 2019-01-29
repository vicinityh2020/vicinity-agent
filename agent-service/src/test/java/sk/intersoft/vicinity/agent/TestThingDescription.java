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
//        JSONObject in = new JSONObject(file2string(new File("").getAbsolutePath() + "/agent-service/src/test/resources/objects/adapter-thing.json"));
        JSONObject in = new JSONObject(file2string(new File("").getAbsolutePath() + "/agent-service/src/test/resources/objects/test-2.json"));

        ThingValidator validator = new ThingValidator(false);
        try{
            ThingDescription t = ThingDescription.create(in, validator);
            System.out.println("READABLE: \n"+t.toString(0));
            System.out.println("JSON: \n"+ThingDescription.toJSON(t).toString(2));

            t.jsonExtension.put("t-key-1", "t-val-1");
            t.jsonExtension.put("t-key-2", "t-val-2");

            t.properties.values().iterator().next().jsonExtension.put("p-key-1", "p-val-1");
            t.properties.values().iterator().next().jsonExtension.put("p-key-2", "p-val-2");

            t.actions.values().iterator().next().jsonExtension.put("a-key-1", "a-val-1");
            t.actions.values().iterator().next().jsonExtension.put("a-key-2", "a-val-2");

            t.events.values().iterator().next().jsonExtension.put("e-key-1", "e-val-1");
            t.events.values().iterator().next().jsonExtension.put("e-key-2", "e-val-2");

            System.out.println(validator.failureMessage().toString(2));
            System.out.println(ThingDescription.toJSON(t).toString(2));


        }
        catch(Exception e){}
        System.out.println("FAILED: " +validator.failed());
        System.out.println(validator.failureMessage().toString(2));

    }

    public void predicate() throws Exception{
//        JSONObject in = new JSONObject(file2string(new File("").getAbsolutePath() + "/agent-service/src/test/resources/objects/adapter-thing.json"));
        JSONObject in = new JSONObject(file2string(new File("").getAbsolutePath() + "/agent-service/src/test/resources/objects/test-1.json"));

        ThingValidator validator = new ThingValidator(false);
        try{
            ThingDescription t = ThingDescription.create(in, validator);
            System.out.println(t.toString(0));

            System.out.println(validator.failureMessage().toString(2));
            System.out.println(ThingDescription.toJSON(t).toString(2));
            System.out.println(t.toString(2));


        }
        catch(Exception e){}
        System.out.println("FAILED: " +validator.failed());
        System.out.println(validator.failureMessage().toString(2));

    }


    public void x() throws Exception{
//        JSONObject in = new JSONObject(file2string(new File("").getAbsolutePath() + "/agent-service/src/test/resources/objects/adapter-thing.json"));
        JSONObject in = new JSONObject(file2string(new File("").getAbsolutePath() + "/agent-service/src/test/resources/objects/test-1.json"));

        ThingValidator validator = new ThingValidator(false);
        try{
            ThingDescription t = validator.create(in);
            System.out.println(t.toString(0));

            System.out.println(validator.failureMessage().toString(2));
            System.out.println(ThingDescription.toJSON(t).toString(2));
            System.out.println(t.toString(2));


        }
        catch(Exception e){}
        System.out.println("FAILED: " +validator.failed());
        System.out.println(validator.failureMessage().toString(2));

    }

    public static void main(String[] args) throws Exception {
        TestThingDescription p = new TestThingDescription();
        p.process();
//        p.predicate();
//        p.x();
    }

}
