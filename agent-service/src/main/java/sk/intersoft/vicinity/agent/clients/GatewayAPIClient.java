package sk.intersoft.vicinity.agent.clients;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.restlet.Request;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Header;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.engine.header.HeaderConstants;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.util.Series;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.service.config.Configuration;

import java.io.StringWriter;
import java.io.Writer;

public class GatewayAPIClient {
    final static Logger logger = LoggerFactory.getLogger(GatewayAPIClient.class.getName());


    // ENDPOINTS:
    public static final String LOGIN_ENDPOINT = "/objects/login";
    public static final String LOGOUT_ENDPOINT = "/objects/logout";

    // interactions:
    public static final String OBJECT_PROPERTY_ENDPOINT = "/objects/{oid}/properties/{pid}";
    public static final String OBJECT_ACTION_ENDPOINT = "/objects/{oid}/actions/{aid}";
    public static final String OBJECT_ACTION_TASK_ENDPOINT = "/objects/{oid}/actions/{aid}/tasks/{tid}";


    // configuration:
    public static String configurationEndpoint(String agentId) {
      return "/agents/"+agentId+"/objects";
    }

    public static String createEndpoint(String agentId) {
        return "/agents/"+agentId+"/objects";
    }

    public static String updateEndpoint(String agentId) {
        return "/agents/"+agentId+"/objects";
    }

    public static String updateContentEndpoint(String agentId) {
        return "/agents/"+agentId+"/objects/update";
    }

    public static String deleteEndpoint(String agentId) {
        return "/agents/"+agentId+"/objects/delete";
    }

    public static String getInteractionEndpoint(String endpoint, String oid, String patternId, String taskId) {
        return endpoint.replaceAll("\\{oid\\}", oid).replaceAll("\\{pid\\}", patternId).replaceAll("\\{aid\\}", patternId).replaceAll("\\{eid\\}", patternId).replaceAll("\\{tid\\}", taskId);
    }

    public static String getInteractionEndpoint(String endpoint, String oid, String patternId) {
        return getInteractionEndpoint(endpoint, oid, patternId, "");
    }

    public static String getOpenEventChannelEndpoint(String eventId) {
        return "/events/"+eventId;
    }
    public static String getEventChannelStatusEndpoint(String oid, String eventId) {
        return "/objects/"+oid+"/events/"+eventId;
    }
    public static String getSubscribeEventChannelEndpoint(String oid, String eventId) {
        return "/objects/"+oid+"/events/"+eventId;
    }
    public static String getPublishEventEndpoint(String eventId) {
        return "/events/"+eventId;
    }

    public static HttpClient getClient(String login, String password) {

        CredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials
                = new UsernamePasswordCredentials(login, password);
        provider.setCredentials(AuthScope.ANY, credentials);

//        logger.info("GTW API CALL CREDENTIALS:");
//        logger.info("login: ["+login+"]");
//        logger.info("password: ["+password+"]");

        return HttpClientBuilder.create()
                .setDefaultCredentialsProvider(provider)
                .build();

    }

    public static void login(String login, String password) throws Exception {
        logger.info("doing login: ["+login+"]["+password+"]");
        get(LOGIN_ENDPOINT, login, password);
    }

    public static void logout(String login, String password) throws Exception {
        logger.info("doing logout: ["+login+"]["+password+"]");
        get(LOGOUT_ENDPOINT, login, password);
    }

