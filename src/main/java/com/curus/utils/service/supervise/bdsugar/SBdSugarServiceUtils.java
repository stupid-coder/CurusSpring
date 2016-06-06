package com.curus.utils.service.supervise.bdsugar;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.curus.dao.CurusDriver;
import com.curus.model.database.PatientUseDrug;
import com.curus.model.database.Quota;
import com.curus.utils.QuotaUtils;
import com.curus.utils.TimeUtils;
import com.curus.utils.constant.BdSugarConst;
import com.curus.utils.constant.DrugConst;
import com.curus.utils.constant.QuotaConst;
import com.curus.utils.service.DrugUtils;
import com.curus.utils.service.drug.DrugServiceUtils;
import com.curus.utils.service.patient.PatientServiceUtils;
import com.curus.utils.service.quota.QuotaServiceUtils;
import com.curus.utils.service.supervise.weight.SWeightSerivceUtils;
import java.util.List;
import java.util.Map;

/**
 * Created by stupid-coder on 6/5/16.
 */
public class SBdSugarServiceUtils {

    public static Long GetQuotaChangeDays(CurusDriver driver,
                                              Long account_id,
                                              Long patient_id) {
        Long lastDays = Long.MAX_VALUE;

        List<Quota> quotaList = driver.quotaDao.selectLastestQuota(account_id, patient_id, QuotaConst.QUOTA_ACT_ID, 1L);
        if (quotaList != null && quotaList.size() == 1)
            lastDays = TimeUtils.dateDiffToNow(quotaList.get(0).getMeasure_date());
        quotaList = driver.quotaDao.selectByMeasureDateLastDays(account_id,patient_id,QuotaConst.QUOTA_DIET_ID,1L);
        if (quotaList !=null && quotaList.size() == 1)
            lastDays = Math.min(TimeUtils.dateDiffToNow(quotaList.get(0).getMeasure_date()), lastDays);
        List<PatientUseDrug> patientUseDrugList = driver.patientUseDrugDao.selectByMeasureDateLastDays(account_id,patient_id,1L);
        if ( patientUseDrugList != null && patientUseDrugList.size() == 1 )
            lastDays = Math.min(TimeUtils.dateDiffToNow(patientUseDrugList.get(0).getChange_time()),lastDays);
        return lastDays;
    }

    public static JSONObject MonitorInterval(CurusDriver driver,
                                       Long account_id,
                                       Long patient_id,
                                       JSONObject ref_bs_degree) {

        JSONObject interval = new JSONObject();
        for ( Map.Entry<String,Long>  entry : QuotaConst.SUB_QUOTA_IDS.entrySet() ) {
            interval.put(entry.getKey(),365L);
        }

        List<PatientUseDrug> patientUseDrugList = DrugServiceUtils.GetPatientUseDrug(driver,patient_id);
        if ( patientUseDrugList != null ) {
            interval.put("kf",30L);
        }
        if ( DrugServiceUtils.DrugType(driver, patientUseDrugList, DrugUtils.GetCompTypeId("胰岛素")) ) {
            interval.put("kf", 1L);
            for ( Map.Entry<String,Long> entry : QuotaConst.SUB_QUOTA_IDS.entrySet() ) {
                interval.put(entry.getKey(),Math.min(interval.getLong(entry.getKey()),30L));
            }
        }

        if (ref_bs_degree == null) {
            return interval;
        }



        return interval;
    }


    public static Double GetAvgSugarValue(JSONArray sugarArray) {
        Double sugarvalue = 0.0;
        for ( int i = 0; i < sugarArray.size(); ++ i) {
            if ( i > 2 ) break;
            sugarvalue += sugarArray.getJSONObject(i).getDouble("sugarvalue");
        }
        return sugarvalue / sugarArray.size();
    }

