package sk.intersoft.vicinity.agent.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtil {
    public static String dateString(long timestamp) {
        Date date = new Date(timestamp);
        return (new SimpleDateFormat("yyyy-MM-dd")).format(date);
    }

    public static long millis(){
        return System.currentTimeMillis();
    }


    public static long duration(long ms) {
        return (millis() - ms);
    }

    public static String hours(long ms) {
        return ((ms / (1000 * 60 * 60)) % 24)+"";
    }

    public static String minutes(long ms) {
        return ((ms / (1000 * 60)) % 60)+"";
    }

    public static String seconds(long ms) {
        return ((ms / 1000) % 60)+"";
    }

    public static String format(long time) {
        return time + "ms :: "+hours(time)+":"+minutes(time)+":"+seconds(time);
    }

}
