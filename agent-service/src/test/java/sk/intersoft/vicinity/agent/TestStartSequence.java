package sk.intersoft.vicinity.agent;


import sk.intersoft.vicinity.agent.service.StartStop;
import sk.intersoft.vicinity.agent.service.config.Configuration;

import java.io.File;

public class TestStartSequence {

    public void test() throws Exception {

        StartStop.start();

    }
    public static void main(String[] args) throws Exception {
        TestStartSequence c = new TestStartSequence();
        c.test();
    }

}
