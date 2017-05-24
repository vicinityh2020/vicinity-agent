package sk.intersoft.vicinity.adapter.certh;



public class TestCERTHAdapter {

    public void test() throws Exception {
        CERTHSmartHomeAdapter adapter = new CERTHSmartHomeAdapter();
        System.out.println("TRY: "+adapter.getPropertiesValue("d6e5acc3-dc29-417f-aa10-ebad34bf9db3", "humidity"));
    }

    public static void main(String[] args) throws Exception {
        TestCERTHAdapter c = new TestCERTHAdapter();
        c.test();
    }

}