    public static String GetMoment(String key)
    {
        if ( key.compareTo("kf") == 0) return "早餐前空腹";
        if ( key.compareTo("zch") == 0) return "早餐后2H";
        if ( key.compareTo("wfq") == 0) return "午餐前";
        if ( key.compareTo("wfh") == 0) return "午饭后";
        if ( key.compareTo("wcq") == 0) return "晚餐前";
        if ( key.compareTo("wch") == 0) return "晚餐后";
        if ( key.compareTo("sq") == 0) return "睡前";
        if ( key.compareTo("yj") == 0) return "夜间";
        if ( key.compareTo("ydq") == 0) return "运动前";
        if ( key.compareTo("ydh") == 0) return "运动后";
        return "NONE";
    }

    public static boolean GetRefBSAndDegreeKF(Long interval,
                                              String key,
                                              JSONObject lastest_quotas,
                                              JSONObject lastest_in_duration,
                                              JSONObject suggestion,
                                              JSONObject degree) {
        Long value = QuotaConst.SUB_QUOTA_IDS.get(key);
        JSONObject kfbs = new JSONObject();
        String kf_suggestion;
        if ( !lastest_in_duration.containsKey(key) ) { // ref_duration 内无记录
            if ( lastest_quotas.containsKey(key) ) {
                Quota quota = lastest_quotas.getObject(key,Quota.class);
                if (TimeUtils.dateDiffToNow(quota.getMeasure_date()) > interval) {
                    suggestion.put(key,String.format("患者%s天内一直没有记录%s血糖变化，系统无法进行评估！",GetMoment(key),interval));
                    return false;
                } else {
                    Double ref_bs = JSONObject.parseObject(quota.getRecord()).getDouble("sugarvalue");
                    kfbs.put("ref_bs",ref_bs);
                    if ( ref_bs <= 2.8 ) {
                        kfbs.put("ref_degree",1.0);
                        kf_suggestion = String.format("%s%s低血糖(可参考度：中)",quota.getMeasure_date().toString(),GetMoment(key));
                    } else if ( ref_bs < 11.1 ) {
                        kfbs.put("ref_degree",0.5);
                        kf_suggestion = String.format("近期无%s血糖记录，暂用%s的血糖值替代(可参考度：低)",GetMoment(key),quota.getMeasure_date().toString());
                    } else {
                        kfbs.put("ref_degree",1.0);
                        kf_suggestion = String.format("近期无%s血糖记录，暂用%s的血糖值替代(可参考度：中)",GetMoment(key),quota.getMeasure_date().toString());
                    }
                }
            } else {
                suggestion.put(key, String.format("患者%s天内一直没有记录%s血糖变化，系统无法进行评估！", interval.getLong(key),GetMoment(key)));
                return false;
            }
        } else if ( lastest_in_duration.getJSONArray(key).size() == 1 ){ // ref_duration 1 条记录
            JSONObject lastest_quota = lastest_in_duration.getJSONArray(key).getJSONObject(0);
            Double ref_bs = lastest_quota.getDouble("sugarvalue");
            kfbs.put("ref_bs",ref_bs);
            if ( ref_bs <= 2.8 ) {
                kfbs.put("ref_degree",3.0);
                kf_suggestion = String.format("%s%s低血糖(可参考度：很高)",lastest_quota.getDate("measure_date").toString(),GetMoment(key));
            } else if ( ref_bs < 11.1 ) {
                kfbs.put("ref_degree",1.0);
                kf_suggestion = String.format("%s%s血糖%f(可参考度：中)",lastest_quota.getDate("measure_date").toString(),GetMoment(key),ref_bs);
            } else if ( ref_bs < 13.9 ) {
                kfbs.put("ref_degree",2.0);
                kf_suggestion = String.format("%s%s血糖%f(可参考度：高)",lastest_quota.getDate("measure_date").toString(),GetMoment(key),ref_bs);
            } else {
                kfbs.put("ref_degree",3.0);
                kf_suggestion = String.format("%s%s血糖%f(可参考度：很高)",lastest_quota.getDate("measure_date").toString(),GetMoment(key),ref_bs);
            }
        } else if ( lastest_in_duration.getJSONArray(key).size() == 2 ) { // ref_duration 2 条记录
            Double lastest_bs = lastest_in_duration.getJSONArray(key).getJSONObject(0).getDouble("sugarvalue");
            Double ref_bs = GetAvgSugarValue(lastest_in_duration.getJSONArray(key));
            kfbs.put("ref_bs",ref_bs);
            if ( lastest_bs < 11.1 ) {
                kfbs.put("ref_degree",2.0);
                kf_suggestion = String.format("最近两次%s血糖平均值%f(可参考度：高)",GetMoment(key),ref_bs);
            } else {
                kfbs.put("ref_degree",3.0);
                kf_suggestion = String.format("最近两次%s血糖平均值%f(可参考度：很高)",GetMoment(key),ref_bs);
            }
        } else {
            Double ref_bs = GetAvgSugarValue(lastest_in_duration.getJSONArray(key));
            kfbs.put("ref_bs",ref_bs);
            kfbs.put("ref_degree",3.0);
            kf_suggestion = String.format("最近三次%s血糖平均值%f(可参考度：很高)",GetMoment(key),ref_bs);
        }

        suggestion.put(key,kf_suggestion);
        degree.put(key,kfbs);
        return true;
    }

