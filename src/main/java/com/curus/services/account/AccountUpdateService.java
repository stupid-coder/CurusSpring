package com.curus.services.account;

import com.curus.dao.AccountDao;
import com.curus.httpio.request.account.AccountUpdateRequest;
import com.curus.httpio.response.ErrorData;
import com.curus.httpio.response.ResponseBase;
import com.curus.model.Account;
import com.curus.utils.CacheUtils;
import com.curus.utils.LogUtils;
import com.curus.utils.SpringContextUtils;
import com.curus.utils.constant.ErrorConst;
import com.curus.utils.constant.StatusConst;
import com.curus.utils.validate.ValueValidate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by stupid-coder on 24/1/16.
 */
public class AccountUpdateService {

    static private Log logger = LogFactory.getLog(AccountUpdateService.class);
    private AccountUpdateRequest request;
    private AccountDao driver;
    private ErrorData errorData;
    public AccountUpdateService(AccountUpdateRequest request) {
        this.request = request;
        errorData = null;
        driver = (AccountDao) SpringContextUtils.getBean("accountDao");
    }

    private ErrorData validate() {
        if ( (errorData = ValueValidate.valueExistValidate(request.getToken(), "token")) != null) {
            logger.warn(LogUtils.Msg(errorData, request));
        } else if ( (errorData = ValueValidate.valueExistValidate(request.getName(),"name")) !=null) {
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (errorData = ValueValidate.valueIntegerValidate(request.getGender(),"gender")) != null) {
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (errorData = ValueValidate.valueIntegerValidate(request.getBirth(),"birth")) != null) {
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (errorData = ValueValidate.idValidation(request.getId_number(),"id_number")) != null) {
            logger.warn(LogUtils.Msg(errorData,request));
        }
        return errorData;
    }

    private ErrorData update() {
        Account account;
        if ( ( account = (Account) CacheUtils.getObject4Cache(request.getToken())) == null) {
            errorData = new ErrorData(ErrorConst.IDX_TOKENEXPIRED_ERROR);
            logger.warn(LogUtils.Msg(errorData,request));
        } else {
            if ( request.getName() != null ) account.setName(request.getName());
            if ( request.getGender() != null ) account.setGender(ValueValidate.genderize(Integer.parseInt(request.getGender())));
            if ( request.getBirth() != null ) account.setBirth( ValueValidate.dateFromTimestamp(request.getBirth()) );
            if ( request.getId_number() != null ) account.setId_number(request.getId_number());
            if ( request.getAddress() != null ) account.setAddress(request.getAddress());
            if ( request.getContact() != null ) account.setOther_contact(request.getContact());
            account.setIs_exp_user(0);
            driver.update(account);
            CacheUtils.putObject2Cache(request.getToken(),account);
        }
        return errorData;
    }

    public ResponseBase process() {
        if (validate() == null && update() == null) {
            logger.info(LogUtils.Msg("Success to Login",request,null));
            return new ResponseBase(StatusConst.OK,null);
        } else return new ResponseBase(StatusConst.ERROR,errorData);

    }

}
