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
    public static final String TEST = "/test";

    public static final String CONFIGURATION = "/configuration";
    public static final String CONFIGURE_AGENT = "/agents/{agid}/configure";

    public static final String OBJECTS = "/objects";

    public static final String REMOTE_OBJECT_PROPERTY = "/remote/objects/{oid}/properties/{pid}";
    public static final String OBJECT_PROPERTY = "/objects/{oid}/properties/{pid}";

    public static final String REMOTE_OBJECT_ACTION = "/remote/objects/{oid}/actions/{aid}";
    public static final String REMOTE_OBJECT_ACTION_TASK = "/remote/objects/{oid}/actions/{aid}/tasks/{tid}";
    public static final String OBJECT_ACTION = "/objects/{oid}/actions/{aid}";
    public static final String OBJECT_ACTION_UPDATE = "/actions/{aid}";

    public static final String OBJECT_EVENT_OPEN_PUBLISH = "/events/{eid}";
    public static final String OBJECT_EVENT_SUBSCRIBE_RECEIVE = "/objects/{oid}/events/{eid}";


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
        apiRouter.attach(TEST, TestResource.class);
        apiRouter.attach(TEST+"/", TestResource.class);

        apiRouter.attach(CONFIGURATION, ConfigurationResource.class);
        apiRouter.attach(CONFIGURATION+"/", ConfigurationResource.class);


        apiRouter.attach(CONFIGURE_AGENT, ConfigureAgentResource.class);
        apiRouter.attach(CONFIGURE_AGENT+"/", ConfigureAgentResource.class);

        apiRouter.attach(OBJECTS, DiscoverAdapterResource.class);
        apiRouter.attach(OBJECTS+"/", DiscoverAdapterResource.class);

        apiRouter.attach(OBJECT_PROPERTY, ObjectPropertyResource.class);
        apiRouter.attach(OBJECT_PROPERTY+"/", ObjectPropertyResource.class);

        apiRouter.attach(REMOTE_OBJECT_PROPERTY, RemoteObjectPropertyResource.class);
        apiRouter.attach(REMOTE_OBJECT_PROPERTY+"/", RemoteObjectPropertyResource.class);

        apiRouter.attach(OBJECT_ACTION, ObjectActionResource.class);
        apiRouter.attach(OBJECT_ACTION+"/", ObjectActionResource.class);

        apiRouter.attach(OBJECT_ACTION_UPDATE, ObjectUpdateActionResource.class);
        apiRouter.attach(OBJECT_ACTION_UPDATE+"/", ObjectUpdateActionResource.class);

        apiRouter.attach(REMOTE_OBJECT_ACTION, RemoteObjectActionResource.class);
        apiRouter.attach(REMOTE_OBJECT_ACTION+"/", RemoteObjectActionResource.class);

        apiRouter.attach(REMOTE_OBJECT_ACTION_TASK, RemoteObjectActionTaskResource.class);
        apiRouter.attach(REMOTE_OBJECT_ACTION_TASK+"/", RemoteObjectActionTaskResource.class);

        apiRouter.attach(OBJECT_EVENT_OPEN_PUBLISH, OpenPublishEventResource.class);
        apiRouter.attach(OBJECT_EVENT_OPEN_PUBLISH+"/", OpenPublishEventResource.class);

        apiRouter.attach(OBJECT_EVENT_SUBSCRIBE_RECEIVE, SubscribeReceiveEventResource.class);
        apiRouter.attach(OBJECT_EVENT_SUBSCRIBE_RECEIVE+"/", SubscribeReceiveEventResource.class);

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

