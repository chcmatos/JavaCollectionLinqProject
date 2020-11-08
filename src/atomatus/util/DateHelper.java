package atomatus.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DateHelper {

    private static final String FUSO_REGEX;
    private static final DateHelper instance;

    static {
        FUSO_REGEX = "([-+]\\d{1,2}:\\d{2}$)";
        instance = new DateHelper();
    }

    public static DateHelper getInstance() {
        return instance;
    }

    private DateHelper() { }

    //region Current Date TimeZone
    public String getDate(TimeZone timeZone){
        return getDate(new Date(), timeZone);
    }

    public String getSmallDate(TimeZone timeZone){
        return getSmallDate(new Date(), timeZone);
    }

    public String getTime(TimeZone timeZone){
        return getTime(new Date(), timeZone);
    }

    public String getSmallTime(TimeZone timeZone){
        return getSmallTime(new Date(), timeZone);
    }
    //endregion

    //region Current Date Default TimeZone
    public String getDate(){
        return getDate(TimeZone.getDefault());
    }

    public String getSmallDate(){
        return getSmallDate(TimeZone.getDefault());
    }

    public String getTime(){
        return getTime(TimeZone.getDefault());
    }

    public String getSmallTime(){
        return getSmallTime(TimeZone.getDefault());
    }
    //endregion

    //region Any Date
    public String getFormattedDate(Date date, String pattern, TimeZone timeZone) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, LocaleHelper.getDefaultLocale());
        sdf.setTimeZone(timeZone == null ? sdf.getTimeZone() : timeZone);
        return sdf.format(date);
    }

    public String getDate(Date date, TimeZone timeZone){
        return getFormattedDate(date, "dd/MM/yyyy HH:mm:ss", timeZone);
    }

    public String getSmallDate(Date date, TimeZone timeZone){
        return getFormattedDate(date, "dd/MM/yy", timeZone);
    }

    public String getTime(Date date, TimeZone timeZone){
        return getFormattedDate(date, "HH:mm:ss", timeZone);
    }

    public String getSmallTime(Date date, TimeZone timeZone){
        return getFormattedDate(date, "HH:mm", timeZone);
    }
    //endregion

    //region Any Date Default TimeZone
    public String getFormattedDate(Date date, String pattern) {
        return getFormattedDate(date, pattern, TimeZone.getDefault());
    }

    public String getDate(Date date){
        return getDate(date, TimeZone.getDefault());
    }

    public String getSmallDate(Date date){
        return getSmallDate(date, TimeZone.getDefault());
    }

    public String getTime(Date date){
        return getTime(date, TimeZone.getDefault());
    }

    public String getSmallTime(Date date){
        return getSmallTime(date, TimeZone.getDefault());
    }
    //endregion

    //region Any Date
    private Date parseDate(String date, String pattern, TimeZone timeZone) {
        try{
            SimpleDateFormat sdf = new SimpleDateFormat(pattern, LocaleHelper.getDefaultLocale());
            sdf.setTimeZone(timeZone == null ? sdf.getTimeZone() : timeZone);
            return sdf.parse(date);
        }catch (ParseException ex){
            return null;
        }
    }

    private Date parseDate(String date, TimeZone timeZone, int index, String... patterns){
        Date r = parseDate(date, patterns[index++], timeZone);
        return index == patterns.length || r != null ? r : parseDate(date, timeZone, index, patterns);
    }

    private Date parseDate(String date, TimeZone timeZone, String... patterns){
        return parseDate(date, timeZone, 0 , patterns);
    }

    public Date parseDate(String date) {
        Matcher matcher = Pattern.compile(FUSO_REGEX).matcher(Objects.requireNonNull(date));
        TimeZone timeZone = TimeZone.getDefault();

        if(matcher.find()){
            String fuso = matcher.group();
            fuso = fuso.substring(0, fuso.indexOf(':')).replace("0", "");
            TimeZone utcTimeZone = new SimpleTimeZone(Integer.parseInt(fuso) * (60 * 60 * 1000), "UTC" + fuso);
            String cT = date.contains("T") ? "'T'" : " ";
            return parseDate(date, utcTimeZone, "yyyy-MM-dd"+cT+"HH:mm:ss Z",
                    "yyyy-MM-dd"+cT+"HH:mm:ss.SSS Z",
                    "yyyy-MM-dd"+cT+"HH:mm:ss",
                    "yyyy-MM-dd"+cT+"HH:mm:ss.SSS");
        } else if(date.contains("T")) {
            timeZone = date.endsWith("Z") ? TimeZone.getTimeZone("UTC") : TimeZone.getTimeZone("PST");
            return parseDate(date, timeZone,  "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                    "yyyy-MM-dd'T'HH:mm:ss.SSS",
                    "yyyy-MM-dd'T'HH:mm:ss");
        } else {
            return parseDate(date, timeZone, "dd/MM/yyyy HH:mm:ss",
                    "dd/MM/yyyy HH:mm:ss.SSS",
                    "dd/MM/yyyy",
                    "yyyy-MM-dd HH:mm:ss",
                    "yyyy-MM-dd HH:mm:ss.SSS",
                    "yyyy-MM-dd",
                    "HH:mm",
                    "HH:mm:ss",
                    "HH:mm:ss.SSS");
        }
    }

    private String getFormattedDate(String date, String pattern) {
        return getFormattedDate(parseDate(date), pattern);
    }

    public String getDate(String date){
        return getFormattedDate(date, "dd/MM/yyyy HH:mm:ss");
    }

    public String getSmallDate(String date){
        return getFormattedDate(date, "dd/MM/yy");
    }

    public String getTime(String date){
        return getFormattedDate(date, "HH:mm:ss");
    }

    public String getSmallTime(String date){
        return getFormattedDate(date, "HH:mm");
    }
    //endregion

}
