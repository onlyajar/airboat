package onlyajar.airboat.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    static SimpleDateFormat dfDate_mmddyyyy = new SimpleDateFormat("MM/dd/yyyy");
    static SimpleDateFormat DATE_FORMAT_YYYYMMDD = new SimpleDateFormat("yyyyMMdd");
    static SimpleDateFormat dfDateTime_mmddyyyy = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

    public static String date(){
        return dfDateTime_mmddyyyy.format(new Date());
    }

    public static String getBeforeDate(int days){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -days);
        return simpleDateFormat.format(calendar.getTime());
    }
}
