package sk.intersoft.vicinity.adapter.testing.service.resource;

import org.restlet.data.Status;
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

    public static String getObjects(){
        try{
            System.out.println("getting objects");
            System.out.println("GETTING OBJECTS FROM: "+System.getProperty("objects.file"));

            return file2string(System.getProperty("objects.file"));

        }
        catch(Exception e){
            System.out.println("no objects");
            System.out.println("NO OBJECTS FILE .. return empty");
            return "[]";
        }

    }

    @Get("json")
    public String getPropertyValue()  {
        return getObjects();
    }

}
