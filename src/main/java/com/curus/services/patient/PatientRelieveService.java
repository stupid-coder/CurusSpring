package com.curus.services.patient;

import com.curus.dao.CurusDriver;
import com.curus.httpio.request.patient.PatientRelieveRequest;
import com.curus.httpio.response.ErrorData;
import com.curus.httpio.response.ResponseBase;
import com.curus.model.database.Account;
import com.curus.model.database.AccountPatient;
import com.curus.utils.CacheUtils;
import com.curus.utils.LogUtils;
import com.curus.utils.TypeUtils;
import com.curus.utils.constant.ErrorConst;
import com.curus.utils.constant.StatusConst;
import com.curus.utils.validate.ValueValidate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by stupid-coder on 28/1/16.
 */
public class PatientRelieveService {

    private Log logger = LogFactory.getLog(PatientRelieveService.class);

    private PatientRelieveRequest request;
    private CurusDriver driver;
    private ErrorData errorData;

    public PatientRelieveService(PatientRelieveRequest request, CurusDriver driver) {
        this.request = request;
        this.driver = driver;
        this.errorData = null;
    }

    private ErrorData validate() {

        if ((errorData = ValueValidate.valueExistValidate(request.getToken(), "token")) != null) {
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (errorData = ValueValidate.valueExistValidate(request.getPatient_id(),"patient_id")) != null) {
            logger.warn(LogUtils.Msg(errorData,request));
        }
        return errorData;
    }

    private ErrorData relievePatient() {
        Account account;
        AccountPatient accountPatient;
        if ( (account = (Account) CacheUtils.getObject4Cache(request.getToken())) == null ) {
            errorData = new ErrorData(ErrorConst.IDX_TOKENEXPIRED_ERROR);
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( driver.accountPatientDao.delete(TypeUtils.getWhereHashMap("account_id",account.getId(),"patient_id",request.getPatient_id())) == 0) {
            errorData = new ErrorData(ErrorConst.IDX_PATIENTNOTEXIST_ERROR);
            logger.warn(LogUtils.Msg(errorData,request));
        }
        return errorData;
    }

    public ResponseBase process() {
        if ( validate() == null && relievePatient() == null) {
            logger.info(LogUtils.Msg("Success to Relieve Patient",request));
            return new ResponseBase(StatusConst.OK,null);
        } else return new ResponseBase(StatusConst.ERROR,null);
    }

}
