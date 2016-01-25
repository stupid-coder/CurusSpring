package com.curus.services.patient;

import com.curus.dao.AccountPatientDao;
import com.curus.dao.PatientDao;
import com.curus.httpio.request.patient.PatientAddRequest;
import com.curus.httpio.response.ErrorData;
import com.curus.httpio.response.ResponseBase;
import com.curus.httpio.response.patient.PatientAddResponseData;
import com.curus.model.Account;
import com.curus.model.AccountPatient;
import com.curus.model.Patient;
import com.curus.utils.*;
import com.curus.utils.constant.ErrorConst;
import com.curus.utils.constant.LogicConst;
import com.curus.utils.constant.StatusConst;
import com.curus.utils.validate.CodeValidate;
import com.curus.utils.validate.PhoneValidate;
import com.curus.utils.validate.ValueValidate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Date;

/**
 * Created by stupid-coder on 25/1/16.
 */
public class PatientAddService {

    private Log logger = LogFactory.getLog(PatientAddService.class);

    private PatientAddRequest request;
    private PatientAddResponseData responseData;
    private PatientDao driver;
    private AccountPatientDao apdriver;
    private ErrorData errorData;

    public PatientAddService(PatientAddRequest request) {
        this.request = request;
        this.responseData = null;
        driver = (PatientDao)SpringContextUtils.getBean("patientDao");
        apdriver = (AccountPatientDao)SpringContextUtils.getBean("accountPatientDao");
        this.errorData = null;
    }

    private ErrorData validate() {
        if ( (errorData = ValueValidate.valueExistValidate(request.getToken(),"token")) != null) {
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (errorData = ValueValidate.valueExistValidate(request.getCode(),"code")) != null) {
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (errorData = PhoneValidate.validatePhoneNumber(request.getPhone())) != null) {
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (errorData = ValueValidate.idValidation(request.getId_number(),"id_number")) != null) {
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (errorData = ValueValidate.valueExistValidate(request.getAppellation(),"appelltaion")) != null) {
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (errorData = ValueValidate.valueExistValidate(request.getName(),"name")) != null) {
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (errorData = ValueValidate.valueExistValidate(request.getAddress(),"address")) != null) {
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (errorData = ValueValidate.valueExistValidate(request.getWeight(),"weight")) != null) {
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (errorData = ValueValidate.valueExistValidate(request.getHeight(),"height")) != null) {
            logger.warn(LogUtils.Msg(errorData,request));
        }
        return errorData;
    }

    private ErrorData addPatient() {
        Account account;
        Patient patient;
        if ( (account = (Account)CacheUtils.getObject4Cache(request.getToken())) == null) {
            errorData = new ErrorData(ErrorConst.IDX_TOKENEXPIRED_ERROR);
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( account.getIs_exp_user() != 0 ) {
            errorData = new ErrorData(ErrorConst.IDX_ISEXPUSER);
            logger.warn(LogUtils.Msg(errorData,request));
        } else {
            patient = driver.select(TypeUtils.getWhereHashMap("id_number",request.getId_number()));
            if ( (errorData = CodeValidate.validateAddPatientCode(request.getToken(),
                    patient !=null ? patient.getPhone() : request.getPhone(),
                    request.getCode())) != null ) {
                logger.warn(LogUtils.Msg(errorData,request,patient));
            } else if ( patient != null ) {
                if ( apdriver.select( TypeUtils.getWhereHashMap("account_id",account.getId(),
                        "patient_id",patient.getId(),
                        "is_super_valide",new Integer(1),
                        "is_patient_valid",new Integer(1))) == null) {
                    AccountPatient ap = apdriver.select( TypeUtils.getWhereHashMap("account_id",account.getId(),"patient_id",patient.getId()) );
                    if (ap == null) ap = new AccountPatient(account.getId(),patient.getId());
                    ap.setAppellation_id(0L); // TODO 关系 不需要用数据库
                    ap.setRole_id(LogicConst.MANAGER_COMMAND);
                    ap.setIs_self( account.getId() == patient.getId() ? 1 : 0);
                    ap.setIs_patient_validate(1);
                    ap.setIs_super_validate(0);
                    apdriver.save(ap);
                    // TODO send message to super manager
                } else logger.info(LogUtils.Msg("Already Manager",request,patient));
            } else { // patient == null
                patient = new Patient(request.getName(),Integer.parseInt(request.getGender()),new Date(Long.parseLong(request.getBirth())),request.getId_number(),request.getPhone(),request.getAddress(), TimeUtils.getTimestamp(),null,null,null,null);
                driver.save(patient);
            }
        }
        return errorData;
    }

    public ResponseBase process() {
        if ( validate() == null && addPatient() == null ) {
            logger.info(LogUtils.Msg("Success to Add Patient",request,responseData));
            return new ResponseBase(StatusConst.OK,responseData);
        } else return new ResponseBase(StatusConst.ERROR,errorData);
    }

}
