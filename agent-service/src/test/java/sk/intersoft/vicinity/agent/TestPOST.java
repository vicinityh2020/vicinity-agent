package sk.intersoft.vicinity.agent;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.MediaType;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import sk.intersoft.vicinity.agent.clients.GatewayAPIClient;
import sk.intersoft.vicinity.agent.service.config.Configuration;

import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Scanner;

public class TestPOST {




    public static String file2string(String path) throws Exception {
        try{
            return new Scanner(new File(path)).useDelimiter("\\Z").next();
        }
        catch(Exception e){
            e.printStackTrace();
            throw e;
        }
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

    public static void main(String[] args) throws Exception {
        String file = (new File("")).getAbsolutePath() +  "/agent-service/src/test/resources/nikolaj/data.json";
//        String file = (new File("")).getAbsolutePath() +  "/testing-adapter/src/test/resources/objects/active-disco-objects.json";

        System.out.println("FILE: "+file);


        Configuration.gatewayAPIEndpoint = "http://localhost:8181/api";

//        String login = "697f4484-f7d3-401a-90d0-fc6c50b3f37b";
//        String pwd = "devgrnadapter";

        String login = "test";
        String pwd = "test";

        JSONObject payload = new JSONObject("{}");
        payload.put("agid", login);
        System.out.println("DATA: "+payload.toString());




//        String endpoint = "http://localhost:8181/api/agents/d29d63b3-975c-48fc-8201-05fc51dd1303/objects";
//        String endpoint = "http://localhost:8181/api/test";
        String endpoint = "http://localhost:9995/adapter";

//        String createResponse = GatewayAPIClient.post(GatewayAPIClient.createEndpoint(login), payload.toString(), login, pwd);


        Writer writer = new StringWriter();

        ClientResource clientResource = new ClientResource(endpoint);
        clientResource.setChallengeResponse(ChallengeScheme.HTTP_BASIC, login, pwd);

        Representation responseRepresentation = clientResource.patch(new JsonRepresentation(payload.toString()), MediaType.APPLICATION_JSON);

        if (responseRepresentation != null){

            responseRepresentation.write(writer);

            // your return values:
            String yourReturnString = writer.toString();
            int returnCode = clientResource.getStatus().getCode();
            String returnCodeReason = clientResource.getStatus().getReasonPhrase();

            System.out.println("RESPONSE: "+yourReturnString);
            System.out.println("core: "+returnCode);
            System.out.println("reason: "+returnCodeReason);

        }

    }

}
