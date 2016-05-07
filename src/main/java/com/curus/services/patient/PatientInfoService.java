package com.curus.services.patient;

import com.curus.dao.CurusDriver;
import com.curus.httpio.request.patient.PatientInfoRequest;
import com.curus.httpio.response.ErrorData;
import com.curus.httpio.response.ResponseBase;
import com.curus.httpio.response.patient.PatientInfoResponseData;
import com.curus.model.database.*;
import com.curus.utils.*;
import com.curus.utils.constant.*;
import com.curus.utils.service.account.AccountPatientServiceUtils;
import com.curus.utils.service.patient.PatientServiceUtils;
import com.curus.utils.service.quota.QuotaServiceUtils;
import com.curus.utils.validate.ValueValidate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by stupid-coder on 7/5/16.
 */
public class PatientInfoService {

    private Log logger = LogFactory.getLog(PatientInfoService.class);

    private PatientInfoRequest request;
    private PatientInfoResponseData responseData;
    private CurusDriver driver;
    private ErrorData errorData;

    public PatientInfoService(PatientInfoRequest request, CurusDriver driver) {
        this.request = request;
        this.driver = driver;
        this.responseData = null;
        this.errorData = null;
    }

    private ErrorData validate() {
        if ( (errorData = ValueValidate.valueExistValidate(request.getToken(),"token")) != null ) {
            logger.warn(LogUtils.Msg(errorData, request));
        } else if ( (errorData = ValueValidate.valueExistValidate(request.getPatient_id(),"patient_id")) != null ) {
            logger.warn(LogUtils.Msg(errorData, request));
        }
        return errorData;
    }

    private ErrorData info() {
        Account account;
        Patient patient;
        AccountPatient accountPatient;
        Quota height;

        if ( ( account = (Account) CacheUtils.getObject4Cache(request.getToken())) == null) {
            errorData = new ErrorData(ErrorConst.IDX_TOKENEXPIRED_ERROR);
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (patient = PatientServiceUtils.select(driver,request.getPatient_id())) == null ) {
            errorData = new ErrorData(ErrorConst.IDX_PATIENTNOTEXIST_ERROR);
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (accountPatient = AccountPatientServiceUtils.select(driver,account.getId(),request.getPatient_id())) == null ) {
            errorData = new ErrorData(ErrorConst.IDX_PATIENTNOTEXIST_ERROR);
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (height = QuotaServiceUtils.getLastQuota(driver, account.getId(), patient.getId(), QuotaConst.QUOTA_HEIGHT_ID)) == null ) {
            errorData = new ErrorData(ErrorConst.IDX_PATIENTNOTEXIST_ERROR);
            logger.warn(LogUtils.Msg(errorData,request));
        } else {
            Boolean patient_post = false;
            Boolean accountpatient_post = false;

            if ( request.getName() != null ) { patient.setName(request.getName()); patient_post = true; }
            if ( request.getPhone() != null ) { patient.setPhone(request.getPhone()); patient_post = true; }
            if ( request.getAddress() != null ) { patient.setAddress(request.getAddress()); patient_post = true; }



            if ( request.getAppellation() != null ) {
                accountPatient.setAppellation_id(AppellationUtils.getAppellationId(request.getAppellation())); accountpatient_post = true;
            }

            if ( request.getRole() != null ) {
                if ( request.getRole().compareTo(RoleConst.ROLE_COMMON) == 0 )
                    accountPatient.setRole_id(RoleUtils.getRoleId(request.getRole()));
                else { // get super permission
                    if (AccountPatientServiceUtils.selectSuper(driver,request.getPatient_id()) == null) { // no super OK
                        accountPatient.setRole_id(RoleUtils.getRoleId(request.getRole()));
                    } else {
                        errorData = new ErrorData(ErrorConst.IDX_NOPERMISSION_ERROR);
                    }
                }
                accountpatient_post = true;
            }

            if ( errorData != null ) return errorData;

            // update
            if ( patient_post == true ) driver.patientDao.save(patient,"id");
            if ( accountpatient_post == true ) driver.accountPatientDao.save(accountPatient,"id");
            if ( request.getHeight() != null ) {
                driver.quotaDao.insert(account.getId(), request.getPatient_id(), QuotaConst.QUOTA_HEIGHT_ID, null, null, String.format("{\"height\":%s}", request.getHeight()));
            } else if ( patient_post == false && accountpatient_post == false )
                responseData = new PatientInfoResponseData(patient,accountPatient,height);
        }
        return errorData;
    }


    public ResponseBase process() {
        if ( validate() == null && info() == null ) {
            logger.info(LogUtils.Msg("Success to Info Patient",request,responseData));
            return new ResponseBase(StatusConst.OK,responseData);
        } else {
            logger.warn(LogUtils.Msg("Failure to Info Patient",request,errorData));
            return new ResponseBase(StatusConst.ERROR, errorData);
        }
    }
}
