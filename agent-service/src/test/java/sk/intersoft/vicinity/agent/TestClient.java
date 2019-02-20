package sk.intersoft.vicinity.agent;

import org.restlet.Client;
import org.restlet.Context;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.MediaType;
import org.restlet.data.Protocol;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import sk.intersoft.vicinity.agent.clients.ClientResponse;
import sk.intersoft.vicinity.agent.service.config.Configuration;

import java.io.StringWriter;
import java.io.Writer;

public class TestClient {
    public String get() throws Exception {
        try {


            String callEndpoint = "http://localhost:9997/agent/objects/7b32f844-aeee-487d-a052-f1bf1cb4d692/properties/example-property";

            System.out.println("GET:");
            System.out.println("endpoint: " + callEndpoint);
            System.out.println("using restlet client ...");

            Writer writer = new StringWriter();

            ClientResource clientResource = new ClientResource(callEndpoint);

            Representation responseRepresentation = clientResource.get(MediaType.APPLICATION_JSON);
            System.out.println("get done with: " + responseRepresentation);

            if (responseRepresentation != null) {

                System.out.println("response exists");

                responseRepresentation.write(writer);

                // your return values:
                String response = writer.toString();

                System.out.println("response: " + response);

                int returnCode = clientResource.getStatus().getCode();
                String returnCodeReason = clientResource.getStatus().getReasonPhrase();

                System.out.println("RESPONSE: " + response);
                System.out.println("code: " + returnCode);
                System.out.println("reason: " + returnCodeReason);

                return response;

            } else throw new Exception("GTW API RETURNED EMPTY RESPONSE");

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public String put() throws Exception {
        try {


            String callEndpoint = "http://localhost:9997/agent/objects/7b32f844-aeee-487d-a052-f1bf1cb4d692/properties/example-property";
            String payload = "{\"x\", \"y\"}";

            System.out.println("PUT:");
            System.out.println("endpoint: " + callEndpoint);
            System.out.println("payload: " + payload);
            System.out.println("using restlet client ...");

            Writer writer = new StringWriter();

            ClientResource clientResource = new ClientResource(callEndpoint);

            Representation payloadContent = null;
            if (payload != null && !payload.trim().equals("")) {
                payloadContent = new JsonRepresentation(payload);
            }

            System.out.println("putting .. " + payloadContent);
            Representation responseRepresentation = clientResource.put(payloadContent, MediaType.APPLICATION_JSON);
            System.out.println("put done with: " + responseRepresentation);

            if (responseRepresentation != null) {

                System.out.println("response exists");

                responseRepresentation.write(writer);

                // your return values:
                String response = writer.toString();

                System.out.println("response: " + response);

                int returnCode = clientResource.getStatus().getCode();
                String returnCodeReason = clientResource.getStatus().getReasonPhrase();

                System.out.println("RESPONSE: " + response);
                System.out.println("code: " + returnCode);
                System.out.println("reason: " + returnCodeReason);

                return response;

            } else throw new Exception("GTW API RETURNED EMPTY RESPONSE");

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }

    public String timeoutPost() throws Exception {
        try{
            String callEndpoint = "http://localhost:9997/agent/test";

            Writer writer = new StringWriter();

            System.out.println("setting timeout");

//            ctx.getParameters().add("idleTimeout", "360000");
//            ctx.getParameters().add("socketTimeout", "360000");
//            ctx.getParameters().add("stopIdleTimeout", "360000");
            Context ctx = new Context();
            ctx.getParameters().add("readTimeout", "360000");
            ctx.getParameters().add("socketConnectTimeoutMs", "360000");

//            Client client = new Client(ctx, Protocol.HTTP);


//            ClientResource clientResource = new ClientResource(callEndpoint);
            ClientResource clientResource = new ClientResource(ctx, callEndpoint);
//            clientResource.setNext(client);
//            clientResource.setRetryOnError(true);


            clientResource.setChallengeResponse(ChallengeScheme.HTTP_BASIC, "", "");

            String payload = "{\"x\": \"y\"}";
            Representation payloadContent = null;
            if (payload != null && !payload.trim().equals("")) {
                payloadContent = new JsonRepresentation(payload);
            }
            System.out.println("posting .. " + payloadContent);

            Representation responseRepresentation = clientResource.post(payloadContent, MediaType.APPLICATION_JSON);

            System.out.println("post done with: " + responseRepresentation);

            if (responseRepresentation != null) {

                System.out.println("response exists");

                responseRepresentation.write(writer);

                // your return values:
                String response = writer.toString();

                System.out.println("response: " + response);

                int returnCode = clientResource.getStatus().getCode();
                String returnCodeReason = clientResource.getStatus().getReasonPhrase();

                System.out.println("RESPONSE: " + response);
                System.out.println("code: " + returnCode);
                System.out.println("reason: " + returnCodeReason);

                return "POST GOOD: "+response;

            } else throw new Exception("GTW API RETURNED EMPTY RESPONSE");

        }
        catch(Exception e){
            throw e;
        }

    }
    public String timeoutGet() throws Exception {
        try{
            String callEndpoint = "http://localhost:9997/agent/test";

            Writer writer = new StringWriter();

            System.out.println("setting timeout");

//            ctx.getParameters().add("idleTimeout", "360000");
//            ctx.getParameters().add("socketTimeout", "360000");
//            ctx.getParameters().add("stopIdleTimeout", "360000");
            Context ctx = new Context();
            ctx.getParameters().add("readTimeout", "360000");
            ctx.getParameters().add("socketConnectTimeoutMs", "360000");

            ctx.getParameters().add("idleTimeout", "360000");
            ctx.getParameters().add("socketTimeout", "360000");

//            Client client = new Client(ctx, Protocol.HTTP);


//            ClientResource clientResource = new ClientResource(callEndpoint);
            ClientResource clientResource = new ClientResource(ctx, callEndpoint);
//            clientResource.setNext(client);
//            clientResource.setRetryOnError(true);


            clientResource.setChallengeResponse(ChallengeScheme.HTTP_BASIC, "", "");

            System.out.println("getting .. ");

            Representation responseRepresentation = clientResource.get();

            System.out.println("get done with: " + responseRepresentation);

            if (responseRepresentation != null) {

                System.out.println("response exists");

                responseRepresentation.write(writer);

                // your return values:
                String response = writer.toString();

                System.out.println("response: " + response);

                int returnCode = clientResource.getStatus().getCode();
                String returnCodeReason = clientResource.getStatus().getReasonPhrase();

                System.out.println("RESPONSE: " + response);
                System.out.println("code: " + returnCode);
                System.out.println("reason: " + returnCodeReason);

                return "GET GOOD: "+response;

            } else throw new Exception("GTW API RETURNED EMPTY RESPONSE");

        }
        catch(Exception e){
            throw e;
        }

    }
    public static void main(String[] args) throws Exception {
        TestClient t = new TestClient();
//        System.out.println("POST: "+t.timeoutPost());
        System.out.println("GET: "+t.timeoutGet());
    }
}