    public static ClientResponse get(String path, String login, String password) throws Exception {
        try{


            String callEndpoint = Configuration.gatewayAPIEndpoint + path;

            logger.info("GTW API GET:");
            logger.info("path: " + path);
            logger.info("endpoint: " + callEndpoint);
            logger.info("credentials: ");
            logger.info("login: " + login);
            logger.info("password: " + password);


//            HttpClient client = getClient(login, password);
//
//            HttpGet request = new HttpGet(callEndpoint);
//
//            request.addHeader("Content-Type", "application/json; charset=utf-8");
//
//            HttpResponse response = client.execute(request);
//
//            int status = response.getStatusLine().getStatusCode();
//            logger.info("GET status: " + status);
//
//            String responseContent = EntityUtils.toString(response.getEntity());
//            logger.info("GTW API response: " + responseContent);
//
//
//            return responseContent;

            logger.info("using restlet client ...");

            Writer writer = new StringWriter();

            ClientResource clientResource = new ClientResource(callEndpoint);
            clientResource.setChallengeResponse(ChallengeScheme.HTTP_BASIC, login, password);

            Representation responseRepresentation = clientResource.get();

            if (responseRepresentation != null){

                responseRepresentation.write(writer);

                // your return values:
                String response = writer.toString();

                int returnCode = clientResource.getStatus().getCode();
                String returnCodeReason = clientResource.getStatus().getReasonPhrase();

                logger.info("RESPONSE: "+response);
                logger.info("code: "+returnCode);
                logger.info("reason: "+returnCodeReason);

                return new ClientResponse(returnCode, returnCodeReason, response);

            }
            else throw new Exception("GTW API RETURNED EMPTY RESPONSE");

        }
        catch(Exception e){
            logger.error("", e);
            throw e;
        }

    }

    public static ClientResponse delete(String path, String login, String password) throws Exception {
        try{


            String callEndpoint = Configuration.gatewayAPIEndpoint + path;

            logger.info("GTW API DELETE:");
            logger.info("path: " + path);
            logger.info("endpoint: " + callEndpoint);
            logger.info("credentials: ");
            logger.info("login: " + login);
            logger.info("password: " + password);


//            HttpClient client = getClient(login, password);
//
//            HttpDelete request = new HttpDelete(callEndpoint);
//
//            request.addHeader("Content-Type", "application/json");
//
//            HttpResponse response = client.execute(request);
//
//            int status = response.getStatusLine().getStatusCode();
//            logger.info("DELETE status: " + status);
//
//            String responseContent = EntityUtils.toString(response.getEntity());
//            logger.info("GTW API response: " + responseContent);
//
//
//            return responseContent;


            logger.info("using restlet client ...");

            Writer writer = new StringWriter();

            ClientResource clientResource = new ClientResource(callEndpoint);
            clientResource.setChallengeResponse(ChallengeScheme.HTTP_BASIC, login, password);

            Representation responseRepresentation = clientResource.delete();

            if (responseRepresentation != null){

                responseRepresentation.write(writer);

                // your return values:
                String response = writer.toString();

                int returnCode = clientResource.getStatus().getCode();
                String returnCodeReason = clientResource.getStatus().getReasonPhrase();

                logger.info("RESPONSE: "+response);
                logger.info("code: "+returnCode);
                logger.info("reason: "+returnCodeReason);

                return new ClientResponse(returnCode, returnCodeReason, response);

            }
            else throw new Exception("GTW API RETURNED EMPTY RESPONSE");

        }
        catch(Exception e){
            logger.error("", e);
            throw e;
        }

    }

    public static ClientResponse post(String path, String payload, String login, String password) throws Exception {
        try{


            String callEndpoint = Configuration.gatewayAPIEndpoint + path;

            logger.info("GTW API POST:");
            logger.info("path: " + path);
            logger.info("endpoint: " + callEndpoint);
            logger.info("payload: " + payload);
            logger.info("credentials: ");
            logger.info("login: " + login);
            logger.info("password: " + password);
            logger.info("headers set to [application/json; charset=utf-8]");




//            HttpClient client = getClient(login, password);
//
//
//            HttpPost request = new HttpPost(callEndpoint);
//            RequestConfig requestConfig = RequestConfig.custom()
//                    .setSocketTimeout(5000)
//                    .setConnectTimeout(5000)
//                    .build();
//            request.setConfig(requestConfig);
//
//
//            request.addHeader("Content-Type", "application/json; charset=utf-8");
//
//            if(payload != null){
//                StringEntity data = new StringEntity(payload, "utf-8");
//                request.setEntity(data);
//            }
//
//
//            HttpResponse response = client.execute(request);
//
//            int status = response.getStatusLine().getStatusCode();
//            logger.info("POST status: " + status);
//
//            String responseContent = EntityUtils.toString(response.getEntity());
//            logger.info("GTW API response: " + responseContent);
//
//
//            return responseContent;

            logger.info("using restlet client ...");

            Writer writer = new StringWriter();

            ClientResource clientResource = new ClientResource(callEndpoint);
            clientResource.setChallengeResponse(ChallengeScheme.HTTP_BASIC, login, password);

            Representation payloadContent = null;
            if(payload != null && !payload.trim().equals("")){
                payloadContent = new JsonRepresentation(payload);
            }
            logger.info("posting .. "+payloadContent);
            Representation responseRepresentation = clientResource.post(payloadContent, MediaType.APPLICATION_JSON);
            logger.info("post done with: "+responseRepresentation);

            if (responseRepresentation != null){

                logger.info("response exists");

                responseRepresentation.write(writer);

                // your return values:
                String response = writer.toString();

                logger.info("response: "+response);

                int returnCode = clientResource.getStatus().getCode();
                String returnCodeReason = clientResource.getStatus().getReasonPhrase();

                logger.info("RESPONSE: "+response);
                logger.info("code: "+returnCode);
                logger.info("reason: "+returnCodeReason);

                return new ClientResponse(returnCode, returnCodeReason, response);

            }
            else throw new Exception("GTW API RETURNED EMPTY RESPONSE");

        }
        catch(Exception e){
            logger.error("", e);
            throw e;
        }

    }

