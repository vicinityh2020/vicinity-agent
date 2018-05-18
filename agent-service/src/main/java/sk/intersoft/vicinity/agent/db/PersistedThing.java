package sk.intersoft.vicinity.agent.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.thing.ThingDescription;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class PersistedThing {
    final static Logger logger = LoggerFactory.getLogger(PersistedThing.class.getName());

    // DB KEYS
    private static final String TABLE = "things";
    private static final String OID = "oid";
    private static final String ADAPTER_INFRASTRUCTURE_ID = "adapter_infrastructure_id";
    private static final String PASSWORD = "password";


    public String oid = null;
    public String adapterInfrastructureId = null;
    public String password = null;

    public PersistedThing(String oid, String infrastructureId, String password){
        this.oid = oid;
        this.adapterInfrastructureId = adapterInfrastructureId;
        this.password = password;
    }

    public PersistedThing(ThingDescription thing){
        this.oid = thing.oid;
        this.adapterInfrastructureId = thing.adapterInfrastructureID;
        this.password = thing.password;
    }


    private static final String createTableQuery(){
        return "CREATE TABLE IF NOT EXISTS "+TABLE+" (" +
                OID+" varchar(255), "+
                ADAPTER_INFRASTRUCTURE_ID+" varchar(255), " +
                PASSWORD+" varchar(255)" +
                ")";
    }

    private static final String clearQuery(){
        return "DELETE FROM "+TABLE;
    }

    private static final String listQuery(){
        return "SELECT * FROM "+TABLE;
    }

    private String insertQuery(){
        return "INSERT INTO "+TABLE+" ("+OID+", "+ADAPTER_INFRASTRUCTURE_ID+", "+PASSWORD+") VALUES (" +
                "'"+oid+"', "+
                "'"+adapterInfrastructureId+"', "+
                "'"+password+"'"+
                ")";
    }


    private static String getByOIDQuery(String oid){
        return "SELECT * FROM "+TABLE+" WHERE "+OID+"='"+oid+"'";
    }

    public static void clear() {
        execute(clearQuery());
    }

    public static void list()  {
        String query = listQuery();
        logger.debug("LISTING: " + query);

        try{
            Connection conn = HSQL.getConnection();
            try{
                Statement statement = conn.createStatement();
                ResultSet result = statement.executeQuery(query);
                while (result.next()) {
                    logger.debug("> RECORD: ");
                    logger.debug("  > " + result.getString(OID));
                    logger.debug("  > " + result.getString(ADAPTER_INFRASTRUCTURE_ID));
                    logger.debug("  > " + result.getString(PASSWORD));
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
        logger.debug("EXECUTE: "+query);

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


    public static PersistedThing get(String query) {
        logger.debug("GET: ");
        logger.debug("query: "+query);

        PersistedThing thing = null;

        try{
            Connection conn = HSQL.getConnection();
            try{
                Statement statement = conn.createStatement();
                ResultSet result = statement.executeQuery(query);
                if(result.next()){
                    thing = new PersistedThing(result.getString(OID), result.getString(ADAPTER_INFRASTRUCTURE_ID), result.getString(PASSWORD));
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

    public static PersistedThing getByOID(String oid) {
        logger.debug("GET BY OID: " + oid);
        return get(getByOIDQuery(oid));
    }


    public static boolean save(ThingDescription thing) {
        logger.debug("SAVING: " + thing.toSimpleString());
        PersistedThing persisted = new PersistedThing(thing);
        String query = persisted.insertQuery();
        logger.debug("save query: " + query);
        return execute(query);
    }

    public String toString(){
        return "[Persisted Thing: ["+oid+"]["+adapterInfrastructureId+"]["+password+"]]";
    }
}
