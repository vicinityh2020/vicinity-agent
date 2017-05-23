package sk.intersoft.vicinity.agent.service;

import org.restlet.Component;
import org.restlet.data.Protocol;

public class AgentComponent extends Component {

    public AgentComponent() throws Exception {

        getServers().add(Protocol.HTTP, 5679);
        // Attach the application to the default virtual host
        getDefaultHost().attach("/agent", new AgentApplication());
    }

}
