package sk.intersoft.vicinity.agent.service;

import org.restlet.Component;
import org.restlet.data.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AgentComponent extends Component {
    final static Logger logger = LoggerFactory.getLogger(AgentComponent.class.getName());

    public AgentComponent() throws Exception {

        getServers().add(Protocol.HTTP, Integer.parseInt(System.getProperty("server.port")));
        // Attach the application to the default virtual host
        getDefaultHost().attach("/agent", new AgentApplication());
    }

    @Override
    public synchronized void stop() throws Exception {
        logger.info("stopping ");
        super.stop();
    }

    public void shutdown()  {
        logger.info("shutdown");
    }

}
