package client;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class RESTClient {

    HttpClient client = HttpClientBuilder.create().build();

    public RESTClient() {
    }


    public String get(String uri) {
        System.out.println("DO GET: " + uri);
        try {
            HttpGet request = new HttpGet(uri);
            HttpResponse response = client.execute(request);
            System.out.println("executed: " + response);

            HttpEntity entity = response.getEntity();
            System.out.println("entity: " + entity);

            if (entity != null) return EntityUtils.toString(entity);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String post(String uri, String json) {
        try{
            HttpPost request = new HttpPost(uri);

            request.addHeader("Accept", "application/json");
            request.addHeader("Content-Type", "application/json");

            StringEntity data = new StringEntity(json);

            request.setEntity(data);

            HttpResponse response = client.execute(request);

            return EntityUtils.toString(response.getEntity());

        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;

    }

}
