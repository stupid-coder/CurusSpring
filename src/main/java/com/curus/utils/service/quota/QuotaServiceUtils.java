package com.curus.utils.service.quota;

import com.alibaba.fastjson.JSONObject;
import com.curus.dao.CurusDriver;
import com.curus.httpio.response.TsValueData;
import com.curus.model.database.Quota;
import com.curus.model.record.QuotaHeightRecord;
import com.curus.model.record.QuotaWeightRecord;
import com.curus.utils.LogUtils;
import com.curus.utils.QuotaUtils;
import com.curus.utils.TimeUtils;
import com.curus.utils.constant.QuotaConst;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by stupid-coder on 3/2/16.
 */
public class QuotaServiceUtils {

    private static Log logger = LogFactory.getLog(QuotaServiceUtils.class);

    static public Double getWeight(String weight_quota) {
        return JSONObject.parseObject(weight_quota, QuotaWeightRecord.class).getWeight();

    }

    static public int addQuotas(CurusDriver driver,
                                Long account_id,
                                Long patient_id,
                                Date date, Map<String, String> quotas) {
        int ret = 0;
        for ( Map.Entry<String,String> entry : quotas.entrySet() ) {
            if (QuotaUtils.getQuotaIds(entry.getKey()) != QuotaConst.QUOTA_UNKNOW_ID) {
               ret += addQuota(driver,account_id,patient_id,entry.getKey(),date,entry.getValue());
            }
        }
        return ret;
    }

    static public int addWeightHeight(CurusDriver driver,
                                      Long account_id, Long patient_id,
                                      Double weight, Double height) {
        Map<String,String> quotas = new HashMap<String, String>();
        quotas.put("weight", weight.toString()); quotas.put("height",height.toString());
        return addQuotas(driver, account_id, patient_id, TimeUtils.getDate(), quotas);
    }

    static public int addQuota(CurusDriver driver,
                               Long account_id,
                               Long patient_id,
                               String cate, Date date, String record) {
        Long quota_id = QuotaUtils.getQuotaIds(cate);
        Quota quota;
        if ( date == null ) date = TimeUtils.getDate();
        int ret = 0;
        JSONObject jo = new JSONObject();
        jo.put(cate, record);
        if ( quota_id.compareTo(0L) == 0 ) {
            logger.warn(LogUtils.Msg("Unknown Cate", cate, jo.toJSONString()));
        } else if ( (quota = driver.quotaDao.selectByMeasureDate(account_id,patient_id,quota_id,date)) == null ) {
            ret = driver.quotaDao.insert(account_id,patient_id,quota_id,date, jo.toJSONString());
        } else {
            quota.setRecord(jo.toJSONString());
            ret =  driver.quotaDao.update(quota,"id");
        }
        return ret;
    }

    static public int listQuotas(CurusDriver driver, String lastestdays,
                                Long account_id, Long patient_id,
                                String cate,
                                List<TsValueData> response) {

        Long quota_id = QuotaUtils.getQuotaIds(cate);
        List<Quota> quotaList;
        Long days = lastestdays == null ? 0L : Long.parseLong(lastestdays);
        if (quota_id.compareTo(0L) != 0) {
            if ( days > 0L ) quotaList = driver.quotaDao.selectLastestQuota(account_id, patient_id, quota_id, days);
            else quotaList = driver.quotaDao.selectByMeasureDateLastDays(account_id, patient_id, quota_id, 90L);

            for (Quota q : quotaList) {
                JSONObject jo = JSONObject.parseObject(q.getRecord());
                response.add(new TsValueData(TimeUtils.date2String(q.getMeasure_date()), jo.getString(cate)));
            }

        } else logger.warn(LogUtils.Msg("Unknown cate", cate));
        return response.size();
    }

}
