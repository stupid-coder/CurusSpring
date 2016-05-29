package com.curus.services.drug;

import com.alibaba.fastjson.JSONObject;
import com.curus.dao.CurusDriver;
import com.curus.httpio.request.drug.DrugDirectionsGetRequest;
import com.curus.httpio.response.ErrorData;
import com.curus.httpio.response.ResponseBase;
import com.curus.model.database.DrugComp;
import com.curus.model.database.DrugInfo;
import com.curus.utils.LogUtils;
import com.curus.utils.TypeUtils;
import com.curus.utils.constant.ErrorConst;
import com.curus.utils.constant.StatusConst;
import com.curus.utils.service.drug.DrugServiceUtils;
import com.curus.utils.validate.ValueValidate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

/**
 * Created by stupid-coder on 5/29/16.
 */
public class DrugDirectionGetService {

    private Log logger = LogFactory.getLog(DrugDirectionGetService.class);

    private DrugDirectionsGetRequest request;
    private JSONObject responseData;
    private CurusDriver driver;
    private ErrorData errorData;

    public DrugDirectionGetService(DrugDirectionsGetRequest request, CurusDriver driver) {
        this.request = request;
        this.driver = driver;
        this.errorData = null;
    }

    private ErrorData validate() {
        if ( (errorData = ValueValidate.valueExistValidate(request.getDrug_id(),"drug_id")) != null )
            logger.warn(LogUtils.Msg(errorData,request));
        return errorData;
    }

    private ErrorData getDirection() {
        DrugInfo drugInfo = driver.drugInfoDao.select(request.getDrug_id());

        if ( drugInfo == null ) {
            errorData = new ErrorData(ErrorConst.IDX_FORM_ERROR,"durg_id");
            logger.warn(LogUtils.Msg(errorData, request));
        } else {
            responseData = new JSONObject();
            responseData.put("direction", DrugServiceUtils.DrugDirections(driver,drugInfo));
        }
        return errorData;
    }

    public ResponseBase process() {
        if ( validate() == null && getDirection() == null ) {
            logger.info(LogUtils.Msg("Success to get drug direction", request, responseData));
            return new ResponseBase(StatusConst.OK,responseData);
        } else {
            logger.warn(LogUtils.Msg("Failure to get drug direction", errorData, request));
            return new ResponseBase(StatusConst.ERROR,errorData);
        }
    }

}
