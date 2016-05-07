package com.curus.services.supervise.bdpressure;

import com.alibaba.fastjson.JSONObject;
import com.curus.dao.CurusDriver;
import com.curus.httpio.request.supervise.bdpressure.SBdPressureNonmedRequest;
import com.curus.httpio.response.ErrorData;
import com.curus.httpio.response.ResponseBase;
import com.curus.model.database.Account;
import com.curus.utils.CacheUtils;
import com.curus.utils.LogUtils;
import com.curus.utils.constant.ErrorConst;
import com.curus.utils.constant.StatusConst;
import com.curus.utils.service.supervise.bdpressure.SBdPressureServiseUtils;
import com.curus.utils.validate.ValueValidate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by stupid-coder on 7/3/16.
 */
public class SBdPressureNonmedService {

    private Log logger = LogFactory.getLog(SBdPressureNonmedService.class);

    private CurusDriver driver;
    private SBdPressureNonmedRequest request;
    private JSONObject responseData;
    private ErrorData errorData;

    public SBdPressureNonmedService(SBdPressureNonmedRequest request, CurusDriver driver) {
        this.driver = driver;
        this.request = request;
        this.responseData = new JSONObject();
        this.errorData = null;
    }

    private ErrorData validate() {
        if ( (errorData = ValueValidate.valueExistValidate(request.getToken(),"token")) != null ) {
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (errorData = ValueValidate.valueExistValidate(request.getPatient_id(),"patient_id")) != null ) {
            logger.warn(LogUtils.Msg(errorData,request));
        }
        return errorData;
    }

    private ErrorData nonmed() {
        Account account;
        if ( (account = (Account) CacheUtils.getObject4Cache(request.getToken())) == null ) {
            errorData = new ErrorData(ErrorConst.IDX_TOKENEXPIRED_ERROR);
            logger.warn(LogUtils.Msg(errorData,request));
        } else {
            responseData = SBdPressureServiseUtils.BdPressureNonmedLoss(driver,account.getId(),request);
        }
        return errorData;
    }

    public ResponseBase process() {
        if ( validate() == null && nonmed() == null ) {
            logger.info(LogUtils.Msg("Success to Test Nonmedicine",request,responseData));
            return new ResponseBase(StatusConst.OK,responseData);
        } else {
            logger.warn(LogUtils.Msg("Failure to Test Nonmedicine",errorData,request));
            return new ResponseBase(StatusConst.ERROR,errorData);
        }
    }
}
