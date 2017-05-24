package sk.intersoft.vicinity.agent.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtil {
    public static String dateString(long timestamp) {
        Date date = new Date(timestamp);
        return (new SimpleDateFormat("yyyy-MM-dd")).format(date);
    }

    public static long milis(){
        return System.currentTimeMillis();
    }

}
