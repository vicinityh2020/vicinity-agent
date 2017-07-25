package sk.intersoft.vicinity.agent;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.restlet.data.ChallengeScheme;
import org.restlet.resource.ClientResource;

public class TestGtw {
    public static final String LOGIN = "lego_0";
    public static final String PASSWORD = "lego_0";

    public void put() throws Exception {
        try{


//            String endpoint = "http://147.232.202.101:9007/api/objects/D77EC6B0-F039-4734-925E-0A90CE7D1B5B__018BD53D/properties/018BD53D:Brightness";
//            String endpoint = "http://147.232.202.101:9007/api/objects/goethe_1/properties/brightness";
            String endpoint = "http://147.232.202.101:9007/api/objects/goethe_2/properties/color";

            System.out.println("> CALLING: "+endpoint);

            CredentialsProvider provider = new BasicCredentialsProvider();
            UsernamePasswordCredentials credentials
                    = new UsernamePasswordCredentials(LOGIN, PASSWORD);
            provider.setCredentials(AuthScope.ANY, credentials);

            HttpClient client = HttpClientBuilder.create()
                    .setDefaultCredentialsProvider(provider)
                    .build();

            HttpPut request = new HttpPut(endpoint);

            request.addHeader("Accept", "application/json");
            request.addHeader("Content-Type", "application/json");

            String payload = "{\"value\": \"cc33cc\"}";
            StringEntity data = new StringEntity(payload);

            request.setEntity(data);

            HttpResponse response = client.execute(request);


            int status = response.getStatusLine().getStatusCode();
            String content = EntityUtils.toString(response.getEntity());

            HttpEntity entity = response.getEntity();

            System.out.println("> STATUS: "+status);
            System.out.println("> RESPONSE: "+content);

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }


    public void get() throws Exception {
        try{


//            String endpoint = "http://147.232.202.101:9007/api/objects/D77EC6B0-F039-4734-925E-0A90CE7D1B5B__018BD53D/properties/018BD53D:Brightness";
//            String endpoint = "http://147.232.202.101:9007/api/objects/goethe_1/properties/brightness";
            String endpoint = "http://147.232.202.101:9007/api/objects/goethe_1/properties/color";
//            String endpoint = "http://160.40.49.115:8282/api/objects/goethe_2/properties/color";


            System.out.println("> CALLING: "+endpoint);
            ClientResource resource = new ClientResource(endpoint);
            resource.setChallengeResponse(ChallengeScheme.HTTP_BASIC, LOGIN, PASSWORD);
            resource.get();
            System.out.println("> STATUS: "+resource.getStatus());
            System.out.println("> RESPONSE: "+resource.getResponse().getEntity().getText());

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        TestGtw c = new TestGtw();
        c.get();
//        c.put();


    }

}
