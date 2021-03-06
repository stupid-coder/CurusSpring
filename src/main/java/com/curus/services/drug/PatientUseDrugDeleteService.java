package com.curus.services.drug;

import com.curus.dao.CurusDriver;
import com.curus.httpio.request.drug.PatientUseDrugDeleteRequest;
import com.curus.httpio.response.ErrorData;
import com.curus.httpio.response.ResponseBase;
import com.curus.model.database.Account;
import com.curus.model.database.PatientUseDrug;
import com.curus.utils.CacheUtils;
import com.curus.utils.LogUtils;
import com.curus.utils.constant.CommonConst;
import com.curus.utils.constant.ErrorConst;
import com.curus.utils.constant.StatusConst;
import com.curus.utils.service.account.AccountPatientServiceUtils;
import com.curus.utils.validate.ValueValidate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by stupid-coder on 6/3/16.
 */
public class PatientUseDrugDeleteService {
    private Log logger = LogFactory.getLog(PatientUseDrugDeleteService.class);

    private PatientUseDrugDeleteRequest request;
    private CurusDriver driver;
    private ErrorData errorData;

    public PatientUseDrugDeleteService(PatientUseDrugDeleteRequest request, CurusDriver driver) {
        this.request = request;
        this.driver = driver;
        this.errorData = null;
    }

    private ErrorData validate() {
        if ( (errorData = ValueValidate.valueExistValidate(request.getDrug_id(),"drug_id")) != null ||
                (errorData = ValueValidate.valueExistValidate(request.getPatient_id(),"patient_id")) != null ||
                (errorData = ValueValidate.valueExistValidate(request.getToken(),"token")) != null )
            logger.warn(LogUtils.Msg(errorData,request));
        return errorData;
    }

    private ErrorData delete() {
        Account account = (Account) CacheUtils.getObject4Cache(request.getToken());
        if ( account == null ) {
            errorData = new ErrorData(ErrorConst.IDX_TOKENEXPIRED_ERROR);
        } else if ( !AccountPatientServiceUtils.checkSuper(driver, account.getId(), request.getPatient_id()) ) {
            errorData = new ErrorData(ErrorConst.IDX_NOPERMISSION_ERROR);
        } else {
            PatientUseDrug patientUseDrug = driver.patientUseDrugDao.selectDrug(request.getPatient_id(), request.getDrug_id(), CommonConst.TRUE);
            if ( patientUseDrug != null ) {
                patientUseDrug.setLast(CommonConst.FALSE);
                driver.patientUseDrugDao.update(patientUseDrug,"id");
            }
        }
        return errorData;
    }

    public ResponseBase process() {
        if ( validate() == null && delete() == null ) {
            logger.info(LogUtils.Msg("Success to Delete Patient Use Drug", request, null));
            return new ResponseBase(StatusConst.OK,null);
        } else {
            logger.warn(LogUtils.Msg("Failure to Delete Patient Use Drug",request,errorData));
            return new ResponseBase(StatusConst.ERROR,errorData);
        }
    }
}
