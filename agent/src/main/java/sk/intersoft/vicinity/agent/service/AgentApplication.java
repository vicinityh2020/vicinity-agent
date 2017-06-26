package sk.intersoft.vicinity.agent.service;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.data.ChallengeScheme;
import org.restlet.routing.Router;
import org.restlet.security.ChallengeAuthenticator;
import sk.intersoft.vicinity.agent.service.resource.ObjectActionResource;
import sk.intersoft.vicinity.agent.service.resource.ObjectActionTaskStubResource;
import sk.intersoft.vicinity.agent.service.resource.ObjectGetSetPropertyResource;
import sk.intersoft.vicinity.agent.service.resource.TestResource;

public class AgentApplication extends Application {
    public static final String TEST = "/alive/{x}";
    public static final String OBJECT_PROPERTY_VALUE = "/objects/{oid}/properties/{pid}";
    public static final String OBJECT_ACTION = "/objects/{oid}/actions/{aid}";
    public static final String OBJECT_ACTION_TASK = "/objects/{oid}/actions/{aid}/tasks/{tid}";



    private ChallengeAuthenticator createApiGuard(Restlet next) {

        ChallengeAuthenticator apiGuard = new ChallengeAuthenticator(getContext(), ChallengeScheme.HTTP_BASIC, "realm");

        apiGuard.setNext(next);

        // In case of anonymous access supported by the API.
        apiGuard.setOptional(true);

        return apiGuard;
    }

    public Router createApiRouter() {
        Router apiRouter = new Router(getContext());
        apiRouter.attach(TEST, TestResource.class);
        apiRouter.attach(OBJECT_PROPERTY_VALUE, ObjectGetSetPropertyResource.class);
        apiRouter.attach(OBJECT_ACTION, ObjectActionResource.class);
        apiRouter.attach(OBJECT_ACTION_TASK, ObjectActionTaskStubResource.class);

        return apiRouter;
    }

    public Restlet createInboundRoot() {

        Router apiRouter = createApiRouter();
        ChallengeAuthenticator guard = createApiGuard(apiRouter);
        return guard;
    }

    @Override
    public synchronized void stop() throws Exception {
        System.out.println("stopping app");
        super.stop();
    }


}
