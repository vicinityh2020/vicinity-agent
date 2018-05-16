package sk.intersoft.vicinity.agent.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class StartStop {
    final static Logger logger = LoggerFactory.getLogger(StartStop.class.getName());


    public static void start() {
        logger.info("Launching starting sequence!");
        try{
            // START SEQUENCE:


            logger.error("STARTUP SEQUENCE COMPLETED!");

        }
        catch(Exception e){
            logger.error("", e);
            logger.error("STARTUP SEQUENCE FAILED!");
        }
    }

    public static void stop() {
        try{
            logger.info("Launching shutdown sequence!");

        }
        catch(Exception e){
            logger.error("", e);
            logger.error("shutdown sequence failed!");
        }
    }

}
