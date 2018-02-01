package sk.intersoft.vicinity.hsqldbtest;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ThingTest {

    public void test() throws Exception {
//        PersistedThing.createTable();
        PersistedThing.list();

        (new PersistedThing("a1", "b1", "c1")).persist();
        (new PersistedThing("a2", "b2", "c2")).persist();
        (new PersistedThing("a3", "b3", "c3")).persist();
        (new PersistedThing("a2", "b21", "c21")).persist();
        (new PersistedThing("a2", "x", "x")).delete();
        PersistedThing.list();


    }


    public static void main(String[] args) throws Exception {
        ThingTest t = new ThingTest();
        t.test();
    }

}
