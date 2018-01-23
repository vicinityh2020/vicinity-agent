package sk.intersoft.vicinity.agent.service.db;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;

public class DataSource {
    private static HikariDataSource ds = create();

    private static HikariDataSource create(){
        ds = new HikariDataSource();

        System.out.println("setting data source .. ");
        ds.setDriverClassName("org.mariadb.jdbc.Driver");
        ds.setJdbcUrl("jdbc:mariadb://localhost:3306/vicinity_test");
        ds.addDataSourceProperty("user", "root");
        ds.addDataSourceProperty("password", "heslo");
//        ds.setAutoCommit(false);
        return ds;
    }

    public static Connection getConnection() throws Exception {
        return ds.getConnection();
    }
}
