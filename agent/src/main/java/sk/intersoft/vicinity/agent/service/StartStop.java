package sk.intersoft.vicinity.agent.service;

import sk.intersoft.vicinity.agent.config.AgentConfig;
import sk.intersoft.vicinity.agent.gateway.GatewayAPIClient;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class StartStop implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.out.println("Starting up!");
        GatewayAPIClient.login();
        GatewayAPIClient.relogin(AgentConfig.agentLogin, AgentConfig.agentPassword);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        System.out.println("Shutting down!");
        GatewayAPIClient.logout();
    }
}
