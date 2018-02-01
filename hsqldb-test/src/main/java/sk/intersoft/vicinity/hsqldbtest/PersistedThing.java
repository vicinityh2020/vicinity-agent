package sk.intersoft.vicinity.hsqldbtest;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class PersistedThing {
    // DB KEYS
    private static final String TABLE = "things";
    private static final String OID = "oid";
    private static final String INFRASTRUCTURE_ID = "infrastructure_id";
    private static final String PASSWORD = "password";


    String oid = null;
    String infrastructureId = null;
    String password = null;

    public PersistedThing(String oid, String infrastructureId, String password){
        this.oid = oid;
        this.infrastructureId = infrastructureId;
        this.password = password;
    }


    private static final String createTableQuery(){
        return "CREATE TABLE IF NOT EXISTS "+TABLE+" (" +
                OID+" varchar(255), "+
                INFRASTRUCTURE_ID+" varchar(255), " +
                PASSWORD+" varchar(255)" +
                ")";
    }

    private static final String listQuery(){
        return "SELECT * FROM "+TABLE;
    }

    private String insertQuery(){
        return "INSERT INTO "+TABLE+" ("+OID+", "+INFRASTRUCTURE_ID+", "+PASSWORD+") VALUES (" +
                "'"+oid+"', "+
                "'"+infrastructureId+"', "+
                "'"+password+"'"+
                ")";
    }

    private String updateQuery(){
        return "UPDATE "+TABLE+" SET "+INFRASTRUCTURE_ID+"='"+infrastructureId+"', "+PASSWORD+"='"+password+"' WHERE "+OID+"='"+oid+"'";
    }

    private String deleteQuery(){
        return "DELETE FROM "+TABLE+" WHERE "+OID+"='"+oid+"'";
    }

    private String getQuery(){
        return "SELECT * FROM "+TABLE+" WHERE "+OID+"='"+oid+"'";
    }


    public static void list() throws Exception {
        System.out.println("DB LIST");
        String query = listQuery();
        System.out.println(query);

        try{
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
                result.close();
                conn.close();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public static boolean execute(String query) {
        System.out.println("DB EXECUTE");
        System.out.println(query);

        try{
            Connection conn = HSQL.getConnection();
            try{
                Statement statement = conn.createStatement();
                statement.executeUpdate(query);
                conn.commit();
                conn.close();
                return true;
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    public static void createTable() {
        execute(createTableQuery());
    }


    public PersistedThing get() {
        System.out.println("DB GET: "+toString());
        String query = getQuery();
        System.out.println(query);

        PersistedThing thing = null;

        try{
            Connection conn = HSQL.getConnection();
            try{
                Statement statement = conn.createStatement();
                ResultSet result = statement.executeQuery(query);
                if(result.next()){
                    thing = new PersistedThing(result.getString(OID), result.getString(INFRASTRUCTURE_ID), result.getString(PASSWORD));
                }
                result.close();
                conn.close();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return thing;
    }

    public boolean delete() {
        System.out.println("DELETE: " + toString());
        String query = deleteQuery();
         System.out.println("query: " + query);
        return execute(query);
    }

    public boolean persist() {
        System.out.println("PERSIST: " + toString());
        PersistedThing thing = get();
        System.out.println("existing thing: " + thing);
        String query = insertQuery();
        if(thing != null) {
            query = updateQuery();
        }
        System.out.println("query: " + query);
        return execute(query);
    }

    public String toString(){
        return "[Persisted Thing: ["+oid+"]["+infrastructureId+"]["+password+"]]";
    }
}
