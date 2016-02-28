package com.curus.services.supervise.weight;

import com.curus.dao.CurusDriver;
import com.curus.httpio.request.supervise.weight.SWeightEstimateRequest;
import com.curus.httpio.response.ErrorData;
import com.curus.httpio.response.ResponseBase;
import com.curus.httpio.response.supervise.weight.SWeightEstimateResponseData;
import com.curus.model.database.Account;
import com.curus.utils.CacheUtils;
import com.curus.utils.LogUtils;
import com.curus.utils.constant.ErrorConst;
import com.curus.utils.constant.StatusConst;
import com.curus.utils.service.supervise.weight.SWeightSerivceUtils;
import com.curus.utils.validate.ValueValidate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by stupid-coder on 28/2/16.
 */
public class SWeightEstimateService {

    private Log logger = LogFactory.getLog(SWeightEstimateService.class);

    private CurusDriver driver;
    private SWeightEstimateRequest request;
    private SWeightEstimateResponseData responseData;
    private ErrorData errorData;

    public SWeightEstimateService(CurusDriver driver, SWeightEstimateRequest request) {
        this.driver = driver;
        this.request = request;
        this.errorData = null;
        this.responseData = null;
    }

    public ErrorData validate() {
        if ( (errorData = ValueValidate.valueExistValidate(request.getToken(),"token")) != null ) {
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (errorData = ValueValidate.valueExistValidate(request.getPatient_id(),"patient_id")) != null ) {
            logger.warn(LogUtils.Msg(errorData,request));
        }
        return errorData;
    }

    public ErrorData estimateWeightLoss() {
        Account account;
        if ( (account = (Account) CacheUtils.getObject4Cache(request.getToken())) == null ) {
            errorData = new ErrorData(ErrorConst.IDX_TOKENEXPIRED_ERROR);
            logger.warn(LogUtils.Msg(errorData, request));
        } else {
            SWeightSerivceUtils.EstimateWeightLoss(driver, account.getId(), request, responseData);
        }
        return errorData;
    }

    public ResponseBase process() {
        if ( validate() == null && estimateWeightLoss() == null ) {
            logger.info(LogUtils.Msg("Success To Estimate WeightLoss",request,responseData));
            return new ResponseBase(StatusConst.OK,responseData);
        } else {
            logger.warn(LogUtils.Msg("Failure to Estimate WeightLoss",request,errorData));
            return new ResponseBase(StatusConst.ERROR,errorData);
        }
    }

}
