package com.curus.services.account.passwd;

import com.curus.dao.CurusDriver;
import com.curus.dao.account.AccountDao;
import com.curus.httpio.request.account.passwd.AccountPasswdForgetRequest;
import com.curus.httpio.response.ErrorData;
import com.curus.httpio.response.ResponseBase;
import com.curus.model.Account;
import com.curus.utils.LogUtils;
import com.curus.utils.SpringContextUtils;
import com.curus.utils.constant.ErrorConst;
import com.curus.utils.constant.StatusConst;
import com.curus.utils.validate.CodeValidate;
import com.curus.utils.validate.PasswdValidate;
import com.curus.utils.validate.PhoneValidate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by stupid-coder on 24/1/16.
 */
public class AccountPasswdForgetService {
    static private Log logger = LogFactory.getLog(AccountPasswdForgetService.class);
    private AccountPasswdForgetRequest request;
    private CurusDriver driver;
    private ErrorData errorData;
    public AccountPasswdForgetService(AccountPasswdForgetRequest request, CurusDriver driver) {
        this.request = request;
        this.errorData = null;
        this.driver = driver;
    }

    private ErrorData validate() {
        if ( (errorData = PhoneValidate.validatePhoneNumber(request.getPhone())) != null) {
            logger.warn(LogUtils.Msg(errorData, request));
        } else if ( (errorData = PasswdValidate.validatePassword(request.getPasswd())) !=null) {
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (errorData = CodeValidate.validateForgetCode(request.getPhone(), request.getCode())) !=null) {
            logger.warn(LogUtils.Msg(errorData,request));
        }
        return errorData;
    }

    private ErrorData forgetPassword() {
        Account account;
        if ( ( account = driver.accountDao.selectByPhone(request.getPhone())) == null) {
            errorData = new ErrorData(ErrorConst.IDX_USERNOTEXIST_ERROR);
            logger.warn(LogUtils.Msg(errorData,request));
        } else {
            account.setPasswd(request.getPasswd());
            assert(driver.accountDao.updatePasswd(account)==1);
        }
        return errorData;
    }

    public ResponseBase process() {
        if (validate() == null && forgetPassword() == null) {
            logger.info(LogUtils.Msg("Success to Change Passwd for forget-passwd",request,null));
            return new ResponseBase(StatusConst.OK,null);
        } else return new ResponseBase(StatusConst.ERROR,errorData);

    }

}
