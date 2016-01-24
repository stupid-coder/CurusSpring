package com.curus.utils;

import java.sql.Timestamp;

/**
 * Created by stupid-coder on 24/1/16.
 */
public class TimeUtils {
    public static Timestamp getTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }
}
