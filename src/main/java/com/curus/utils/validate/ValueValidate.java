package com.curus.utils.validate;

import com.curus.httpio.response.ErrorData;
import com.curus.utils.constant.ErrorConst;
import java.sql.Date;

/**
 * Created by stupid-coder on 24/1/16.
 */
public class ValueValidate {
    public static ErrorData valueExistValidate(Object value, String key) {
        if ( value == null ) return new ErrorData(ErrorConst.IDX_INVALIDPARM_ERROR,key);
        else return null;
    }

    public static ErrorData valueIntegerValidate(String value, String key) {
        try {
            Integer.parseInt(value);
            return null;
        } catch ( NumberFormatException e ) {
            return new ErrorData(ErrorConst.IDX_INVALIDPARM_ERROR,key);
        }
    }

    public static ErrorData valueLongValidate(String value, String key) {
        try {
            Long.parseLong(value);
            return null;
        } catch ( NumberFormatException e ) {
            return new ErrorData(ErrorConst.IDX_INVALIDPARM_ERROR,key);
        }
    }

    static final byte[] CHECK = {'1','0','X','9','8','7','6','5','4','3','2'};

    private static boolean idCheckSum(String id) {
        byte[] bytes = id.getBytes();
        int sum = 0;
        int base = 2;
        for ( int i = bytes.length - 2 ; i >= 0 ; -- i) {
            sum += (bytes[i]-48) * base;
            base = base * 2 % 11;
        }
        return bytes[bytes.length-1] == CHECK[sum % 11];
    }

    public static ErrorData idValidation(String value, String key) {
        if ( value != null && value.length() == 18 && idCheckSum(value) ) {
            return null;
        }
        return null; //new ErrorData(ErrorConst.IDX_INVALIDPARM_ERROR,key);
    }

    public static Integer genderize(Integer gender) {
        if ( gender > 2 && gender < 1 ) return 0;
        else return gender;
    }

    public static Date dateFromTimestamp(String ts_seconds) {
        return new Date(Long.parseLong(ts_seconds)*1000);
    }
}
