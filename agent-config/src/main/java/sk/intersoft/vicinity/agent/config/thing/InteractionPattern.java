package sk.intersoft.vicinity.agent.config.thing;

import org.json.JSONObject;
import sk.intersoft.vicinity.agent.utils.JSONUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InteractionPattern {
    public static final String PROPERTY = "property";
    public static final String ACTION = "action";


    public static final String PID = "pid";
    public static final String AID = "aid";

    public String id;
    public String readEndpoint;
    public String writeEndpoint;



    public InteractionPattern(String id, String readEndpoint, String writeEndpoint) {
        this.id = id;
        this.readEndpoint = readEndpoint;
        this.writeEndpoint = writeEndpoint;
    }

    public static String getHref(List<JSONObject> links) throws Exception {

        String link = JSONUtil.getString("href", links.get(0));
        if(link == null) throw new Exception("missing href thing description link: "+link.toString());

        return link;
    }

    public static InteractionPattern create(JSONObject object, String idKey) throws Exception {
        String id = JSONUtil.getString(idKey, object);


        List<JSONObject> links = JSONUtil.getObjectArray("links", object);
        List<JSONObject> reads = JSONUtil.getObjectArray("read_links", object);
        List<JSONObject> writes = JSONUtil.getObjectArray("write_links", object);

        if(id == null) throw new Exception("Missing  "+idKey+" in: "+object.toString());

        if(reads != null && writes != null){
            String readHref = getHref(reads);
            String writeHref = getHref(writes);
            return new InteractionPattern(id, readHref, writeHref);
        }
        else if(links != null){
            String href = getHref(links);
            return new InteractionPattern(id, href, href);
        }
        else {
            throw new Exception("Missing or wrong configuration of links/read_links/write_links in: "+object.toString());
        }



    }
}
