package sk.intersoft.vicinity.adapter.aau.service;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.data.ChallengeScheme;
import org.restlet.routing.Router;
import org.restlet.security.ChallengeAuthenticator;
import sk.intersoft.vicinity.adapter.aau.service.resource.ObjectGetPropertyResource;

public class AAUAdapterApplication extends Application {
    public static final String OBJECT_PROPERTY_VALUE = "/objects/{oid}/properties/{pid}";

    private ChallengeAuthenticator createApiGuard(Restlet next) {

        ChallengeAuthenticator apiGuard = new ChallengeAuthenticator(getContext(), ChallengeScheme.HTTP_BASIC, "realm");

        apiGuard.setNext(next);

        // In case of anonymous access supported by the API.
        apiGuard.setOptional(true);

        return apiGuard;
    }

    public Router createApiRouter() {
        Router apiRouter = new Router(getContext());
        apiRouter.attach(OBJECT_PROPERTY_VALUE, ObjectGetPropertyResource.class);

        return apiRouter;
    }

    public Restlet createInboundRoot() {

        Router apiRouter = createApiRouter();
        ChallengeAuthenticator guard = createApiGuard(apiRouter);
        return guard;
    }

}
