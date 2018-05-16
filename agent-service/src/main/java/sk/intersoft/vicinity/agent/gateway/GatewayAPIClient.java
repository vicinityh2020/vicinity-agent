package sk.intersoft.vicinity.agent.gateway;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.service.config.AgentConfig;
import sk.intersoft.vicinity.agent.service.resource.ResourceResponse;

public class GatewayAPIClient {
    final static Logger logger = LoggerFactory.getLogger(GatewayAPIClient.class.getName());


    // ENDPOINTS:
    public static final String LOGIN = "/objects/login";
    public static final String LOGOUT = "/objects/logout";

    // interactions:
    public static final String OBJECT_PROPERTY = "/objects/{oid}/properties/{pid}";
    public static final String OBJECT_ACTION = "/objects/{oid}/actions/{aid}";
    public static final String OBJECT_EVENT = "/objects/{oid}/events/{eid}";



}