package com.curus.utils.service.quota;

import com.alibaba.fastjson.JSONObject;
import com.curus.dao.CurusDriver;
import com.curus.httpio.response.TsValueData;
import com.curus.model.database.Account;
import com.curus.model.database.Patient;
import com.curus.model.database.Quota;
import com.curus.utils.LogUtils;
import com.curus.utils.QuotaUtils;
import com.curus.utils.TimeUtils;
import com.curus.utils.constant.QuotaConst;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by stupid-coder on 3/2/16.
 */
public class QuotaServiceUtils {

    private static Log logger = LogFactory.getLog(QuotaServiceUtils.class);

    static public int initQuota(CurusDriver driver,
                                Long account_id, Long patient_id,
                                Map<String,String> quotas) {
        Long id = 0L;
        List<Quota> quotaList = new ArrayList<Quota>();
        for ( Map.Entry<String,String> entry : quotas.entrySet() ) {
            if ((id = QuotaUtils.getQuotaIds(entry.getKey())) != QuotaConst.QUOTA_UNKNOW_ID) {
                JSONObject jb = new JSONObject(); jb.put(entry.getKey(),entry.getValue());
                Quota quota = new Quota(account_id,patient_id, TimeUtils.getTimestamp(), TimeUtils.getTimestamp(),
                        id, jb.toJSONString());
                quotaList.add(quota);
            }
        }
        return driver.quotaDao.insert(quotaList);
    }

    static public int addWeightHeight(CurusDriver driver,
                                      Account account, Patient patient,
                                      String weight, String height) {
        int ret = 0;
        ret += addQuotaWeight(driver,account.getId(),patient.getId(), TimeUtils.getTimestamp(), weight);
        ret += addQuotaHeight(driver,account.getId(),patient.getId(), TimeUtils.getTimestamp(), height);
        return ret;
    }

    static public int addQuota(CurusDriver driver,
                               Account account,
                               Long patient_id,
                               String cate, Timestamp date, String value) {
        Long quotaId =  QuotaUtils.getQuotaIds(cate);
        if ( QuotaConst.QUOTA_WEIGHT_ID.compareTo(quotaId) == 0 ) {
            return addQuotaWeight(driver,account.getId(),patient_id,date,value);
        } else if ( QuotaConst.QUOTA_HEIGHT_ID.compareTo(quotaId) == 0) {
            return addQuotaHeight(driver,account.getId(),patient_id,date,value);
        } else {
            logger.warn(LogUtils.Msg("Unknown Quota Category",cate));
        }
        return 0;
    }

    static public int addQuotaWeight(CurusDriver driver,
                                     Long account_id,
                                     Long patient_id,
                                     Timestamp date,
                                     String weight) {
        JSONObject jb = new JSONObject(); jb.put("weight", Double.parseDouble(weight));
        String record = jb.toJSONString();
        Quota quota = new Quota(account_id,patient_id,date,QuotaConst.QUOTA_WEIGHT_ID,record);
        logger.info(LogUtils.Msg("Add Weight Quota",quota));
        return driver.quotaDao.insert(quota);
    }

    static public int addQuotaHeight(CurusDriver driver,
                                     Long account_id,
                                     Long patient_id,
                                     Timestamp date,
                                     String height) {
        JSONObject jb = new JSONObject(); jb.put("height",Double.parseDouble(height));
        String record = jb.toJSONString();
        Quota quota = new Quota(account_id, patient_id, date, QuotaConst.QUOTA_HEIGHT_ID,record);
        logger.info(LogUtils.Msg("Add Height Quota",quota));
        return driver.quotaDao.insert(quota);
    }

    static public int listQuota(CurusDriver driver,
                                Long account_id, Long patient_id,
                                String cate,
                                List<TsValueData> response) {

        Long cate_id = QuotaUtils.getQuotaIds(cate);
        List<Quota> quotaList = driver.quotaDao.selectByMeasureTime90Days(account_id,patient_id,cate_id);

        for ( Quota q : quotaList ) {
            JSONObject record = JSONObject.parseObject(q.getRecord());
            if ( cate_id == QuotaConst.QUOTA_WEIGHT_ID ) {
                response.add(new TsValueData(TimeUtils.timestamp2String(q.getMeasure_time().getTime()),String.valueOf(record.getDoubleValue("weight"))));
            } else if ( cate_id == QuotaConst.QUOTA_HEIGHT_ID ){
                response.add(new TsValueData(TimeUtils.timestamp2String(q.getMeasure_time().getTime()),String.valueOf(record.getDoubleValue("height"))));
            }
        }
        return response.size();
    }

}
