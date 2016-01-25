package com.curus.services.patient;

import com.curus.dao.AccountPatientDao;
import com.curus.dao.PatientDao;
import com.curus.httpio.request.patient.PatientPreAddRequest;
import com.curus.httpio.response.ErrorData;
import com.curus.httpio.response.ResponseBase;
import com.curus.httpio.response.patient.PatientPreAddResponseData;
import com.curus.model.Account;
import com.curus.model.AccountPatient;
import com.curus.model.Patient;
import com.curus.services.common.SendCodeService;
import com.curus.utils.CacheUtils;
import com.curus.utils.LogUtils;
import com.curus.utils.SpringContextUtils;
import com.curus.utils.TypeUtils;
import com.curus.utils.constant.CateConst;
import com.curus.utils.constant.ErrorConst;
import com.curus.utils.constant.StatusConst;
import com.curus.utils.validate.PhoneValidate;
import com.curus.utils.validate.ValueValidate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by stupid-coder on 25/1/16.
 */
public class PatientPreAddService {

    private Log logger = LogFactory.getLog(PatientPreAddService.class);

    private PatientPreAddRequest request;
    private PatientPreAddResponseData responseData;
    private PatientDao driver;
    private AccountPatientDao apdriver;
    private ErrorData errorData;


    public PatientPreAddService(PatientPreAddRequest request) {
        this.request = request;
        this.responseData = new PatientPreAddResponseData();
        this.driver = (PatientDao) SpringContextUtils.getBean("patientDao");
        this.apdriver = (AccountPatientDao) SpringContextUtils.getBean("accountPatientDao");
        this.errorData = null;
    }

    private ErrorData validate() {
        if ( (errorData = ValueValidate.valueExistValidate(request,"token")) != null) {
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (errorData = ValueValidate.idValidation(request.getId_number(),"id_number")) != null) {
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (errorData = PhoneValidate.validatePhoneNumber(request.getPhone())) != null) {
            logger.warn(LogUtils.Msg(errorData,request));
        }
        return null;
    }

    private ErrorData preAdd() {
        Account account;
        Patient patient;
        AccountPatient accountPatient;
        if ( (account = (Account) CacheUtils.getObject4Cache(request.getToken())) == null) {
            errorData = new ErrorData(ErrorConst.IDX_TOKENEXPIRED_ERROR);
            logger.warn(LogUtils.Msg(errorData, request));
        } else if ( account.getIs_exp_user() == 0 ) {
            errorData = new ErrorData(ErrorConst.IDX_ISEXPUSER);
            logger.warn(LogUtils.Msg(errorData,request));
        } else {
            patient = driver.select(TypeUtils.getWhereHashMap("phone", request.getPhone()));
            if (patient == null) {
                responseData.setCode(SendCodeService.getCodeAndSave2Cache(driver, CateConst.ADD_PATIENT, request.getToken(), request.getPhone(), request.getId_number()));
                responseData.setIs_exist(0L);
            } else {
                responseData.setIs_exist(1L);
                if ((accountPatient = apdriver.select(TypeUtils.getWhereHashMap("account_id",account.getId(),"patient_id",patient.getId()))) != null) {
                    responseData.setUnder_manager(1L);
                } else {
                    responseData.setCode(SendCodeService.getCodeAndSave2Cache(driver,CateConst.ADD_PATIENT,request.getToken(),patient.getPhone(),request.getId_number()));
                    responseData.setUnder_manager(0L);
                    responseData.setPatient(patient);
                    responseData.setManagers(apdriver.selectAll(TypeUtils.getWhereHashMap("patient_id",patient.getId(),"is_patient_validate",new Integer(1))));
                }
            }
        }
        return null;
    }

    public ResponseBase process() {
        if ( validate() == null && preAdd() == null ) {
            logger.info(LogUtils.Msg("Success to PreAdd Patient",request,responseData));
            return new ResponseBase(StatusConst.OK,responseData);
        } else return new ResponseBase(StatusConst.ERROR,errorData);
    }

}