    public static boolean GetRefBSAndDegreeH(Long interval,
                                             String key,
                                             JSONObject lastest_quotas,
                                             JSONObject lastest_in_duration,
                                             JSONObject suggestion,
                                             JSONObject degree)
    {
        Double kfbs = degree.getJSONObject("kf").getDouble("ref_bs");
        JSONObject zchbs = new JSONObject();
        String zch_suggestion = null;
        Double ref_bs = null;
        if ( !lastest_in_duration.containsKey(key) ) { // 无记录
            if ( lastest_quotas.containsKey(key) ) {
                Quota item = lastest_quotas.getObject(key,Quota.class);
                Double lastest_bs = JSONObject.parseObject(item.getRecord()).getDouble("sugarvalue");

                if ( lastest_bs < 16.6 ) {
                    zchbs.put("ref_degree",0.5);
                    ref_bs = lastest_bs;
                    zch_suggestion = String.format("近期无%s血糖，暂用%s的%s血糖%f替代(可参考度：低)",GetMoment(key),item.getMeasure_date().toString(),GetMoment(key),ref_bs);
                } else {
                    zchbs.put("ref_degree",1.0);
                    ref_bs = lastest_bs;
                    zch_suggestion = String.format("近期无%s血糖，暂用%s的%s血糖%f替代(可参考度：中)",GetMoment(key),item.getMeasure_date().toString(),GetMoment(key),ref_bs);
                }
            }

            if ( ref_bs == null ) {
                if (kfbs < 7) ref_bs = 7.0;
                else ref_bs = 12.0;
                zchbs.put("ref_degree", 0.2);
            }
            zchbs.put("ref_bs", ref_bs);
        } else if ( lastest_in_duration.getJSONArray(key).size() == 1 ) { // ref_duration 1条记录
            JSONObject lastest_quota = lastest_in_duration.getJSONArray(key).getJSONObject(0);
            ref_bs = lastest_quota.getDouble("sugarvalue");
            if ( ref_bs < 16.6 ) {
                zchbs.put("ref_degree",1.0);
                zch_suggestion = String.format("%s%s血糖%f(可参考度：中)",lastest_quota.getDate("measure_date"),GetMoment(key),ref_bs);
            } else {
                zchbs.put("ref_degree",2.0);
                zch_suggestion = String.format("%s%s血糖%f(可参考度：高)",lastest_quota.getDate("measure_date"),GetMoment(key),ref_bs);
            }
        } else if (lastest_in_duration.getJSONArray(key).size() == 2 ) {
            JSONObject lastest_quota = lastest_in_duration.getJSONArray(key).getJSONObject(0);
            ref_bs = GetAvgSugarValue(lastest_in_duration.getJSONArray(key));
            if (lastest_quota.getDouble("sugarvalue") < 16.6) {
                zchbs.put("ref_degree",2.0);
                zch_suggestion = String.format("最近两次%s血糖平均值%f(可参考度：高)",GetMoment(key),ref_bs);
            } else {
                zchbs.put("ref_degree",3.0);
                zch_suggestion = String.format("最近两次%s血糖平均值%f(可参考度：很高)",GetMoment(key),ref_bs);
            }
        } else {
            ref_bs = GetAvgSugarValue(lastest_in_duration.getJSONArray(key));
            zchbs.put("ref_degree",3.0);
            zch_suggestion = String.format("最近三次%s血糖平均值%f",GetMoment(key),ref_bs);
        }
        degree.put(key,zchbs);
        suggestion.put(key,zch_suggestion);
        return true;
    }

