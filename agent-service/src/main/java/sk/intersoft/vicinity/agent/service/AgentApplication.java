package sk.intersoft.vicinity.agent.service;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.data.ChallengeScheme;
import org.restlet.routing.Router;
import org.restlet.security.ChallengeAuthenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.service.resource.*;

public class AgentApplication extends Application {
    final static Logger logger = LoggerFactory.getLogger(AgentApplication.class.getName());

    public static final String ALIVE = "/alive";
    public static final String OBJECT_PROPERTY = "/objects/{oid}/properties/{pid}";
    public static final String OBJECT_ACTION = "/objects/{oid}/actions/{aid}";
    public static final String OBJECT_EVENT = "/objects/{oid}/events/{eid}";


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
        apiRouter.attach(ALIVE+"/", AliveResource.class);

        apiRouter.attach(OBJECT_PROPERTY, ObjectPropertyResource.class);
        apiRouter.attach(OBJECT_PROPERTY+"/", ObjectPropertyResource.class);

        apiRouter.attach(OBJECT_ACTION, ObjectActionResource.class);
        apiRouter.attach(OBJECT_ACTION+"/", ObjectActionResource.class);

        apiRouter.attach(OBJECT_EVENT, ObjectEventResource.class);
        apiRouter.attach(OBJECT_EVENT+"/", ObjectEventResource.class);


        return apiRouter;
    }

    public Restlet createInboundRoot() {

        Router apiRouter = createApiRouter();
        ChallengeAuthenticator guard = createApiGuard(apiRouter);
        return guard;
    }

    @Override
    public synchronized void stop() throws Exception {
        logger.info("stopping app");
        super.stop();
    }


}

