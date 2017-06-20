package sk.intersoft.vicinity.adapter.aau.service.resource;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import java.io.File;
import java.util.Scanner;

public class ObjectsResource extends ServerResource {

    public static String file2string(String path) throws Exception {
        try{
            return new Scanner(new File(path)).useDelimiter("\\Z").next();
        }
        catch(Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    @Get("json")
    public String getPropertyValue()  {
        try{

            System.out.println("getting objects");
            getLogger().info("GETTING OBJECTS ...");

            return file2string(System.getProperty("objects.file"));

        }
        catch(Exception e){
            System.out.println("no objects");
            getLogger().info("NO OBJECTS FILE .. return empty");
            return "[]";
        }
    }

}
