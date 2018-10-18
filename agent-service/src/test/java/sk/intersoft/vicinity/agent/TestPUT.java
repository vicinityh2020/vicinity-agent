package sk.intersoft.vicinity.agent;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
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

public class TestPUT {



    public void go() throws Exception{
        String login = "04408eb3-af75-4b33-a18e-37a8ffb1d5fe";
        String password = "SYVupqjz5tD9HYcAaViaKvq+JXakfJ6pJoSUjqjrZmk=";
        String payload = "{\"x\": y}";
        String path = "/events/live";
        Configuration.gatewayAPIEndpoint = "http://localhost:8181/api";
        GatewayAPIClient.put(path, payload, login, password);
    }


    public static void main(String[] args) throws Exception {

        TestPUT t = new TestPUT();
        t.go();
    }

}
