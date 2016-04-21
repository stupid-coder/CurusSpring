package com.curus.services.supervise;

import com.curus.dao.CurusDriver;
import com.curus.httpio.request.supervise.SuperviseGetRequest;
import com.curus.httpio.response.ErrorData;
import com.curus.httpio.response.ResponseBase;
import com.curus.httpio.response.supervise.SuperviseGetResponseData;
import com.curus.model.database.Account;
import com.curus.model.database.PatientSupervise;
import com.curus.utils.CacheUtils;
import com.curus.utils.LogUtils;
import com.curus.utils.QuotaUtils;
import com.curus.utils.constant.ErrorConst;
import com.curus.utils.constant.StatusConst;
import com.curus.utils.validate.QuotaValidate;
import com.curus.utils.validate.ValueValidate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by stupid-coder on 21/4/16.
 */
public class SuperviseGetService {
    private Log logger = LogFactory.getLog(SuperviseGetService.class);

    private SuperviseGetRequest request;
    private PatientSupervise responseData;
    private CurusDriver driver;
    private ErrorData errorData;

    public SuperviseGetService(SuperviseGetRequest request, CurusDriver driver) {
        this.request =request;
        this.driver = driver;
        this.responseData = null;
        this.errorData = null;
    }

    private ErrorData validate() {
        if ( (errorData = ValueValidate.valueExistValidate(request.getToken(),"token")) != null)
            logger.warn(LogUtils.Msg(errorData, request));
        else if ( (errorData = ValueValidate.valueExistValidate(request.getPatient_id(),"patient_id")) != null)
            logger.warn(LogUtils.Msg(errorData, request));
        else if ( (errorData = QuotaValidate.quotaCateValidate(request.getCate(),"cate")) != null)
            logger.warn(LogUtils.Msg(errorData, request));
        return errorData;
    }

    private ErrorData getSupervise() {
        Account account;
        if ( (account = (Account) CacheUtils.getObject4Cache(request.getToken())) == null ) {
            errorData = new ErrorData(ErrorConst.IDX_TOKENEXPIRED_ERROR);
            logger.warn(LogUtils.Msg(errorData, request));
        } else {
            responseData = driver.patientSuperviseDao.selectLastSupervise(account.getId(),request.getPatient_id(), QuotaUtils.getQuotaIds(request.getCate()));
        }
        return errorData;
    }

    public ResponseBase process() {
        if ( validate() == null && getSupervise() == null) {
            logger.info(LogUtils.Msg("Success to Get Supervise",request,responseData));
            return new ResponseBase(StatusConst.OK,responseData);
        } else {
            logger.warn(LogUtils.Msg("Failure to Get Supervise",request,errorData));
            return new ResponseBase(StatusConst.ERROR,errorData);
        }
    }

}
