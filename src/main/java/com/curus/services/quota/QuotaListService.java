package com.curus.services.quota;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.curus.dao.CurusDriver;
import com.curus.httpio.request.quota.QuotaListRequest;
import com.curus.httpio.response.ErrorData;
import com.curus.httpio.response.ResponseBase;
import com.curus.httpio.response.TsValueData;
import com.curus.model.database.Account;
import com.curus.utils.CacheUtils;
import com.curus.utils.LogUtils;
import com.curus.utils.QuotaUtils;
import com.curus.utils.constant.ErrorConst;
import com.curus.utils.constant.QuotaConst;
import com.curus.utils.constant.StatusConst;
import com.curus.utils.service.quota.QuotaServiceUtils;
import com.curus.utils.validate.ValueValidate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by stupid-coder on 5/2/16.
 */
public class QuotaListService {

    private Log logger = LogFactory.getLog(QuotaListService.class);

    private QuotaListRequest request;
    private CurusDriver driver;
    private JSONObject responseData;
    private ErrorData errorData;

    public QuotaListService(QuotaListRequest request, CurusDriver driver) {
        this.request = request;
        this.driver = driver;
        this.responseData = new JSONObject();
        this.errorData = null;
    }

    private ErrorData validate() {
        if ( (errorData = ValueValidate.valueExistValidate(request.getToken(),"token")) != null) {
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (errorData = ValueValidate.valueExistValidate(request.getPatient_id(), "patient_id")) != null) {
            logger.warn(LogUtils.Msg(errorData, request));
        } else if ( request.getCate() != null && QuotaUtils.getQuotaIds(request.getCate()).compareTo(QuotaConst.QUOTA_UNKNOW_ID) == 0 ) {
            errorData = new ErrorData(ErrorConst.IDX_INVALIDPARM_ERROR,"cate");
            logger.warn(LogUtils.Msg(errorData,request));
        }
        return errorData;
    }

    private ErrorData listQuota() {
        Account account;

        if ( (account = (Account) CacheUtils.getObject4Cache(request.getToken())) == null) {
            errorData = new ErrorData(ErrorConst.IDX_TOKENEXPIRED_ERROR);
            logger.warn(LogUtils.Msg(errorData,request));
        } else {
            if ( request.getCate() == null )
                QuotaServiceUtils.listQuotas(driver,account.getId(),request.getPatient_id(),responseData);
            else QuotaServiceUtils.listQuotas(driver,request.getDays(),account.getId(),request.getPatient_id(),request.getCate(),request.getSubcate(),responseData);
        }

        return errorData;
    }

    public ResponseBase process() {
        if ( validate() == null && listQuota() == null) {
            logger.info(LogUtils.Msg("Success to List Quotas",request,responseData));
            return new ResponseBase(StatusConst.OK,responseData);
        } else return new ResponseBase(StatusConst.ERROR,errorData);
    }
}
