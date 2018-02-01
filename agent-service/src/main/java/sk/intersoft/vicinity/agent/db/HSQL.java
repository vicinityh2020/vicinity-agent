package sk.intersoft.vicinity.agent.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;

public class HSQL {
    final static Logger logger = LoggerFactory.getLogger(HSQL.class.getName());

    static final String dbFile = System.getProperty("db.file");

    public static Connection getConnection() throws Exception {
        logger.debug("persistence file: "+dbFile);
        Class.forName("org.hsqldb.jdbcDriver");
        return DriverManager.getConnection("jdbc:hsqldb:file:" + dbFile, "SA", "");
    }

}
