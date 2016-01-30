package com.curus.services.account;

import com.curus.dao.CurusDriver;
import com.curus.dao.account.AccountDao;
import com.curus.httpio.request.account.AccountRegisterRequest;
import com.curus.httpio.response.ErrorData;
import com.curus.httpio.response.ResponseBase;
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
 * Created by stupid-coder on 23/1/16.
 */
public class AccountRegisterService {

    private static Log logger = LogFactory.getLog(AccountRegisterService.class);
    private AccountRegisterRequest request;
    private ErrorData errorData;
    private CurusDriver driver;

    public AccountRegisterService(AccountRegisterRequest request, CurusDriver driver) {
        this.request = request;
        this.driver = driver;
        errorData = null;
    }

    private ErrorData validate() {
        if ( (errorData = PhoneValidate.validateNoduplPhone(request.getPhone(), driver.accountDao)) != null) {
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (errorData = PasswdValidate.validatePassword(request.getPasswd())) != null) {
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (errorData = CodeValidate.validateRegisterCode(request.getPhone(),request.getCode())) != null) {
            logger.warn(LogUtils.Msg(errorData,request));
        }
        return errorData;
    }

    private ErrorData register() {
        if (driver.accountDao.insert(request.getPhone(),request.getPasswd()) == 0) {
            logger.warn(LogUtils.Msg("Failure to Register",request));
            errorData = new ErrorData(ErrorConst.IDX_MYSQL_ERROR);
        }
        return errorData;
    }

    public ResponseBase process() {
        logger.info(request.toString());
        if ( validate() == null && register() == null ) {
            logger.info(LogUtils.Msg("Success to Register",request));
            return new ResponseBase(StatusConst.OK,null);
        } return new ResponseBase(StatusConst.ERROR,errorData);
    }
}
