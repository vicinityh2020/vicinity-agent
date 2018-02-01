package sk.intersoft.vicinity.agent.thing.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.db.HSQL;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class PersistedThing {
    final static Logger logger = LoggerFactory.getLogger(PersistedThing.class.getName());

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


    public static void list()  {
        String query = listQuery();
        logger.info("LISTING: " + query);

        try{
            Connection conn = HSQL.getConnection();
            try{
                Statement statement = conn.createStatement();
                ResultSet result = statement.executeQuery(query);
                while (result.next()) {
                    logger.info("> RECORD: ");
                    logger.info("  > " + result.getString(OID));
                    logger.info("  > " + result.getString(INFRASTRUCTURE_ID));
                    logger.info("  > " + result.getString(PASSWORD));
                }
                result.close();
                conn.close();
            }
            catch (Exception e){
                logger.error("", e);
            }
        }
        catch(Exception ex){
            logger.error("", ex);
        }
    }

    public static boolean execute(String query) {
        logger.info("EXECUTE: "+query);

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
                logger.error("", e);
            }
        }
        catch(Exception ex){
            logger.error("", ex);
        }
        return false;
    }

    public static void createTable() {
        execute(createTableQuery());
    }


    public PersistedThing get() {
        logger.info("GET: "+toString());
        String query = getQuery();
        logger.info("query: "+query);

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
                logger.error("", e);
            }
        }
        catch(Exception ex){
            logger.error("", ex);
        }
        return thing;
    }

    public boolean delete() {
        logger.info("DELETE: " + toString());
        String query = deleteQuery();
         logger.info("query: " + query);
        return execute(query);
    }

    public boolean persist() {
        logger.info("PERSIST: " + toString());
        PersistedThing thing = get();
        logger.info("existing thing: " + thing);
        String query = insertQuery();
        if(thing != null) {
            query = updateQuery();
        }
        logger.info("persist query: " + query);
        return execute(query);
    }

    public String toString(){
        return "[Persisted Thing: ["+oid+"]["+infrastructureId+"]["+password+"]]";
    }
}
