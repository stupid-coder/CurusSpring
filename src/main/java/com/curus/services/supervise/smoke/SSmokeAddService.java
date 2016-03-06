package com.curus.services.supervise.smoke;

import com.curus.dao.CurusDriver;
import com.curus.httpio.request.supervise.smoke.SSmokeAddRequest;
import com.curus.httpio.response.ErrorData;
import com.curus.httpio.response.ResponseBase;
import com.curus.model.database.Account;
import com.curus.utils.CacheUtils;
import com.curus.utils.LogUtils;
import com.curus.utils.TimeUtils;
import com.curus.utils.constant.ErrorConst;
import com.curus.utils.constant.QuotaConst;
import com.curus.utils.constant.StatusConst;
import com.curus.utils.service.quota.QuotaServiceUtils;
import com.curus.utils.validate.ValueValidate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by stupid-coder on 4/3/16.
 */
public class SSmokeAddService {

    private Log logger = LogFactory.getLog(SSmokeAddService.class);

    private CurusDriver driver;
    private SSmokeAddRequest request;
    private ErrorData errorData;

    public SSmokeAddService(SSmokeAddRequest request, CurusDriver driver) {
        this.driver = driver;
        this.request = request;
        this.errorData = null;
    }

    private ErrorData validate() {
        
        if ( (errorData = ValueValidate.valueExistValidate(request.getToken(),"token")) != null ) {
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (errorData = ValueValidate.valueExistValidate(request.getPatient_id(),"patient_id")) != null ) {
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (errorData = ValueValidate.valueExistValidate(request.getValue(),"value")) != null ) {
            logger.warn(LogUtils.Msg(errorData,request));
        }
        return errorData;
    }

    private ErrorData addSmock() {
        Account account;
        if ( (account = (Account) CacheUtils.getObject4Cache(request.getToken())) == null ) {
            errorData = new ErrorData(ErrorConst.IDX_TOKENEXPIRED_ERROR);
            logger.warn(LogUtils.Msg(errorData,request));
        } else if (QuotaServiceUtils.addQuota(driver,account.getId(),request.getPatient_id(), QuotaConst.QUOTA_SMOKE,null, TimeUtils.getDate(),request.getValue())!=1) {
            errorData = new ErrorData(ErrorConst.IDX_MYSQL_ERROR);
            logger.warn(LogUtils.Msg(errorData,request));
        }
        return errorData;
    }

    public ResponseBase process() {

        if ( validate() == null && addSmock() == null ) {
            logger.info(LogUtils.Msg("Success to Add Smock",request));
            return new ResponseBase(StatusConst.OK,null);
        } else {
            logger.warn(LogUtils.Msg("Failure to Add Smock",request));
            return new ResponseBase(StatusConst.ERROR,errorData);
        }

    }
}
