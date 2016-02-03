package com.curus.utils.validate;

import com.curus.httpio.response.ErrorData;
import com.curus.utils.QuotaUtils;
import com.curus.utils.constant.ErrorConst;
import com.curus.utils.constant.QuotaConst;

/**
 * Created by stupid-coder on 3/2/16.
 */
public class QuotaValidate {
    public static ErrorData quotaCateValidate(String cate, String key) {
        if (QuotaUtils.getQuotaIds(cate) == QuotaConst.QUOTA_UNKNOW_ID )
            return new ErrorData(ErrorConst.IDX_INVALIDPARM_ERROR,key);
        else return null;
    }
}
