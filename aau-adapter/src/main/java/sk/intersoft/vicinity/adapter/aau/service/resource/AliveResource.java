package sk.intersoft.vicinity.adapter.aau.service.resource;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import java.io.File;
import java.util.Scanner;

public class AliveResource extends ServerResource {


    @Get("json")
    public String getAlive()  {
        try{

            System.out.println("getting alive");
            getLogger().info("GETTING ALIVE ...");

            return "{\"echo\": \"yes, i'm alive\"}";

        }
        catch(Exception e){
            System.out.println("no objects");
            getLogger().info("NO OBJECTS FILE .. return empty");
            return "{\"echo\": \"something went ape for: alive\"}";
        }
    }

}
