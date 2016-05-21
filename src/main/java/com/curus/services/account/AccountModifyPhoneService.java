package com.curus.services.account;

import com.curus.dao.CurusDriver;
import com.curus.httpio.request.account.AccountModifyPhoneRequest;
import com.curus.httpio.response.ErrorData;
import com.curus.httpio.response.ResponseBase;
import com.curus.model.database.Account;
import com.curus.model.database.Patient;
import com.curus.utils.CacheUtils;
import com.curus.utils.LogUtils;
import com.curus.utils.constant.ErrorConst;
import com.curus.utils.constant.StatusConst;
import com.curus.utils.service.patient.PatientServiceUtils;
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
    private CurusDriver driver;
    private ErrorData errorData;
    public AccountModifyPhoneService(AccountModifyPhoneRequest request, CurusDriver driver) {
        this.request = request;
        this.errorData = null;
        this.driver = driver;
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

    private ErrorData modifyPhone() {
        Account account;
        if ( ( account = (Account)CacheUtils.deleteObjectAndGet4Cache(request.getToken())) == null) {
            errorData = new ErrorData(ErrorConst.IDX_USERNOTEXIST_ERROR);
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (account = driver.accountDao.selectById(account.getId())) == null ) {
            errorData = new ErrorData(ErrorConst.IDX_INCPASSWD_ERROR);
        } else {
            account.setPhone(request.getPhone());
            driver.accountDao.updatePhone(account);
            Patient patient= PatientServiceUtils.select(driver,account.getId_number());
            patient.setPhone(request.getPhone());
            driver.patientDao.update(patient,"id");
        }
        return errorData;
    }

    public ResponseBase process() {
        if (validate() == null && modifyPhone() == null) {
            logger.info(LogUtils.Msg("Success to Login",request,null));
            return new ResponseBase(StatusConst.OK,null);
        } else return new ResponseBase(StatusConst.ERROR,errorData);

    }
}
