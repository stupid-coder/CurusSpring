package com.curus.services.supervise.bdsugar;

import com.alibaba.fastjson.JSONObject;
import com.curus.dao.CurusDriver;
import com.curus.httpio.response.ErrorData;
import com.curus.httpio.response.ResponseBase;
import com.curus.io.Cache;
import com.curus.model.database.Account;
import com.curus.model.database.PatientSupervise;
import com.curus.utils.CacheUtils;
import com.curus.utils.LogUtils;
import com.curus.utils.TimeUtils;
import com.curus.utils.constant.CommonConst;
import com.curus.utils.constant.ErrorConst;
import com.curus.utils.constant.QuotaConst;
import com.curus.utils.constant.StatusConst;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by stupid-coder on 6/7/16.
 */
public class SBdSugarAddService {
    private Log logger = LogFactory.getLog(SBdSugarAddService.class);

    private JSONObject request;
    private ErrorData errorData;
    private CurusDriver driver;

    public SBdSugarAddService(JSONObject request,CurusDriver driver) {
        this.driver = driver;
        this.request = request;
    }

    private ErrorData validate() {
        if ( !request.containsKey("token") ) {
            errorData = new ErrorData(ErrorConst.IDX_FORM_ERROR,"token");
        } else if ( !request.containsKey("patient_id") ) {
            errorData = new ErrorData(ErrorConst.IDX_FORM_ERROR,"patient_id");
        } else if ( !request.containsKey("diet") && !request.containsKey("activity") ) {
            errorData = new ErrorData(ErrorConst.IDX_FORM_ERROR,"diet-activity");
        }

        if ( errorData != null )
            logger.warn(LogUtils.Msg(errorData,request));

        return errorData;
    }

    private ErrorData add() {
        Account account = (Account)CacheUtils.getObject4Cache(request.getString("token"));
        if ( account == null ) {
            errorData = new ErrorData(ErrorConst.IDX_TOKENEXPIRED_ERROR);
        } else {
            PatientSupervise supervise = new PatientSupervise();
            supervise.setAccount_id(account.getId());
            supervise.setPatient_id(request.getLong("patient_id"));
            supervise.setQuota_cat_id(QuotaConst.QUOTA_BS_ID);
            supervise.setCreate_time(TimeUtils.getTimestamp());

            JSONObject policy = new JSONObject();
            if ( request.containsKey("activity") ) policy.put("activity",request.getDouble("activity"));
            if ( request.containsKey("diet") ) policy.put("diet",request.getDouble("diet"));

            supervise.setPolicy(policy.toJSONString());
            supervise.setLast(CommonConst.TRUE);
            driver.patientSuperviseDao.insert(supervise);
        }
        return errorData;
    }

    public ResponseBase process() {
        if ( validate() == null && add() == null ) {
            logger.info(LogUtils.Msg("Success to add Supervise",request,null));
            return new ResponseBase(StatusConst.OK,null);
        } else {
            logger.warn(LogUtils.Msg("Failure to add Supervise",request,errorData));
            return new ResponseBase(StatusConst.ERROR,errorData);
        }
    }
}
