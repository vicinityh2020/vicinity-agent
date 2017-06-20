package sk.intersoft.vicinity.adapter.testing.service;

import org.restlet.Component;
import org.restlet.data.Protocol;

public class TestingAdapterComponent extends Component {

    public TestingAdapterComponent() throws Exception {

        getServers().add(Protocol.HTTP, Integer.parseInt(System.getProperty("server.port")));
        // Attach the application to the default virtual host
        getDefaultHost().attach("/adapter", new TestingAdapterApplication());
    }


}
