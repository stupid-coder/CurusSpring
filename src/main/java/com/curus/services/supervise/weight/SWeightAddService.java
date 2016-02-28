package com.curus.services.supervise.weight;

import com.curus.dao.CurusDriver;
import com.curus.httpio.request.supervise.weight.SWeightAddRequest;
import com.curus.httpio.response.ErrorData;
import com.curus.httpio.response.ResponseBase;
import com.curus.model.database.Account;
import com.curus.utils.CacheUtils;
import com.curus.utils.LogUtils;
import com.curus.utils.constant.ErrorConst;
import com.curus.utils.constant.StatusConst;
import com.curus.utils.service.supervise.weight.SWeightSerivceUtils;
import com.curus.utils.validate.ValueValidate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by stupid-coder on 27/2/16.
 */
public class SWeightAddService {

    private Log logger = LogFactory.getLog(SWeightAddService.class);

    private CurusDriver driver;
    private SWeightAddRequest request;
    private ErrorData errorData;

    public SWeightAddService(SWeightAddRequest request, CurusDriver driver) {
        this.driver = driver;
        this.request = request;
        this.errorData = null;
    }

    private ErrorData validate() {
        if ( (errorData = ValueValidate.valueExistValidate(request.getToken(),"token")) != null ) {
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (errorData = ValueValidate.valueExistValidate(request.getPatient_id(),"patient_id")) != null ) {
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (errorData = ValueValidate.valueExistValidate(request.getLunch(),"lunch")) != null ) {
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (errorData = ValueValidate.valueExistValidate(request.getDinner(),"dinner")) != null ) {
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (errorData = ValueValidate.valueExistValidate(request.getSnachs(),"snacks")) != null ) {
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (errorData = ValueValidate.valueExistValidate(request.getFatink(),"fatink")) != null ) {
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (errorData = ValueValidate.valueExistValidate(request.getSports(),"sports")) != null ) {
            logger.warn(LogUtils.Msg(errorData,request));
        }
        return errorData;
    }

    private ErrorData addWeightSupervise() {
        Account account;
        if ( (account = (Account) CacheUtils.getObject4Cache(request.getToken())) == null ) {
            errorData = new ErrorData(ErrorConst.IDX_TOKENEXPIRED_ERROR);
            logger.info(LogUtils.Msg(errorData,request));
        } else {
            SWeightSerivceUtils.AddSupervise(driver, account.getId(), request);
        }
        return errorData;
    }

    public ResponseBase process() {
        if ( validate() != null && addWeightSupervise() != null ) {
            logger.info(LogUtils.Msg("Success To Add Weight Supervice",request,null));
            return new ResponseBase(StatusConst.OK,null);
        } else {
            logger.warn(LogUtils.Msg("Failure to Add Weight Supervice",request,errorData));
            return new ResponseBase(StatusConst.ERROR,errorData);
        }
    }
}
