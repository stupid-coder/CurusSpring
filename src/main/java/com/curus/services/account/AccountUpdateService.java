package com.curus.services.account;

import com.curus.dao.CurusDriver;
import com.curus.httpio.request.account.AccountUpdateRequest;
import com.curus.httpio.response.ErrorData;
import com.curus.httpio.response.ResponseBase;
import com.curus.model.database.Account;
import com.curus.model.database.Patient;
import com.curus.utils.*;
import com.curus.utils.constant.*;
import com.curus.utils.service.patient.PatientServiceUtils;
import com.curus.utils.service.quota.QuotaServiceUtils;
import com.curus.utils.validate.ValueValidate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by stupid-coder on 24/1/16.
 */
public class AccountUpdateService {

    static private Log logger = LogFactory.getLog(AccountUpdateService.class);
    private AccountUpdateRequest request;
    private CurusDriver driver;
    private ErrorData errorData;
    public AccountUpdateService(AccountUpdateRequest request,CurusDriver driver) {
        this.request = request;
        this.errorData = null;
        this.driver = driver;
    }

    private ErrorData validate() {
        if ( (errorData = ValueValidate.valueExistValidate(request.getToken(), "token")) != null) {
            logger.warn(LogUtils.Msg(errorData, request));
        } else if ( (errorData = ValueValidate.valueExistValidate(request.getName(),"name")) !=null) {
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (errorData = ValueValidate.valueIntegerValidate(request.getGender(),"gender")) != null) {
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (errorData = ValueValidate.valueDateValidate(request.getBirth(),"birth")) != null) {
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (errorData = ValueValidate.idValidation(request.getId_number(),"id_number")) != null) {
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (errorData = ValueValidate.valueExistValidate(request.getAddress(),"address")) != null) {
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (errorData = ValueValidate.valueDoubleValidate(request.getWeight(),"weight")) != null) {
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (errorData = ValueValidate.valueDoubleValidate(request.getHeight(),"height")) != null) {
            logger.warn(LogUtils.Msg(errorData,request));
        }
        return errorData;
    }

    private ErrorData update() {
        Account account;
        Patient patient;
        if ( ( account = (Account) CacheUtils.getObject4Cache(request.getToken())) == null) {
            errorData = new ErrorData(ErrorConst.IDX_TOKENEXPIRED_ERROR);
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( driver.accountDao.select(TypeUtils.getWhereHashMap("id_number",request.getId_number())) != null ) {
            errorData = new ErrorData(ErrorConst.IDX_INVALIDID_ERROR);
            logger.warn(LogUtils.Msg(errorData,request));
        } else {
            if ( request.getName() != null ) account.setName(request.getName());
            if ( request.getGender() != null ) account.setGender(ValueValidate.genderize(Integer.parseInt(request.getGender())));
            if ( request.getBirth() != null ) account.setBirth( ValueValidate.dateFromTimestamp(request.getBirth()) );
            if ( request.getId_number() != null ) account.setId_number(request.getId_number());
            if ( request.getAddress() != null ) account.setAddress(request.getAddress());
            if ( request.getContact() != null ) account.setOther_contact(request.getContact());
            account.setIs_exp_user(0);
            driver.accountDao.update(account,"id");
            CacheUtils.putObject2Cache(request.getToken(),account);
            patient = PatientServiceUtils.select(driver,account.getId_number());

            if (patient == null) {
                patient = new Patient(account.getName(), account.getGender(), account.getBirth(),
                        account.getId_number(), account.getPhone(), account.getAddress(),
                        TimeUtils.getTimestamp(), null, null, null, account.getOther_contact());
            } else {
                patient.setName(account.getName()); patient.setGender(account.getGender()); patient.setBirth(account.getBirth());
                patient.setId_number(account.getId_number()); patient.setAddress(account.getAddress()); patient.setOther_contact(account.getOther_contact());
                driver.patientDao.save(patient,"id");
            }
            patient = PatientServiceUtils.AddPatient(driver,account, patient, null, AppellationConst.APPELLATION_SELF);

            QuotaServiceUtils.addWeightHeight(driver,account.getId(),patient.getId(),Double.parseDouble(request.getWeight()),Double.parseDouble(request.getHeight()));

        }
        return errorData;
    }

    public ResponseBase process() {
        if (validate() == null && update() == null) {
            logger.info(LogUtils.Msg("Success to Update",request,null));
            return new ResponseBase(StatusConst.OK,null);
        } else return new ResponseBase(StatusConst.ERROR,errorData);

    }

}
