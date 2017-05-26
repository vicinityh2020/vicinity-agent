package sk.intersoft.vicinity.adapter.certh;



public class TestCERTHAdapter {

    public void test() throws Exception {
        CERTHSmartHomeAdapter adapter1 = new CERTHSmartHomeAdapter();
        CERTHLinksmartAdapter adapter2 = new CERTHLinksmartAdapter();
        System.out.println("TRY SmartHome: "+adapter1.getPropertiesValue("d6e5acc3-dc29-417f-aa10-ebad34bf9db3", "humidity"));
        System.out.println("TRY LinkSmart: "+adapter2.getPropertiesValue("0D485748-CF2A-450C-BCF6-02AC1CB39A2D:6", "PowerConsumption"));
    }

    public static void main(String[] args) throws Exception {
        TestCERTHAdapter c = new TestCERTHAdapter();
        c.test();
    }

}
