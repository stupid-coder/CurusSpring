package com.curus.utils;

import java.sql.Timestamp;

/**
 * Created by stupid-coder on 24/1/16.
 */
public class TimeUtils {
    public static Timestamp getTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }
    public static Timestamp getTimestamp(Long i) { return new Timestamp(System.currentTimeMillis()+i*1000); }
    public static Timestamp parseTimestamp(String ts) {
        return new Timestamp(Long.parseLong(ts)*1000);
    }
}