    public static ClientResponse put(String path, String payload, String login, String password) throws Exception {
        try{


            String callEndpoint = Configuration.gatewayAPIEndpoint + path;

            logger.info("GTW API PUT:");
            logger.info("path: " + path);
            logger.info("endpoint: " + callEndpoint);
            logger.info("payload: " + payload);
            logger.info("credentials: ");
            logger.info("login: " + login);
            logger.info("password: " + password);
            logger.info("headers set to [application/json; charset=utf-8]");




//            HttpClient client = getClient(login, password);
//
//
//            HttpPut request = new HttpPut(callEndpoint);
//            RequestConfig requestConfig = RequestConfig.custom()
//                    .setSocketTimeout(5000)
//                    .setConnectTimeout(5000)
//                    .build();
//            request.setConfig(requestConfig);
//
//            request.addHeader("Content-Type", "application/json; charset=utf-8");
//
//            if(payload != null){
//                StringEntity data = new StringEntity(payload, "utf-8");
//                request.setEntity(data);
//            }
//
//
//            HttpResponse response = client.execute(request);
//
//            int status = response.getStatusLine().getStatusCode();
//            logger.info("PUT status: " + status);
//
//            String responseContent = EntityUtils.toString(response.getEntity());
//            logger.info("GTW API response: " + responseContent);
//
//            return responseContent;

            logger.info("using restlet client ...");

            Writer writer = new StringWriter();

            ClientResource clientResource = new ClientResource(callEndpoint);
            clientResource.setChallengeResponse(ChallengeScheme.HTTP_BASIC, login, password);

            Representation payloadContent = null;
            if(payload != null && !payload.trim().equals("")){
                payloadContent = new JsonRepresentation(payload);
            }

//            String cntType = "application/json; charset=utf-8";
//            Series<Header> headerValue = new Series<>(Header.class);
//            Request request = clientResource.getRequest();
//            headerValue.add("Content-Type", cntType);
//            request.getAttributes().put(HeaderConstants.ATTRIBUTE_HEADERS, headerValue);            //            request.addHeader("Content-Type", "application/json; charset=utf-8");
//
//            logger.info("content-type header set to: "+cntType);

            logger.info("putting .. "+payloadContent);
            Representation responseRepresentation = clientResource.put(payloadContent, MediaType.APPLICATION_JSON);
            logger.info("put done with: "+responseRepresentation);

            if (responseRepresentation != null){

                logger.info("response exists");

                responseRepresentation.write(writer);

                // your return values:
                String response = writer.toString();

                logger.info("response: "+response);

                int returnCode = clientResource.getStatus().getCode();
                String returnCodeReason = clientResource.getStatus().getReasonPhrase();

                logger.info("RESPONSE: "+response);
                logger.info("code: "+returnCode);
                logger.info("reason: "+returnCodeReason);

                return new ClientResponse(returnCode, returnCodeReason, response);

            }
            else throw new Exception("GTW API RETURNED EMPTY RESPONSE");

        }
        catch(Exception e){
            logger.error("", e);
            throw e;
        }

    }



}