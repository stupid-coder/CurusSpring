package com.curus.services.patient;

import com.curus.dao.CurusDriver;
import com.curus.httpio.request.patient.PatientAddRequest;
import com.curus.httpio.response.ErrorData;
import com.curus.httpio.response.ResponseBase;
import com.curus.httpio.response.patient.PatientAddResponseData;
import com.curus.model.database.Account;
import com.curus.model.database.AccountPatient;
import com.curus.model.database.Patient;
import com.curus.model.database.Quota;
import com.curus.utils.*;
import com.curus.utils.constant.*;
import com.curus.utils.service.patient.PatientServiceUtils;
import com.curus.utils.service.quota.QuotaServiceUtils;
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
    private CurusDriver driver;
    private ErrorData errorData;

    public PatientAddService(PatientAddRequest request, CurusDriver driver) {
        this.request = request;
        this.responseData = new PatientAddResponseData();
        this.driver = driver;
        this.errorData = null;
    }

    private ErrorData validate() {
        if ((errorData = ValueValidate.valueExistValidate(request.getToken(), "token")) != null) {
            logger.warn(LogUtils.Msg(errorData, request));
        } else if ((errorData = ValueValidate.valueExistValidate(request.getCode(), "code")) != null) {
            logger.warn(LogUtils.Msg(errorData, request));
        } else if ((errorData = PhoneValidate.validatePhoneNumber(request.getPhone())) != null) {
            logger.warn(LogUtils.Msg(errorData, request));
        } else if ((errorData = ValueValidate.idValidation(request.getId_number(), "id_number")) != null) {
            logger.warn(LogUtils.Msg(errorData, request));
        } else if ((errorData = ValueValidate.valueExistValidate(request.getAppellation(), "appellation")) != null) {
            logger.warn(LogUtils.Msg(errorData, request));
        } else if ((errorData = ValueValidate.valueExistValidate(request.getName(), "name")) != null) {
            logger.warn(LogUtils.Msg(errorData, request));
        } else if ((errorData = ValueValidate.valueExistValidate(request.getGender(),"gender")) != null) {
            logger.warn(LogUtils.Msg(errorData, request));
        } else if ((errorData = ValueValidate.valueExistValidate(request.getBirth(),"brith")) != null) {
            logger.warn(LogUtils.Msg(errorData, request));
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
        AccountPatient accountPatient = null;
        if ( (account = (Account)CacheUtils.getObject4Cache(request.getToken())) == null) {
            errorData = new ErrorData(ErrorConst.IDX_TOKENEXPIRED_ERROR);
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( account.getIs_exp_user() == CommonConst.TRUE ) {
            errorData = new ErrorData(ErrorConst.IDX_ISEXPUSER);
            logger.warn(LogUtils.Msg(errorData,request));
        } else {
            patient = PatientServiceUtils.select(driver,request.getId_number());
            if ( (errorData = CodeValidate.validateAddPatientCode(request.getToken(),
                    patient == null ? request.getPhone() : patient.getPhone(),
                    request.getCode())) != null ) {
                logger.warn(LogUtils.Msg(errorData, request, patient));
            } else {
                if (patient==null) {
                    patient = new Patient(request.getName(),Integer.parseInt(request.getGender()),
                            new Date(Long.parseLong(request.getBirth())),
                            request.getId_number(),request.getPhone(),request.getAddress(), TimeUtils.getTimestamp(),null,null,null,null);
                } else {
                    accountPatient = driver.accountPatientDao.select(TypeUtils.getWhereHashMap("account_id", account.getId(),
                            "patient_id", patient.getId()));
                    if (accountPatient.getIs_super_validate().compareTo(CommonConst.TRUE) == 0 ) {
                        patient.setName(request.getName());
                        patient.setGender(Integer.parseInt(request.getGender()));
                        patient.setBirth(new Date(Long.parseLong(request.getBirth())));
                        patient.setId_number(request.getId_number());
                        patient.setPhone(request.getPhone());
                    }
                }
                patient = PatientServiceUtils.AddPatient(driver, account, patient, accountPatient, request.getAppellation());
                responseData.setPatient_id(patient.getId());
                QuotaServiceUtils.addQuota(driver,account.getId(),patient.getId(),QuotaConst.QUOTA_WEIGHT,null,null,
                        QuotaServiceUtils.getKVJSON(QuotaConst.QUOTA_WEIGHT, request.getWeight()));

                QuotaServiceUtils.addQuota(driver,account.getId(),patient.getId(),QuotaConst.QUOTA_HEIGHT,null,null,
                        QuotaServiceUtils.getKVJSON(QuotaConst.QUOTA_HEIGHT, request.getHeight()));
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
