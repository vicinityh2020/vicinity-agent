package sk.intersoft.vicinity.hsqldbtest;

import org.json.JSONObject;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Tryout {
    String dbFile = "/home/kostelni/work/eu-projekty/vicinity/unikl-workspace/vicinity-agent/hsqldb-test/src/test/resources/data/test.db";

    public void dropTable() throws Exception {
        System.out.println("TRYING DB DROP TABLE: "+dbFile);
        try{
            Class.forName("org.hsqldb.jdbcDriver");
            Connection conn = DriverManager.getConnection("jdbc:hsqldb:file:"+dbFile+"", "SA", "");

            String create = "DROP TABLE  things";
            Statement statement = conn.createStatement();
            statement.executeUpdate(create);
            conn.commit();
            conn.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public void createTable() throws Exception {
        System.out.println("TRYING DB CREATE TABLE: "+dbFile);
        try{
            Class.forName("org.hsqldb.jdbcDriver");
            Connection conn = DriverManager.getConnection("jdbc:hsqldb:file:"+dbFile, "SA", "");

            String create = "CREATE TABLE IF NOT EXISTS things (" +
                    "id INTEGER IDENTITY PRIMARY KEY, "+
                    "json CLOB NOT NULL" +
                    ")";
            Statement statement = conn.createStatement();
            statement.executeUpdate(create);
            conn.commit();
            conn.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public void insert() throws Exception {
        System.out.println("TRYING DB INSERT STATEMENTS: "+dbFile);
        try{
            Class.forName("org.hsqldb.jdbcDriver");
            Connection conn = DriverManager.getConnection("jdbc:hsqldb:file:"+dbFile, "SA", "");

            JSONObject o = new JSONObject();
            o.put("x", "y");
            o.put("y", "z");
            String create = "INSERT INTO things (json) VALUES ('"+o.toString()+"')";
            Statement statement = conn.createStatement();
            statement.executeUpdate(create);
            conn.commit();
            conn.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public void persist(String a, String b) throws Exception {
        System.out.println("TRYING DB PERSIST: "+dbFile);
        System.out.println("A: "+a);
        System.out.println("B: "+b);
        try{
            Class.forName("org.hsqldb.jdbcDriver");
            Connection conn = DriverManager.getConnection("jdbc:hsqldb:file:"+dbFile, "SA", "");

            JSONObject o = new JSONObject();
            o.put("x", "y");
            o.put("y", "z");
            String create = "INSERT INTO things (json) VALUES ('"+o.toString()+"')";
            Statement statement = conn.createStatement();
            statement.executeUpdate(create);
            conn.commit();
            conn.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public void list() throws Exception {
        System.out.println("TRYING DB LIST: "+dbFile);
        try{
            Class.forName("org.hsqldb.jdbcDriver");
            Connection conn = DriverManager.getConnection("jdbc:hsqldb:file:"+dbFile, "SA", "");

            String create = "SELECT * FROM things";
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery(create);
            while (result.next()) {
                System.out.println("> RECORD: ");
                System.out.println("  > "+ result.getInt("id"));
                System.out.println("  > "+ (new JSONObject(result.getString("json"))).toString(2));
            }

            conn.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public void test() throws Exception {
//        dropTable();
//        createTable();
//        insert();
//        insert();
//        insert();
        persist("a1", "b1");
        persist("a2", "b2");
        list();
    }

    public static void main(String[] args) throws Exception {
        Tryout t = new Tryout();
        t.test();
    }

}
