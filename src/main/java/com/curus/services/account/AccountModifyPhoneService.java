package com.curus.services.account;

import com.curus.dao.AccountDao;
import com.curus.httpio.request.account.AccountModifyPhoneRequest;
import com.curus.httpio.response.ErrorData;
import com.curus.httpio.response.ResponseBase;
import com.curus.model.Account;
import com.curus.utils.CacheUtils;
import com.curus.utils.LogUtils;
import com.curus.utils.SpringContextUtils;
import com.curus.utils.constant.ErrorConst;
import com.curus.utils.constant.StatusConst;
import com.curus.utils.validate.CodeValidate;
import com.curus.utils.validate.PhoneValidate;
import com.curus.utils.validate.ValueValidate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by stupid-coder on 24/1/16.
 */
public class AccountModifyPhoneService {
    static private Log logger = LogFactory.getLog(AccountModifyPhoneService.class);
    private AccountModifyPhoneRequest request;
    private AccountDao driver;
    private ErrorData errorData;
    public AccountModifyPhoneService(AccountModifyPhoneRequest request) {
        this.request = request;
        errorData = null;
        driver = (AccountDao) SpringContextUtils.getBean("accountDao");
    }

    private ErrorData validate() {
        if ( (errorData = ValueValidate.valueExistValidate(request.getToken(), "token")) != null) {
            logger.warn(LogUtils.Msg(errorData, request));
        } else if ( (errorData = PhoneValidate.validatePhoneNumber(request.getPhone())) != null) {
            logger.warn(LogUtils.Msg(errorData, request));
        } else if ( (errorData = ValueValidate.valueExistValidate(request.getCode(),"code")) !=null){
            logger.warn(LogUtils.Msg(errorData, request));
        } else if ( (errorData = CodeValidate.validateModifyPhoneCode(request.getToken(),request.getPhone(),request.getCode())) != null) {
            logger.warn(LogUtils.Msg(errorData,request));
        }
        return errorData;
    }

    private ErrorData login() {
        Account account;
        if ( ( account = (Account)CacheUtils.deleteObjectAndGet4Cache(request.getToken())) == null) {
            errorData = new ErrorData(ErrorConst.IDX_USERNOTEXIST_ERROR);
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (account = driver.selectById(account.getId())) == null ) {
            errorData = new ErrorData(ErrorConst.IDX_INCPASSWD_ERROR);
        } else {
            account.setPhone(request.getPhone());
            driver.updatePhone(account);
        }
        return errorData;
    }

    public ResponseBase process() {
        if (validate() == null && login() == null) {
            logger.info(LogUtils.Msg("Success to Login",request,null));
            return new ResponseBase(StatusConst.OK,null);
        } else return new ResponseBase(StatusConst.ERROR,errorData);

    }
}
