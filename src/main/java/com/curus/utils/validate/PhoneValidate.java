package com.curus.utils.validate;

import com.curus.dao.account.AccountDao;
import com.curus.httpio.response.ErrorData;
import com.curus.utils.constant.ErrorConst;
import org.apache.commons.lang.StringUtils;


import java.util.Arrays;
import java.util.List;

/**
 * Created by stupid-coder on 23/1/16.
 */
public class PhoneValidate {

    private static final List<String> PHONE_PREFIX = Arrays.asList("130", "131", "132", "133", "134", "135", "136", "137", "138",
            "139", "150", "151", "152", "153", "156", "158", "159", "170", "183", "182", "185", "186", "188", "189");

    private static final Integer PHONE_BITS = 11;

    public static ErrorData validatePhoneNumber(String phone) {
        if ( phone == null ) {
            return new ErrorData(ErrorConst.IDX_INVALIDPARM_ERROR,"phone");
        } else if ( phone.length() != PHONE_BITS || !StringUtils.isNumeric(phone) || !PHONE_PREFIX.contains(phone.substring(0,3)) ) {
            return new ErrorData(ErrorConst.IDX_INVALIDEPHONE_ERROR);
        }
        return null;
    }

    public static ErrorData validateNoduplPhone(String phone, AccountDao accountDao) {
        ErrorData errorData;
        if ( (errorData=validatePhoneNumber(phone)) != null ) return errorData;
        else if ( accountDao.existsByPhone(phone) ) {
            return new ErrorData(ErrorConst.IDX_DUPLPHONE_ERROR);
        } else return null;
    }
}
