package com.curus.services.quota;

import com.curus.dao.CurusDriver;
import com.curus.httpio.request.quota.QuotaAddRequest;
import com.curus.httpio.response.ErrorData;
import com.curus.httpio.response.ResponseBase;
import com.curus.model.database.Account;
import com.curus.utils.CacheUtils;
import com.curus.utils.LogUtils;
import com.curus.utils.TimeUtils;
import com.curus.utils.constant.ErrorConst;
import com.curus.utils.constant.StatusConst;
import com.curus.utils.service.quota.QuotaServiceUtils;
import com.curus.utils.validate.QuotaValidate;
import com.curus.utils.validate.ValueValidate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Timestamp;

/**
 * Created by stupid-coder on 3/2/16.
 */
public class QuotaAddService {

    private Log logger = LogFactory.getLog(QuotaAddService.class);

    private CurusDriver driver;
    private QuotaAddRequest request;
    private ErrorData errorData;

    public QuotaAddService(QuotaAddRequest request,CurusDriver driver) {
        this.driver = driver;
        this.request = request;
        this.errorData = null;
    }

    private ErrorData validate() {
        if ( (errorData = ValueValidate.valueExistValidate(request.getToken(), "token")) != null) {
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (errorData = QuotaValidate.quotaCateValidate(request.getCate(),"cate")) != null) {
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (errorData =ValueValidate.valueLongValidate(request.getPatient_id(), "patient_id")) != null) {
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (errorData = ValueValidate.valueExistValidate(request.getDate(), "date")) != null) {
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (errorData = ValueValidate.valueExistValidate(request.getValue(), "value")) != null) {
            logger.warn(LogUtils.Msg(errorData,request));
        }
        return errorData;
    }

    private ErrorData addQuota() {
        Account account;
        if ( (account = (Account) CacheUtils.getObject4Cache(request.getToken())) == null) {
            errorData = new ErrorData(ErrorConst.IDX_TOKENEXPIRED_ERROR);
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( QuotaServiceUtils.addQuota(driver, account, Long.parseLong(request.getPatient_id()),
                request.getCate(), TimeUtils.parseTimestamp(request.getDate()),request.getValue()) == 0) {
            errorData = new ErrorData(ErrorConst.IDX_SERVER_ERROR);
            logger.warn(LogUtils.Msg(errorData,request));
        }
        return errorData;
    }

    public ResponseBase process() {
        if (validate() == null && addQuota() == null) {
            logger.info(LogUtils.Msg("Success To Add Quota",request));
            return new ResponseBase(StatusConst.OK,null);
        } else return new ResponseBase(StatusConst.ERROR,errorData);
    }


}
