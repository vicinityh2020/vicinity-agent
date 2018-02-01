package sk.intersoft.vicinity.hsqldbtest;

import java.sql.Connection;
import java.sql.DriverManager;

public class HSQL {
    static final String dbFile = "/home/kostelni/work/eu-projekty/vicinity/unikl-workspace/vicinity-agent/hsqldb-test/src/test/resources/data/test-ds-thing.db";

    public static Connection getConnection() throws Exception {
        Class.forName("org.hsqldb.jdbcDriver");
        return DriverManager.getConnection("jdbc:hsqldb:file:" + dbFile, "SA", "");
    }

}
