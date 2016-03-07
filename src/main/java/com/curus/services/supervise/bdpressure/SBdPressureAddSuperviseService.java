package com.curus.services.supervise.bdpressure;

import com.curus.dao.CurusDriver;
import com.curus.httpio.request.supervise.bdpressure.SBdPressureAddSuperviseRequest;
import com.curus.httpio.response.ErrorData;
import com.curus.httpio.response.ResponseBase;
import com.curus.model.database.Account;
import com.curus.utils.CacheUtils;
import com.curus.utils.LogUtils;
import com.curus.utils.constant.ErrorConst;
import com.curus.utils.constant.StatusConst;
import com.curus.utils.service.supervise.bdpressure.SBdPressureServiseUtils;
import com.curus.utils.validate.ValueValidate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by stupid-coder on 7/3/16.
 */
public class SBdPressureAddSuperviseService {
    private Log logger = LogFactory.getLog(SBdPressureAddSuperviseService.class);

    private CurusDriver driver;
    private SBdPressureAddSuperviseRequest request;
    private ErrorData errorData;

    public SBdPressureAddSuperviseService(SBdPressureAddSuperviseRequest request, CurusDriver driver) {
        this.driver = driver;
        this.request = request;
        this.errorData = null;
    }

    private ErrorData validate() {

        if ( (errorData = ValueValidate.valueExistValidate(request.getToken(),"token")) != null ) {
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (errorData = ValueValidate.valueExistValidate(request.getPatient_id(),"patient_id")) != null ) {
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (errorData = ValueValidate.valueExistValidate(request.getLossweight(),"lossweight")) != null ) {
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (errorData = ValueValidate.valueExistValidate(request.getDihealthscore(),"dihealthscore")) != null ) {
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (errorData = ValueValidate.valueExistValidate(request.getPhyactivity(),"phyactivity")) != null ) {
            logger.warn(LogUtils.Msg(errorData,request));
        }
        return errorData;
    }

    private ErrorData add() {
        Account account;

        if ( (account = (Account) CacheUtils.getObject4Cache(request.getToken())) == null ) {
            errorData = new ErrorData(ErrorConst.IDX_TOKENEXPIRED_ERROR);
            logger.warn(LogUtils.Msg(errorData,request));
        } else {
            if ( SBdPressureServiseUtils.AddSupervise(driver,account.getId(),request) != 1 ) {
                errorData = new ErrorData(ErrorConst.IDX_MYSQL_ERROR);
                logger.warn(LogUtils.Msg(errorData,request));
            }
        }

        return errorData;
    }

    public ResponseBase process() {
        if ( validate() == null && add() == null ) {
            logger.info(LogUtils.Msg("Success to Add BdPressure Supervise",request));
            return new ResponseBase(StatusConst.OK,null);
        } else {
            logger.warn(LogUtils.Msg("Failure to Add BdPressure Supervise",errorData,request));
            return new ResponseBase(StatusConst.ERROR,errorData);
        }
    }

}
