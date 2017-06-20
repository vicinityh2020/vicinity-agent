package sk.intersoft.vicinity.agent.config;


import java.util.logging.Logger;

public class BasicAuthConfig extends AuthConfig {
    private final static Logger LOGGER = Logger.getLogger(BasicAuthConfig.class.getName());

    public String login = "";
    public String password = "";

    public BasicAuthConfig(String login, String password){
        this.login = login;
        this.password = password;
    }


    public void show(){
        System.out.println("BasicAuth");
        System.out.println("login: "+login);
        System.out.println("password: "+password);
    }

}
