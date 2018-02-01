package sk.intersoft.vicinity.hsqldbtest;

import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DSTryout {

    public String createTableQuery(){
        return "CREATE TABLE IF NOT EXISTS things (" +
                "oid varchar(255), "+
                "infrastructure_id varchar(255), " +
                "password varchar(255)" +
                ")";
    }

    public String insertQuery(String oid, String infrastructureId, String password){
        return "INSERT INTO things (oid, infrastructure_id, password) VALUES (" +
                "'"+oid+"', "+
                "'"+infrastructureId+"', "+
                "'"+password+"'"+
                ")";
    }

    public String updateQuery(String oid, String infrastructureId, String password){
        return "UPDATE things SET infrastructure_id='"+infrastructureId+"', password='"+password+"' WHERE oid='"+oid+"'";
    }

    public String deleteQuery(String oid){
        return "DELETE FROM WHERE oid='"+oid+"'";
    }

    public String listQuery(){
        return "SELECT * FROM things";
    }
    public String getOIDQuery(String oid){
        return "SELECT * FROM things WHERE oid='"+oid+"'";
    }

    public void query(String query) throws Exception {
        System.out.println("DB QUERY");
        System.out.println(query);

        Connection conn = HSQL.getConnection();
        try{
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery(query);
            while (result.next()) {
                System.out.println("> RECORD: ");
                System.out.println("  > "+ result.getString("oid"));
                System.out.println("  > "+ result.getString("infrastructure_id"));
                System.out.println("  > "+ result.getString("password"));
            }

            conn.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void exec(String query) throws Exception {
        System.out.println("DB EXEC");
        Connection conn = HSQL.getConnection();
        try{
            System.out.println(query);
            Statement statement = conn.createStatement();
            statement.executeUpdate(query);
            conn.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    public void test() throws Exception {
//        createTable();
//        insert("a1", "b1", "c1");
//        insert("a2", "b2", "c2");
//        exec(createTableQuery());
//        exec(insertQuery("a2", "b2", "c2"));
//        exec(insertQuery("a3", "b3", "c3"));
//        exec(insertQuery("a3", "b3", "c3"));
//        exec(insertQuery("a4", "b4", "c4"));
//        list();

//        exec(updateQuery("a2", "b21", "c21"));
        query(listQuery());
        query(getOIDQuery("a2"));
        query(getOIDQuery("a5"));
    }

    public static void main(String[] args) throws Exception {
        DSTryout t = new DSTryout();
        t.test();
    }

}
