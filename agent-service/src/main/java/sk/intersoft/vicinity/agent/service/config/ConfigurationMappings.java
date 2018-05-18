package sk.intersoft.vicinity.agent.service.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.utils.Dump;
import sk.intersoft.vicinity.agent.utils.FileUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ConfigurationMappings {
    final static Logger logger = LoggerFactory.getLogger(ConfigurationMappings.class.getName());

    private static String configFolder = System.getProperty("agents.config");

    public Map<String, AgentConfig> agents = new HashMap<String, AgentConfig>();
    public Map<String, String> adapter2agent = new HashMap<String, String>();

    public void addMappings(AgentConfig config) throws Exception {

        for (String adapterId : config.adapters.keySet()) {
            if(adapter2agent.get(adapterId) != null){
                throw new Exception("duplicate adapter-id ["+adapterId+"] of agent-id ["+config.agentId+"]!");
            }
            adapter2agent.put(adapterId, config.agentId);
        }
        agents.put(config.agentId, config);

    }

    public void createAgent(File configFile) throws Exception {

        try{

            logger.debug("CONFIGURING AGENT FROM FILE: " + configFile.getAbsolutePath());

            AgentConfig config = AgentConfig.create(configFile);

            if(agents.get(config.agentId) != null){
                throw new Exception("duplicate agent-id ["+config.agentId+"] in ["+configFile.getAbsolutePath()+"]!");
            }

            addMappings(config);

        }
        catch(Exception e){
            logger.error("", e);
            throw new Exception(e.getMessage() + " unable to process config: ["+configFile.getAbsolutePath()+"]! ");
        }

    }

//    public AgentConfig addAgent(String agentId) {
//        logger.debug("ADDING AGENT CONFIG ["+agentId+"] .. searching in config files");
//        if(agents.get(agentId) != null){
//            logger.debug("AGENT EXISTS .. RETURNING!");
//            return  agents.get(agentId);
//        }
//        try{
//            File folder = new File(configFolder);
//            File[] files = folder.listFiles();
//            if(files.length == 0){
//                throw new Exception("no agent config files found in ["+configFolder+"]!");
//            }
//            for(File f : files){
//                try {
//                    AgentConfig config = AgentConfig.create(f);
//                    logger.debug("AGENT-ID MATCH ["+config.agentId+"]:["+agentId+"]!");
//                    if(config.agentId.equals(agentId)){
//                        logger.debug("MATCH .. adding");
//                        addMappings(config);
//                        return config;
//                    }
//                    else {
//                        logger.debug("NO MATCH .. continue");
//                    }
//
//                }
//                catch(Exception e){
//                    logger.error("unable to process config from file:  ["+f.getAbsolutePath()+"]", e);
//                }
//            }
//
//        }
//        catch(Exception e){
//            logger.error("SOMETHING WENT WRONG WHEN ADDING NEW AGENT CONFIG ["+agentId+"]", e);
//        }
//        return null;
//
//    }
//
    public void create() throws Exception {
        logger.debug("CONFIGURING AGENTS FROM FOLDER: "+configFolder);
        File folder = new File(configFolder);
        File[] files = folder.listFiles();
        if(files.length == 0){
            throw new Exception("no agent config files found in ["+configFolder+"]!");
        }
        for(File f : files){
            createAgent(f);
        }
    }


    public String toString(int indent) {
        Dump dump = new Dump();

        dump.add("CONFIGURATION MAPPINGS: ", indent);

        dump.add("Agents: "+agents.keySet().size(), indent);
        for (Map.Entry<String, AgentConfig> entry : agents.entrySet()) {
            String id = entry.getKey();
            AgentConfig ac = entry.getValue();
            dump.add("agent-id: "+id, (indent + 1));
            dump.add(ac.toString(indent + 2));
        }
        dump.add("Adapter2Agent: "+adapter2agent.keySet().size(), indent);
        for (Map.Entry<String, String> entry : adapter2agent.entrySet()) {
            String adid = entry.getKey();
            String agid = entry.getValue();
            dump.add("["+adid+"] -> ["+agid+"]", (indent + 1));
        }

        return dump.toString();
    }

}
