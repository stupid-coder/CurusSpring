package com.curus.services.supervise.smoke;

import com.curus.dao.CurusDriver;
import com.curus.httpio.request.supervise.smoke.SSmokeEstimateSuperviseRequest;
import com.curus.httpio.response.ErrorData;
import com.curus.httpio.response.ResponseBase;
import com.curus.httpio.response.supervise.smoke.SSmokeEstimateSuperviseResponseData;
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
public class SSmokeEstimateSuperviseService {
    private Log logger = LogFactory.getLog(SSmokeEstimateSuperviseService.class);

    private CurusDriver driver;
    private SSmokeEstimateSuperviseRequest request;
    private SSmokeEstimateSuperviseResponseData responseData;
    private ErrorData errorData;


    public SSmokeEstimateSuperviseService(SSmokeEstimateSuperviseRequest request,CurusDriver driver) {
        this.request = request;
        this.driver = driver;
        this.responseData = null;
        this.errorData = null;
    }

    private ErrorData validate() {
        if ( (errorData = ValueValidate.valueExistValidate(request.getToken(),"token")) != null ) {
            logger.warn(LogUtils.Msg(errorData, request));
        } else if ( ( errorData = ValueValidate.valueExistValidate(request.getPatient_id(),"patient_id")) != null ) {
            logger.warn(LogUtils.Msg(errorData, request));
        }
        return errorData;
    }

    private ErrorData estimate() {
        Account account;

        if ( (account = (Account) CacheUtils.getObject4Cache(request.getToken())) == null) {
            errorData  = new ErrorData(ErrorConst.IDX_TOKENEXPIRED_ERROR);
            logger.warn(LogUtils.Msg(errorData,request));
        } else {
            responseData = SSmokeServiseUtils.estimate(driver,account.getId(),request.getPatient_id());
        }
        return errorData;
    }

    public ResponseBase process() {
        if ( validate() == null && estimate() == null ) {
            logger.info(LogUtils.Msg("Success to Estimate Smoke Supervise",request,responseData));
            return new ResponseBase(StatusConst.OK,responseData);
        } else {
            logger.warn(LogUtils.Msg("Failure to Estimate Smoke Supervise",errorData,request));
            return new ResponseBase(StatusConst.ERROR,errorData);
        }
    }


}
