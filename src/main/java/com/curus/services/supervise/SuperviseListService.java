package com.curus.services.supervise;

import com.curus.dao.CurusDriver;
import com.curus.httpio.request.supervise.SuperviseListRequest;
import com.curus.httpio.response.ErrorData;
import com.curus.httpio.response.ResponseBase;
import com.curus.httpio.response.supervise.SuperviseListResponseData;
import com.curus.model.database.Account;
import com.curus.model.database.PatientSupervise;
import com.curus.utils.CacheUtils;
import com.curus.utils.LogUtils;
import com.curus.utils.QuotaUtils;
import com.curus.utils.constant.ErrorConst;
import com.curus.utils.constant.StatusConst;
import com.curus.utils.validate.ValueValidate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

/**
 * Created by stupid-coder on 13/4/16.
 */
public class SuperviseListService {
    private Log logger = LogFactory.getLog(SuperviseListService.class);

    private SuperviseListRequest request;
    private SuperviseListResponseData responseData;
    private CurusDriver driver;
    private ErrorData errorData;

    public SuperviseListService(SuperviseListRequest request, CurusDriver driver) {
        this.request = request;
        this.driver = driver;
        this.responseData = null;
        this.errorData = null;
    }

    private ErrorData validate() {
        if ( (errorData = ValueValidate.valueExistValidate(request.getToken(),"token")) != null)
            logger.warn(LogUtils.Msg(errorData, request));
        else if ( (errorData = ValueValidate.valueExistValidate(request.getPatient_id(),"patient_id")) != null)
            logger.warn(LogUtils.Msg(errorData, request));
        return errorData;
    }

    private ErrorData listSupervise() {
        Account account;
        if ((account = (Account) CacheUtils.getObject4Cache(request.getToken())) == null ) {
            errorData = new ErrorData(ErrorConst.IDX_TOKENEXPIRED_ERROR);
            logger.warn(LogUtils.Msg(errorData,request));
        } else {
            List<PatientSupervise> patientSuperviseList = driver.patientSuperviseDao.selectAllLastSupervise(account.getId(),request.getPatient_id());
            responseData = new SuperviseListResponseData();
            if (patientSuperviseList != null ) {
                for (PatientSupervise patientSupervise : patientSuperviseList) {
                    responseData.addQuotaList(QuotaUtils.getQuotaName(patientSupervise.getQuota_cat_id()));
                }
            }
        }
        return errorData;
    }

    public ResponseBase process() {
        if ( validate() == null && listSupervise() == null) {
            logger.info(LogUtils.Msg("Success to List Supervise",request,responseData));
            return new ResponseBase(StatusConst.OK, responseData);
        } else {
            logger.warn(LogUtils.Msg("Failure to List Supervise",request,errorData));
            return new ResponseBase(StatusConst.ERROR, errorData);
        }
    }
}
