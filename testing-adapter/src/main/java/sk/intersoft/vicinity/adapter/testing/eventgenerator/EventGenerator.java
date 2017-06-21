package sk.intersoft.vicinity.adapter.testing.eventgenerator;

import org.restlet.resource.ClientResource;

import java.util.UUID;

public class EventGenerator extends Thread {
    public void run()  {
        try{
            System.out.println("MyThread running");

            while(true){
                System.out.println("generating event.. ");

                String endpoint = System.getProperty("adapter.endpoint") +
                        "/objects/" + System.getProperty("oid") +
                        "/properties/" + System.getProperty("pid") +
                        "/event";
                ClientResource setResource = new ClientResource(endpoint);

                System.out.println("posting event to: "+endpoint);

                setResource.post("{\"some-event\": "+ UUID.randomUUID().toString()+"}");

                sleep(1000);

            }

        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        EventGenerator generator = new EventGenerator();
        generator.start();
    }
}
