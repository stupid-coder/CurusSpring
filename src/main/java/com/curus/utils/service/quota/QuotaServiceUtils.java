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
import com.curus.utils.TypeUtils;
import com.curus.utils.constant.QuotaConst;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by stupid-coder on 3/2/16.
 */
public class QuotaServiceUtils {

    private static Log logger = LogFactory.getLog(QuotaServiceUtils.class);

    static public int initQuota(CurusDriver driver,
                                Long account_id, Long patient_id,
                                Map<String,Object> quotas) {
        Long id = 0L;
        List<Quota> quotaList = new ArrayList<Quota>();
        for ( Map.Entry<String,Object> entry : quotas.entrySet() ) {
            if ((id = QuotaUtils.getQuotaIds(entry.getKey())) != QuotaConst.QUOTA_UNKNOW_ID) {
                JSONObject jb = new JSONObject(); jb.put(entry.getKey(),entry.getValue());
                Quota quota = new Quota(account_id,patient_id, TimeUtils.getTimestamp(), TimeUtils.getTimestamp(),
                        id, jb.toJSONString());
                quotaList.add(quota);
            }
        }
        return driver.quotaDao.insert(quotaList);
    }

    static public String getQuota(String cate, String value) {
        JSONObject jb = new JSONObject();
        jb.put(cate,value);
        return jb.toJSONString();
    }

    static public int addQuotas(CurusDriver driver,
                                Long account_id,
                                Long patient_id,
                                Timestamp ts, Map<String, String> quotas) {
        int ret = 0;
        for ( Map.Entry<String,String> entry : quotas.entrySet() ) {
            Long quotaId =  QuotaUtils.getQuotaIds(entry.getKey());
            if ( quotaId.compareTo(0L) != 0 ) {
                logger.info(LogUtils.Msg("Add Quotas",account_id,patient_id,entry));
                ret += driver.quotaDao.insert(account_id, patient_id, quotaId, ts, getQuota(entry.getKey(),entry.getValue()));
            }
        }
        return ret;
    }

    static public int addWeightHeight(CurusDriver driver,
                                      Long account_id, Long patient_id,
                                      String weight, String height) {
        Map<String,String> quotas = new HashMap<String, String>();
        quotas.put("weight",weight); quotas.put("height",height);
        return addQuotas(driver, account_id, patient_id, TimeUtils.getTimestamp(), quotas);
    }

    static public int addQuotas(CurusDriver driver,
                               Long account_id,
                               Long patient_id,
                               String cate, String timestamp, String value) {
        Long quotaId = QuotaUtils.getQuotaIds(cate);
        Timestamp ts = timestamp == null ? TimeUtils.getTimestamp() : TimeUtils.parseTimestamp(timestamp);
        if ( quotaId.compareTo(0L) != 0 ) {
            return driver.quotaDao.insert(account_id,patient_id,quotaId,ts,getQuota(cate,value));
        } else {
            logger.warn(LogUtils.Msg("Unknown Cate",cate,value));
            return 0;
        }
    }

    static public int listQuota(CurusDriver driver,
                                Long account_id, Long patient_id,
                                String cate,
                                List<TsValueData> response) {

        Long cate_id = QuotaUtils.getQuotaIds(cate);
        List<Quota> quotaList = driver.quotaDao.selectByMeasureTime90Days(account_id, patient_id, cate_id);

        if (cate_id.compareTo(0L) != 0) {
            for (Quota q : quotaList) {
                JSONObject record = JSONObject.parseObject(q.getRecord());
                response.add(new TsValueData(TimeUtils.timestamp2String(q.getMeasure_time().getTime()), record.getJSONObject(cate)));
            }
        } else logger.warn(LogUtils.Msg("Unknown cate",cate));
        return response.size();
    }

}
