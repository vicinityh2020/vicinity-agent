package sk.intersoft.vicinity.agent.service;

import sk.intersoft.vicinity.agent.adapter.AgentAdapter;
import sk.intersoft.vicinity.agent.config.BasicAuthConfig;
import sk.intersoft.vicinity.agent.config.AgentConfig;
import sk.intersoft.vicinity.agent.gateway.GatewayAPIClient;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class StartStop implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.out.println("Starting up!");
        try{
            AgentConfig.create(System.getProperty("config.file"));
//            GatewayAPIClient.login();
//            BasicAuthConfig auth = (BasicAuthConfig) AgentConfig.auth;
//            GatewayAPIClient.relogin(auth.login, auth.password);
            AgentAdapter.create(AgentConfig.adapterEndpoint);
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println("SOMETHING WENT APE BY INITIALIZATION!");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        System.out.println("Shutting down!");
//        GatewayAPIClient.logout();
//        BasicAuthConfig auth = (BasicAuthConfig) AgentConfig.auth;
//        GatewayAPIClient.logout(auth.login, auth.password);
    }
}
