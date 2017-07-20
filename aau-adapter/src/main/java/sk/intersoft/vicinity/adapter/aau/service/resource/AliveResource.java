package sk.intersoft.vicinity.adapter.aau.service.resource;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import java.io.File;
import java.util.Scanner;

public class AliveResource extends ServerResource {


    @Get("txt")
    public String getAlive()  {
        try{

            System.out.println("getting alive");
            getLogger().info("GETTING ALIVE ...");

            return "ADAPTER IS ALIVE";

        }
        catch(Exception e){
            System.out.println("no objects");
            getLogger().info("NO OBJECTS FILE .. return empty");
            return "ADAPTER ALIVE: something went ape???";
        }
    }

}
