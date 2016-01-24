package com.curus.services.account.passwd;

import com.curus.dao.AccountDao;
import com.curus.httpio.request.account.passwd.AccountPasswdModifyRequest;
import com.curus.httpio.response.ErrorData;
import com.curus.httpio.response.ResponseBase;
import com.curus.model.Account;
import com.curus.utils.CacheUtils;
import com.curus.utils.LogUtils;
import com.curus.utils.SpringContextUtils;
import com.curus.utils.constant.ErrorConst;
import com.curus.utils.constant.StatusConst;
import com.curus.utils.validate.PasswdValidate;
import com.curus.utils.validate.ValueValidate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by stupid-coder on 24/1/16.
 */
public class AccountPasswdModifyService {
    static private Log logger = LogFactory.getLog(AccountPasswdModifyService.class);
    private AccountPasswdModifyRequest request;
    private AccountDao driver;
    private ErrorData errorData;
    public AccountPasswdModifyService(AccountPasswdModifyRequest request) {
        this.request = request;
        errorData = null;
        driver = (AccountDao) SpringContextUtils.getBean("accountDao");
    }

    private ErrorData validate() {
        if ( (errorData = ValueValidate.valueExistValidate(request.getToken(), "token")) != null) {
            logger.warn(LogUtils.Msg(errorData, request));
        } else if ( (errorData = PasswdValidate.validatePassword(request.getOld_passwd())) !=null) {
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (errorData = PasswdValidate.validatePassword(request.getNew_passwd())) !=null) {
            logger.warn(LogUtils.Msg(errorData,request));
        }
        return errorData;
    }

    private ErrorData modify() {
        Account account;

        if ( ( account = (Account)CacheUtils.getObject4Cache(request.getToken())) == null) {
            errorData = new ErrorData(ErrorConst.IDX_TOKENEXPIRED_ERROR);
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( account.getPasswd().compareTo(request.getOld_passwd()) != 0) {
            errorData = new ErrorData(ErrorConst.IDX_INCPASSWD_ERROR);
            logger.warn(LogUtils.Msg(errorData,request,account));
        } else {
            account.setPasswd(request.getNew_passwd());
            assert(driver.updatePasswd(account)==1);
            CacheUtils.putObject2Cache(request.getToken(),account);
        }
        return errorData;
    }

    public ResponseBase process() {
        if (validate() == null && modify() == null) {
            logger.info(LogUtils.Msg("Success to Modify Password",request,null));
            return new ResponseBase(StatusConst.OK,null);
        } else return new ResponseBase(StatusConst.ERROR,errorData);

    }
}
