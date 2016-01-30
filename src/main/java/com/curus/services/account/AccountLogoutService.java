package com.curus.services.account;

import com.curus.dao.CurusDriver;
import com.curus.httpio.request.account.AccountLogoutRequest;
import com.curus.httpio.response.ErrorData;
import com.curus.httpio.response.ResponseBase;
import com.curus.utils.CacheUtils;
import com.curus.utils.LogUtils;
import com.curus.utils.constant.ErrorConst;
import com.curus.utils.constant.StatusConst;
import com.curus.utils.validate.PhoneValidate;
import com.curus.utils.validate.ValueValidate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by stupid-coder on 24/1/16.
 */
public class AccountLogoutService {

    static private Log logger = LogFactory.getLog(AccountLogoutService.class);
    private AccountLogoutRequest request;
    private ErrorData errorData;
    public AccountLogoutService(AccountLogoutRequest request,CurusDriver driver) {
        this.request = request;
        errorData = null;
    }

    private ErrorData validate() {
        if ( (errorData = ValueValidate.valueExistValidate(request.getToken(),"token")) != null) {
            logger.warn(LogUtils.Msg(errorData, request));
        }
        return errorData;
    }

    private ErrorData logout() {
        if ( CacheUtils.deleteObjectAndGet4Cache(request.getToken()) == null) {
            errorData = new ErrorData(ErrorConst.IDX_TOKENEXPIRED_ERROR);
            logger.warn(LogUtils.Msg(errorData,request));
        }
        return errorData;
    }

    public ResponseBase process() {
        if (validate() == null && logout() == null) {
            logger.info(LogUtils.Msg("Success to Logout",request,null));
            return new ResponseBase(StatusConst.OK,null);
        } else return new ResponseBase(StatusConst.ERROR,errorData);

    }

}
