package sk.intersoft.vicinity.agent.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.thing.ThingDescription;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

public class Persistence {
    final static Logger logger = LoggerFactory.getLogger(Persistence.class.getName());

    // DB KEYS
    private static final String THINGS_TABLE = "things";
    private static final String AGENTS_TABLE = "agents";
    public static final String RECOVERY_TABLE = "recovery";
    private static final String OID = "oid";
    private static final String AGENT_ID = "agent_id";
    private static final String ADAPTER_ID = "adapter_id";
    private static final String ADAPTER_INFRASTRUCTURE_ID = "adapter_infrastructure_id";
    private static final String PASSWORD = "password";
    private static final String RECOVERY_DATA = "recovery_data";



    private static final String createTableQuery(){
        String createCredentials = "CREATE TABLE IF NOT EXISTS "+THINGS_TABLE+" (" +
                OID+" varchar(255), "+
                AGENT_ID+" varchar(255) DEFAULT NULL, " +
                ADAPTER_ID+" varchar(255), " +
                ADAPTER_INFRASTRUCTURE_ID+" varchar(255), " +
                PASSWORD+" varchar(255)" +
                "); ";
        String createRecovery = "CREATE TABLE IF NOT EXISTS "+RECOVERY_TABLE+" (" +
                AGENT_ID+" varchar(255) DEFAULT NULL, " +
                ADAPTER_ID+" varchar(255), " +
                RECOVERY_DATA+" clob" +
                "); ";
        String createAgentCredentials = "CREATE TABLE IF NOT EXISTS "+AGENTS_TABLE+" (" +
                AGENT_ID+" varchar(255) DEFAULT NULL, " +
                PASSWORD+" varchar(255) DEFAULT NULL" +
                "); ";
        return createCredentials + createRecovery + createAgentCredentials;

    }

    private static String addThingAgentIdColumnQuery() {
        return "ALTER TABLE "+THINGS_TABLE+" ADD COLUMN " +
                AGENT_ID+" varchar(255) DEFAULT NULL " +
                " BEFORE "+ADAPTER_ID + "; ";
//        return "ALTER TABLE "+THINGS_TABLE+" DROP COLUMN " +
//                AGENT_ID+ "; ";
    }
    private static String addRecoveryAgentIdColumnQuery() {
        return "ALTER TABLE "+RECOVERY_TABLE+" ADD COLUMN " +
                AGENT_ID+" varchar(255) DEFAULT NULL " +
                " BEFORE "+ADAPTER_ID + "; ";
//        return "ALTER TABLE "+RECOVERY_TABLE+" DROP COLUMN " +
//                AGENT_ID+ "; ";
    }

    private static final String clearAdapterQuery(String adapterId){
        return "DELETE FROM "+THINGS_TABLE+" WHERE "+ADAPTER_ID+"='"+adapterId+"'";
    }

    private static final String clearRecoveryAdapterQuery(String adapterId){
        return "DELETE FROM "+RECOVERY_TABLE+" WHERE "+ADAPTER_ID+"='"+adapterId+"'";
    }

    private static final String clearAgentQuery(String agentId){
        return "DELETE FROM "+AGENTS_TABLE+" WHERE "+AGENT_ID+"='"+agentId+"'";
    }
    private static final String saveAgentQuery(String agentId, String password){
        return "INSERT INTO "+AGENTS_TABLE+" ("+AGENT_ID+", "+PASSWORD+") VALUES (" +
                "'"+agentId+"', "+
                "'"+password+"'"+
                ")";
    }


    private static final String listThingsQuery(){
        return "SELECT * FROM "+THINGS_TABLE;
    }
    private static final String listRecoveryQuery(){
        return "SELECT * FROM "+RECOVERY_TABLE;
    }
    private static final String listAgentsQuery(){
        return "SELECT * FROM "+AGENTS_TABLE;
    }

    private static final String thingsAdaptersQuery(String agentId){
        String where = "";
        if(agentId != null && !agentId.trim().equals("")){
            where = " WHERE "+AGENT_ID+" = '"+agentId+"'";
        }
        return "SELECT "+ADAPTER_ID+" FROM "+THINGS_TABLE + where;
    }
    private static final String recoveryAdaptersQuery(String agentId){
        String where = "";
        if(agentId != null && !agentId.trim().equals("")){
            where = " WHERE "+AGENT_ID+" = '"+agentId+"'";
        }
        return "SELECT "+ADAPTER_ID+" FROM "+RECOVERY_TABLE + where;
    }

    private static final String adapterThingsQuery(String adapterId){
        return "SELECT "+OID+", "+AGENT_ID+", "+ADAPTER_ID+" FROM "+THINGS_TABLE+" WHERE "+ADAPTER_ID+"='"+adapterId+"'";
    }

    private static String insertRecoveryQuery(String agentId, String adapterId, String data){
        return "INSERT INTO "+RECOVERY_TABLE+" ("+AGENT_ID+", "+ADAPTER_ID+", "+RECOVERY_DATA+") VALUES (" +
                "'"+agentId+"', "+
                "'"+adapterId+"', "+
                "'"+data+"'"+
                ")";
    }

