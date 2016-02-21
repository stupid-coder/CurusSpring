package com.curus.services.patient;

import com.curus.dao.CurusDriver;
import com.curus.httpio.request.patient.PatientPushConfigRequest;
import com.curus.httpio.response.ErrorData;
import com.curus.httpio.response.ResponseBase;
import com.curus.model.database.Patient;
import com.curus.utils.LogUtils;
import com.curus.utils.TypeUtils;
import com.curus.utils.constant.ErrorConst;
import com.curus.utils.constant.StatusConst;
import com.curus.utils.validate.ValueValidate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by stupid-coder on 28/1/16.
 */
public class PatientPushConfigService {

    private Log logger = LogFactory.getLog(PatientPushConfigService.class);

    private PatientPushConfigRequest request;
    private CurusDriver driver;
    private ErrorData errorData;

    public PatientPushConfigService(PatientPushConfigRequest request, CurusDriver driver) {
        this.request = request;
        this.driver = driver;
        this.errorData = null;
    }

    private ErrorData validate() {
        if ( (errorData = ValueValidate.valueExistValidate(request.getToken(),"token'")) != null) {
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (errorData = ValueValidate.valueExistValidate(request.getPatient_id(),"patient_id")) != null) {
            logger.warn(LogUtils.Msg(errorData,request));
        }
        return errorData;
    }

    private ErrorData pushConfig() {
        Patient patient;
        if ( (patient = driver.patientDao.select(TypeUtils.getWhereHashMap("id",request.getPatient_id()))) == null) {
            errorData = new ErrorData(ErrorConst.IDX_PATIENTNOTEXIST_ERROR);
            logger.warn(LogUtils.Msg(errorData,request));
        } else try {
            patient.setCan_app_push(Integer.parseInt(request.getCan_app_push()));
            patient.setCan_email_push(Integer.parseInt(request.getCan_email_push()));
            patient.setCan_phone_push(Integer.parseInt(request.getCan_phone_push()));
            patient.setCan_qq_push(Integer.parseInt(request.getCan_qq_push()));
            patient.setCan_weixin_push(Integer.parseInt(request.getCan_weixin_push()));
            driver.patientDao.save(patient,"id");
        } catch (Exception e) {
            errorData = new ErrorData(ErrorConst.IDX_INVALIDPARM_ERROR,"All");
        }
        return errorData;
    }

    public ResponseBase process() {
        if ( validate() == null && pushConfig() == null) {
            logger.info(LogUtils.Msg("Success to Change the Push Configuration",request));
            return new ResponseBase(StatusConst.OK,null);
        } else return new ResponseBase(StatusConst.ERROR,errorData);
    }

}
