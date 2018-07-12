package sk.intersoft.vicinity.agent;


import org.restlet.data.Reference;
import sk.intersoft.vicinity.agent.service.config.Configuration;

public class TestReference {

    public void show(Reference r) {
        String domain = r.getHostDomain();
        int port = r.getHostPort();
        String scheme = r.getScheme();
        System.out.println("REF: "+r);
        System.out.println("domain: "+domain);
        System.out.println("port: "+port);
        System.out.println("scheme: "+scheme);

    }

    public void go() throws Exception {
        System.out.println("REF: ");

        Reference a1 = new Reference("http://localhost:8484/ssd");
        Reference a2 = new Reference("http://localhost:8484/ssd/asd/asd");
        Reference a3 = new Reference("http://14.23.232.23:8484/ssd/asd/asd");
        Reference a4 = new Reference("http://127.0.0.1:8484/ssd/asd/asd");


        show(a1);
        show(a2);
        show(a3);
        show(a4);

    }


    public static void main(String[] args) throws Exception {
        TestReference t = new TestReference();
        t.go();
    }

}
