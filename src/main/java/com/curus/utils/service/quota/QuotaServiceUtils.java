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

import java.sql.Date;
import java.sql.Time;
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


    static public int addQuotas(CurusDriver driver,
                                Long account_id,
                                Long patient_id,
                                Date date, Map<String, String> quotas) {
        Long id = 0L;
        List<Quota> quotaList = new ArrayList<Quota>();
        for ( Map.Entry<String,String> entry : quotas.entrySet() ) {
            if ((id = QuotaUtils.getQuotaIds(entry.getKey())) != QuotaConst.QUOTA_UNKNOW_ID) {
                Quota quota = new Quota(account_id,patient_id, date,
                        id, entry.getValue());
                quotaList.add(quota);
            }
        }
        return driver.quotaDao.insert(quotaList);
    }

    static public int addWeightHeight(CurusDriver driver,
                                      Long account_id, Long patient_id,
                                      String weight, String height) {
        Map<String,String> quotas = new HashMap<String, String>();
        quotas.put("weight",weight); quotas.put("height",height);
        return addQuotas(driver, account_id, patient_id, TimeUtils.getDate(), quotas);
    }

    static public int addQuota(CurusDriver driver,
                               Long account_id,
                               Long patient_id,
                               String cate, String unix_time, String record) {
        Long quotaId = QuotaUtils.getQuotaIds(cate);
        Date date = unix_time == null ? TimeUtils.getDate() : TimeUtils.parseDate(unix_time);
        Quota quota;
        int ret = 0;
        if ( quotaId.compareTo(0L) == 0 ) {
            logger.warn(LogUtils.Msg("Unknown Cate", cate, record));
        } else if ( (quota = driver.quotaDao.selectByMeasureDate(account_id,patient_id,quotaId,date)) == null ) {
            ret = driver.quotaDao.insert(account_id,patient_id,quotaId,date,record);
        } else {
            quota.setRecord(record);
            ret =  driver.quotaDao.update(quota,"id");
        }
        logger.info(LogUtils.Msg("Success to Add Quota",record));

        return ret;
    }

    static public int listQuotas(CurusDriver driver, String lastest,
                                Long account_id, Long patient_id,
                                String cate,
                                List<TsValueData> response) {

        Long quota_id = QuotaUtils.getQuotaIds(cate);
        List<Quota> quotaList;
        Long size = quota_id == null ? 0L : Long.parseLong(lastest);
        if (quota_id.compareTo(0L) != 0) {
            if ( size > 0L ) quotaList = driver.quotaDao.selectLastestQuota(account_id, patient_id, quota_id,Long.parseLong(lastest));
            else quotaList = driver.quotaDao.selectByMeasureDateLast90Days(account_id, patient_id, quota_id);

            for (Quota q : quotaList) {
                response.add(new TsValueData(TimeUtils.date2String(q.getMeasure_date()), q.getRecord()));
            }

        } else logger.warn(LogUtils.Msg("Unknown cate", cate));
        return response.size();
    }

}
