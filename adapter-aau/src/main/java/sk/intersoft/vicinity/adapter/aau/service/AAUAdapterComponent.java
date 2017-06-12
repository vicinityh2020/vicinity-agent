package sk.intersoft.vicinity.adapter.aau.service;

import org.restlet.Component;
import org.restlet.data.Protocol;

public class AAUAdapterComponent extends Component {

    public AAUAdapterComponent() throws Exception {

        getServers().add(Protocol.HTTP, 5677);
        // Attach the application to the default virtual host
        getDefaultHost().attach("/aau-adapter", new AAUAdapterApplication());
    }

}
