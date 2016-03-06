package com.curus.services.supervise.smoke;

import com.alibaba.fastjson.JSONObject;
import com.curus.dao.CurusDriver;
import com.curus.httpio.request.supervise.smoke.SSmokeListRequest;
import com.curus.httpio.response.ErrorData;
import com.curus.httpio.response.ResponseBase;
import com.curus.model.database.Account;
import com.curus.utils.CacheUtils;
import com.curus.utils.LogUtils;
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
public class SSmokeListService {

    private Log logger = LogFactory.getLog(SSmokeListService.class);
    private CurusDriver driver;
    private SSmokeListRequest request;
    private JSONObject responseData;
    private ErrorData errorData;

    public SSmokeListService(SSmokeListRequest request, CurusDriver driver) {
        this.driver = driver;
        this.request = request;
        this.responseData = new JSONObject();
        this.errorData = null;
    }

    private ErrorData validate() {

        if ( (errorData = ValueValidate.valueExistValidate(request.getToken(),"token")) != null) {
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (errorData = ValueValidate.valueExistValidate(request.getPatient_id(),"patient_id")) != null) {
            logger.warn(LogUtils.Msg(errorData,request));
        }
        return errorData;
    }

    private ErrorData listSmock() {

        Account account;

        if ( (account = (Account) CacheUtils.getObject4Cache(request.getToken())) == null) {
            errorData = new ErrorData(ErrorConst.IDX_TOKENEXPIRED_ERROR);
            logger.warn(LogUtils.Msg(errorData,request));
        } else {
            QuotaServiceUtils.listQuotas(driver,null,account.getId(),request.getPatient_id(), QuotaConst.QUOTA_SMOKE,
                    null, responseData);
        }

        return errorData;
    }

    public ResponseBase process() {

        if ( validate() == null && listSmock() == null ) {
            logger.info(LogUtils.Msg("Success to List Smock",request,responseData));
            return new ResponseBase(StatusConst.OK,responseData);
        } else {
            logger.warn(LogUtils.Msg("Failure to List Smock",errorData,request));
            return new ResponseBase(StatusConst.ERROR,request);
        }
    }
}
