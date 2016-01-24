package com.curus.utils.validate;

import com.curus.httpio.response.ErrorData;
import com.curus.utils.constant.ErrorConst;

import java.util.regex.Pattern;

/**
 * Created by stupid-coder on 24/1/16.
 */
public class PasswdValidate {

    private static Pattern passwordPattern = Pattern.compile("^\\w{6,30}$");

    public static ErrorData validatePassword(String password) {
        if ( password == null ) return new ErrorData(ErrorConst.IDX_INVALIDPARM_ERROR,"passwd");
        else if (passwordPattern.matcher(password).matches()) return null;
        else return new ErrorData(ErrorConst.IDX_INVALIDPARM_ERROR,"passwd");
    }

    public static ErrorData validatePasswordCorrect(String passwdi, String passwdm) {
        ErrorData errorData = null;
        if ( passwdi.compareTo(passwdm) !=0 ) errorData = new ErrorData(ErrorConst.IDX_INCPASSWD_ERROR);
        return errorData;
    }

}
