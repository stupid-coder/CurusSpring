package com.curus.utils;

import com.curus.httpio.response.ErrorData;

/**
 * Created by stupid-coder on 23/1/16.
 */
public class LogUtils {

    public static String Msg(ErrorData errorData, Object object) {
        return String.format("%s - %s",errorData.getError_msg(),object);
    }

    public static String Msg(ErrorData errorData, Object object1, Object object2) {
        return String.format("%s - %s\t%s",errorData.getError_msg(),object1,object2);
    }

    public static String Msg(String msg, Object object) {
        return String.format("%s - %s",msg,object);
    }

    public static String Msg(String msg, Object object1, Object object2) {
        return String.format("%s - %s\t%s",msg,object1,object2);
    }
}
