package com.curus.utils.service.supervise.bdsugar;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.curus.dao.CurusDriver;
import com.curus.model.database.PatientUseDrug;
import com.curus.model.database.Quota;
import com.curus.utils.QuotaUtils;
import com.curus.utils.TimeUtils;
import com.curus.utils.constant.BdSugarConst;
import com.curus.utils.constant.QuotaConst;
import com.curus.utils.service.quota.QuotaServiceUtils;
import com.curus.utils.service.supervise.weight.SWeightSerivceUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stupid-coder on 6/5/16.
 */
public class SBdSugarServiceUtils {

    public static Long GetQuotaChangeDays(CurusDriver driver,
                                              Long account_id,
                                              Long patient_id) {
        Long lastDays = 7L;

        List<Quota> quotaList = driver.quotaDao.selectLastestQuota(account_id, patient_id, QuotaConst.QUOTA_ACT_ID, 1L);
        if (quotaList != null && quotaList.size() == 1)
            lastDays = Math.min(TimeUtils.dateDiffToNow(quotaList.get(0).getMeasure_date()), lastDays);
        quotaList = driver.quotaDao.selectByMeasureDateLastDays(account_id,patient_id,QuotaConst.QUOTA_DIET_ID,1L);
        if (quotaList !=null && quotaList.size() == 1)
            lastDays = Math.min(TimeUtils.dateDiffToNow(quotaList.get(0).getMeasure_date()), lastDays);
        List<PatientUseDrug> patientUseDrugList = driver.patientUseDrugDao.selectByMeasureDateLastDays(account_id,patient_id,1L);
        if ( patientUseDrugList != null && patientUseDrugList.size() == 1 )
            lastDays = Math.min(TimeUtils.dateDiffToNow(patientUseDrugList.get(0).getChange_time()),lastDays);
        return lastDays;
    }

    public static Long MonitorInterval(CurusDriver driver,
                                       Long account_id,
                                       Long patient_id,
                                       String moment) {

        return 365L;
    }

    public static JSONObject GetRefBSAndDegress(CurusDriver driver,
                                                Long account_id,
                                                Long patient_id) {
        JSONObject bs_degress = new JSONObject();
        Long ref_duration = GetQuotaChangeDays(driver,account_id,patient_id);
        JSONObject bs_values = new JSONObject();
        QuotaServiceUtils.listQuotas(driver,ref_duration,account_id,patient_id,QuotaConst.QUOTA_BS,null,bs_values);

        return bs_degress;
    }

    public static Double BdSugarLevel(String moment, Double bsvalue) {
        Long moment_id = QuotaUtils.getSubQuotaIds(moment);
        if ( moment_id != null ) {
            List<QuotaConst.BdSugerLevelConfig> bdSugerLevelConfigs = QuotaConst.BS_LEVEL_CONFIG.get(moment_id);
            for ( QuotaConst.BdSugerLevelConfig bdSugerLevelConfig : bdSugerLevelConfigs ) {
                if ( bsvalue < bdSugerLevelConfig.max_value ) return bdSugerLevelConfig.level;
            }
        }
        return null;
    }

    public static void BdSugarNonmedSuggestion(CurusDriver driver,
                                               Long account_id,
                                               Long patient_id,
                                               Double diet, Double activity,
                                               JSONObject responseData) {

        Double activity_decrease = activity;
        Double diet_decrease = diet;
        Double ref_sugar = 100.0;

        if ( diet == null ) {

            diet_decrease = Math.max((SWeightSerivceUtils.GetBMI(driver,account_id,patient_id)-22)*0.05,0.0);
        }

        if ( activity_decrease == null ) {
            Double now_activity = SWeightSerivceUtils.GetActEnergy(driver,account_id,patient_id);
            activity_decrease = Math.max(0.0,60.0-(now_activity==null?40.0:now_activity));
        }
        JSONArray losses = new JSONArray();
        // diet
        {
            JSONObject item = new JSONObject();
            Double diet_sugar_loss = Math.min(diet_decrease * 10, Math.max(ref_sugar - 6.1, 0.0));
            item.put("diet_sugar_loss", diet_sugar_loss);
            item.put("diet_loss", diet_decrease);
            losses.add(item);
        }
        // activity
        {
            JSONObject item = new JSONObject();
            Double act_sugar_loss = Math.min(activity_decrease / 20, Math.max(ref_sugar - 6.1, 0.0));
            item.put("act_sugar_loss", act_sugar_loss);
            item.put("act_loss", activity_decrease);
            losses.add(item);
        }
        responseData.put("sugar_losses",losses);
        responseData.put("monitor_duration", BdSugarConst.SugarMonitorConfig);
    }
}
