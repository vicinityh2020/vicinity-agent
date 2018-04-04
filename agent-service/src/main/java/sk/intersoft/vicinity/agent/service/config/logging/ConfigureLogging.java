package sk.intersoft.vicinity.agent.service.config.logging;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;
import java.util.logging.LogManager;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigureLogging {

    public static String toString(InputStream stream){
        if(stream != null){
            java.util.Scanner s = new java.util.Scanner(stream).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        }
        else return null;
    }

    private String replace(String content, Properties replacement) {
        Pattern re = Pattern.compile("\\$\\{(.+?)\\}");
        Matcher m = re.matcher(content);
        StringBuffer result = new StringBuffer();
        while (m.find()) {
            String variable = m.group(1);
            String resolved = replace(replacement.getProperty(variable), replacement);
            m.appendReplacement(result, resolved);
        }
        m.appendTail(result);
        return result.toString();
    }

    public void configure(String logFolder) {
        try{
            ClassLoader classLoader = getClass().getClassLoader();

            String logProps = toString(classLoader.getResourceAsStream("config/logging.properties"));
            String logback = toString(classLoader.getResourceAsStream("config/logback.xml"));

            Properties replacement = new Properties();
            replacement.setProperty("LOGS_FOLDER", logFolder);

            String logPropsUpdate = replace(logProps, replacement);
            String logbackUpdate = replace(logback, replacement);
            try{
                LogManager.getLogManager().readConfiguration(new ByteArrayInputStream(logPropsUpdate.getBytes()));
            }
            catch(Exception e){
                e.printStackTrace();
            }

            try{
                LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
                JoranConfigurator configurator = new JoranConfigurator();
                configurator.setContext(context);
                // Call context.reset() to clear any previous configuration, e.g. default
                // configuration. For multi-step configuration, omit calling context.reset().
                context.reset();
                configurator.doConfigure(new ByteArrayInputStream(logbackUpdate.getBytes()));
            }
            catch(Exception e){
                e.printStackTrace();
            }

        }
        catch(Exception e){

        }

    }

}
