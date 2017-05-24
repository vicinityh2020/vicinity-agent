package sk.intersoft.vicinity.agent.utils;

public class RESTUtil {
    public static  String replacePathVariable(String variable, String value, String endpoint) throws Exception {
        if(value == null || value.trim().equals("")) {
            throw new Exception("Missing value for path variable ["+variable+"]!");
        }

        return endpoint.replace("{"+variable.trim()+"}", value.trim());

    }


    public static  String addQueryParameter(String variable, String value, String endpoint) throws Exception {
        return endpoint + "&"+variable.trim()+"="+value.trim();

    }
}
