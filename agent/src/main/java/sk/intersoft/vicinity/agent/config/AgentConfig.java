package sk.intersoft.vicinity.agent.config;


import sk.intersoft.vicinity.adapter.AgentAdapter;
import sk.intersoft.vicinity.adapter.certh.CERTHSmartHomeAdapter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AgentConfig {
    public static final Map<String, String> deviceUUID2Infrastructure =
            Collections.unmodifiableMap(new HashMap<String, String>() {{
                put("001", "d6e5acc3-dc29-417f-aa10-ebad34bf9db3");
            }});

    public static AgentAdapter getAdapter(){
        return new CERTHSmartHomeAdapter();
    }
}
