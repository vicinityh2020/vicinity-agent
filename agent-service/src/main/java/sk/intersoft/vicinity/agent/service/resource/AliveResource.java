package sk.intersoft.vicinity.agent.service.resource;

import org.restlet.Request;
import org.restlet.data.Header;
import org.restlet.ext.servlet.ServletAdapter;
import org.restlet.ext.servlet.ServletUtils;
import org.restlet.ext.servlet.internal.ServletCall;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.restlet.util.Series;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;

public class AliveResource extends ServerResource {
    final static Logger logger = LoggerFactory.getLogger(AliveResource.class.getName());
    public static String getBasePath(org.restlet.Request request){
        org.restlet.data.Reference rootRef = request.getRootRef();
        org.restlet.data.Reference hostRef = request.getHostRef();
        int hostPort = hostRef.getHostPort();
        return (    rootRef.getScheme()
                + "://"
                + hostRef.getHostDomain()
                + ( ( hostPort==-1 ) ? "" : (":" + hostPort) )
                + rootRef.getPath()
                + "/"
        );
    }

    @Get("txt")
    public String doSomeGet() throws Exception {
        logger.info("DO GET");


        logger.info("ra: "+getRequestAttributes());

        Series<Header> series = (Series<Header>) getRequestAttributes().get("org.restlet.http.headers");
        org.restlet.Request restletRequest = getRequest();
        HttpServletRequest servletRequest = ServletUtils.getRequest(restletRequest);
        logger.info("srx: "+servletRequest);

        HttpServletRequest sr = ServletUtils.getRequest(getRequest());
        HttpServletRequest sr1 = ServletUtils.getRequest(Request.getCurrent());
        logger.info("sr: "+sr);
        logger.info("sr1: "+sr1);
        if(sr != null){
            logger.info("sr: "+sr.getRemoteUser());
            logger.info("sr: "+sr.getRemoteAddr());
            logger.info("sr: "+sr.getRemoteHost());
            logger.info("sr: "+sr.getRemotePort());

        }

        logger.info(": "+getResponse().getAttributes());
        logger.info(": "+getResponse().getRecipientsInfo());
        logger.info(": "+getResponse().getLocationRef());

        logger.info(": "+getRequest().getAttributes());
        logger.info(": "+getRequest().getCookies());
        logger.info(": "+getRequest().getRecipientsInfo());

        logger.info("base path: "+getBasePath(getRequest()));
        logger.info("base path: "+getBasePath(Request.getCurrent()));

        logger.info("root: "+Request.getCurrent().getRootRef());
        logger.info("original: "+Request.getCurrent().getOriginalRef());
        logger.info("resource: "+Request.getCurrent().getResourceRef());
        logger.info("referrer: "+Request.getCurrent().getReferrerRef());
        logger.info("host: "+Request.getCurrent().getHostRef());

        logger.info("root: "+getRequest().getRootRef());
        logger.info("original: "+getRequest().getOriginalRef());
        logger.info("resource: "+getRequest().getResourceRef());
        logger.info("referrer: "+getRequest().getReferrerRef());
        logger.info("host: "+getRequest().getHostRef());

        logger.info("l ref: "+getResponse().getLocationRef());
        logger.info("rp root: "+getResponse().getRequest().getRootRef());
        logger.info("rp original: "+getResponse().getRequest().getOriginalRef());
        logger.info("rp resource: "+getResponse().getRequest().getResourceRef());
        logger.info("rp referrer: "+getResponse().getRequest().getReferrerRef());
        logger.info("rp host: "+getResponse().getRequest().getHostRef());
        logger.info("rp 1: "+getResponse().getAccessControlAllowOrigin());
        logger.info("rp 1: "+getResponse().getAccessControlAllowHeaders());
        logger.info("r1: "+getRequest().getAccessControlRequestHeaders());

        logger.info("c: "+getRequest().getChallengeResponse());
        if(getRequest().getChallengeResponse() != null){
            logger.info("c: "+getRequest().getChallengeResponse().getIdentifier());
            logger.info("c: "+getRequest().getChallengeResponse().getRawValue());
            logger.info("c: "+getRequest().getChallengeResponse().getServerNonce());
            logger.info("c: "+getRequest().getChallengeResponse().getDigestRef());
            logger.info("c: "+getRequest().getChallengeResponse().getScheme());
            logger.info("c: "+getRequest().getChallengeResponse().getParameters());
        }

        logger.info("headers: ");
        Iterator<Header> i = series.iterator();
        while (i.hasNext()) {
            Header h = i.next();
            logger.info("["+h.getName()+"]:["+h.getValue()+"]");
        }

        logger.info("c: "+getClientInfo().getAddress());
        logger.info("c: "+getClientInfo().getAgent());
        logger.info("c: "+getClientInfo().getCipherSuite());
        logger.info("c: "+getClientInfo().getAgentAttributes());
        logger.info("c: "+getClientInfo().getAgentProducts());
        logger.info("c: "+getClientInfo().getMainAgentProduct());
        logger.info("c: "+getClientInfo().getPort());
        logger.info("c: "+getClientInfo().getForwardedAddresses());
        logger.info("c: "+getClientInfo().getUpstreamAddress());
        logger.info("c: "+getClientInfo().getPort());
        logger.info("c: "+ Request.getCurrent().getClientInfo().getAddress());
        logger.info("c: "+ Request.getCurrent().getClientInfo().getUpstreamAddress());
        logger.info("c: "+ Request.getCurrent().getClientInfo().getForwardedAddresses());
        logger.info("c: "+ Request.getCurrent().getClientInfo().getPort());

        return "AGENT IS ALIVE";
    }

}
