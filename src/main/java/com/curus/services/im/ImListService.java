package com.curus.services.im;

import com.curus.dao.CurusDriver;
import com.curus.httpio.request.im.ImListRequest;
import com.curus.httpio.response.ErrorData;
import com.curus.httpio.response.ResponseBase;
import com.curus.httpio.response.im.ImListResponseData;
import com.curus.model.database.Account;
import com.curus.model.database.AccountPatient;
import com.curus.model.database.Patient;
import com.curus.utils.CacheUtils;
import com.curus.utils.LogUtils;
import com.curus.utils.TypeUtils;
import com.curus.utils.constant.CommonConst;
import com.curus.utils.constant.ErrorConst;
import com.curus.utils.constant.StatusConst;
import com.curus.utils.service.account.AccountPatientServiceUtils;
import com.curus.utils.service.patient.PatientServiceUtils;
import com.curus.utils.validate.ValueValidate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by stupid-coder on 5/21/16.
 */
public class ImListService {

    private Log logger = LogFactory.getLog(ImListService.class);

    private CurusDriver driver;
    private ImListRequest request;
    private ImListResponseData responseData;
    private ErrorData errorData;

    public ImListService(ImListRequest request, CurusDriver driver) {
        this.request = request;
        this.driver = driver;
        this.responseData = null;
        this.errorData = null;
    }

    private ErrorData validate() {
        if ( (errorData = ValueValidate.valueExistValidate(request.getToken(),"token")) != null)
            logger.warn(LogUtils.Msg(errorData, request));
        return errorData;
    }

    private ErrorData listIm() {
        Account account;
        if ( (account = (Account) CacheUtils.getObject4Cache(request.getToken())) == null ) {
            errorData = new ErrorData(ErrorConst.IDX_TOKENEXPIRED_ERROR);
            logger.warn(LogUtils.Msg(errorData,request));
        } else {
            responseData = new ImListResponseData();
            List<ImListResponseData.ImListItem> contactsList = new ArrayList<ImListResponseData.ImListItem>();
            Set<String> idNumberSet = new HashSet<String>();
            Long selfId = null;
            {
                List<AccountPatient> accountPatientList = AccountPatientServiceUtils.selectValidate(driver,account.getId());
                for (AccountPatient accountPatient : accountPatientList) {
                    Patient patient = PatientServiceUtils.select(driver,accountPatient.getPatient_id());
                    if ( patient != null && driver.accountDao.checkByIdNumber(patient.getId_number()) ) {
                        if ( accountPatient.getIs_self().compareTo(CommonConst.TRUE) == 0 )
                            selfId = patient.getId();
                        contactsList.add(responseData.new ImListItem(patient.getName(),accountPatient.getIs_self(),patient.getId_number()));
                        idNumberSet.add(patient.getId_number());
                    }
                }
            }

            {
                List<AccountPatient> accountPatientList = AccountPatientServiceUtils.selectByPatientId(driver,selfId);
                for (AccountPatient accountPatient : accountPatientList) {
                    Account account1 = driver.accountDao.select(TypeUtils.getWhereHashMap("id",accountPatient.getAccount_id()));
                    if (account1 != null && !idNumberSet.contains(account1.getId_number())) {
                        contactsList.add(responseData.new ImListItem(account1.getName(),accountPatient.getIs_self(),account1.getId_number()));
                        idNumberSet.add(account1.getId_number());
                    }
                }
            }

            responseData.setContacts(contactsList);
        }

        if ( errorData == null )
            request.setToken(account.toString());
        return errorData;
    }

    public ResponseBase process() {
        if ( validate() == null && listIm() == null ) {
            logger.info(LogUtils.Msg("Success to List Im",request, responseData));
            return new ResponseBase(StatusConst.OK, responseData);
        } else {
            logger.info(LogUtils.Msg("Failure to List Im",request,errorData));
            return new ResponseBase(StatusConst.ERROR, errorData);
        }
    }


}
