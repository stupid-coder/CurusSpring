package com.curus.services.supervise.weight;

import com.curus.dao.CurusDriver;
import com.curus.httpio.request.supervise.weight.SWeightLossTipsRequst;
import com.curus.httpio.response.ErrorData;
import com.curus.httpio.response.ResponseBase;
import com.curus.httpio.response.supervise.weight.SWeightLossTipsResponseData;
import com.curus.utils.LogUtils;
import com.curus.utils.constant.StatusConst;
import com.curus.utils.validate.ValueValidate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by stupid-coder on 29/2/16.
 */
public class SWeightLossTips {

    private Log logger = LogFactory.getLog(SWeightLossTips.class);

    private CurusDriver driver;
    private SWeightLossTipsRequst request;
    private SWeightLossTipsResponseData responseData;
    private ErrorData errorData;

    public SWeightLossTips(CurusDriver driver, SWeightLossTipsRequst request) {
        this.driver = driver;
        this.request = request;
        this.errorData = null;
        this.responseData = null;
    }

    private ErrorData validate() {
        if ( (errorData = ValueValidate.valueExistValidate(request.getToken(),"token")) == null) {
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (errorData = ValueValidate.valueExistValidate(request.getPatient_id(),"patient_id")) == null) {
            logger.warn(LogUtils.Msg(errorData,request));
        }
        return errorData;
    }

    private ErrorData weightLossTips() {

        return errorData;
    }

    public ResponseBase process() {

        if ( validate() != null && weightLossTips() != null ) {
            logger.info(LogUtils.Msg("Success To Weight Loss Tips", request, responseData));
            return new ResponseBase(StatusConst.OK,null);
        } else {
            logger.warn(LogUtils.Msg("Failure to Weight Loss Tips",request,errorData));
            return new ResponseBase(StatusConst.ERROR,errorData);
        }

    }
}
