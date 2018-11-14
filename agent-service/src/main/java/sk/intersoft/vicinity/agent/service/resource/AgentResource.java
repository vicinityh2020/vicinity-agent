package sk.intersoft.vicinity.agent.service.resource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.Context;
import org.restlet.Response;
import org.restlet.data.*;
import org.restlet.resource.Resource;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.restlet.util.Series;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.clients.ClientResponse;
import sk.intersoft.vicinity.agent.service.config.AdapterConfig;
import sk.intersoft.vicinity.agent.service.config.AgentConfig;
import sk.intersoft.vicinity.agent.service.config.Configuration;
import sk.intersoft.vicinity.agent.service.config.processor.ThingDescriptions;
import sk.intersoft.vicinity.agent.thing.ThingDescription;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;

public class AgentResource extends ServerResource {
    final static Logger logger = LoggerFactory.getLogger(AgentResource.class.getName());

    String CALLER_ID_HEADER = "infrastructure-id";
    String ADAPTER_ID_HEADER = "adapter-id";
    String STATUS_HEADER = "status";

    private String getHeader(String name) {
        try{
            Series<Header> series = (Series<Header>) getRequestAttributes().get("org.restlet.http.headers");

            Iterator<Header> i = series.iterator();
            while (i.hasNext()) {
                Header h = i.next();
                if (h.getName().trim().equalsIgnoreCase(name.trim())) {
                    return h.getValue().trim();
                }
            }

        }
        catch(Exception e){
            e.printStackTrace();
        }


        return null;
    }


    protected ThingDescription getThingByOID(String oid) throws Exception {
        ThingDescription thing = Configuration.thingsByOID.get(oid);
        if(thing == null) throw new Exception("Unknown thing for OID: ["+oid+"]");
        return thing;
    }

    protected String getStatusHeader() throws Exception {
        String status = getHeader(STATUS_HEADER);
        if(status == null || status.trim().equals("")){
            throw new Exception("Missing ["+STATUS_HEADER+"] header!");
        }
        return status;
    }

    protected ThingDescription getCallerObject() throws Exception {
        String infrastructureId = getHeader(CALLER_ID_HEADER);
        String adapterId = getHeader(ADAPTER_ID_HEADER);
        logger.debug("caller headers: [adapter-id: "+adapterId+"][infrastructure-id: "+infrastructureId+"]");

        if(infrastructureId == null) throw new Exception("Missing ["+CALLER_ID_HEADER+"] header of caller object!");
        if(adapterId == null) throw new Exception("Missing ["+ADAPTER_ID_HEADER+"] header of caller object!");

        ThingDescriptions adapterThings = Configuration.things.get(adapterId);
        if(adapterThings == null){
            throw new Exception("Adapter ["+adapterId+"] does not exist in configuration!");
        }

        String id = ThingDescription.identifier(infrastructureId, adapterId);
        ThingDescription thing = adapterThings.byAdapterInfrastructureID.get(id);
        if(thing == null) {
            throw new Exception("Thing [adapter-id: "+adapterId+"][infrastructure-id: "+infrastructureId+"] does not exist in configuration!");
        }

        return thing;
    }

    private boolean isLocalhost(String d){
        return (d.equals("localhost") || d.equals("127.0.0.1"));
    }

    private boolean sameDomain(String d1, String d2){
        if(isLocalhost(d1) && isLocalhost(d2)){
            return true;
        }
        else if(d1.equals(d2)){
            return true;
        }
        else return false;
    }