    public static boolean GetRefBSAndDegreeQ(Long interval,
                                             String key,
                                             JSONObject lastest_quotas,
                                             JSONObject lastest_in_duration,
                                             JSONObject suggestion,
                                             JSONObject degree)
    {
        Double kfbs = degree.getJSONObject("kf").getDouble("ref_bs");
        JSONObject qbs = new JSONObject();
        String zch_suggestion = null;
        Double ref_bs = null;
        if ( !lastest_in_duration.containsKey(key) ) { // 无记录
            if ( lastest_quotas.containsKey(key) ) {
                Quota item = lastest_quotas.getObject(key,Quota.class);
                Double lastest_bs = JSONObject.parseObject(item.getRecord()).getDouble("sugarvalue");

                if ( lastest_bs <= 2.8 ) {
                    qbs.put("ref_degree",1.0);
                    ref_bs = lastest_bs;
                    zch_suggestion = String.format("%s%s低血糖(可参考度：中)",item.getMeasure_date().toString(),GetMoment(key));
                } else if ( lastest_bs < 11.1 && degree.getJSONObject("kf").getDouble("ref_degree") <= 1.0 ) {
                    qbs.put("ref_degree",0.5);
                    ref_bs = lastest_bs;
                    zch_suggestion = String.format("近期无%s血糖记录，暂用%s的%s血糖%f替代(可参考度：低)",GetMoment(key),item.getMeasure_date().toString(),GetMoment(key),ref_bs);
                } else if ( lastest_bs < 11.1 && degree.getJSONObject("kf").getDouble("ref_degree") > 1.0 ) {
                    ref_bs = kfbs;
                    qbs.put("ref_degree",0.5);
                    zch_suggestion = String.format("近期无%s血糖记录，暂用%s%s血糖%f替代(可参考度：低)",GetMoment(key),item.getMeasure_date().toString(),GetMoment("kf"),ref_bs);
                } else {
                    ref_bs = lastest_bs;
                    qbs.put("ref_degree",1.0);
                    zch_suggestion = String.format("近期无%s记录，暂用%s%s血糖%f替代(可参考度：中)",GetMoment(key),item.getMeasure_date().toString(),GetMoment(key),ref_bs);

                }
            }

            if ( ref_bs == null ) {
                ref_bs = Math.max(6.0,kfbs);
                qbs.put("ref_degree", 0.2);
            }
            qbs.put("ref_bs", ref_bs);
        } else if ( lastest_in_duration.getJSONArray(key).size() == 1 ) { // ref_duration 1条记录
            JSONObject lastest_quota = lastest_in_duration.getJSONArray(key).getJSONObject(0);
            ref_bs = lastest_quota.getDouble("sugarvalue");
            if ( ref_bs <= 2.8 ) {
                qbs.put("ref_degree",3.0);
                zch_suggestion = String.format("%s%s血糖低血糖(可参考度：很高)",lastest_quota.getDate("measure_date"),GetMoment(key));
            } else if ( ref_bs < 11.1 ){
                qbs.put("ref_degree", 1.0);
                zch_suggestion = String.format("%s%s血糖%f(可参考度：中)",lastest_quota.getDate("measure_date"),GetMoment(key),ref_bs);
            } else if ( ref_bs < 13.9 ){
                qbs.put("ref_degree", 2.0);
                zch_suggestion = String.format("%s%s血糖%f(可参考度：高)", lastest_quota.getDate("measure_date"), GetMoment(key), ref_bs);
            } else {
                qbs.put("ref_degree",3.0);
                zch_suggestion = String.format("%s%s血糖%f(可参考度：很高)",lastest_quota.getDate("measure_date"),GetMoment(key),ref_bs);
            }
        } else if (lastest_in_duration.getJSONArray(key).size() == 2 ) {
            JSONObject lastest_quota = lastest_in_duration.getJSONArray(key).getJSONObject(0);
            ref_bs = GetAvgSugarValue(lastest_in_duration.getJSONArray(key));
            if (lastest_quota.getDouble("sugarvalue") < 11.1) {
                qbs.put("ref_degree",2.0);
                zch_suggestion = String.format("最近两次%s血糖平均值%f(可参考度：高)",GetMoment(key),ref_bs);
            } else {
                qbs.put("ref_degree",3.0);
                zch_suggestion = String.format("最近两次%s血糖平均值%f(可参考度：很高)",GetMoment(key),ref_bs);
            }
        } else {
            ref_bs = GetAvgSugarValue(lastest_in_duration.getJSONArray(key));
            qbs.put("ref_degree",3.0);
            zch_suggestion = String.format("最近三次%s血糖平均值%f",GetMoment(key),ref_bs);
        }
        degree.put(key,qbs);
        suggestion.put(key,zch_suggestion);
        return true;
    }

