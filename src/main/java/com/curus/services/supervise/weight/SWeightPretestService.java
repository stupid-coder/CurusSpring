package com.curus.services.supervise.weight;

import com.curus.dao.CurusDriver;
import com.curus.httpio.request.supervise.weight.SWeightPretestRequest;
import com.curus.httpio.response.ErrorData;
import com.curus.httpio.response.ResponseBase;
import com.curus.httpio.response.supervise.weight.SWeightPretestResponseData;
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
public class SWeightPretestService {

    private Log logger = LogFactory.getLog(SWeightPretestService.class);

    private CurusDriver driver;
    private SWeightPretestRequest request;
    private SWeightPretestResponseData responseData;
    private ErrorData errorData;

    public SWeightPretestService(SWeightPretestRequest request, CurusDriver driver) {
        this.driver = driver;
        this.request = request;
        this.responseData = null;
        this.errorData = null;
    }

    private ErrorData validate() {
        if ( (errorData = ValueValidate.valueExistValidate(request.getToken(), "token")) != null ) {
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (errorData = ValueValidate.valueExistValidate(request.getPatient_id(), "patient_id")) != null ) {
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (errorData = ValueValidate.valueExistValidate(request.getWeight_loss(), "weight_loss")) != null ) {
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (errorData = ValueValidate.valueExistValidate(request.getDiet(), "diet")) != null &&
                request.getDiet().compareTo("[]") == 0 ) {
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (errorData = ValueValidate.valueExistValidate(request.getActivity(),"activity")) != null ) {
            logger.warn(LogUtils.Msg(errorData,request));
        }
        return errorData;
    }

    private ErrorData pretestWeight() {
        Account account;
        if ( (account = (Account)CacheUtils.getObject4Cache(request.getToken())) == null ) {
            errorData = new ErrorData(ErrorConst.IDX_TOKENEXPIRED_ERROR);
            logger.warn(LogUtils.Msg(errorData,request));
        } else {
            responseData = new SWeightPretestResponseData();
            SWeightSerivceUtils.Pretest(driver, account.getId(),request,responseData);
            if ( responseData.getWtloss() < request.getWeight_loss() ) responseData.setEvaluation("累计减重不能达到预期目标，请调整。");
            else if ( responseData.getEvaluation() == null ) responseData.setEvaluation("恭喜，减重计划可行！请发布计划，系统会给予必要提醒和评估，也会得到其他管理者的帮助和督促。");
        }
        return errorData;
    }

    public ResponseBase process() {
        if ( validate() == null && pretestWeight() == null ) {
            logger.info(LogUtils.Msg("Success Pretest Weight Supervice",request,responseData));
            return new ResponseBase(StatusConst.OK,responseData);
        } else {
            logger.warn(LogUtils.Msg("Failure Pretest Weight Supervice",request,errorData));
            return new ResponseBase(StatusConst.ERROR,errorData);
        }
    }
}
