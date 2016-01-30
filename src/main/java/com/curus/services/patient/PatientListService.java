package com.curus.services.patient;

import com.curus.dao.CurusDriver;
import com.curus.httpio.request.patient.PatientListRequest;
import com.curus.httpio.response.ErrorData;
import com.curus.httpio.response.ResponseBase;
import com.curus.httpio.response.patient.PatientListResponseData;
import com.curus.model.Account;
import com.curus.model.AccountPatient;
import com.curus.model.Patient;
import com.curus.utils.CacheUtils;
import com.curus.utils.LogUtils;
import com.curus.utils.TypeUtils;
import com.curus.utils.constant.CommonConst;
import com.curus.utils.constant.ErrorConst;
import com.curus.utils.constant.StatusConst;
import com.curus.utils.validate.ValueValidate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stupid-coder on 28/1/16.
 */
public class PatientListService {

    private Log logger = LogFactory.getLog(PatientListService.class);

    private PatientListRequest request;
    private PatientListResponseData responseData;
    private CurusDriver driver;
    private ErrorData errorData;

    public PatientListService(PatientListRequest request, CurusDriver driver) {
        this.request = request;
        this.driver =driver;
        this.responseData = null;
        this.errorData = null;
    }

    private ErrorData validate() {
        if ( (errorData = ValueValidate.valueExistValidate(request.getToken(),"token")) != null)
            logger.warn(LogUtils.Msg(errorData, request));
        return errorData;
    }

    private ErrorData listPatient() {
        Account account;
        if ( (account = (Account) CacheUtils.getObject4Cache(request.getToken())) == null) {
            errorData = new ErrorData(ErrorConst.IDX_TOKENEXPIRED_ERROR);
            logger.warn(LogUtils.Msg(errorData,request));
        } else {
            responseData = new PatientListResponseData();
            List<PatientListResponseData.PatientListInfo> patientList = new ArrayList<PatientListResponseData.PatientListInfo>();
            List<AccountPatient> accountPatientList = driver.accountPatientDao.selectAll(TypeUtils.getWhereHashMap("account_id",account.getId(),
                    "is_super_validate", CommonConst.TRUE,"is_patient_validate",CommonConst.TRUE));
            for ( AccountPatient accountPatient  : accountPatientList) {
                Patient patient = driver.patientDao.select(TypeUtils.getWhereHashMap("patient_id", accountPatient.getPatient_id()));
                patientList.add(responseData.new PatientListInfo(patient,accountPatient));
            }
            responseData.setPatients(patientList);
        }
        return errorData;
    }

    public ResponseBase process() {
        if ( validate() == null && listPatient() == null) {
            logger.info(LogUtils.Msg("Success to List Patient",request,responseData));
            return new ResponseBase(StatusConst.OK,responseData);
        } else return new ResponseBase(StatusConst.ERROR,errorData);
    }

}
