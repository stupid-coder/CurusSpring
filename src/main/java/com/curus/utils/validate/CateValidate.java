package com.curus.utils.validate;

import com.curus.httpio.response.ErrorData;
import com.curus.utils.constant.CateConst;
import com.curus.utils.constant.ErrorConst;

/**
 * Created by stupid-coder on 23/1/16.
 */
public class CateValidate {
    public static int getIdx(String cate) {
        if ( cate == null) return -1;
        else if ( cate.compareTo(CateConst.REGISTER) == 0 ) return CateConst.IREGISTER;
        else if ( cate.compareTo(CateConst.MODIFY_PHONE) == 0 ) return CateConst.IMODIFY_PHONE;
        else if ( cate.compareTo(CateConst.FORGET_PASSWD) == 0 ) return CateConst.IFORGET_PASSWD;
        else if ( cate.compareTo(CateConst.ADD_PATIENT) == 0 ) return CateConst.IADD_PATIENT;
        else if ( cate.compareTo(CateConst.APV) == 0 ) return CateConst.IAPV;
        return -1;
    }

    public static ErrorData validateCateAndToken(String cate, String token) {
        int cateIdx = getIdx(cate);
        if (cateIdx == -1) return new ErrorData(ErrorConst.IDX_INVALIDPARM_ERROR, "cate");
        else if ((cateIdx == CateConst.IMODIFY_PHONE || cateIdx == CateConst.IADD_PATIENT) && token == null)
            return new ErrorData(ErrorConst.IDX_INVALIDPARM_ERROR, "token");
        return null;
    }
}
