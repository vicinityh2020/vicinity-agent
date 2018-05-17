package sk.intersoft.vicinity.agent;

import org.json.JSONObject;
import sk.intersoft.vicinity.agent.thing.DataSchema;
import sk.intersoft.vicinity.agent.thing.ThingValidator;

import java.io.File;
import java.util.Scanner;

public class TestDataSchema {

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
        JSONObject in1 = new JSONObject(file2string(new File("").getAbsolutePath() + "/agent-service/src/test/resources/schema/test-simple.json"));
        JSONObject in2 = new JSONObject(file2string(new File("").getAbsolutePath() + "/agent-service/src/test/resources/schema/test-object.json"));
        JSONObject in3 = new JSONObject(file2string(new File("").getAbsolutePath() + "/agent-service/src/test/resources/schema/test-array.json"));

        ThingValidator validator = new ThingValidator(true);
        System.out.println(DataSchema.create(in1, validator).toString(0));
        System.out.println(DataSchema.create(in2, validator).toString(0));

        ThingValidator validatorAll = new ThingValidator(true);
        try{

            System.out.println(DataSchema.create(in3, validatorAll).toString(0));
        }
        catch(Exception e) {
        }
        System.out.println(validatorAll.failureMessage().toString(2));

    }

    public static void main(String[] args) throws Exception {
        TestDataSchema p = new TestDataSchema();
        p.process();
    }

}
