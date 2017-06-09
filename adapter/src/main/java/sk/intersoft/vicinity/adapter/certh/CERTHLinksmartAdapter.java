package sk.intersoft.vicinity.adapter.certh;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import sk.intersoft.vicinity.adapter.AgentAdapter;
import sk.intersoft.vicinity.agent.utils.RESTUtil;

import java.util.logging.Logger;

public class CERTHLinksmartAdapter implements AgentAdapter {
    private final static Logger LOGGER = Logger.getLogger(CERTHLinksmartAdapter.class.getName());

    HttpClient client = HttpClientBuilder.create().build();
    String ENDPOINT = "http://160.40.51.227:8091/Get_Obj_Prop_Adapter-0.0.1-SNAPSHOT/objects/{oid}/properties/{pid}";
    String ACTIONS_ENDPOINT = "http://160.40.51.227:8091/Get_Obj_Prop_Adapter-0.0.1-SNAPSHOT/objects/{oid}/actions/{aid}";

    static String OBJECT_ID = "oid";
    static String PROPERTY_ID = "pid";
    static String ACTION_ID = "aid";

    public JSONArray getPropertiesValue(String objectID, String propertyID) throws Exception {
        LOGGER.info("GETTING PROPERTY VALUE FOR ["+objectID+"]["+propertyID+"]");
        String endpoint = ENDPOINT;

        endpoint = RESTUtil.replacePathVariable(OBJECT_ID, objectID.trim(), endpoint);
        endpoint = RESTUtil.replacePathVariable(PROPERTY_ID, propertyID.trim(), endpoint);
        LOGGER.info("replacement: " + endpoint);


        HttpGet request = new HttpGet(endpoint);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        String content = EntityUtils.toString(response.getEntity());

        LOGGER.info("EXECUTED CERTH LinkSmart CALL ... ");
        LOGGER.info("STATUS CODE: "+statusCode);
        LOGGER.info("CONTENT: "+content);
        if(statusCode == 200){
            return new JSONArray("["+content+"]");

        }
        else {
            throw new Exception(content);
        }

    }


}
