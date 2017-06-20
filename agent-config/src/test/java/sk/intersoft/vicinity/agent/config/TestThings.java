package sk.intersoft.vicinity.agent.config;

import org.json.JSONArray;
import sk.intersoft.vicinity.agent.config.thing.ThingDescription;
import sk.intersoft.vicinity.agent.config.thing.ThingsProcessor;

import java.io.File;
import java.util.List;
import java.util.Scanner;

public class TestThings {

    public static String file2string(String path) {
        try{
            return new Scanner(new File(path)).useDelimiter("\\Z").next();
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }


    public void test() throws Exception {

        JSONArray json = new JSONArray(file2string(new File("").getAbsolutePath() + "/testing-adapter/src/test/resources/objects/1.json"));

        List<ThingDescription> list = ThingsProcessor.process(json);
        System.out.println("PROCESSED");
        for(ThingDescription d : list){
            d.show();
        }

    }
    public static void main(String[] args) throws Exception {
        TestThings c = new TestThings();
//        c.testLogin();
        c.test();
    }

}
