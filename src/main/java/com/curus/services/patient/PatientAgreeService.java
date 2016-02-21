package com.curus.services.patient;

import com.curus.dao.CurusDriver;
import com.curus.httpio.response.ErrorData;
import com.curus.httpio.response.ResponseBase;
import com.curus.model.database.AccountPatient;
import com.curus.utils.LogUtils;
import com.curus.utils.TypeUtils;
import com.curus.utils.constant.CommonConst;
import com.curus.utils.constant.ErrorConst;
import com.curus.utils.constant.StatusConst;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by stupid-coder on 21/2/16.
 */
public class PatientAgreeService {

    private Log logger = LogFactory.getLog(PatientAgreeService.class);

    private Long account_id;
    private Long patient_id;
    private CurusDriver driver;
    private ErrorData errorData;

    public PatientAgreeService(Long account_id,Long patient_id,CurusDriver driver) {
        this.account_id = account_id;
        this.patient_id = patient_id;
        this.driver = driver;
        this.errorData = null;
    }

    private ErrorData validate() {
        return errorData;
    }

    private ErrorData agree() {
        AccountPatient accountPatient = driver.accountPatientDao.select(TypeUtils.getWhereHashMap("account_id",account_id,"patient_id",patient_id));
        if ( accountPatient != null ) {
            accountPatient.setIs_super_validate(CommonConst.TRUE);
            driver.accountPatientDao.update(accountPatient, "id");
        } else {
            errorData = new ErrorData(ErrorConst.IDX_INVALIDPARM_ERROR,"aid or pid error");
            logger.warn(LogUtils.Msg("Failure to Get AccountPatient",account_id,patient_id));
        }
        return errorData;
    }

    public ResponseBase process() {

        if ( validate() == null && agree() == null ) {
            logger.info(LogUtils.Msg("Success to Patient Agree",account_id,patient_id));
            return new ResponseBase(StatusConst.OK,null);
        } else return new ResponseBase(StatusConst.ERROR,errorData);
    }
}
