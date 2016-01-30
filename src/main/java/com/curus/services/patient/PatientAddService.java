package com.curus.services.patient;

import com.curus.dao.CurusDriver;
import com.curus.httpio.request.patient.PatientAddRequest;
import com.curus.httpio.response.ErrorData;
import com.curus.httpio.response.ResponseBase;
import com.curus.httpio.response.patient.PatientAddResponseData;
import com.curus.model.Account;
import com.curus.model.AccountPatient;
import com.curus.model.Message;
import com.curus.model.Patient;
import com.curus.utils.*;
import com.curus.utils.constant.*;
import com.curus.utils.validate.CodeValidate;
import com.curus.utils.validate.PhoneValidate;
import com.curus.utils.validate.ValueValidate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Date;
import java.util.HashMap;

/**
 * Created by stupid-coder on 25/1/16.
 */
public class PatientAddService {

    private Log logger = LogFactory.getLog(PatientAddService.class);

    private PatientAddRequest request;
    private PatientAddResponseData responseData;
    private CurusDriver driver;
    private ErrorData errorData;

    public PatientAddService(PatientAddRequest request, CurusDriver driver) {
        this.request = request;
        this.responseData = null;
        this.driver = driver;
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
            patient = driver.patientDao.select(TypeUtils.getWhereHashMap("id_number", request.getId_number()));
            if ( (errorData = CodeValidate.validateAddPatientCode(request.getToken(),
                    patient !=null ? patient.getPhone() : request.getPhone(),
                    request.getCode())) != null ) {
                logger.warn(LogUtils.Msg(errorData,request,patient));
            } else if ( patient != null ) { // Patient Already Exists
                AccountPatient ap = driver.accountPatientDao.select( TypeUtils.getWhereHashMap("account_id",account.getId(),"patient_id",patient.getId()) );
                if (ap == null) ap = new AccountPatient(account.getId(),patient.getId());
                if ( ap.getIs_patient_validate() == 1L && ap.getIs_super_validate() == 1L ) {
                    logger.info(LogUtils.Msg("Already Manager",request,patient));
                } else {
                    ap.setAppellation_id(AppellationUtils.getAppellationId(request.getAppellation()));
                    ap.setRole_id(RoleConst.ROLE_IDS.get("common"));
                    ap.setIs_self(account.getId() == patient.getId() ? 1 : 0);
                    ap.setIs_patient_validate(1);
                    ap.setIs_super_validate(0);
                }
                driver.accountPatientDao.save(ap);

                // Send Msg To Supper
                if ( (ap = driver.accountPatientDao.select( TypeUtils.getWhereHashMap("patient_id",patient.getId(),
                        "is_super_validate", CommonConst.TRUE,"is_patient_validate",CommonConst.TRUE,
                        "role_id",RoleConst.ROLE_IDS.get("supper")))) != null) {
                    driver.messageDao.insert(new Message(CommonConst.SYSNAME,ap.getAccount_id(), MessageConst.ADD_PATIENT_TITLE,
                            String.format(MessageConst.ADD_PATIENT_CONTENT,request.getName(),patient.getName(),patient.getName()),
                            String.format(MessageConst.ADD_PATIENT_AGREE_LINK,patient.getId(),account.getId())));
                }

            } else { // Patient Not Exists
                patient = new Patient(request.getName(),Integer.parseInt(request.getGender()),
                        new Date(Long.parseLong(request.getBirth())),request.getId_number(),request.getPhone(),request.getAddress(), TimeUtils.getTimestamp(),null,null,null,null);
                driver.patientDao.save(patient);
                patient = driver.patientDao.select(TypeUtils.getWhereHashMap("id_number",request.getId_number()));

                AccountPatient ap = new AccountPatient(account.getId(),patient.getId(),
                        account.getId_number()==patient.getId_number()?CommonConst.TRUE:CommonConst.FALSE, // is self
                        CommonConst.TRUE, // is supper
                        CommonConst.TRUE,
                        RoleConst.ROLE_IDS.get("super"), // role
                        AppellationUtils.getAppellationId(request.getAppellation()));
                driver.accountPatientDao.save(ap);
                driver.patientIssueDao.initPatientIssue(patient.getId());
                //TOOD quotas
                driver.quotaDao.initQuota(account.getId(),patient.getId(),
                        new HashMap<String,String>() {{put("height",request.getHeight());put("weight",request.getWeight());}});
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
