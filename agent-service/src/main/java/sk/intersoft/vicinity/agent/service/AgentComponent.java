package sk.intersoft.vicinity.agent.service;

import org.restlet.Component;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AgentComponent extends Component {
    final static Logger logger = LoggerFactory.getLogger(AgentComponent.class.getName());

    public AgentComponent() throws Exception {

        Server s = new Server(Protocol.HTTP, Integer.parseInt(System.getProperty("server.port")), this);
        getServers().add(s);
        s.getContext().getParameters().add("useForwardedForHeader", "true");
        s.getContext().getParameters().add("maxThreads", "1000");

        getClients().add(Protocol.HTTP);

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
