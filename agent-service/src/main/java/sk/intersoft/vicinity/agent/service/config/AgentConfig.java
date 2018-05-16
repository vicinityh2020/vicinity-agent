package sk.intersoft.vicinity.agent.service.config;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.utils.Dump;

import java.io.File;
import java.util.*;

public class AgentConfig {
    final static Logger logger = LoggerFactory.getLogger(AgentConfig.class.getName());

    private static final String CREDENTIALS_KEY = "credentials";
    private static final String AGENT_ID_KEY = "agent-id";
    private static final String PASSWORD_KEY = "password";

    private static final String ADAPTERS_KEY = "adapters";

    public String agentId = "";
    public String password = "";

    public Map<String, AdapterConfig> adapters = new HashMap<String, AdapterConfig>();

    public static AgentConfig create(String source) throws Exception {
        AgentConfig config = new AgentConfig();

        JSONObject json = new JSONObject(source);
        logger.info("CREATING AGENT CONFIG FROM: "+json.toString(2));
        JSONObject credentials = json.getJSONObject(CREDENTIALS_KEY);
        config.agentId = credentials.getString(AGENT_ID_KEY);
        config.password = credentials.getString(PASSWORD_KEY);


        JSONArray adaptersArray = json.getJSONArray(ADAPTERS_KEY);
        if(adaptersArray.length() == 0){
            throw new Exception("no adapters in agent config ["+config.agentId+"]");
        }

        Iterator<Object> i = adaptersArray.iterator();
        while (i.hasNext()) {
            JSONObject adapterConfig = (JSONObject) i.next();
            AdapterConfig ac = AdapterConfig.create(adapterConfig);
            if(config.adapters.get(ac.adapterId) != null){
                throw new Exception("duplicate adapter-id ["+ac.adapterId+"] in agent ["+config.agentId+"]!");
            }
            config.adapters.put(ac.adapterId, ac);
        }
        return config;
    }

    public String toString(int indent) {
        Dump dump = new Dump();

        dump.add("AGENT CONFIG: ", indent);

        dump.add("Credentials: ", (indent + 1));
        dump.add("agent-id: [" + agentId + "]", (indent + 2));
        dump.add("password: [" + password + "]", (indent + 2));
        dump.add("Adapters: "+adapters.keySet().size(), (indent + 1));
        for (Map.Entry<String, AdapterConfig> entry : adapters.entrySet()) {
            String id = entry.getKey();
            AdapterConfig ac = entry.getValue();
            dump.add("adapter-id: "+id, (indent + 3));
            dump.add(ac.toString(indent + 4));
        }

        return dump.toString();
    }

}