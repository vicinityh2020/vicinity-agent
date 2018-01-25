package sk.intersoft.vicinity.agent.thing;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.utils.Dump;
import sk.intersoft.vicinity.agent.utils.JSONUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThingDescriptionDiff {
    final static Logger logger = LoggerFactory.getLogger(ThingDescriptionDiff.class.getName());

    public static boolean sameEndpoint(InteractionPatternEndpoint endpoint1,
                                       InteractionPatternEndpoint endpoint2,
                                       int indent) {
        if(endpoint1 == null && endpoint2 == null) return true;
        else if (endpoint1 != null && endpoint2 != null) return true;
        else {
            return false;
        }
    }

    public static boolean sameParameter(InteractionPatternParameter parameter1,
                                        InteractionPatternParameter parameter2,
                                        int indent) {
        if(parameter1 == null && parameter2 == null) return true;
        else if (parameter1 != null && parameter2 != null){
            if(!parameter1.units.equalsIgnoreCase(parameter2.units)){
                logger.debug(Dump.indent("parameter units are different!", indent));
                return false;
            }
            else return true;
        }
        else {
            logger.debug(Dump.indent("parameters are different!", indent));
            return false;
        }
    }

    public static boolean samePatterns(InteractionPattern pattern1,
                                       InteractionPattern pattern2,
                                       int indent) {
        if(!pattern1.refersTo.equalsIgnoreCase(pattern2.refersTo)){
            logger.debug(Dump.indent("patterns refers-to is different!", indent));
            return false;
        }

        boolean outputsSame = sameParameter(pattern1.output, pattern2.output, indent);
        if(!outputsSame){
            logger.debug(Dump.indent("patterns have different outputs!", indent));
            return false;
        }


        boolean readEndpointSame = sameEndpoint(pattern1.readEndpoint, pattern2.readEndpoint, indent);
        if(!readEndpointSame){
            logger.debug(Dump.indent("patterns have different read endpoints!", indent));
            return false;
        }
        boolean writeEndpointSame = sameEndpoint(pattern1.writeEndpoint, pattern2.writeEndpoint, indent);
        if(!writeEndpointSame){
            logger.debug(Dump.indent("patterns have different write endpoints!", indent));
            return false;
        }

        return true;
    }


    public static boolean samePatterns(Map<String, InteractionPattern> patterns1,
                                       Map<String, InteractionPattern> patterns2,
                                       int indent) {
        if(patterns1.keySet().size() != patterns2.keySet().size()){
            logger.debug(Dump.indent("different number of patterns ["+patterns1.keySet().size()+"] -> ["+patterns2.keySet().size()+"]!", indent));
            return false;
        }

        for (Map.Entry<String, InteractionPattern> entry : patterns1.entrySet()) {
            String id = entry.getKey();
            InteractionPattern pattern1 = entry.getValue();

            logger.debug(Dump.indent("checking patterns for id: ["+id+"]", indent));

            InteractionPattern pattern2 = patterns2.get(id);
            if(pattern2 == null) {
                logger.debug(Dump.indent("pattern ["+pattern1.id+"] is not mutual!", (indent + 2)));
                return false;
            }

            boolean patternsSame = samePatterns(pattern1, pattern2, (indent + 2));
            if(!patternsSame) {
                logger.debug(Dump.indent("patterns ["+id+"] are not same!", (indent + 2)));
                return false;
            }

        }

        return true;
    }

}
