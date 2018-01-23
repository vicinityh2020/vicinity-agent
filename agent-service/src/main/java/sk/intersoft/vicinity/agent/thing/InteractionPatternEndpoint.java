package sk.intersoft.vicinity.agent.thing;

import org.json.JSONObject;
import sk.intersoft.vicinity.agent.utils.Dump;
import sk.intersoft.vicinity.agent.utils.JSONUtil;

import java.util.List;

public class InteractionPatternEndpoint {
    public String href = null;
    public String mediatype = null;

    // JSON keys
    public static final String HREF_KEY = "href";
    public static final String MEDIATYPE_KEY = "mediatype";



    public static InteractionPatternEndpoint create(List<JSONObject> linksJSON) throws Exception {
        if(linksJSON != null && linksJSON.size() > 0){
            JSONObject linkJSON = linksJSON.get(0);
            InteractionPatternEndpoint endpoint = new InteractionPatternEndpoint();

            endpoint.href = JSONUtil.getString(HREF_KEY, linkJSON);
            if(endpoint.href == null) throw new Exception("Missing ["+HREF_KEY+"] in: "+linkJSON.toString());

            endpoint.mediatype = JSONUtil.getString(MEDIATYPE_KEY, linkJSON);
            return endpoint;
        }
        else return null;
    }


    public String toString(int indent) {
        Dump dump = new Dump();

        dump.add("endpoint:", indent);
        dump.add("href: "+href, (indent + 1));
        dump.add("media-type: "+mediatype, (indent + 1));

        return dump.toString();
    }
}
