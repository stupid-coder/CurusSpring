package com.curus.utils.service.quota;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.curus.dao.CurusDriver;
import com.curus.httpio.response.TsValueData;
import com.curus.model.database.PatientSupervise;
import com.curus.model.database.Quota;
import com.curus.model.record.QuotaHeightRecord;
import com.curus.model.record.QuotaWeightRecord;
import com.curus.utils.LogUtils;
import com.curus.utils.QuotaUtils;
import com.curus.utils.TimeUtils;
import com.curus.utils.constant.QuotaConst;
import com.curus.utils.service.supervise.bdsugar.SBdSugarServiceUtils;
import com.curus.utils.service.supervise.food.SFoodServiceUtils;
import com.curus.utils.service.supervise.smoke.SSmokeServiseUtils;
import com.curus.utils.service.supervise.weight.SWeightSerivceUtils;
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

    static public String getWeightLossQuota(Double weight_loss) {
        JSONObject jo = new JSONObject(); jo.put("weight_loss",weight_loss);
        return jo.toJSONString();
    }
    static public Double getWeightLoss(String quota) {
        return JSONObject.parseObject(quota).getDouble("weight_loss");
    }
    static public Double getWeight(String quota) {
        return JSONObject.parseObject(quota).getDouble("weight");
    }
    static public Double getHeight(String quota) {
        return JSONObject.parseObject(quota).getDouble("height");
    }
    static public Double getLastWeight(CurusDriver driver, Long account_id, Long patient_id) {
        List<Quota> weightList = driver.quotaDao.selectLastestQuota(account_id,patient_id, QuotaConst.QUOTA_WEIGHT_ID, 1L);
        if ( weightList != null && weightList.size() > 0 ) return getWeight(weightList.get(0).getRecord());
        return null;
    }
    static public Double getLastHeight(CurusDriver driver, Long account_id, Long patient_id) {
        List<Quota> heightList = driver.quotaDao.selectLastestQuota(account_id,patient_id, QuotaConst.QUOTA_HEIGHT_ID, 1L);
        if ( heightList != null && heightList.size() > 0 ) return getHeight(heightList.get(0).getRecord());
        return null;
    }
    static public Quota getLastSmokeQuota(CurusDriver driver, Long account_id, Long patient_id) {
        return getLastQuota(driver,account_id,patient_id,QuotaConst.QUOTA_SMOKE_ID);
    }
    static public Quota getLastQuota(CurusDriver driver, Long account_id, Long patient_id, Long quota_id) {
        List<Quota> quotaList = driver.quotaDao.selectLastestQuota(account_id,patient_id,quota_id,1L);
        if ( quotaList != null && quotaList.size() > 0) return quotaList.get(0);
        else return null;
    }
    static public String getKVJSON(String k, String v) {
        return String.format("{\"%s\":%s}",k,v);
    }
    static public int addQuotas(CurusDriver driver,
                                Long account_id,
                                Long patient_id,
                                Date date,
                                Map<String,String> quotas) {
        int ret = 0;
        for ( Map.Entry<String,String> quota : quotas.entrySet()) {
            ret += addQuota(driver,account_id,patient_id,quota.getKey(),null,date,quota.getValue());

        }
        return ret;
    }
    static public int addQuota(CurusDriver driver,
                                Long account_id,
                                Long patient_id,
                                String cate,
                                String subcate,
                                Date date,
                                String quota_str) {

        Quota quota;
        Long quota_id = QuotaUtils.getQuotaIds(cate);
        Long sub_quota_id = QuotaUtils.getSubQuotaIds(subcate);
        date = date == null ? TimeUtils.getDate() : date.compareTo(TimeUtils.getDate()) > 0 ? TimeUtils.getDate() : date;

        int ret = 0;
        if ( quota_id.compareTo(QuotaConst.QUOTA_UNKNOW_ID) == 0 ) {
            return 0;
        } else {
            quota = driver.quotaDao.selectByMeasureDate(account_id,patient_id,quota_id,sub_quota_id,date);
            if ( quota != null ) {
                quota.setRecord(quota_str);
                ret += driver.quotaDao.update(quota,"id");
            } else ret += driver.quotaDao.insert(account_id,patient_id,quota_id,sub_quota_id,date,quota_str);

        }
        return ret;
    }
    static public int listQuotas(CurusDriver driver, Long days,
                                 Long account_id, Long patient_id,
                                 String cate, String subcate,
                                 JSONObject response) {
        days = (days == null ? 90L : days);
        Long quota_id = QuotaUtils.getQuotaIds(cate);
        Long subcate_id = QuotaUtils.getSubQuotaIds(subcate);
        List<Quota> quotaList = null;
        if (quota_id.compareTo(QuotaConst.QUOTA_ACT_ID) == 0) {
            quotaList = driver.quotaDao.selectLastestQuota(account_id, patient_id, quota_id, 1L);
            if (quotaList != null && quotaList.size() != 0) {
                JSONObject act = JSONObject.parseObject(quotaList.get(0).getRecord());
                response.put("score", SWeightSerivceUtils.CalculateActivityEnergy(act));
                act.put("measure_date", TimeUtils.date2Long(quotaList.get(0).getMeasure_date()));
                response.put("value", act);
            }
        } else if (quota_id.compareTo(QuotaConst.QUOTA_FOOD_ID) == 0) {
            quotaList = driver.quotaDao.selectLastestQuota(account_id, patient_id, quota_id, 1L);
            if (quotaList != null && quotaList.size() > 0) {
                JSONObject food = JSONObject.parseObject(quotaList.get(0).getRecord());
                response.put("score", SFoodServiceUtils.CalculateFoodScore(food));
                response.put("measure_date", TimeUtils.date2Long(quotaList.get(0).getMeasure_date()));
                response.put("value", food);
            }
        } else if (quota_id.compareTo(QuotaConst.QUOTA_DIET_ID) == 0) {
            quotaList = driver.quotaDao.selectLastestQuota(account_id, patient_id, quota_id, 1L);
            if (quotaList != null && quotaList.size() > 0) {
                JSONObject diet = JSONObject.parseObject(quotaList.get(0).getRecord());
                response.put("score", SWeightSerivceUtils.CalculateDietEnergy(diet));
                response.put("measure_date", TimeUtils.date2Long(quotaList.get(0).getMeasure_date()));
                response.put("value", diet);
            }
        } else if (quota_id.compareTo(QuotaConst.QUOTA_SMOKE_ID) == 0) {
            quotaList = driver.quotaDao.selectLastestQuota(account_id, patient_id, quota_id, 1L);
            if (quotaList != null && quotaList.size() > 0) {
                JSONObject smoke = JSONObject.parseObject(quotaList.get(0).getRecord());
                response.put("measure_date", TimeUtils.date2Long(quotaList.get(0).getMeasure_date()));
                response.put("value", smoke);
            }
        } else if ( quota_id.compareTo(QuotaConst.QUOTA_BS_ID) == 0 ) {
            quotaList = driver.quotaDao.selectByMeasureDateLastDays(account_id, patient_id, quota_id, subcate_id, days);
            for ( Quota q : quotaList ) {
                String sub_cat = QuotaUtils.getSubQuotaName(q.getSub_cat());
                if ( sub_cat == null ) continue;
                if (!response.containsKey(sub_cat)) response.put(sub_cat,new JSONArray());
                JSONArray subcat_array = response.getJSONArray(sub_cat);
                JSONObject item = JSONObject.parseObject(q.getRecord());
                item.put("measure_date", TimeUtils.date2Long(q.getMeasure_date()));
                subcat_array.add(item);
            }
        } else if ( quota_id.compareTo(QuotaConst.QUOTA_UNKNOW_ID) != 0 ) {
            quotaList = driver.quotaDao.selectLastestQuota(account_id,patient_id,quota_id,1L);
            if ( quotaList != null && quotaList.size() > 0 ) {
                response.put("value", JSONObject.parseObject(quotaList.get(0).getRecord()));
                response.put("measure",TimeUtils.date2Long(quotaList.get(0).getMeasure_date()));
            }
        }
        return quotaList != null ? quotaList.size() : 0;
    }
    static public int listQuotas(CurusDriver driver,
                                 Long account_id, Long patient_id,
                                 JSONObject response) {
        List<Quota> quotaList = null;
        int ret = 0;
        quotaList = driver.quotaDao.selectLastestQuota(account_id,patient_id,QuotaConst.QUOTA_WEIGHT_ID,1L);
        if (quotaList != null && quotaList.size() > 0) {
            JSONObject quotaWeight = JSONObject.parseObject(quotaList.get(0).getRecord());
            JSONObject responseItem = new JSONObject();
            responseItem.put("measure_date", TimeUtils.date2Long(quotaList.get(0).getMeasure_date()));
            responseItem.put("value", quotaWeight);
            response.put(QuotaConst.QUOTA_WEIGHT, responseItem);
            ret ++;
        }

        quotaList = driver.quotaDao.selectLastestQuota(account_id,patient_id,QuotaConst.QUOTA_BP_ID,1L);
        if (quotaList != null && quotaList.size() > 0) {
            JSONObject quotaBP = JSONObject.parseObject(quotaList.get(0).getRecord());
            JSONObject responseItem = new JSONObject();
            responseItem.put("measure_date", TimeUtils.date2String(quotaList.get(0).getMeasure_date()));
            responseItem.put("value",quotaBP);
            response.put(QuotaConst.QUOTA_BP,responseItem);
            ret ++;
        }

        Long start_time = TimeUtils.getTimestamp().getTime();
        { // BS
            JSONObject bs_quotas = driver.quotaDao.selectLastestBSQuota(account_id, patient_id);
            JSONObject ref = SBdSugarServiceUtils.GetRefAndDegreeTotal(driver,account_id,patient_id);
            JSONObject all_interval = SBdSugarServiceUtils.MonitorInterval(driver,account_id,patient_id,ref);
            JSONArray quota_object = new JSONArray();
            for ( String sub_cat :bs_quotas.keySet() ) {
                Quota item = bs_quotas.getObject(sub_cat,Quota.class);
                JSONObject quotaBS = JSONObject.parseObject(item.getRecord());
                JSONObject responseItem = new JSONObject();
                Long interval = all_interval.getLong(sub_cat);

                responseItem.put("measure_date", TimeUtils.date2String(item.getMeasure_date()));
                responseItem.put("value", quotaBS);
                responseItem.put("score", SBdSugarServiceUtils.BdSugarLevel(sub_cat,quotaBS.getDouble("sugarvalue")));

                Long lastmonitor_diff_now =  TimeUtils.dateDiffToNow(item.getMeasure_date());

                if ( lastmonitor_diff_now.compareTo(interval) < 0 ) responseItem.put("old",0);
                else if ( lastmonitor_diff_now.compareTo(interval * 2L) >=0 ) responseItem.put("old", 2);
                else responseItem.put("old",1);
                quota_object.add(responseItem);
                ret++;
            }
            response.put(QuotaConst.QUOTA_BS, quota_object);
        }
        Long end_time = TimeUtils.getTimestamp().getTime();
        logger.info("运行时间："+(end_time - start_time));
        quotaList = driver.quotaDao.selectLastestQuota(account_id,patient_id,QuotaConst.QUOTA_ACT_ID,1L);
        if (quotaList != null && quotaList.size() > 0) {
            JSONObject quotaAct = JSONObject.parseObject(quotaList.get(0).getRecord());
            JSONObject responseItem = new JSONObject();
            responseItem.put("measure_date", TimeUtils.date2String(quotaList.get(0).getMeasure_date()));
            responseItem.put("score",SWeightSerivceUtils.CalculateActivityEnergy(quotaAct));
            responseItem.put("value",quotaAct);
            response.put(QuotaConst.QUOTA_ACT,responseItem);
            ret ++;
        }

        quotaList = driver.quotaDao.selectLastestQuota(account_id,patient_id,QuotaConst.QUOTA_DIET_ID,1L);
        if (quotaList != null && quotaList.size() > 0) {
            JSONObject quotaDiet = JSONObject.parseObject(quotaList.get(0).getRecord());
            JSONObject responseItem = new JSONObject();
            responseItem.put("measure_date", TimeUtils.date2String(quotaList.get(0).getMeasure_date()));
            responseItem.put("score",SWeightSerivceUtils.CalculateDietEnergy(quotaDiet));
            responseItem.put("value",quotaDiet);
            response.put(QuotaConst.QUOTA_DIET,responseItem);
            ret ++;
        }

        quotaList = driver.quotaDao.selectLastestQuota(account_id,patient_id,QuotaConst.QUOTA_FOOD_ID,1L);
        if (quotaList != null && quotaList.size() > 0) {
            JSONObject quotaFood = JSONObject.parseObject(quotaList.get(0).getRecord());
            JSONObject responseItem = new JSONObject();
            responseItem.put("measure_date", TimeUtils.date2String(quotaList.get(0).getMeasure_date()));
            responseItem.put("score",SFoodServiceUtils.CalculateFoodScore(quotaFood));
            responseItem.put("value",quotaFood);
            response.put(QuotaConst.QUOTA_FOOD,responseItem);
            ret ++;
        }

        PatientSupervise patientSupervise = driver.patientSuperviseDao.selectLastSupervise(account_id, patient_id, QuotaConst.QUOTA_SMOKE_ID);
        if (patientSupervise != null) {
            JSONObject responseItem = new JSONObject();
            responseItem.put("score",TimeUtils.timestampDiff(patientSupervise.getCreate_time(),TimeUtils.getTimestamp()));
            response.put(QuotaConst.QUOTA_SMOKE,responseItem);
            ret ++;
        }
        return ret;
    }
}
