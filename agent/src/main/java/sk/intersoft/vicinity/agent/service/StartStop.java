package sk.intersoft.vicinity.agent.service;

import sk.intersoft.vicinity.agent.adapter.AgentAdapter;
import sk.intersoft.vicinity.agent.config.BasicAuthConfig;
import sk.intersoft.vicinity.agent.config.NewAgentConfig;
import sk.intersoft.vicinity.agent.gateway.GatewayAPIClient;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;

public class StartStop implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.out.println("Starting up!");
        try{
            NewAgentConfig.create("/home/kostelni/work/eu-projekty/vicinity/bitbucket-workspace/vicinity-agent/agent/bin/agent-config.json");
            GatewayAPIClient.login();
            BasicAuthConfig auth = (BasicAuthConfig)NewAgentConfig.auth;
            GatewayAPIClient.relogin(auth.login, auth.password);
            AgentAdapter.create(NewAgentConfig.adapterEndpoint);
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println("SOMETHING WENT APE BY INITIALIZATION!");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        System.out.println("Shutting down!");
        GatewayAPIClient.logout();
        BasicAuthConfig auth = (BasicAuthConfig)NewAgentConfig.auth;
        GatewayAPIClient.logout(auth.login, auth.password);
    }
}
