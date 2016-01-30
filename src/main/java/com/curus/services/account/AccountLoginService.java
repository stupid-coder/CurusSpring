package com.curus.services.account;

import com.curus.dao.CurusDriver;
import com.curus.dao.account.AccountDao;
import com.curus.httpio.request.account.AccountLoginRequest;
import com.curus.httpio.response.ErrorData;
import com.curus.httpio.response.ResponseBase;
import com.curus.httpio.response.account.AccountLoginResponseData;
import com.curus.model.Account;
import com.curus.utils.CacheUtils;
import com.curus.utils.LogUtils;
import com.curus.utils.SpringContextUtils;
import com.curus.utils.constant.ErrorConst;
import com.curus.utils.constant.StatusConst;
import com.curus.utils.validate.PasswdValidate;
import com.curus.utils.validate.PhoneValidate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by stupid-coder on 24/1/16.
 */
public class AccountLoginService {

    static private Log logger = LogFactory.getLog(AccountLoginService.class);
    private AccountLoginRequest request;
    private AccountLoginResponseData responseData;
    private CurusDriver driver;
    private ErrorData errorData;
    public AccountLoginService(AccountLoginRequest request, CurusDriver driver) {
        this.request = request;
        this.responseData = null;
        this.errorData = null;
        this.driver = driver;
    }

    private ErrorData validate() {
        if ( (errorData = PhoneValidate.validatePhoneNumber(request.getPhone())) != null) {
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (errorData = PasswdValidate.validatePassword(request.getPasswd())) !=null) {
            logger.warn(LogUtils.Msg(errorData,request));
        }
        return errorData;
    }

    private ErrorData login() {
        Account account;
        if ( ( account = driver.accountDao.selectByPhone(request.getPhone())) == null) {
            errorData = new ErrorData(ErrorConst.IDX_USERNOTEXIST_ERROR);
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( account.getPasswd().compareTo(request.getPasswd()) != 0) {
            errorData = new ErrorData(ErrorConst.IDX_INCPASSWD_ERROR);
        } else {
            responseData = new AccountLoginResponseData(CacheUtils.putObject2Cache(account),account.getIs_exp_user(),account.getId());
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
