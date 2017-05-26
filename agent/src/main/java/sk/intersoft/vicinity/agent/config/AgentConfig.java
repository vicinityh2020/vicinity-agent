package sk.intersoft.vicinity.agent.config;


import sk.intersoft.vicinity.adapter.AgentAdapter;
import sk.intersoft.vicinity.adapter.certh.CERTHLinksmartAdapter;
import sk.intersoft.vicinity.adapter.certh.CERTHSmartHomeAdapter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AgentConfig {
    public static final Map<String, String> deviceUUID2Infrastructure =
            Collections.unmodifiableMap(new HashMap<String, String>() {{
                put("001", "0D485748-CF2A-450C-BCF6-02AC1CB39A2D:6");
            }});

    public static AgentAdapter getAdapter(){
        return new CERTHLinksmartAdapter();
    }
}
