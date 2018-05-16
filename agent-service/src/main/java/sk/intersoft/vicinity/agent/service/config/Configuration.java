package sk.intersoft.vicinity.agent.service.config;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.utils.Dump;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Configuration {
    final static Logger logger = LoggerFactory.getLogger(Configuration.class.getName());

    private static final String GATEWAY_API_ENDPOINT_KEY = "gateway-api-endpoint";

    public static Map<String, AgentConfig> agents = new HashMap<String, AgentConfig>();
    public static Map<String, String> adapter2agent = new HashMap<String, String>();

    public static String gatewayAPIEndpoint = "";

    public static String file2string(File file) {
        try{
            return new Scanner(file).useDelimiter("\\Z").next();
        }
        catch(Exception e){
            logger.error("", e);
            return null;
        }
    }

    public static void create(String configFile, String configFolder) throws Exception {


        logger.info("CREATING CONFIG FROM FILE : "+configFile);
        JSONObject configSource = new JSONObject(file2string(new File(configFile)));
        gatewayAPIEndpoint = configSource.getString(GATEWAY_API_ENDPOINT_KEY);

        logger.info("CONFIGURING AGENTS FROM FOLDER: "+configFolder);
        File folder = new File(configFolder);
        File[] files = folder.listFiles();
        if(files.length == 0){
            throw new Exception("no agent config files found in ["+configFolder+"]!");
        }
        for(File f : files){
            String source = file2string(f);
            logger.info("config file: "+f.getAbsolutePath());
            try{
                AgentConfig config = AgentConfig.create(source);
                if(agents.get(config.agentId) != null){
                    throw new Exception("duplicate agent-id in ["+f.getAbsolutePath()+"]!");
                }

                agents.put(config.agentId, config);

                for (String adapterId : config.adapters.keySet()) {
                    if(adapter2agent.get(adapterId) != null){
                        throw new Exception("duplicate adapter-id ["+adapterId+"] of agent-id ["+config.agentId+"] in ["+f.getAbsolutePath()+"]!");
                    }
                    adapter2agent.put(adapterId, config.agentId);
                }


            }
            catch(Exception e){
                logger.error("", e);
                throw new Exception(e.getMessage() + " unable to process config: ["+f.getAbsolutePath()+"]! ");
            }
        }
    }

    public static String toString(int indent) {
        Dump dump = new Dump();

        dump.add("AGENT-SERVICE CONFIGURATION: ", indent);

        dump.add("GatewayAPI Endpoint: " + gatewayAPIEndpoint, indent);

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
