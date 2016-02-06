package com.curus.services.account;

import com.curus.dao.CurusDriver;
import com.curus.httpio.request.account.AccountIDetailRequest;
import com.curus.httpio.response.ErrorData;
import com.curus.httpio.response.ResponseBase;
import com.curus.httpio.response.account.AccountIDetailResponseData;
import com.curus.model.database.Account;
import com.curus.utils.LogUtils;
import com.curus.utils.constant.ErrorConst;
import com.curus.utils.constant.StatusConst;
import com.curus.utils.validate.ValueValidate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by stupid-coder on 24/1/16.
 */
public class AccountIDetailService {

    static private Log logger = LogFactory.getLog(AccountIDetailService.class);
    private AccountIDetailRequest request;
    private AccountIDetailResponseData responseData;
    private CurusDriver driver;
    private ErrorData errorData;
    public AccountIDetailService(AccountIDetailRequest request, CurusDriver driver) {
        this.request = request;
        responseData = null;
        errorData = null;
        this.driver = driver;
    }

    private ErrorData validate() {
        if ( (errorData = ValueValidate.valueExistValidate(request.getUid(), "uid")) != null) {
            logger.warn(LogUtils.Msg(errorData, request));
        } else if ( (errorData = ValueValidate.valueLongValidate(request.getUid(), "uid")) != null) {
            logger.warn(LogUtils.Msg(errorData, request));
        }
        return errorData;
    }

    private ErrorData login() {
        Account account;
        if ( ( account = driver.accountDao.selectById(Long.parseLong(request.getUid()))) == null) {
            errorData = new ErrorData(ErrorConst.IDX_USERNOTEXIST_ERROR);
            logger.warn(LogUtils.Msg(errorData,request));
        } else {
            responseData = new AccountIDetailResponseData(account.getName(),account.getPhone());
        }
        return errorData;
    }

    public ResponseBase process() {
        if (validate() == null && login() == null) {
            logger.info(LogUtils.Msg("Success to Login",request,responseData));
            return new ResponseBase(StatusConst.OK,responseData);
        } else return new ResponseBase(StatusConst.ERROR,errorData);

    }
}
