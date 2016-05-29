package com.curus.services.drug;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.curus.dao.CurusDriver;
import com.curus.httpio.request.drug.PatientUseDrugListRequest;
import com.curus.httpio.response.ErrorData;
import com.curus.httpio.response.ResponseBase;
import com.curus.model.database.Account;
import com.curus.model.database.DrugInfo;
import com.curus.model.database.PatientUseDrug;
import com.curus.utils.CacheUtils;
import com.curus.utils.LogUtils;
import com.curus.utils.constant.CommonConst;
import com.curus.utils.constant.ErrorConst;
import com.curus.utils.constant.StatusConst;
import com.curus.utils.validate.ValueValidate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

/**
 * Created by stupid-coder on 5/29/16.
 */
public class PatientUseDrugListService {

    static private Log logger = LogFactory.getLog(PatientUseDrugListService.class);

    private PatientUseDrugListRequest request;
    private JSONObject responseData;
    private CurusDriver driver;
    private ErrorData errorData;

    public PatientUseDrugListService(PatientUseDrugListRequest request, CurusDriver driver) {
        this.request = request;
        this.driver = driver;
    }


    private ErrorData validate() {
        if ( (errorData = ValueValidate.valueExistValidate(request.getToken(), "token")) != null )
            logger.warn(LogUtils.Msg(errorData, request));
        else if ( (errorData = ValueValidate.valueExistValidate(request.getPatient_id(),"patient_id")) != null )
            logger.warn(LogUtils.Msg(errorData, request));
        return errorData;
    }

    private ErrorData list() {
        Account account = (Account) CacheUtils.getObject4Cache(request.getToken());
        if ( account == null ) {
            errorData = new ErrorData(ErrorConst.IDX_TOKENEXPIRED_ERROR);
        } else {
            responseData = new JSONObject();
            JSONArray drugs = new JSONArray();
            List<PatientUseDrug> patientUseDrugList = driver.patientUseDrugDao.selectAll(request.getPatient_id(), CommonConst.TRUE);
            if (patientUseDrugList != null) {
                for ( PatientUseDrug patientUseDrug : patientUseDrugList ) {
                    DrugInfo drugInfo = driver.drugInfoDao.select(patientUseDrug.getDrug_id());
                    if ( drugInfo == null ) continue;

                    JSONObject drug = new JSONObject();
                    drug.put("drug_id",drugInfo.getDrug_id());
                    drug.put("product_name",drugInfo.getProduct_name());
                    drug.put("use_type",drugInfo.getUse());
                    drug.put("use_policy",patientUseDrug.getUse_policy());
                    drug.put("min_once",drugInfo.getMin_once());
                    drug.put("change_time",patientUseDrug.getChange_time());
                    drugs.add(drug);
                }
            }
            responseData.put("drugs",drugs);
        }
        return errorData;
    }

    public ResponseBase process() {

        if ( validate() == null && list() == null ) {
            logger.info(LogUtils.Msg("Success to List Patient use Drugs",request,responseData));
            return new ResponseBase(StatusConst.OK, responseData);
        } else {
            logger.warn(LogUtils.Msg("Failure to List Patient use Drugs",request,errorData));
            return new ResponseBase(StatusConst.ERROR, errorData);
        }


    }


}
