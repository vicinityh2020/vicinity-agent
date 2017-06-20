package sk.intersoft.vicinity.adapter.testing.service;

public class TestingAdapterServer {
    public static void main(String [] args) throws Exception {
        TestingAdapterComponent component = new TestingAdapterComponent();
        component.start();

        System.out.println("starting");

    }
}
