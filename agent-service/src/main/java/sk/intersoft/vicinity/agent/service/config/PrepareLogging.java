package sk.intersoft.vicinity.agent.service.config;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PrepareLogging {
    public static final String LOGBACK_PROPERTY = "logback.configurationFile";
    public static final String LOGGING_PROPERTY = "java.util.logging.config.file";
    public static final String LOGS = "logs.folder";

    private static File file(String path) {
        return new File(new File("").getAbsolutePath() + "/" + path);
    }

    private static String file2string(File file) throws Exception {
        return new Scanner(file).useDelimiter("\\Z").next();
    }

    private static File destinationFile(File file) {
//        System.out.println("making destination file for: "+file.getAbsolutePath());

        String name = file.getName();
        String parent = file.getParentFile().getAbsolutePath() + "/";

        String newName = "unresolved";
        String[] parts = name.split("\\.");
        if(parts.length > 1) {
            String suffix = "."+parts[parts.length - 1];
//            System.out.println("has suffix: "+suffix);
            List<String> update = new ArrayList<String>();
            update.add("resolved");
            for(int i = 0; i < parts.length - 1; i++){
//                System.out.println("adding to name: "+parts[i]);
                update.add(parts[i]);
            }
//            System.out.println("update: "+update);
            newName = StringUtils.join(update, ".")+ suffix;
        }
        else {
            newName = "resolved."+name;
        }

        File destination = new File(parent + newName);

//        System.out.println("name: "+name);
//        System.out.println("parent: "+parent);
//        System.out.println("new name: "+newName);
//        System.out.println("new file: "+destination.getAbsolutePath());


        return destination;
    }

    private static String replace(String content, Properties replacement) {
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

    private static void write(String content, File destination) throws Exception {
        BufferedWriter writer = new BufferedWriter(new FileWriter(destination));
        writer.write(content);

        writer.close();
    }

    private static void handle(String property, Properties replacement) {

        try{
            String path = System.getProperty(property);


//            System.out.println("UPDATING: ");
//            System.out.println("property: "+property);
//            System.out.println("path: "+path);
//            System.out.println("replacement: "+replacement);



            File file = file(path);
            String content = file2string(file);
//            System.out.println("content: "+content);

            String replaced = replace(content, replacement);
//            System.out.println("resolved content: \n"+replaced);


            File destination = destinationFile(file);
            write(replaced, destination);

            System.setProperty(property, destination.getAbsolutePath());
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

    public static void go() {
        try{
//            System.out.println("PREPARING LOGGING ...");

            String logs = file(System.getProperty("logs.folder")).getAbsolutePath();
            System.setProperty(LOGS, logs);

            Properties replacement = new Properties();
            replacement.setProperty("LOGS_FOLDER", logs);

            handle(LOGBACK_PROPERTY, replacement);
            handle(LOGGING_PROPERTY, replacement);

//            System.out.println("LOGBACK: "+System.getProperty(LOGBACK_PROPERTY));
//            System.out.println("LOGGING: "+System.getProperty(LOGGING_PROPERTY));
//            System.out.println("LOGS: "+System.getProperty(LOGS));
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws Exception {
        PrepareLogging.go();
    }

}
