package com.curus.services.drug;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.curus.dao.CurusDriver;
import com.curus.dao.drug.DrugInfoDao;
import com.curus.httpio.request.drug.DrugSearchRequest;
import com.curus.httpio.response.ErrorData;
import com.curus.httpio.response.ResponseBase;
import com.curus.model.database.DrugInfo;
import com.curus.utils.LogUtils;
import com.curus.utils.constant.ErrorConst;
import com.curus.utils.constant.StatusConst;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stupid-coder on 5/29/16.
 */
public class DrugSearchService {

    private Log logger = LogFactory.getLog(DrugSearchService.class);

    private static final Long limit = 20L;
    private DrugSearchRequest request;
    private JSONArray responseData;
    private CurusDriver driver;
    private ErrorData errorData;


    public DrugSearchService(DrugSearchRequest request, CurusDriver driver) {
        this.request = request;
        this.driver = driver;
        this.errorData = null;
    }

    private ErrorData validate() {
        if ( request.getGov_id() == null && request.getManu_name() == null && request.getProduct_name() == null ) {
            errorData = new ErrorData(ErrorConst.IDX_FORM_ERROR,"all");
        }
        return errorData;
    }

    private ErrorData search() {
        List<DrugInfo> drugInfoList = new ArrayList<DrugInfo>();
        if ( request.getGov_id() != null ) {
            List<DrugInfo> govDrugInfoList = driver.drugInfoDao.selectRlike("gov_id",request.getGov_id(),limit-drugInfoList.size());
            if ( govDrugInfoList != null ) drugInfoList.addAll(govDrugInfoList);
        }

        if ( drugInfoList.size() < 20 && request.getManu_name() != null ) {
            List<DrugInfo> productNameDrugInfoList = driver.drugInfoDao.selectRlike("product_name",request.getProduct_name(),limit-drugInfoList.size());
            if (    productNameDrugInfoList != null ) drugInfoList.addAll(productNameDrugInfoList);
        }

        if ( drugInfoList.size() < 20 && request.getManu_name() != null ) {
            List<DrugInfo> manuNameDrugInfoList = driver.drugInfoDao.selectRlike("manu_name",request.getManu_name(),limit-drugInfoList.size());
            if ( manuNameDrugInfoList != null ) drugInfoList.addAll(manuNameDrugInfoList);
        }

        responseData = new JSONArray();
        for ( DrugInfo drugInfo : drugInfoList ) {
            JSONObject drug = new JSONObject();
            drug.put("drug_id",drugInfo.getDrug_id());
            drug.put("product_name",drugInfo.getProduct_name());
            drug.put("gov_id",drugInfo.getGov_id());
            drug.put("manu_name",drugInfo.getManu_name());
            responseData.add(drug);
        }
        return errorData;
    }

    public ResponseBase process() {
        if ( validate() == null && search() == null ) {
            logger.info(LogUtils.Msg("Success to Search Drug",request,responseData));
            return new ResponseBase(StatusConst.OK,responseData);
        } else {
            logger.warn(LogUtils.Msg("Failure to Search Drug",request,responseData));
            return new ResponseBase(StatusConst.ERROR,errorData);
        }
    }

}
