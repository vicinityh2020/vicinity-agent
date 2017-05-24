package sk.intersoft.vicinity.adapter.certh;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import sk.intersoft.vicinity.adapter.AgentAdapter;
import sk.intersoft.vicinity.agent.utils.DateTimeUtil;
import sk.intersoft.vicinity.agent.utils.RESTUtil;

import java.util.logging.Logger;

public class CERTHSmartHomeAdapter implements AgentAdapter {
    private final static Logger LOGGER = Logger.getLogger(CERTHSmartHomeAdapter.class.getName());

    String ENDPOINT = "https://smarthome.iti.gr:8443/sitewhere/api/assignments/{thingID}/measurements/series?tenantAuthToken=0817e7f4-f555-4b5c-9ae5-69b460b7b15d";
    HttpClient client = (new CERTHSmartHomeClient()).client;

    static String THING_ID = "thingID";
    static String PROPERTIES_PARAM = "measurementIds";
    static String START_DATE_PARAM = "startDate";
    static String END_DATE_PARAM = "endDate";

    public JSONArray getPropertiesValue(String thingID, String propertyIDs) throws Exception {
        LOGGER.info("GETTING PROPERTY VALUE FOR ["+thingID+"]["+propertyIDs+"]");
        String endpoint = ENDPOINT;

        endpoint = RESTUtil.replacePathVariable(THING_ID, thingID.trim(), endpoint);
        if(propertyIDs != null && !propertyIDs.trim().equals("")){
            endpoint = RESTUtil.addQueryParameter(PROPERTIES_PARAM, propertyIDs.trim(), endpoint);

        }
        LOGGER.info("replacement: " + endpoint);

        String todayString = DateTimeUtil.dateString(DateTimeUtil.milis());
        String fakeStart = todayString+"T00%3A00%3A00.000%2B0000";
        String fakeEnd = todayString+"T23%3A59%3A59.000%2B0000";

        endpoint = RESTUtil.addQueryParameter(START_DATE_PARAM, fakeStart, endpoint);
        endpoint = RESTUtil.addQueryParameter(END_DATE_PARAM, fakeEnd, endpoint);

        LOGGER.info("with fake interval: " + endpoint);


        HttpResponse response = client.execute(
                new HttpGet(endpoint));
        int statusCode = response.getStatusLine()
                .getStatusCode();

        LOGGER.info("EXECUTED CERTH SMARTHOME CALL ... ");
        LOGGER.info("STATUS CODE: "+statusCode);
        String content = EntityUtils.toString(response.getEntity());
        LOGGER.info("CONTENT: "+content);
        if(statusCode == 200){
            return new JSONArray(content);

        }
        else {
            throw new Exception(content);
        }

    }
}
