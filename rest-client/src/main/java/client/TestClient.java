package client;


import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.security.*;
import java.util.Scanner;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

public class TestClient {

    public String ENDPOINT = "http://localhost:9997/agent/";

    public static String file2string(String path) {
        try{
            return new Scanner(new File(path)).useDelimiter("\\Z").next();
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }


    public static String path(String path) {
        return new File("").getAbsolutePath() + path;
    }

    private CloseableHttpClient newClient() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
        SSLContext context = SSLContexts.custom()
                .loadTrustMaterial(TrustSelfSignedStrategy.INSTANCE)
                .build();

        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", new SSLConnectionSocketFactory(context, NoopHostnameVerifier.INSTANCE))
                .build();

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);

        CredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials
                = new UsernamePasswordCredentials("smarthome", "smarthome");
        provider.setCredentials(AuthScope.ANY, credentials);

        return HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultCredentialsProvider(provider)
                .build();
    }

    public void test() throws Exception {
        RESTClient client = new RESTClient();

        System.out.println("TEST GET and so: ");
        System.out.println("simple get: "+client.get(ENDPOINT+"alive/kokot"));

//        String testCERTH = "https://smarthome.iti.gr:8443/sitewhere/api/assignments/d6e5acc3-dc29-417f-aa10-ebad34bf9db3/measurements/series?tenantAuthToken=0817e7f4-f555-4b5c-9ae5-69b460b7b15d&startDate=2017-05-22T13%3A10%3A00.000%2B0000";
//        String testCERTH = "https://smarthome.iti.gr:8443/sitewhere/api/assignments/d6e5acc3-dc29-417f-aa10-ebad34bf9db3/measurements/series?tenantAuthToken=0817e7f4-f555-4b5c-9ae5-69b460b7b15d&startDate=2017-05-22T13:10:00.000%2B0000";
        String testCERTH = "https://smarthome.iti.gr:8443/sitewhere/api/assignments/d6e5acc3-dc29-417f-aa10-ebad34bf9db3/measurements/series?tenantAuthToken=0817e7f4-f555-4b5c-9ae5-69b460b7b15d&startDate=2017-05-23T13%3A02%3A03.000%2B0000";

        System.out.println("try apache http: ");


//

        HttpClient httpClient = newClient();
        HttpResponse response = httpClient.execute(
                new HttpGet(testCERTH));
        int statusCode = response.getStatusLine()
                .getStatusCode();

        System.out.println("CERTH get: ");
        System.out.println(statusCode);
        String content = EntityUtils.toString(response.getEntity());
        System.out.println("CERTH content: \n"+content);
        JSONArray certhResponse = new JSONArray(content);
        System.out.println("CNT: "+certhResponse.toString(2));


    }

    public static void main(String[] args) throws Exception {
        TestClient c = new TestClient();
        c.test();
    }

}
