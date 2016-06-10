package com.curus.services.supervise.bdsugar;

import com.alibaba.fastjson.JSONObject;
import com.curus.dao.CurusDriver;
import com.curus.httpio.request.supervise.bdsugar.SBdSugarEstimateRequest;
import com.curus.httpio.response.ErrorData;
import com.curus.httpio.response.ResponseBase;
import com.curus.model.database.Account;
import com.curus.utils.CacheUtils;
import com.curus.utils.LogUtils;
import com.curus.utils.constant.ErrorConst;
import com.curus.utils.constant.StatusConst;
import com.curus.utils.service.drug.DrugServiceUtils;
import com.curus.utils.service.supervise.bdsugar.SBdSugarServiceUtils;
import com.curus.utils.validate.ValueValidate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by stupid-coder on 6/7/16.
 */
public class SBdSugarEstimateService {

    private Log logger = LogFactory.getLog(SBdSugarEstimateService.class);

    private SBdSugarEstimateRequest request;
    private JSONObject responseData;
    private ErrorData errorData;
    private CurusDriver driver;

    public SBdSugarEstimateService(SBdSugarEstimateRequest request, CurusDriver driver) {
        this.request = request;
        this.driver = driver;
    }

    private ErrorData validate() {
        if ( (errorData = ValueValidate.valueExistValidate(request.getToken(),"token")) != null ||
                (errorData = ValueValidate.valueExistValidate(request.getPatient_id(),"patient_id")) != null ) {
            logger.warn(LogUtils.Msg(errorData,request));
        }
        return errorData;
    }

    private ErrorData estimate() {
        Account account = (Account) CacheUtils.getObject4Cache(request.getToken());
        if ( account == null ) {
            errorData = new ErrorData(ErrorConst.IDX_TOKENEXPIRED_ERROR);
        } else {
            responseData = SBdSugarServiceUtils.estimate(driver,account.getId(),request.getPatient_id());
        }
        if ( errorData != null )
            logger.warn(LogUtils.Msg(errorData,request));
        return errorData;
    }

    public ResponseBase process() {
        if ( validate() == null && estimate() == null ) {
            logger.info(LogUtils.Msg("Success to estimate blood sugar",request,responseData));
            return new ResponseBase(StatusConst.OK,responseData);
        } else {
            logger.warn(LogUtils.Msg("Failure to estimate blood sugar",request,errorData));
            return new ResponseBase(StatusConst.ERROR,errorData);
        }
    }


}
