package com.curus.services.account;

import com.curus.dao.CurusDriver;
import com.curus.httpio.request.account.AccountDetailRequest;
import com.curus.httpio.response.ErrorData;
import com.curus.httpio.response.ResponseBase;
import com.curus.httpio.response.account.AccountDetailResponseData;
import com.curus.model.Account;
import com.curus.utils.CacheUtils;
import com.curus.utils.LogUtils;
import com.curus.utils.constant.ErrorConst;
import com.curus.utils.constant.StatusConst;
import com.curus.utils.validate.ValueValidate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by stupid-coder on 24/1/16.
 */
public class AccountDetailService {

    static private Log logger = LogFactory.getLog(AccountDetailService.class);
    private AccountDetailRequest request;
    private AccountDetailResponseData responseData;
    private ErrorData errorData;
    public AccountDetailService(AccountDetailRequest request,CurusDriver driver) {
        this.request = request;
        responseData = null;
        errorData = null;
    }

    private ErrorData validate() {
        if ( (errorData = ValueValidate.valueExistValidate(request.getToken(), "token")) != null) {
            logger.warn(LogUtils.Msg(errorData, request));
        }
        return errorData;
    }

    private ErrorData detail() {
        Account account;

        if ( ( account = (Account) CacheUtils.getObject4Cache(request.getToken())) == null) {
            errorData = new ErrorData(ErrorConst.IDX_TOKENEXPIRED_ERROR);
            logger.warn(LogUtils.Msg(errorData,request));
        } else {
            account.setPasswd(null);
            responseData = new AccountDetailResponseData(account);
        }
        return errorData;
    }

    public ResponseBase process() {
        if (validate() == null && detail() == null) {
            logger.info(LogUtils.Msg("Success to Detail Account Info",request,responseData));
            return new ResponseBase(StatusConst.OK,responseData);
        } else return new ResponseBase(StatusConst.ERROR,errorData);

    }

}
