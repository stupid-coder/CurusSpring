package com.curus.utils;

import java.sql.Date;
import java.sql.Timestamp;
//import java.util.Date;
//import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.*;
/**
 * Created by stupid-coder on 24/1/16.
 */
public class TimeUtils {
    public static final Long unit = 1000L;
    public static final int offset = TimeZone.getDefault().getRawOffset();
    public static Long t2m(Long time) { return time*unit; }
    public static Long m2t(Long millis) { return millis/unit; }
    public static Timestamp getTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }
    public static Timestamp getTimestamp(Long unix_time) { return new Timestamp(System.currentTimeMillis()+t2m(unix_time)); }
    public static Timestamp parseTimestamp(String ts) {
        return new Timestamp(t2m(Long.parseLong(ts)));
    }
    public static Date getDate() { return new Date(System.currentTimeMillis()); }
    public static Date getDate(Long days) { return new Date(System.currentTimeMillis()+days*t2m(3600*24L)); }
    public static Date parseDate(String unix_time) { return new Date(t2m(Long.parseLong(unix_time))); }
    public static Date parseDate(Long unix_time) { return new Date(t2m(unix_time)); }
    public static String date2String(Date date) { return m2t(date.getTime()).toString(); }
    public static Long date2Long(Date date) { return m2t(date.getTime()); }
    public static Long timestampDiff(Timestamp bts, Timestamp ets) {
        return (ets.getTime()+offset)/t2m(24*3600L)-(bts.getTime()+offset)/t2m(24*3600L) + 1;
    }
    public static Date getDate(Date now, Integer days)
    {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(now);
        calendar.add(calendar.DATE,days);
        return new Date(calendar.getTime().getTime());
    }
    public static Long dateDiff(Date bdate, Date edate) {
        return (edate.getTime()+offset)/t2m(24*3600L) - (bdate.getTime()+offset)/t2m(24*3600L) + 1;
    }
    public static Long dateDiffToNow(Date bdate) {
        return (System.currentTimeMillis()+offset)/t2m(24*3600L) - (bdate.getTime()+offset)/t2m(24*3600L) + 1;
    }
    public static String DateFormat(Date date) {
        return date.toString();
        //return DateFormat.getDateInstance().format(date);
    }
    public static Double timestampDiffHour(Timestamp bts) {
        return (getTimestamp().getTime()-bts.getTime())/3600.0/1000.0;
    }
}
