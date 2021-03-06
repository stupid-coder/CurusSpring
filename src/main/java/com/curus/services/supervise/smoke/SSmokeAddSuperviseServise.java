package com.curus.services.supervise.smoke;

import com.curus.dao.CurusDriver;
import com.curus.httpio.request.supervise.smoke.SSmokeAddSuperviseRequest;
import com.curus.httpio.response.ErrorData;
import com.curus.httpio.response.ResponseBase;
import com.curus.model.database.Account;
import com.curus.utils.CacheUtils;
import com.curus.utils.LogUtils;
import com.curus.utils.constant.ErrorConst;
import com.curus.utils.constant.StatusConst;
import com.curus.utils.service.supervise.smoke.SSmokeServiseUtils;
import com.curus.utils.validate.ValueValidate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by stupid-coder on 5/3/16.
 */
public class SSmokeAddSuperviseServise {

    private Log logger = LogFactory.getLog(SSmokeAddSuperviseServise.class);

    private CurusDriver driver;
    private SSmokeAddSuperviseRequest request;
    private ErrorData errorData;

    public SSmokeAddSuperviseServise(SSmokeAddSuperviseRequest request,CurusDriver driver) {
        this.driver = driver;
        this.request = request;
        errorData = null;
    }

    private ErrorData validate() {
        if ( (errorData = ValueValidate.valueExistValidate(request.getToken(),"token")) != null ) {
            logger.warn(LogUtils.Msg(errorData, request));
        } else if ( (errorData = ValueValidate.valueExistValidate(request.getPatient_id(),"patient_d")) != null ) {
            logger.warn(LogUtils.Msg(errorData, request));
        } else if ( (errorData = ValueValidate.valueExistValidate(request.getSmplan(),"smplan")) != null) {
            logger.warn(LogUtils.Msg(errorData, request));
        }
        return errorData;
    }

    private ErrorData addSupervise() {
        Account account;

        if ( (account = (Account)CacheUtils.getObject4Cache(request.getToken())) == null ) {
            errorData = new ErrorData(ErrorConst.IDX_TOKENEXPIRED_ERROR);
            logger.warn(LogUtils.Msg(errorData,request));
        } else {
            SSmokeServiseUtils.addSupervise(driver,account.getId(),request.getPatient_id(),request.getSmplan());
        }
        return errorData;
    }

    public ResponseBase process() {

        if ( validate() == null && addSupervise() == null ) {
            logger.info(LogUtils.Msg("Success to Add Smoke Supervise",request));
            return new ResponseBase(StatusConst.OK,null);
        } else {
            logger.warn(LogUtils.Msg("Failure to Add Smoke Supervise",errorData,request));
            return new ResponseBase(StatusConst.ERROR,errorData);
        }
    }
}
