package sk.intersoft.vicinity.agent.service;

import org.restlet.Component;
import org.restlet.data.Protocol;

public class AgentComponent extends Component {

    public AgentComponent() throws Exception {

        getServers().add(Protocol.HTTP, Integer.parseInt(System.getProperty("server.port")));
        // Attach the application to the default virtual host
        getDefaultHost().attach("/agent", new AgentApplication());
    }

    @Override
    public synchronized void stop() throws Exception {
        System.out.println("stopping ");
        super.stop();
    }

    public void shutdown()  {
        System.out.println("shutdown");
    }

}
