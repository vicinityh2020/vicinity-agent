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
    private static final String THINGS_TABLE = "things";
    public static final String RECOVERY_TABLE = "recovery";
    private static final String OID = "oid";
    private static final String ADAPTER_ID = "adapter_id";
    private static final String ADAPTER_INFRASTRUCTURE_ID = "adapter_infrastructure_id";
    private static final String PASSWORD = "password";
    private static final String RECOVERY_DATA = "recovery_data";


    public String oid = null;
    public String adapterId = null;
    public String adapterInfrastructureId = null;
    public String password = null;

    public PersistedThing(String oid, String adapterId, String adapterInfrastructureId, String password){
        this.oid = oid;
        this.adapterId = adapterId;
        this.adapterInfrastructureId = adapterInfrastructureId;
        this.password = password;
    }

    public PersistedThing(ThingDescription thing){
        this.oid = thing.oid;
        this.adapterId = thing.adapterId;
        this.adapterInfrastructureId = thing.adapterInfrastructureID;
        this.password = thing.password;
    }


    private static final String createTableQuery(){
        String createCredentials = "CREATE TABLE IF NOT EXISTS "+THINGS_TABLE+" (" +
                OID+" varchar(255), "+
                ADAPTER_ID+" varchar(255), " +
                ADAPTER_INFRASTRUCTURE_ID+" varchar(255), " +
                PASSWORD+" varchar(255)" +
                "); ";
        String createRecovery = "CREATE TABLE IF NOT EXISTS "+RECOVERY_TABLE+" (" +
                ADAPTER_ID+" varchar(255), " +
                RECOVERY_DATA+" clob" +
                "); ";
        return createCredentials + createRecovery;

    }

    private static final String clearAdapterQuery(String adapterId){
        return "DELETE FROM "+THINGS_TABLE+" WHERE "+ADAPTER_ID+"='"+adapterId+"'";
    }

    private static final String clearRecoveryAdapterQuery(String adapterId){
        return "DELETE FROM "+RECOVERY_TABLE+" WHERE "+ADAPTER_ID+"='"+adapterId+"'";
    }

    private static final String listThingsQuery(){
        return "SELECT * FROM "+THINGS_TABLE;
    }
    private static final String listRecoveryQuery(){
        return "SELECT * FROM "+RECOVERY_TABLE;
    }


    private static String insertRecoveryQuery(String adapterId, String data){
        return "INSERT INTO "+RECOVERY_TABLE+" ("+ADAPTER_ID+", "+RECOVERY_DATA+") VALUES (" +
                "'"+adapterId+"', "+
                "'"+data+"'"+
                ")";
    }

    private String insertQuery(){
        return "INSERT INTO "+THINGS_TABLE+" ("+OID+", "+ADAPTER_ID+", "+ADAPTER_INFRASTRUCTURE_ID+", "+PASSWORD+") VALUES (" +
                "'"+oid+"', "+
                "'"+adapterId+"', "+
                "'"+adapterInfrastructureId+"', "+
                "'"+password+"'"+
                ")";
    }


    private static String getByOIDQuery(String oid){
        return "SELECT * FROM "+THINGS_TABLE+" WHERE "+OID+"='"+oid+"'";
    }

    private static String getByAdapterIDQuery(String adapterId){
        return "SELECT * FROM "+THINGS_TABLE+" WHERE "+ADAPTER_ID+"='"+adapterId+"'";
    }

    private static String getRecoveryDataByAdapterIDQuery(String adapterId){
        return "SELECT * FROM "+RECOVERY_TABLE+" WHERE "+ADAPTER_ID+"='"+adapterId+"'";
    }

    public static void clearAdapter(String adapterId) {
        execute(clearAdapterQuery(adapterId));
    }

    public static void clearRecoveryAdapter(String adapterId) {
        execute(clearRecoveryAdapterQuery(adapterId));
    }

    public static void listThings(Connection conn) throws Exception{
        String query = listThingsQuery();
        logger.debug("LISTING THINGS PERSISTENCE: " + query);
        Statement statement = conn.createStatement();
        ResultSet result = statement.executeQuery(query);
        while (result.next()) {
            logger.debug("> RECORD: ");
            logger.debug("  > " + result.getString(OID));
            logger.debug("  > " + result.getString(ADAPTER_ID));
            logger.debug("  > " + result.getString(ADAPTER_INFRASTRUCTURE_ID));
            logger.debug("  > " + result.getString(PASSWORD));
        }
        result.close();
    }
    public static void listRecovery(Connection conn) throws Exception{
        String query = listRecoveryQuery();
        logger.debug("LISTING RECOVERY PERSISTENCE: " + query);
        Statement statement = conn.createStatement();
        ResultSet result = statement.executeQuery(query);
        while (result.next()) {
            logger.debug("> RECORD: ");
            logger.debug("  > " + result.getString(ADAPTER_ID));
            logger.debug("  > " + result.getString(RECOVERY_DATA));
        }
        result.close();
    }
    public static void listRecovery() throws Exception {
        try{
            Connection conn = HSQL.getConnection();
            try{
                listRecovery(conn);
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

    public static void list()  {

        try{
            Connection conn = HSQL.getConnection();
            try{
                listThings(conn);
                listRecovery(conn);
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


    public static String getRecoveryDataByAdapterID(String adapterId) {
        logger.debug("GET RECOVERY DATA BY ADAPTER: " + adapterId);
        String query = getRecoveryDataByAdapterIDQuery(adapterId);
        logger.debug("query: "+query);


        try{
            Connection conn = HSQL.getConnection();
            try{
                Statement statement = conn.createStatement();
                ResultSet result = statement.executeQuery(query);
                if(result.next()){
                    return result.getString(RECOVERY_DATA);
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
        return null;
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
                    thing = new PersistedThing(result.getString(OID), result.getString(ADAPTER_ID), result.getString(ADAPTER_INFRASTRUCTURE_ID), result.getString(PASSWORD));
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

    public static PersistedThing getByAdapterID(String adapterId) {
        logger.debug("GET BY ADAPTER: " + adapterId);
        return get(getByAdapterIDQuery(adapterId));
    }

    public static boolean save(ThingDescription thing) {
        logger.debug("SAVING: " + thing.toSimpleString());
        PersistedThing persisted = new PersistedThing(thing);
        String query = persisted.insertQuery();
        logger.debug("save query: " + query);
        return execute(query);
    }

    public static boolean saveRecovery(String adapterId, String data) {
        logger.debug("SAVING RECOVERY FOR: " + adapterId);
        String query = insertRecoveryQuery(adapterId, data);
        logger.debug("save recovery query: " + query);
        return execute(query);
    }

    public String toString(){
        return "[Persisted Thing: ["+oid+"]["+adapterInfrastructureId+"]["+password+"]]";
    }
}