    protected boolean isGatewayRequest() {
        logger.debug("CHECKING IF THIS IS REQUEST FROM GATEWAY");
        try{
            Reference gtw = new Reference(Configuration.gatewayAPIEndpoint);
            String gtwDomain = gtw.getHostDomain();
            int gtwPort = gtw.getHostPort();
            String gtwScheme = gtw.getScheme();

            Reference requestRef = getRequest().getHostRef();
            String rDomain = requestRef.getHostDomain();
            int rPort = requestRef.getHostPort();
            String rScheme = requestRef.getScheme();


            logger.debug("domain: "+rDomain);
            logger.debug("port: "+rPort);
            logger.debug("scheme: "+rScheme);

            logger.debug("GATEWAY: ");
            logger.debug("domain: "+gtwDomain);
            logger.debug("port: "+gtwPort);
            logger.debug("scheme: "+gtwScheme);

            return false;


//            if(sameDomain(gtwDomain, rDomain)){
//                logger.debug("same domain");
//                if(gtwPort == rPort){
//                    logger.debug("same port");
//                    if(gtwScheme.equals(rScheme)){
//                        logger.debug("same scheme");
//                        logger.debug("IS GATEWAY REQUEST!!");
//                        return true;
//                    }
//                    else {
//                        logger.debug("different scheme ...fail");
//                        return false;
//                    }
//                }
//                else {
//                    logger.debug("different port ...fail");
//                    return false;
//                }
//            }
//            else {
//                logger.debug("different domain ...fail");
//                return false;
//            }

        }
        catch(Exception e){
            logger.error("", e);
        }
        return false;
    }

    public String getQueryString(Form query){
        if(query != null){
            String qString = query.getQueryString();
            if(qString != null && !qString.trim().equals("")){
                return "?"+qString.trim();
            }
        }

        return null;

    }

    // RESPONSE HANDLER: START
    public static final String STATUS = "status";
    public static final String SUCCESS = "success";
    public static final String FAILURE = "failure";
    public static final String REASON = "reason";
    public static final String DATA = "data";



    public JSONObject gtwWrapper(Status status, JSONObject message) {
        getResponse().setStatus(Status.SUCCESS_OK);

        JSONObject out = new JSONObject();
        out.put("error", false);
        out.put("statusCode", status.getCode());
        out.put("statusCodeReason", status.getReasonPhrase());
        JSONArray msgArray = new JSONArray();
        if(message != null){
            msgArray.put(message);
        }
        out.put("message", msgArray);

        return out;
    }

    public JSONObject gtwWrapper(String message) {
        JSONObject msgObject = new JSONObject();
        msgObject.put("response", message);
        return gtwWrapper(Status.SUCCESS_OK, msgObject);
    }

    public JSONObject gtwError(Status status, Exception e) {
        getResponse().setStatus(status);

        JSONObject out = new JSONObject();
        out.put("error", true);
        out.put("statusCode", status.getCode());
        out.put("statusCodeReason", status.getReasonPhrase() + ": " + e.getMessage());
        out.put("message", new JSONArray());
        logger.debug("return error ("+getResponse().getStatus()+"): "+out.toString());

        return out;
    }


    public String gtwSuccess(ClientResponse response) throws JSONException {
        JSONObject out = new JSONObject(response.data);
        getResponse().setStatus(new Status(response.statusCode, response.statusCodeReason));
        logger.debug("setting response status code to: "+getResponse().getStatus().getCode());
        return out.toString();
    }

    public JSONObject gtwError(Exception e) {
        return gtwError(Status.SERVER_ERROR_INTERNAL, e);
    }


    public String adapterSuccess(ClientResponse response) throws JSONException {
        JSONObject out = new JSONObject(response.data);
        getResponse().setStatus(new Status(response.statusCode, response.statusCodeReason));
        logger.debug("setting response status code to: "+getResponse().getStatus().getCode());
        return out.toString();
    }


    public JSONObject adapterError(Exception e)  {
        getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
        logger.debug("setting response status code to: "+getResponse().getStatus().getCode());
        JSONObject error = new JSONObject();
        error.put("error", true);
        error.put("reason", e.getMessage());

        logger.debug("return error ("+getResponse().getStatus()+"): "+error.toString());
        return error;
    }

    //RESPONSE HANDLER: END

}
