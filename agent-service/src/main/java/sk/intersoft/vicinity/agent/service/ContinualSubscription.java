package sk.intersoft.vicinity.agent.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.db.Persistence;
import sk.intersoft.vicinity.agent.service.config.AdapterConfig;
import sk.intersoft.vicinity.agent.service.config.AgentConfig;
import sk.intersoft.vicinity.agent.service.config.Configuration;

import java.util.Map;

public class ContinualSubscription  implements Runnable  {
    final static Logger logger = LoggerFactory.getLogger(ContinualSubscription.class.getName());

    final long sleepTime = 30000;

    public void sleep(long millis){
        try{
            Thread.sleep(millis);
        }
        catch(Exception e){}
    }

    private void subscribe(){
//        logger.info("RUNNING CONTINUAL SUBSCRIPTION FOR ["+Configuration.adapters.keySet().size()+"] CONFIGURED ADAPTERS");
        for (Map.Entry<String, AdapterConfig> entry : Configuration.adapters.entrySet()) {
            AdapterConfig adapter = entry.getValue();
            adapter.subscribeEventChannels(false);
        }
    }

    public void run() {
        while(true){
            subscribe();
            sleep(sleepTime);

        }
    }
}
