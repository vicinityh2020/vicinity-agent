package sk.intersoft.vicinity.agent;

import sk.intersoft.vicinity.agent.thing.persistence.PersistedThing;

public class ThingPersistenceTest {

    public void test()  {
        PersistedThing.createTable();
        PersistedThing.list();

//        (new PersistedThing("a1", "b1", "c1")).persist();
//        (new PersistedThing("a2", "b2", "c2")).persist();
//        (new PersistedThing("a3", "b3", "c3")).persist();
//        (new PersistedThing("a2", "b21", "c21")).persist();
//        (new PersistedThing("a2", "x", "x")).delete();
//        (new PersistedThing("a1", "b11", "c11")).persist();
//        (new PersistedThing("a2", "b2", "c2")).persist();
//        (new PersistedThing("d282cb08-48da-4f1b-a681-3db34edca6bf", "test-bulb1", "x")).delete();
//        (new PersistedThing("d282cb08-48da-4f1b-a681-3db34edca6bf", "test-bulb1", "x")).persist();
        PersistedThing.list();


    }


    public static void main(String[] args)  {
        ThingPersistenceTest t = new ThingPersistenceTest();
        t.test();
    }

}
