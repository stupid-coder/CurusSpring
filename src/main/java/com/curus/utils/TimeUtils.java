package com.curus.utils;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Created by stupid-coder on 24/1/16.
 */
public class TimeUtils {
    public static Timestamp getTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }
    public static Timestamp getTimestamp(Long unix_time) { return new Timestamp(System.currentTimeMillis()+unix_time*1000); }
    public static Timestamp parseTimestamp(String ts) {
        return new Timestamp(Long.parseLong(ts)*1000);
    }
    public static Date getDate() { return new Date(System.currentTimeMillis()); }
    public static Date getDate(Long days) { return new Date(System.currentTimeMillis()+days*1000*3600*24); }
    public static Date parseDate(String unix_time) { return new Date(Long.parseLong(unix_time)*1000); }
    public static String date2String(Date date) { return new Long(date.getTime()/1000).toString(); }
}