    private static String insertThingQuery(PersistedThing thing){
        return "INSERT INTO "+THINGS_TABLE+" ("+OID+", "+AGENT_ID+", "+ADAPTER_ID+", "+ADAPTER_INFRASTRUCTURE_ID+", "+PASSWORD+") VALUES (" +
                "'"+thing.oid+"', "+
                "'"+thing.agentId+"', "+
                "'"+thing.adapterId+"', "+
                "'"+thing.adapterInfrastructureId+"', "+
                "'"+thing.password+"'"+
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

    private static String getAgentQuery(String agentId){
        return "SELECT * FROM "+AGENTS_TABLE+" WHERE "+AGENT_ID+"='"+agentId+"'";
    }

    public static void clearAdapter(String adapterId) {
        execute(clearAdapterQuery(adapterId));
    }

    public static void clearRecoveryAdapter(String adapterId) {
        execute(clearRecoveryAdapterQuery(adapterId));
    }


    public static void clearAgent(String agentId) {
        execute(clearAgentQuery(agentId));
    }
    public static void saveAgent(String agentId, String password) {
        execute(saveAgentQuery(agentId, password));
    }

    public static void listThings(Connection conn) throws Exception{
        String query = listThingsQuery();
        logger.debug("LISTING THINGS PERSISTENCE: " + query);
        Statement statement = conn.createStatement();
        ResultSet result = statement.executeQuery(query);
        while (result.next()) {
            logger.debug("> RECORD: ");
            logger.debug("  > oid: " + result.getString(OID));
            logger.debug("  > agent: " + result.getString(AGENT_ID));
            logger.debug("  > adapter: " + result.getString(ADAPTER_ID));
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
            logger.debug("  > agent: " + result.getString(AGENT_ID));
            logger.debug("  > adapter: " + result.getString(ADAPTER_ID));
            logger.debug("  > " + result.getString(RECOVERY_DATA));
        }
        result.close();
    }
    public static void listAgents(Connection conn) throws Exception{
        String query = listAgentsQuery();
        logger.debug("LISTING AGENTS PERSISTENCE: " + query);
        Statement statement = conn.createStatement();
        ResultSet result = statement.executeQuery(query);
        while (result.next()) {
            logger.debug("> RECORD: ");
            logger.debug("  > agent: " + result.getString(AGENT_ID));
            logger.debug("  > password: " + result.getString(PASSWORD));
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


    public static Set<String> thingAdapterIds(Connection conn, String agentId) throws Exception{
        Set<String> ids = new HashSet<String>();

        String query = thingsAdaptersQuery(agentId);
        Statement statement = conn.createStatement();
        ResultSet result = statement.executeQuery(query);
        while (result.next()) {
            ids.add(result.getString(ADAPTER_ID));
        }
        result.close();
        return ids;
    }
    public static Set<String> recoveryAdapterIds(Connection conn, String agentId) throws Exception{
        Set<String> ids = new HashSet<String>();

        String query = recoveryAdaptersQuery(agentId);
        Statement statement = conn.createStatement();
        ResultSet result = statement.executeQuery(query);
        while (result.next()) {
            ids.add(result.getString(ADAPTER_ID));
        }
        result.close();
        return ids;
    }

    public static Set<String> getAdapterIds(String agentId) throws Exception {
        Set<String> ids = new HashSet<String>();
        try{
            Connection conn = HSQL.getConnection();
            try{
                ids.addAll(recoveryAdapterIds(conn, agentId));
                ids.addAll(thingAdapterIds(conn, agentId));
                conn.close();
            }
            catch (Exception e){
                logger.error("", e);
            }
        }
        catch(Exception ex){
            logger.error("", ex);
        }
        return ids;
    }

    public static Set<PersistedThing> getAdapterThings(String adapterId) {
        Set<PersistedThing> things = new HashSet<PersistedThing>();
        try{
            Connection conn = HSQL.getConnection();
            try{

                String query = adapterThingsQuery(adapterId);
                Statement statement = conn.createStatement();
                ResultSet result = statement.executeQuery(query);
                while (result.next()) {
                    things.add(new PersistedThing(result.getString(OID), result.getString(AGENT_ID), result.getString(ADAPTER_ID)));
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
        return things;
    }


    public static void list()  {

        try{
            Connection conn = HSQL.getConnection();
            try{
                listThings(conn);
                listRecovery(conn);
                listAgents(conn);
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

    public static boolean execute(String query, boolean silent) {
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
                if(silent){
                    logger.error(e.getMessage());

                }
                else{
                    logger.error("", e);
                }
            }
        }
        catch(Exception ex){
            if(silent){
                logger.error(ex.getMessage());

            }
            else{
                logger.error("", ex);
            }
        }
        return false;
    }

    public static boolean execute(String query) {
        return execute(query, false);
    }

        public static void createTable() {
        execute(createTableQuery());
        // SCHEMA UPDATE
        execute(addThingAgentIdColumnQuery(), true);
        execute(addRecoveryAgentIdColumnQuery(), true);
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
                    thing = new PersistedThing(result.getString(OID), result.getString(AGENT_ID), result.getString(ADAPTER_ID), result.getString(ADAPTER_INFRASTRUCTURE_ID), result.getString(PASSWORD));
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
        String query = insertThingQuery(persisted);
        logger.debug("save query: " + query);
        return execute(query);
    }

    public static boolean saveRecovery(String agentId, String adapterId, String data) {
        logger.debug("SAVING RECOVERY FOR: " + adapterId);
        String query = insertRecoveryQuery(agentId, adapterId, data);
        logger.debug("save recovery query: " + query);
        return execute(query);
    }

    public static PersistedAgent getAgent(String agentId) {
        String query = getAgentQuery(agentId);
        logger.debug("GET AGENT: "+query);

        PersistedAgent agent = null;

        try{
            Connection conn = HSQL.getConnection();
            try{
                Statement statement = conn.createStatement();
                ResultSet result = statement.executeQuery(query);
                if(result.next()){
                    agent = new PersistedAgent(result.getString(AGENT_ID), result.getString(PASSWORD));
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
        return agent;
    }

}
