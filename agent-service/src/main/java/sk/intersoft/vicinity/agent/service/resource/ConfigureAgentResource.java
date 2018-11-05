package sk.intersoft.vicinity.agent.service.resource;

import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.service.config.AgentConfig;
import sk.intersoft.vicinity.agent.service.config.Configuration;

import java.io.File;

public class ConfigureAgentResource extends AgentResource {
    final static Logger logger = LoggerFactory.getLogger(ConfigureAgentResource.class.getName());

    private static String AGENT_ID = "agid";

    @Put()
    public String configure() throws Exception {
        String agentId = getAttribute(AGENT_ID);
        try{


            logger.info("RE-CONFIGURING AGENT ["+agentId+"]");
            File configFile = Configuration.findAgentConfigFile(agentId);
            if(configFile != null){
                logger.info("found configuration file: "+configFile.getAbsolutePath());
                if(AgentConfig.configure(configFile, false)){
                    return gtwSuccess("Agent ["+agentId+"] was successfully configured!").toString();
                }
                else {
                    throw new Exception("Agent ["+agentId+"] was NOT configured! .. see agent service logs!");
                }
            }
            else {
                throw new Exception("Unable to find config file for agent ["+agentId+"]!");
            }

        }
        catch(Exception e){
            logger.error("UNABLE TO CONFIGURE AGENT ["+agentId+"]", e);
            return gtwError(e).toString();
        }
    }

}
