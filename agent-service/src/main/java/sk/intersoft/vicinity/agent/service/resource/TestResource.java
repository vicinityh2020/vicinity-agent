package sk.intersoft.vicinity.agent.service.resource;

import org.restlet.Request;
import org.restlet.data.Header;
import org.restlet.data.Status;
import org.restlet.ext.servlet.ServletUtils;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.restlet.util.Series;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.clients.GatewayAPIClient;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;

public class TestResource extends AgentResource {
    final static Logger logger = LoggerFactory.getLogger(TestResource.class.getName());

    @Get("json")
    public String doSomeGet() throws Exception {
        logger.info("TEST DO GET");

        try{
            String oid = "7b32f844-aeee-487d-a052-f1bf1cb4d692";
            String pid = "RJj2WEv7QgG+F8cZxF0D/SmslcFnJgx0X8UZPpySqtg=";
//        getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
//            String returnCodeReason = clientResource.getStatus().getReasonPhrase();

            String endpoint = GatewayAPIClient.getInteractionEndpoint(GatewayAPIClient.OBJECT_PROPERTY_ENDPOINT, oid, pid);

            logger.info("GTW API ENDPOINT: "+endpoint);

            throw new Exception("ERROR MESSAGE FOR MARY GOES HERE");

//            String ee = "/bla/bla";
//            String gtwResponse = GatewayAPIClient.get(ee, oid, pid);
//            logger.info("GTW API RAW RESPONSE: \n"+gtwResponse);
//
//
//            return "{\"wtf\": \"sure\"}";

        }
        catch(Exception e){
            logger.error("EXCEPTION HERE"+e);
            getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);


            return gtwError(Status.SERVER_ERROR_INTERNAL, e).toString();

        }
    }

}
