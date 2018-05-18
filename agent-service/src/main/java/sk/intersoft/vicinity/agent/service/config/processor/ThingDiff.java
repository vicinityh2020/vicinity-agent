package sk.intersoft.vicinity.agent.service.config.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.thing.InteractionPattern;
import sk.intersoft.vicinity.agent.thing.InteractionPatternEndpoint;
import sk.intersoft.vicinity.agent.thing.ThingDescription;
import sk.intersoft.vicinity.agent.utils.Dump;

import java.util.Map;

public class ThingDiff {
    final static Logger logger = LoggerFactory.getLogger(ThingDiff.class.getName());

    public static boolean sameEndpoint(InteractionPatternEndpoint endpoint1,
                                       InteractionPatternEndpoint endpoint2,
                                       int indent) {
        if(endpoint1 == null && endpoint2 == null) return true;
        else if (endpoint1 != null && endpoint2 != null) return true;
        else {
            return false;
        }
    }

    public static boolean samePattern(InteractionPattern pattern1,
                                       InteractionPattern pattern2,
                                       int indent) {
        if(!pattern1.refersTo.equalsIgnoreCase(pattern2.refersTo)){
            logger.debug(Dump.indent("patterns refers-to is different!", indent));
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

            InteractionPattern pattern2 = patterns2.get(id);
            if(pattern2 == null) {
                logger.debug(Dump.indent("pattern ["+pattern1.id+"] is not mutual!", (indent + 2)));
                return false;
            }

            boolean patternsSame = samePattern(pattern1, pattern2, (indent + 2));
            if(!patternsSame) {
                logger.debug(Dump.indent("patterns ["+id+"] are not same!", (indent + 2)));
                return false;
            }

        }

        return true;
    }

    public static boolean isSame(ThingDescription thing, ThingDescription other) {
        logger.debug(Dump.indent("DOING DIFF", 0));
        logger.debug(Dump.indent("Thing 1: "+thing.toSimpleString(), 0));
        logger.debug(Dump.indent("Thing 2: "+other.toSimpleString(), 0));
        if(!thing.type.equalsIgnoreCase(other.type)){
            logger.debug(Dump.indent("Thing [type] diff: ["+thing.type+"] -> ["+other.type+"]", 1));
            return false;
        }

        boolean propertiesAreSame = samePatterns(thing.properties, other.properties, 2);
        if(!propertiesAreSame) {
            logger.debug(Dump.indent("Thing properties are different", 2));
            return false;
        }

        boolean actionsAreSame = samePatterns(thing.actions, other.actions, 2);
        if(!actionsAreSame) {
            logger.debug(Dump.indent("Thing actions are different", 2));
            return false;
        }

        boolean eventsAreSame = samePatterns(thing.events, other.events, 2);
        if(!eventsAreSame) {
            logger.debug(Dump.indent("Thing events are different", 2));
            return false;
        }

        return true;
    }
}
