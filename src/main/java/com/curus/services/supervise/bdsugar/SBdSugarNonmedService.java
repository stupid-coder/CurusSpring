package com.curus.services.supervise.bdsugar;

import com.alibaba.fastjson.JSONObject;
import com.curus.dao.CurusDriver;
import com.curus.httpio.request.supervise.bdsugar.SBdSugarNonmedRequest;
import com.curus.httpio.response.ErrorData;
import com.curus.httpio.response.ResponseBase;
import com.curus.model.database.Account;
import com.curus.utils.CacheUtils;
import com.curus.utils.LogUtils;
import com.curus.utils.constant.StatusConst;
import com.curus.utils.service.supervise.bdsugar.SBdSugarServiceUtils;
import com.curus.utils.validate.ValueValidate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by stupid-coder on 6/5/16.
 */
public class SBdSugarNonmedService {

    private Log logger = LogFactory.getLog(SBdSugarNonmedService.class);

    private SBdSugarNonmedRequest request;
    private JSONObject responseData;
    private CurusDriver driver;
    private ErrorData errorData;

    public SBdSugarNonmedService(SBdSugarNonmedRequest request, CurusDriver driver) {
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

    private ErrorData nonmed() {
        Account account = (Account) CacheUtils.getObject4Cache(request.getToken());
        responseData = new JSONObject();
        SBdSugarServiceUtils.BdSugarNonmedSuggestion(driver,account.getId(),request.getPatient_id(),request.getDiet(),request.getActivity(),responseData);

        return errorData;
    }

    private ResponseBase process() {
        if ( validate() == null && nonmed() == null ) {
            logger.info(LogUtils.Msg("Success to Service Nonmed",request,responseData));
            return new ResponseBase(StatusConst.OK, responseData);
        } else {
            logger.warn(LogUtils.Msg("Failure to Service nonmed",request,errorData));
            return new ResponseBase(StatusConst.ERROR, errorData);
        }
    }
}