    public static String TrustedLevel(Double degree) {
        if ( degree >= 3.0 ) return "很高";
        else if ( degree >= 2.0 ) return "高";
        else if ( degree >= 1.0 ) return "中";
        else if ( degree >= 0.5 ) return "低";
        else return "很低";
    }

    public static void GetRefAndDegreeALC(CurusDriver driver,
                                          Long account_id,
                                          Long patient_id,
                                          Long change_days,
                                          JSONObject degree) {
        Quota alcQuota = QuotaServiceUtils.getLastQuota(driver, account_id, patient_id, QuotaUtils.getQuotaIds("alc"));
        Double ref_alc_degree = null;
        Double ref_alc = null;
        Double ref_alc_value = null;
        Double ref_alc_todate = null;
        String suggestion = null;
        if ( alcQuota == null ) {
            ref_alc = 5.0;
            ref_alc_degree = 0.2;
        } else {
            ref_alc_todate = (30-TimeUtils.dateDiffToNow(alcQuota.getMeasure_date())) / 20.0;
            ref_alc_value = (change_days - TimeUtils.dateDiffToNow(alcQuota.getMeasure_date()) - 15L)/30.0;
            if ( ref_alc_todate > 0.0 && ref_alc_value > 0.0 ) {
                ref_alc_degree = ref_alc_todate * ref_alc_value;
                if ( ref_alc_degree >= 3.0 ) ref_alc_degree = 3.0;
                else if ( ref_alc_degree >= 2.0 ) ref_alc_degree = 2.0;
                else if ( ref_alc_degree >= 1.0 ) ref_alc_degree = 1.0;
                else if ( ref_alc_degree >= 0.5 ) ref_alc_degree = 0.5;
                else ref_alc_degree = 0.2;
            }
        }
        if ( ref_alc_value != null && ref_alc_todate != null && ref_alc_degree != null ) {
            String monitor_date = alcQuota.getMeasure_date().toString();
            if ( ref_alc_value <= 0.0 ) suggestion = String.format("A1c可反映过去2-3个月血糖控制的平均水平，因此如需判断现行治疗方案是否合适，需在稳定治疗2-3个月以后在进行A1c检测。本次A1c的检测日期%s距最近的治疗方案调整日期%s太近，甚至是在之前，这对进一步调整治疗方案没有参考意义。建议在%s前后再查一次A1c。",
                    monitor_date,TimeUtils.getDate(change_days*-1),TimeUtils.getDate(90L-change_days));
            else if ( ref_alc_todate <= 0.0 ) suggestion = String.format("最近的A1c是一个月前所查，系统将不再据此做出评价。");
            else if ( ref_alc_value > 0.0 && ref_alc_degree > 0.0 ) {
                ref_alc = JSONObject.parseObject(alcQuota.getRecord()).getDouble("alc");
                suggestion = String.format("%s的糖化血红蛋白%f（可参考度：%s）",
                        monitor_date,ref_alc,TrustedLevel(ref_alc_degree));
            }
        }
        JSONObject item = new JSONObject();
        item.put("ref_alc",ref_alc);
        item.put("ref_degree",ref_alc_degree);
        degree.put("alc",item);
        if (suggestion != null) degree.getJSONObject("suggestion").put("alc",suggestion);
    }

