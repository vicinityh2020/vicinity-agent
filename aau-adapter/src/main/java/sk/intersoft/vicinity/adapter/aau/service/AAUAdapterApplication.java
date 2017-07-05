package sk.intersoft.vicinity.adapter.aau.service;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.data.ChallengeScheme;
import org.restlet.routing.Router;
import org.restlet.security.ChallengeAuthenticator;
import sk.intersoft.vicinity.adapter.aau.service.resource.AliveResource;
import sk.intersoft.vicinity.adapter.aau.service.resource.EventListenerResource;
import sk.intersoft.vicinity.adapter.aau.service.resource.ObjectsResource;

public class AAUAdapterApplication extends Application {
    public static final String ALIVE = "/alive";
    public static final String OBJECTS = "/objects";
    public static final String RECEIVE_EVENTS = "/objects/{oid}/events/{eid}";



    private ChallengeAuthenticator createApiGuard(Restlet next) {

        ChallengeAuthenticator apiGuard = new ChallengeAuthenticator(getContext(), ChallengeScheme.HTTP_BASIC, "realm");

        apiGuard.setNext(next);

        // In case of anonymous access supported by the API.
        apiGuard.setOptional(true);

        return apiGuard;
    }

    public Router createApiRouter() {
        Router apiRouter = new Router(getContext());
        apiRouter.attach(ALIVE, AliveResource.class);
        apiRouter.attach(OBJECTS, ObjectsResource.class);
        apiRouter.attach(OBJECTS+"/", ObjectsResource.class);
        apiRouter.attach(RECEIVE_EVENTS, EventListenerResource.class);

        return apiRouter;
    }

    public Restlet createInboundRoot() {

        Router apiRouter = createApiRouter();
        ChallengeAuthenticator guard = createApiGuard(apiRouter);
        return guard;
    }


}
