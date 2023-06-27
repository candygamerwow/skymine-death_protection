package org.cnadygamer.deathprotectionplugin;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
public class TimeUtils {
    public static String getIsraelTime(long millis) {
        TimeZone tz = TimeZone.getTimeZone("Asia/Jerusalem");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(tz);
        return df.format(new Date(millis));
    }
}
