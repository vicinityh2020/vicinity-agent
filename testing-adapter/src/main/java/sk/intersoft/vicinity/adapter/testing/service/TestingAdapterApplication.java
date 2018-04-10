package sk.intersoft.vicinity.adapter.testing.service;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.data.ChallengeScheme;
import org.restlet.routing.Router;
import org.restlet.security.ChallengeAuthenticator;
import sk.intersoft.vicinity.adapter.testing.service.resource.*;

public class TestingAdapterApplication extends Application {
    public static final String OBJECTS = "/objects";

    public static final String GET_PROPERTY = "/objects/{oid}/properties/{pid}";
    public static final String GET_CUSTOM_PROPERTY = "/custom/{oid}/x";

    public static final String SET_CUSTOM_PROPERTY = "/custom-set/{oid}/y";

    public static final String POST_ACTION = "/objects/{oid}/actions/{aid}";
    public static final String POST_CUSTOM_ACTION = "/custom-post/{oid}/z";

    public static final String EVENT_LISTENER = "/objects/{oid}/events/{eid}";

    public static final String ADAPTER_ID = "adapter-1";

    private ChallengeAuthenticator createApiGuard(Restlet next) {

        ChallengeAuthenticator apiGuard = new ChallengeAuthenticator(getContext(), ChallengeScheme.HTTP_BASIC, "realm");

        apiGuard.setNext(next);

        // In case of anonymous access supported by the API.
        apiGuard.setOptional(true);

        return apiGuard;
    }

    public Router createApiRouter() {
        Router apiRouter = new Router(getContext());
        apiRouter.attach(OBJECTS, ObjectsResource.class);
        apiRouter.attach(OBJECTS+"/", ObjectsResource.class);
        apiRouter.attach(GET_PROPERTY, GetSetPropertyResource.class);
        apiRouter.attach(POST_ACTION, ActionResource.class);
        apiRouter.attach(GET_CUSTOM_PROPERTY, GetCustomPropertyResource.class);
        apiRouter.attach(SET_CUSTOM_PROPERTY, SetCustomPropertyResource.class);
        apiRouter.attach(POST_CUSTOM_ACTION, CustomActionResource.class);
        apiRouter.attach(EVENT_LISTENER, EventListenerResource.class);

        return apiRouter;
    }

    public Restlet createInboundRoot() {

        Router apiRouter = createApiRouter();
        ChallengeAuthenticator guard = createApiGuard(apiRouter);
        return guard;
    }


}
