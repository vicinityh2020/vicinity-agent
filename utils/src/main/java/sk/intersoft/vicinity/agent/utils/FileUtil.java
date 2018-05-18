package sk.intersoft.vicinity.agent.utils;

import java.io.File;
import java.util.Scanner;

public class FileUtil {
    public static String file2string(File file) throws Exception {
        return new Scanner(file).useDelimiter("\\Z").next();
    }
}
