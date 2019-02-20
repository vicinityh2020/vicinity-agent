package sk.intersoft.vicinity.agent.service.resource;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Header;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.ext.servlet.ServletUtils;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.restlet.util.Series;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.clients.GatewayAPIClient;

import javax.servlet.http.HttpServletRequest;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;

public class TestResource extends AgentResource {
    final static Logger logger = LoggerFactory.getLogger(TestResource.class.getName());

    @Post()
    public String doSomePost(Representation entity) throws Exception {
        logger.info("TEST DO POST");
        String rawPayload = entity.getText();

        logger.info("PAYLOAD: " + rawPayload);

        try{
            Thread.sleep(80000);

            logger.info("done .. returning value");

            return "post result";

        }
        catch(Exception e){
            logger.error("EXCEPTION HERE"+e);
            getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);


            return gtwError(Status.SERVER_ERROR_INTERNAL, e).toString();

        }
    }

    public String adapterCall() throws Exception {
        try{
            String callEndpoint = "http://localhost:9995/adapter/sleep";

            Writer writer = new StringWriter();

            logger.info("ADAPTER CALL: "+callEndpoint);

            ClientResource clientResource = new ClientResource(callEndpoint);
            clientResource.setChallengeResponse(ChallengeScheme.HTTP_BASIC, "", "");

            String payload = "{\"x\": \"y\"}";
            Representation payloadContent = null;
            if (payload != null && !payload.trim().equals("")) {
                payloadContent = new JsonRepresentation(payload);
            }
            logger.info("posting ["+callEndpoint+"] .. " + payloadContent);

            Representation responseRepresentation = clientResource.post(payloadContent, MediaType.APPLICATION_JSON);

            logger.info("post done with: " + responseRepresentation);

            if (responseRepresentation != null) {

                logger.info("response exists");

                responseRepresentation.write(writer);

                // your return values:
                String response = writer.toString();

                logger.info("response: " + response);

                int returnCode = clientResource.getStatus().getCode();
                String returnCodeReason = clientResource.getStatus().getReasonPhrase();

                logger.info("RESPONSE: " + response);
                logger.info("code: " + returnCode);
                logger.info("reason: " + returnCodeReason);

                return "post ok: "+response;

            } else {
                logger.error("EXCEPTION IN ADAPTER CALL!!!");
                throw new Exception("GTW API RETURNED EMPTY RESPONSE");
            }

        }
        catch(Exception e){
            logger.error("EXCEPTION IN ADAPTER CALL TRACE!!!", e);
            throw e;
        }

    }



    @Get()
    public String doSomeGet() throws Exception {
        logger.info("TEST DO GET");
        String result = adapterCall();
        return "get ok: "+result;
    }

}
