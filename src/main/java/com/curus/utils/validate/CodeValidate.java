package com.curus.utils.validate;

import com.curus.httpio.response.ErrorData;
import com.curus.utils.CacheUtils;
import com.curus.utils.constant.CateConst;
import com.curus.utils.constant.ErrorConst;

/**
 * Created by stupid-coder on 24/1/16.
 */
public class CodeValidate {
    private static ErrorData validation(String code, String cacheCode) {
        if ( cacheCode == null ) return new ErrorData(ErrorConst.IDX_INCVERIFYCODE_RROR);
        else if ( code == null ) return new ErrorData(ErrorConst.IDX_INVALIDPARM_ERROR,"code");
        else if ( code.compareTo(cacheCode) != 0 ) return new ErrorData(ErrorConst.IDX_INCVERIFYCODE_RROR);
        else return null;

    }

    public static ErrorData validateRegisterCode(String phone, String code) {
        ErrorData errorData;
        if ( ( errorData = validation(code, CacheUtils.getCode4Cache(CateConst.REGISTER, null, phone))) != null) {}
        else { CacheUtils.deleteCode4Cache(CateConst.REGISTER,null,phone); }
        return errorData;
    }

    public static ErrorData validateForgetCode(String phone, String code) {
        ErrorData errorData;
        if ( ( errorData = validation(code,CacheUtils.getCode4Cache(CateConst.FORGET_PASSWD,null,phone))) != null) {}
        else { CacheUtils.deleteCode4Cache(CateConst.FORGET_PASSWD,null,phone); }
        return errorData;
    }

    public static ErrorData validateModifyPhoneCode(String token,String phone,String code) {
        ErrorData errorData;
        if ( ( errorData = validation(code,CacheUtils.getCode4Cache(CateConst.MODIFY_PHONE,token,phone))) != null) {}
        else { CacheUtils.deleteCode4Cache(CateConst.MODIFY_PHONE,token,phone);}
        return  errorData;
    }

    public static ErrorData validateAddPatientCode(String token, String phone, String code) {
        ErrorData errorData;
        if ( ( errorData = validation(code,CacheUtils.getCode4Cache(CateConst.ADD_PATIENT,token,phone))) != null) {}
        else { CacheUtils.deleteCode4Cache(CateConst.ADD_PATIENT,token,phone);}
        return errorData;
    }

}