    public static JSONObject GetRefAndDegreeTotal(CurusDriver driver,
                                               Long account_id,
                                               Long patient_id) {
        JSONObject interval = MonitorInterval(driver,account_id,patient_id,null);

        JSONObject degree = new JSONObject();
        Long change_days = GetQuotaChangeDays(driver,account_id,patient_id);
        Long ref_duration = Math.min(7L,change_days);
        JSONObject quotas = new JSONObject();
        QuotaServiceUtils.listQuotas(driver,ref_duration,account_id,patient_id,QuotaConst.QUOTA_BS,null,quotas);
        JSONObject lastestQuotaList = driver.quotaDao.selectLastestBSQuota(account_id,patient_id);

        degree.put("suggestion",new JSONObject());
        JSONObject suggestion = degree.getJSONObject("suggestion");
        suggestion.put("header", String.format("%s 当前血糖水平(可参考度)如下：", PatientServiceUtils.GetPatientName(driver, patient_id)));


        if (!GetRefBSAndDegreeKF(interval.getLong("kf"),"kf",lastestQuotaList,quotas,suggestion,degree))
            return degree;
        GetRefBSAndDegreeH(interval.getLong("zch"), "zch", lastestQuotaList, quotas, suggestion, degree);
        GetRefBSAndDegreeQ(interval.getLong("wfq"), "wfq", lastestQuotaList, quotas, suggestion, degree);
        GetRefBSAndDegreeH(interval.getLong("wfh"), "wfh", lastestQuotaList, quotas, suggestion, degree);
        GetRefBSAndDegreeQ(interval.getLong("wcq"), "wcq", lastestQuotaList, quotas, suggestion, degree);
        GetRefBSAndDegreeH(interval.getLong("wch"), "wch", lastestQuotaList, quotas, suggestion, degree);
        GetRefBSAndDegreeQ(interval.getLong("sq"), "sq", lastestQuotaList, quotas, suggestion, degree);
        GetRefBSAndDegreeQ(interval.getLong("yj"), "yj", lastestQuotaList, quotas, suggestion, degree);
        GetRefBSAndDegreeH(interval.getLong("ydq"), "ydq", lastestQuotaList, quotas, suggestion, degree);
        GetRefBSAndDegreeQ(interval.getLong("ydh"), "ydh", lastestQuotaList, quotas, suggestion, degree);

        //ALC
        GetRefAndDegreeALC(driver,account_id,patient_id,change_days,degree);
        return degree;
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
