package com.curus.utils.service.supervise.bdsugar;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.curus.dao.CurusDriver;
import com.curus.model.database.*;
import com.curus.utils.QuotaUtils;
import com.curus.utils.TimeUtils;
import com.curus.utils.constant.BdSugarConst;
import com.curus.utils.constant.InternalDataConst;
import com.curus.utils.constant.QuotaConst;
import com.curus.utils.service.DrugUtils;
import com.curus.utils.service.drug.DrugServiceUtils;
import com.curus.utils.service.patient.PatientServiceUtils;
import com.curus.utils.service.quota.QuotaServiceUtils;
import com.curus.utils.service.supervise.weight.SWeightSerivceUtils;

import java.util.*;

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

    public static JSONObject GetMonitorInterval(CurusDriver driver, Long account_id, Long patient_id,
                                                Boolean UseYDS) {

        InternalData interval_data = driver.internalDataDao.select(patient_id, InternalDataConst.BD_SUGAR_MONITER_INTERVAL);
        JSONObject moniter_interval;
        if ( interval_data == null ) { // Default
            if ( UseYDS == null ) return null;
            moniter_interval = new JSONObject();
            for ( Map.Entry<String,Long> entry : QuotaConst.SUB_QUOTA_IDS.entrySet() ) {
                Long default_interval = 365L;
                if ( UseYDS ) {
                    if ( entry.getKey().compareTo("kf") == 0 ) default_interval = 1L;
                    else default_interval = 30L;
                }
                moniter_interval.put(entry.getKey(),default_interval);
            }
            UpdateMoniterInterval(driver,patient_id,moniter_interval);
        } else {
            moniter_interval = JSONObject.parseObject(interval_data.getData());
            moniter_interval.put("id",interval_data.getId());
        }
        return moniter_interval;
    }

    public static void UpdateMoniterInterval(CurusDriver driver, Long patient_id, JSONObject moniter_interval) {
        driver.internalDataDao.save(patient_id,InternalDataConst.BD_SUGAR_MONITER_INTERVAL,moniter_interval);
    }



    public static String GetRefLevelSuggestionWithLevelithoutDrugFK(String patientName,
                                                                    Double value,
                                                                    Double level,
                                                                    boolean BMI_ACT_NO,
                                                                    JSONObject monitor_interval) {
        String moment = "kf";
        String suggestion = null;
        Long default_interval = monitor_interval.getLong(moment);

            if ( level <=  -2.0 ) {
                suggestion = "早晨出现了低血糖！建议规律早餐或适当提前早餐时间或在晨起后吃些水果或零食，平时做好应对低血糖的准备，并按提示监测空腹血糖，如再次发生低血糖或出现低血糖症状，建议尽早看医生。";
                default_interval = 7L;
            } else if (level <= 0.0 ) {
                suggestion = "空腹血糖处在正常水平，请保持健康的生活方式，按提示监测空腹血糖。";
                default_interval = Math.min(365L,default_interval);
            } else if (level == 0.5 ) {
                if ( BMI_ACT_NO )
                    suggestion = String.format("空腹血糖偏高（空腹血糖受损），建议强化健康生活方式，并按提示监测空腹血糖。通过减重、控制饮食或增加身体活动，%s仍有很大空间可使血糖恢复正常或延迟进展为糖尿病！研究显示，超重肥胖者减重5%或强化生活方式干预可使糖尿病的发病率减少50%；低能量摄入（少吃同时结合低能量食物的摄入）和高能量消耗（主要通过有氧运动）的降糖作用均不亚于降糖药物。对超重肥胖者建议利用“体重管理”工具帮助少吃多动，双管齐下；对体重正常或偏瘦者可利用“身体活动管理”维持正常的糖代谢水平。",
                            patientName);
                else
                    suggestion = "空腹血糖偏高（空腹血糖受损），建议强化健康生活方式，并按提示监测空腹血糖。";
                    default_interval = Math.min(90L,default_interval*2);
            } else if (level == 1.0) {
                suggestion = "空腹血糖达糖尿病水平！请按提示监测空腹血糖。";
                if ( BMI_ACT_NO )
                    suggestion = suggestion+"空腹血糖长期得不到控制会导致心脑眼肾等脏器并发症。目前"+patientName+"在减重、控制饮食或增加身体活动等方面仍存在较大的降糖空间，强烈建议加大非药物干预力度，努力使血糖恢复正常！研究显示，超重肥胖者减重5%或强化生活方式干预可使糖尿病的发病率减少50%；低能量摄入（少吃同时结合低能量食物的摄入）和高能量消耗（主要通过有氧运动）的降糖作用均不亚于降糖药物。对超重肥胖者建议利用“体重管理”工具帮助少吃多动，双管齐下；对体重正常或偏瘦者可利用“身体活动管理”维持正常的糖代谢水平。";
                else
                    suggestion = suggestion+"目前"+patientName+"已有较好饮食控制及身体活动强度，单纯非药物治疗很难使血糖降至正常水平，建议咨询医师看是否需要尽快启动药物治疗。";
                default_interval = 30L;
            } else if (level == 1.5) {
                if ( BMI_ACT_NO )
                    suggestion = "空腹血糖已进展至糖尿病较高水平，长期得不到控制会导致心脑眼肾等脏器并发症。目前"+patientName+"在减重、控制饮食或增加身体活动等方面仍存在较大的降糖空间，强烈建议加大非药物干预力度，努力使血糖恢复正常！同时按提示监测空腹血糖变化。研究显示，低能量摄入（少吃同时结合低能量食物的摄入）和高能量消耗（主要通过有氧运动）的降糖作用均不亚于降糖药物。";
                else
                    suggestion =  "虽经努力，空腹血糖水平仍然较高，且非药物干预的效果已接近极限，建议尽早就诊接受药物治疗，并按提示监测空腹血糖。";
                default_interval = 15L;
            } else if (level >= 2.0) {
                if ( BMI_ACT_NO )
                    suggestion = "空腹血糖水平过高！通过严格的非药物干预"+patientName+"虽仍有较大的降糖空间，但仍建议在强化饮食控制和身体活动的情况下尽快就医，并按提示监测空腹血糖变化。";
                else
                    suggestion = "空腹血糖非常严峻，单纯非药物干预不可能使血糖得到控制。建议尽早看医生接受药物治疗，之前每周至少测量一次空腹血糖！";
                default_interval = 7L;
            }

        monitor_interval.put(moment, default_interval);
        return suggestion;
    }
    public static String GetRefLevelSuggestionWithValueWithoutDrug(String patientName,
                                                                   String moment,
                                                                   Double value,
                                                                   Double level,
                                                                   boolean BMI_ACT_NO,
                                                                   JSONObject monitor_interval)
    {

        String context = GetMomentContext(moment);
        String suggestion = null;
        Long default_interval = monitor_interval.getLong(moment);

        if (value < 7.8) {
            suggestion = String.format("%s血糖正常，必要时再按照提示对%s血糖进行监测。",context,context);
            default_interval = Math.min(365L, 4 * default_interval);
        } else if ( value < 11.1 ) {
            if ( BMI_ACT_NO )
                suggestion = String.format("%s血糖偏高（糖耐量减低），但尚未达到糖尿病水平。减重、控制饮食或增加身体活动仍有较大的降糖空间，建议借助本系统提供的“体重管理”和“身体活动管理”功能模块进行非药物干预。尤其注意适当减少能量摄入，增加%s的运动量，并按提示监测%s血糖！",
                        context,context,context);
            else suggestion = String.format("%s血糖偏高（糖耐量减低），但尚未达到糖尿病水平。已有较大的非药物干预力度，请继续保持，并按提示监测%s血糖。",
                    context,context);
            default_interval = Math.min(180L, 2 * default_interval);
        } else {
            if ( BMI_ACT_NO )
                suggestion = String.format("%s血糖已达糖尿病水平。减重、控制饮食或增加身体活动仍有较大的降糖空间，建议借助本系统提供的“体重管理”和“身体活动管理”功能模块进行非药物干预。尤其注意适当减少能量摄入，增加%s的运动量，并按提示监测%s血糖，如持续升高建议及早就医！",
                        context,context,context);
            else
                suggestion = String.format("%s血糖已达糖尿病水平。单纯非药物干预似乎很难使%s的血糖达标，建议尽早就医，并按提示监测%s血糖。",
                        context,context,context);
            default_interval = Math.min(30L,default_interval/2L);
        }
        monitor_interval.put(moment,default_interval);

        return suggestion;
    }
    public static String GetSuggestionByLevel(String moment, Double level, boolean BMI_ACT_NO) {
        if ( moment.compareTo("wfq") == 0 ) {
            if ( level == -2.0 ) return "午餐前出现了低血糖！建议规律早午餐或适当提前午餐时间或在午餐前吃些水果或零食，平时做好应对低血糖的准备，并按提示监测午餐前血糖，如再次发生低血糖或出现低血糖症状，建议尽早看医生。";
            else if ( level <= 0.0 ) return "本次午餐前血糖正常，无需特殊处理，可按提示监测午餐前血糖。";
            else if ( BMI_ACT_NO ) return "本次午餐前血糖高于正常。减重、控制饮食或增加身体活动仍有较大的降糖空间，建议借助本系统提供的“体重管理”和“身体活动管理”功能模块进行非药物干预。尤其注意适当减少晚餐的能量摄入，增加晚餐后的运动量，并按提示监测午餐前血糖，如持续升高建议及早就医！";
            else return "本次午餐前血糖高于正常。单纯非药物干预似乎很难使午餐前血糖达标，建议尽早就医，并按提示监测午餐前血糖。";
        } else if ( moment.compareTo("wcq") == 0 ) {
            if ( level == -2.0 ) return "晚餐前出现了低血糖！建议规律午晚餐或适当提前晚餐时间或在晚餐前吃些水果或零食，平时做好应对低血糖的准备，并按提示监测晚餐前血糖，如再次发生低血糖或出现低血糖症状，建议尽早看医生。";
            else if ( level <= 0.0 ) return "本次晚餐前血糖正常，无需特殊处理，可按提示监测晚餐前血糖。";
            else if ( BMI_ACT_NO ) return "本次晚餐前血糖高于正常。减重、控制饮食或增加身体活动仍有较大的降糖空间，建议借助本系统提供的“体重管理”和“身体活动管理”功能模块进行非药物干预。尤其注意适当减少晚餐的能量摄入，增加晚餐后的运动量，并按提示监测晚餐前血糖，如持续升高建议及早就医！";
            else return "本次晚餐前血糖高于正常。单纯非药物干预似乎很难使晚餐前血糖达标，建议尽早就医，并按提示监测晚餐前血糖。";
        } else if ( moment.compareTo("sq") == 0 ) {
            if ( level == -2.0 ) return "睡前出现了低血糖！建议规律晚餐或适当延迟晚餐时间或在睡前吃些水果或零食，平时做好应对低血糖的准备，并按提示监测睡前血糖，如再次发生低血糖或出现低血糖症状，建议尽早看医生。";
            else if ( level <= 0.0 ) return "本次睡前血糖正常，无需特殊处理，可按提示监测睡前血糖。";
            else if ( BMI_ACT_NO ) return "本次睡前血糖高于正常。减重、控制饮食或增加身体活动仍有较大的降糖空间，建议借助本系统提供的“体重管理”和“身体活动管理”功能模块进行非药物干预。尤其注意适当减少晚餐的能量摄入，增加晚餐后的运动量，并按提示监测睡前血糖，如持续升高建议及早就医！";
            else return "单纯非药物干预似乎很难使睡前血糖达标，建议尽早就医，并按提示监测睡前血糖。";
        } else if ( moment.compareTo("yj") == 0 ) {
            if ( level == -2.0) return "夜间出现了低血糖！建议规律晚餐或适当延迟晚餐时间或在睡前吃些水果或零食，平时做好应对低血糖的准备，并按提示监测夜间血糖，如再次发生低血糖或出现低血糖症状，建议尽早看医生。";
            else return "本次夜间血糖不低，无需特殊处理，可按提示监测夜间血糖。";
        } else if ( moment.compareTo("ydh") == 0 ) {
            if ( level == -2.0 ) return "运动后出现了低血糖！建议避免餐前空腹进行长时间或过于激烈的运动，或在运动前适当吃些富含能量的甜点或零食，出门随身携带能够快速补充糖分的小零食，察觉有心慌或出虚汗时尽早食用，并按提示监测运动后血糖，如再次发生低血糖或出现低血糖症状，建议尽早看医生。";
            else if ( level <= 0.0 ) return "本次运动后血糖正常，无需特殊处理，可按提示监测运动后血糖。";
            else return "本次运动后血糖高于正常，请注意监测早晨空腹甚至是午晚餐餐前血糖水平，以便判断是否及如何处理。";
        }
        return "GetSuggetionNone";
    }
    public static String GetRefLevelSuggestionWithLevelWithoutDrug(String patientName,
                                                                   String moment,
                                                                   Double value,
                                                                   Double level,
                                                                   boolean BMI_ACT_NO,
                                                                   JSONObject moniter_interval)
    {

        String suggestion = null;
        Long default_interval = moniter_interval.getLong(moment);
        if ( level == -2.0 ) {
            suggestion = GetSuggestionByLevel(moment, level, BMI_ACT_NO);
            default_interval = 7L;
        } else if ( level <= 0.0 ) {
            suggestion = GetSuggestionByLevel(moment, level, BMI_ACT_NO);
            default_interval = 4*default_interval;
        } else {
            suggestion = GetSuggestionByLevel(moment, level, BMI_ACT_NO);
            default_interval = Math.min(30L,default_interval/2L);
        }
        moniter_interval.put(moment,default_interval);
        return suggestion;
    }

    public static String GetRefLevelSuggestionWithLevelWithoutDrugYJ(String patientName,
                                                                      String moment,
                                                                      Double value,
                                                                      Double level,
                                                                      boolean BMI_ACT_NO,
                                                                      JSONObject moniter_interval) {
        String suggestion = null;
        Long default_interval = moniter_interval.getLong(moment);
        if ( level == -2.0 ) {
            suggestion = GetSuggestionByLevel(moment,level, BMI_ACT_NO);
            default_interval = 7L;
        } else {
            suggestion = GetSuggestionByLevel(moment,level, BMI_ACT_NO);
            default_interval = 4 * default_interval;
        }
        moniter_interval.put(moment,default_interval);
        return suggestion;
    }

    public static String GetRefLevelSuggestionContextWihtoutDrug(String patientName,
                                                                 String moment,
                                                                 Double value,
                                                                 Double level,
                                                                 Double degree,
                                                                 boolean BMI_PA_NO,
                                                                 List<String> low_degrees,
                                                                 JSONObject moniter_interval) {
        String context = GetMomentContext(moment);
        String suggestion = null;
        Long default_interval = moniter_interval.getLong(moment);
        if ( degree >= 1.0 ) {
            if ( moment.compareTo("kf") == 0 ) {
                suggestion = GetRefLevelSuggestionWithLevelithoutDrugFK(patientName, value, level, BMI_PA_NO, moniter_interval);
            } else if ( moment.compareTo("zch") == 0 ) {
                suggestion = GetRefLevelSuggestionWithLevelWithoutDrug(patientName,moment,value,level,BMI_PA_NO,moniter_interval);
            } else if ( moment.compareTo("wfq") == 0 ) {
                suggestion = GetRefLevelSuggestionWithLevelWithoutDrug(patientName,moment,value,level,BMI_PA_NO,moniter_interval);
            } else if ( moment.compareTo("wfh") == 0 ) {
                suggestion = GetRefLevelSuggestionWithValueWithoutDrug(patientName,moment,value,level,BMI_PA_NO,moniter_interval);
            } else if ( moment.compareTo("wcq") == 0 ) {
                suggestion = GetRefLevelSuggestionWithLevelWithoutDrug(patientName,moment,value,level,BMI_PA_NO,moniter_interval);
            } else if ( moment.compareTo("wch") == 0 ) {
                suggestion = GetRefLevelSuggestionWithValueWithoutDrug(patientName,moment,value,level,BMI_PA_NO,moniter_interval);
            } else if ( moment.compareTo("sq")  == 0 ) {
                suggestion = GetRefLevelSuggestionWithLevelWithoutDrug(patientName,moment,value,level,BMI_PA_NO,moniter_interval);
            } else if ( moment.compareTo("yj") == 0 ) {
                suggestion = GetRefLevelSuggestionWithLevelWithoutDrugYJ(patientName, moment, value, level, BMI_PA_NO, moniter_interval);
            } else if ( moment.compareTo("ydh") == 0 ) {
                suggestion = GetRefLevelSuggestionWithLevelWithoutDrug(patientName,moment,value,level,BMI_PA_NO,moniter_interval);
            }
        } else {
            if ( moment.compareTo("ydq") != 0 && moment.compareTo("ydh") != 0)
                low_degrees.add(context);
        }
        moniter_interval.put(moment,default_interval);
        return suggestion;
    }

    public static List<String> GetRefLevelSuggestion(String patientName,
                                                     JSONObject reference,
                                                     boolean BMI_PA_NO,
                                                     Map<String,PatientUseDrug> patientUseDrugMap,
                                                     Map<String,DrugInfo> drugInfoMap,
                                                     Map<String,Double> compRelateMap,
                                                     Map<String,DrugComp> drugCompMap,
                                                     JSONObject moniter_intervals) {
        List<String> suggestions = new ArrayList<String>();
        JSONObject values = reference.getJSONObject("values");
        JSONObject degrees = reference.getJSONObject("degrees");
        List<String> low_degrees = new ArrayList<String>();
        if ( drugInfoMap == null || drugInfoMap.size() == 0 ) { // no drug
            for ( String key : QuotaConst.SUB_QUOTA_IDS.keySet() ) {
                if ( values.containsKey(key) ) {
                    Double value = values.getDouble(key);
                    Double level = BdSugarLevel(key, value);
                    Double degree = degrees.getDouble(key);
                    String suggestion = GetRefLevelSuggestionContextWihtoutDrug(patientName, key, value, level, degree, BMI_PA_NO, low_degrees, moniter_intervals);
                    if (suggestion != null) suggestions.add(suggestion);
                }
            }
            suggestions.add(String.format("以上未对可参考度较低的时点血糖%s进行评价。",low_degrees));

        } else { // withdrug

            Set<String> TYPE_YDS = DrugServiceUtils.CompType(null,compRelateMap,drugCompMap, "胰岛素");
            Set<String> TYPE_TGMYZS = DrugServiceUtils.CompType(null,compRelateMap,drugCompMap, "α-糖苷酶抑制剂");
            Set<String> AIM_JT = DrugServiceUtils.CompAim(null,compRelateMap,drugCompMap,"高血糖");
            Set<String> LONG_HYL = DrugServiceUtils.CompProcess(DrugServiceUtils.DrugTech(DrugServiceUtils.CompType(null, compRelateMap,drugCompMap, "磺脲类"),compRelateMap,drugInfoMap),compRelateMap,drugCompMap,4);
            Set<String> LONG_GLNL = DrugServiceUtils.CompProcess(DrugServiceUtils.DrugTech(DrugServiceUtils.CompType(null, compRelateMap,drugCompMap, "格列奈类"),compRelateMap,drugInfoMap),compRelateMap,drugCompMap, 4);
            Map<String,String> BLONG_17_HYL = DrugServiceUtils.DrugTime(DrugServiceUtils.CompProcess(DrugServiceUtils.DrugTech(DrugServiceUtils.CompType(null, compRelateMap,drugCompMap, "磺脲类"), compRelateMap,drugInfoMap), compRelateMap,drugCompMap, 3),compRelateMap,patientUseDrugMap,"wcq","sq");
            Map<String,String> BLONG_17_GLNL = DrugServiceUtils.DrugTime(DrugServiceUtils.CompProcess(DrugServiceUtils.DrugTech(DrugServiceUtils.CompType(null, compRelateMap,drugCompMap, "格列奈类"), compRelateMap,drugInfoMap), compRelateMap,drugCompMap, 3), compRelateMap,patientUseDrugMap, "wcq", "sq");

            JSONObject levels = new JSONObject();
            for ( String key : values.keySet() ) {
                levels.put(key,BdSugarLevel(key, values.getDouble(key)));
            }


            if ( TYPE_YDS.size() == 0 ) {
                if ( degrees.getDouble("kf") >= 1.0 && levels.getDouble("kf") <= -1.0 ) {
                    suggestions.add("出现了早晨空腹低血糖。");
                    if ( AIM_JT.size() != 0 && TYPE_TGMYZS.size() != 0 )
                        suggestions.add(String.format("特殊用药提醒： %s目前正在使用α-糖苷酶抑制剂（%s)，它可抑制蔗糖和淀粉类食物的吸收，因此用传统食物缓解低血糖的效果较差。建议家中常备葡萄糖或蜂蜜，其缓解低血糖的效果更佳。",
                                patientName, DrugServiceUtils.GetProductionName(TYPE_TGMYZS, drugInfoMap)));
                    if ( LONG_HYL != null )
                        suggestions.add(String.format("特殊用药提醒：目前患者正在使用的%s属磺脲类降糖药或含有该成分，它极易导致低血糖，建议首先对其减量或停用，再尽早咨询医生，同时连续监测低血糖发生情况。",
                                DrugServiceUtils.GetProductionName(LONG_HYL,drugInfoMap)));
                    if ( BLONG_17_HYL != null ) {
                        String product_name = DrugServiceUtils.GetProductionName(BLONG_17_HYL.entrySet().iterator().next(),drugInfoMap);
                        suggestions.add(String.format("特殊用药提醒：目前患者正在使用的%s属磺脲类降糖药或含有该成分，它很可能是导致早餐前低血糖的罪魁祸首，建议首先对【使用时间】使用的%s进行减量或停用，再尽早咨询医生，同时连续监测低血糖发生情况。",
                                product_name,product_name));
                    }
                    if ( LONG_GLNL != null )
                        suggestions.add(String.format("特殊用药提醒：目前患者正在使用的%s属格列奈类降糖药或含有该成分，它很可能与早餐前低血糖有关，建议首先对其减量或停用，再尽早咨询医生，同时连续监测低血糖发生情况。",
                                DrugServiceUtils.GetProductionName(LONG_GLNL,drugInfoMap)));
                    if ( BLONG_17_GLNL != null ) {
                        String product_name = DrugServiceUtils.GetProductionName(BLONG_17_GLNL.entrySet().iterator().next(),drugInfoMap);
                        String use_moment = DrugServiceUtils.GetMomentContext(BLONG_17_GLNL.entrySet().iterator().next());
                        suggestions.add(String.format("特殊用药提醒：目前患者正在使用的%s属格列奈类降糖药或含有该成分，它很可能与早餐前低血糖有关，建议首先对%s使用的%s进行减量或停用，再尽早咨询医生，同时连续监测低血糖发生情况。",
                                product_name, use_moment, product_name));
                    }
                    if ( LONG_HYL == null && BLONG_17_HYL == null && LONG_GLNL == null && BLONG_17_GLNL == null )
                        suggestions.add(String.format("系统未发现%s正在使用特别易于诱发早晨低血糖的用药。",patientName));
                    else suggestions.add("需要注意，出现低血糖还可能与其他降糖药的使用（尤其当成分不明时）、晚餐过少或过早、早餐过晚、饮茶或咖啡及心理变化等因素有关，须尽量避免上述诱因。");
                    moniter_intervals.put("kf",1L);
                }

                if ( degrees.getDouble("wfq") >= 1.0 && levels.getDouble("wfq") <= -1.0 ) {
                    suggestions.add("出现了午餐前低血糖。");
                    if ( AIM_JT != null && TYPE_TGMYZS != null )
                        suggestions.add(String.format("特殊用药提醒： %s目前正在使用α-糖苷酶抑制剂（%s)，它可抑制蔗糖和淀粉类食物的吸收，因此用传统食物缓解低血糖的效果较差。建议家中常备葡萄糖或蜂蜜，其缓解低血糖的效果更佳。",
                                patientName, DrugServiceUtils.GetProductionName(TYPE_TGMYZS,drugInfoMap)));
                    if ( LONG_HYL != null )
                        suggestions.add(String.format("特殊用药提醒：目前患者正在使用的%s属磺脲类降糖药或含有该成分，它很可能是导致午餐前低血糖的罪魁祸首，建议首先对其减量或停用，再尽早咨询医生，同时连续监测低血糖发生情况。",
                                DrugServiceUtils.GetProductionName(LONG_HYL, drugInfoMap)));
                    if ( BLONG_17_HYL != null ) {
                        String product_name = DrugServiceUtils.GetProductionName(BLONG_17_HYL.entrySet().iterator().next(),drugInfoMap);
                        String use_moment = DrugServiceUtils.GetMomentContext(BLONG_17_HYL.entrySet().iterator().next());
                        suggestions.add(String.format("特殊用药提醒：目前患者正在使用的%s属磺脲类降糖药或含有该成分，它很可能是导致早餐前低血糖的罪魁祸首，建议首先对%s使用的%s进行减量或停用，再尽早咨询医生，同时连续监测低血糖发生情况。",
                                product_name,use_moment,product_name));
                    }
                    if ( LONG_GLNL != null )
                        suggestions.add(String.format("特殊用药提醒：目前患者正在使用的%s属格列奈类降糖药或含有该成分，它很可能与早餐前低血糖有关，建议首先对其减量或停用，再尽早咨询医生，同时连续监测低血糖发生情况。",
                                DrugServiceUtils.GetProductionName(LONG_GLNL, drugInfoMap)));
                    if ( BLONG_17_GLNL != null ) {
                        String product_name = DrugServiceUtils.GetProductionName(BLONG_17_GLNL.entrySet().iterator().next(),drugInfoMap);
                        String use_moment = DrugServiceUtils.GetMomentContext(BLONG_17_GLNL.entrySet().iterator().next());
                        suggestions.add(String.format("特殊用药提醒：目前患者正在使用的%s属格列奈类降糖药或含有该成分，它很可能与早餐前低血糖有关，建议首先对%s使用的%s进行减量或停用，再尽早咨询医生，同时连续监测低血糖发生情况。",
                                product_name,use_moment,product_name));
                    }
                    if ( LONG_HYL == null && BLONG_17_HYL == null && LONG_GLNL == null && BLONG_17_GLNL == null )
                        suggestions.add(String.format("系统未发现%s正在使用特别易于诱发早晨低血糖的用药。",patientName));
                    else suggestions.add("需要注意，出现低血糖还可能与其他降糖药的使用（尤其当成分不明时）、晚餐过少或过早、早餐过晚、饮茶或咖啡及心理变化等因素有关，须尽量避免上述诱因。");
                    moniter_intervals.put("kf",1L);
                }

            } else {

            }

        }

        return suggestions;
    }


    public static Double GetAvgSugarValue(JSONArray sugarArray) {
        Double sugarvalue = 0.0;
        for ( int i = 0; i < sugarArray.size(); ++ i) {
            if ( i > 2 ) break;
            sugarvalue += sugarArray.getJSONObject(i).getDouble("sugarvalue");
        }
        return sugarvalue / sugarArray.size();
    }

    public static String GetMomentContext(String key)
    {
        if ( key.compareTo("kf") == 0) return "早餐前空腹";
        if ( key.compareTo("zch") == 0) return "早餐后2小时";
        if ( key.compareTo("wfq") == 0) return "午餐前";
        if ( key.compareTo("wfh") == 0) return "午饭后2小时";
        if ( key.compareTo("wcq") == 0) return "晚餐前";
        if ( key.compareTo("wch") == 0) return "晚餐后2小时";
        if ( key.compareTo("sq") == 0) return "睡前";
        if ( key.compareTo("yj") == 0) return "夜间";
        if ( key.compareTo("ydq") == 0) return "运动前";
        if ( key.compareTo("ydh") == 0) return "运动后";
        return "NONE";
    }

    public static boolean GetRefBSAndDegreeKF(String patient_name,
                                              Long monitor_interval,
                                              JSONObject values_lastest,
                                              JSONObject values_in_ref_duration,
                                              List<String> suggestions,
                                              JSONObject references) {
        String moment = "kf";
        String moment_context = GetMomentContext(moment);
        String suggestion = null;
        Double ref_value;
        Double ref_degree;
        if ( !values_in_ref_duration.containsKey(moment) ) { // ref_duration 内无记录
            if ( values_lastest.containsKey(moment) ) {
                Quota quota = values_lastest.getObject(moment,Quota.class);
                if ( TimeUtils.dateDiffToNow(quota.getMeasure_date()) > monitor_interval ) {
                    suggestion = String.format("患者%s天内一直没有记录%s血糖变化，系统无法进行评估！",monitor_interval,moment_context);
                    if (suggestions!=null)  suggestions.add(suggestion);
                    return false;
                } else {
                    ref_value = JSONObject.parseObject(quota.getRecord()).getDouble("sugarvalue");
                    if ( ref_value <= 2.8 ) {
                        ref_degree = 1.0;
                        suggestion = String.format("%s%s低血糖(可参考度：中)",TimeUtils.DateFormat(quota.getMeasure_date()),moment_context);
                    } else if ( ref_value < 11.1 ) {
                        ref_degree = 0.5;
                        suggestion = String.format("近期无%s血糖记录，暂用%s的血糖值替代(可参考度：低)",moment_context,TimeUtils.DateFormat(quota.getMeasure_date()));
                    } else {
                        ref_degree = 1.0;
                        suggestion = String.format("近期无%s血糖记录，暂用%s的血糖值替代(可参考度：中)",moment_context,TimeUtils.DateFormat(quota.getMeasure_date()));
                    }
                }
            } else {
                if ( suggestions != null )
                suggestions.add(String.format("患者%d天内一直没有记录%s血糖变化，系统无法进行评估！", monitor_interval, moment_context));
                return false;
            }
        } else if ( values_in_ref_duration.getJSONArray(moment).size() == 1 ) { // ref_duration 1 条记录
            JSONObject lastest_quota = values_in_ref_duration.getJSONArray(moment).getJSONObject(0);
            ref_value = lastest_quota.getDouble("sugarvalue");
            String date_string = TimeUtils.DateFormat(TimeUtils.parseDate(lastest_quota.getLong("measure_date")));
            if ( ref_value <= 2.8 ) {
                ref_degree = 3.0;
                suggestion = String.format("%s%s低血糖(可参考度：很高)",date_string,moment_context);
            } else if ( ref_value < 11.1 ) {
                ref_degree = 1.0;
                suggestion = String.format("%s%s血糖%.2f(可参考度：中)",date_string,moment_context,ref_value);
            } else if ( ref_value < 13.9 ) {
                ref_degree = 2.0;
                suggestion = String.format("%s%s血糖%.2f(可参考度：高)",date_string,moment_context,ref_value);
            } else {
                ref_degree = 3.0;
                suggestion = String.format("%s%s血糖%.2f(可参考度：很高)",date_string,moment_context,ref_value);
            }
        } else if ( values_in_ref_duration.getJSONArray(moment).size() == 2 ) { // ref_duration 2 条记录
            Double lastest_value = values_in_ref_duration.getJSONArray(moment).getJSONObject(0).getDouble("sugarvalue");
            ref_value = GetAvgSugarValue(values_in_ref_duration.getJSONArray(moment));
            if ( lastest_value < 11.1 ) {
                ref_degree = 2.0;
                suggestion = String.format("最近两次%s血糖平均值%.2f(可参考度：高)",moment_context,ref_value);
            } else {
                ref_degree = 3.0;
                suggestion = String.format("最近两次%s血糖平均值%.2f(可参考度：很高)",moment_context,ref_value);
            }
        } else {
            ref_value = GetAvgSugarValue(values_in_ref_duration.getJSONArray(moment));
            ref_degree = 3.0;
            suggestion = String.format("最近三次%s血糖平均值%.2f(可参考度：很高)",moment_context,ref_value);
        }

        if ( suggestions != null )
            suggestions.add(suggestion);

        references.getJSONObject("values").put(moment,ref_value);
        references.getJSONObject("degrees").put(moment,ref_degree);
        return true;
    }

    public static boolean GetRefBSAndDegreeH(String patient_name,
                                             Long monitor_interval,
                                             String key,
                                             JSONObject lastest_quotas,
                                             JSONObject lastest_in_duration,
                                             List<String> suggestions,
                                             JSONObject references)
    {
        String moment_context = GetMomentContext(key);
        Double kf_value = references.getJSONObject("values").getDouble("kf");
        Double ref_value = null;
        Double ref_degree = null;
        String suggestion = null;
        if ( !lastest_in_duration.containsKey(key) ) { // 无记录
            if ( lastest_quotas.containsKey(key) ) {
                Quota item = lastest_quotas.getObject(key,Quota.class);
                Double lastest_bs = JSONObject.parseObject(item.getRecord()).getDouble("sugarvalue");
                String date_string = TimeUtils.DateFormat(item.getMeasure_date());
                if ( lastest_bs < 16.6 ) {
                    ref_degree = 0.5;
                    ref_value = lastest_bs;
                    suggestion = String.format("近期无%s血糖，暂用%s的%s血糖%.2f替代(可参考度：低)",moment_context,date_string,moment_context,ref_value);
                } else {
                    ref_degree = 1.0;
                    ref_value = lastest_bs;
                    suggestion = String.format("近期无%s血糖，暂用%s的%s血糖%.2f替代(可参考度：中)",moment_context,date_string,moment_context,ref_value);
                }
            }

            if ( ref_value == null ) {
                if (kf_value < 7) ref_value = 7.0;
                else ref_value = 12.0;
                ref_degree = 0.2;
            }
        } else if ( lastest_in_duration.getJSONArray(key).size() == 1 ) { // ref_duration 1条记录
            JSONObject lastest_quota = lastest_in_duration.getJSONArray(key).getJSONObject(0);
            ref_value = lastest_quota.getDouble("sugarvalue");
            String date_string = TimeUtils.DateFormat(TimeUtils.parseDate(lastest_quota.getLong("measure_date")));
            if ( ref_value < 16.6 ) {
                ref_degree = 1.0;
                suggestion = String.format("%s%s血糖%.2f(可参考度：中)",date_string,moment_context,ref_value);
            } else {
                ref_degree = 2.0;
                suggestion = String.format("%s%s血糖%.2f(可参考度：高)",date_string,moment_context,ref_value);
            }
        } else if (lastest_in_duration.getJSONArray(key).size() == 2 ) {
            JSONObject lastest_quota = lastest_in_duration.getJSONArray(key).getJSONObject(0);
            ref_value = GetAvgSugarValue(lastest_in_duration.getJSONArray(key));
            if (lastest_quota.getDouble("sugarvalue") < 16.6) {
                ref_degree = 2.0;
                suggestion = String.format("最近两次%s血糖平均值%.2f(可参考度：高)",moment_context,ref_value);
            } else {
                ref_degree = 3.0;
                suggestion = String.format("最近两次%s血糖平均值%.2f(可参考度：很高)",moment_context,ref_value);
            }
        } else {
            ref_value = GetAvgSugarValue(lastest_in_duration.getJSONArray(key));
            ref_degree = 3.0;
            suggestion = String.format("最近三次%s血糖平均值%.2f",moment_context,ref_value);
        }
        references.getJSONObject("values").put(key,ref_value);
        references.getJSONObject("degrees").put(key,ref_degree);

        if ( suggestion != null )
            suggestions.add(suggestion);

        return true;
    }

    public static boolean GetRefBSAndDegreeQ(String patient_name,
                                             Long moniter_interval,
                                             String key,
                                             JSONObject lastest_quotas,
                                             JSONObject lastest_in_duration,
                                             List<String> suggestions,
                                             JSONObject references)
    {

        String moment_context = GetMomentContext(key);
        String suggestion = null;
        Double ref_value = null;
        Double ref_degree = null;

        if ( !lastest_in_duration.containsKey(key) ) { // 无记录
            Double kf_value = references.getJSONObject("values").getDouble("kf");
            Double kf_degree = references.getJSONObject("degrees").getDouble("kf");

            if ( lastest_quotas.containsKey(key) ) {
                Quota item = lastest_quotas.getObject(key,Quota.class);
                Double lastest_bs = JSONObject.parseObject(item.getRecord()).getDouble("sugarvalue");
                String date_string = TimeUtils.DateFormat(item.getMeasure_date());
                if ( lastest_bs <= 2.8 ) {
                    ref_degree = 1.0;
                    ref_value = lastest_bs;
                    suggestion = String.format("%s%s低血糖(可参考度：中)",date_string,moment_context);
                } else if ( lastest_bs < 11.1 && kf_degree <= 1.0 ) {
                    ref_degree = 0.2;
                    ref_value = lastest_bs;
                    suggestion = String.format("近期无%s血糖记录，暂用%s的%s血糖%.2f替代(可参考度：低)",moment_context,date_string,moment_context,ref_value);
                } else if ( lastest_bs < 11.1 && kf_degree > 1.0 ) {
                    ref_value = kf_value;
                    ref_degree = 0.5;
                    suggestion = String.format("近期无%s血糖记录，暂用%s%s血糖%.2f替代(可参考度：低)",moment_context,date_string,moment_context,ref_value);
                } else {
                    ref_value = lastest_bs;
                    ref_degree = 1.0;
                    suggestion = String.format("近期无%s记录，暂用%s%s血糖%.2f替代(可参考度：中)",moment_context,date_string,moment_context,ref_value);

                }
            }

            if ( ref_value == null ) {
                ref_value = Math.max(6.0,kf_value);
                ref_degree = 0.2;
            }

        } else if ( lastest_in_duration.getJSONArray(key).size() == 1 ) { // ref_duration 1条记录
            JSONObject lastest_quota = lastest_in_duration.getJSONArray(key).getJSONObject(0);
            ref_value = lastest_quota.getDouble("sugarvalue");
            String date_string = TimeUtils.DateFormat(TimeUtils.parseDate(lastest_quota.getLong("measure_date")));
            if ( ref_value <= 2.8 ) {
                ref_degree = 3.0;
                suggestion = String.format("%s%s血糖低血糖(可参考度：很高)",date_string,moment_context);
            } else if ( ref_value < 11.1 ){
                ref_degree = 1.0;
                suggestion = String.format("%s%s血糖%.2f(可参考度：中)",date_string,moment_context,ref_value);
            } else if ( ref_value < 13.9 ){
                ref_degree = 2.0;
                suggestion = String.format("%s%s血糖%.2f(可参考度：高)",date_string, moment_context, ref_value);
            } else {
                ref_degree = 3.0;
                suggestion = String.format("%s%s血糖%.2f(可参考度：很高)",date_string,moment_context,ref_value);
            }
        } else if (lastest_in_duration.getJSONArray(key).size() == 2 ) {
            JSONObject lastest_quota = lastest_in_duration.getJSONArray(key).getJSONObject(0);
            ref_value = GetAvgSugarValue(lastest_in_duration.getJSONArray(key));
            if (lastest_quota.getDouble("sugarvalue") < 11.1) {
                ref_degree = 2.0;
                suggestion = String.format("最近两次%s血糖平均值%.2f(可参考度：高)",moment_context,ref_value);
            } else {
                ref_degree = 3.0;
                suggestion = String.format("最近两次%s血糖平均值%.2f(可参考度：很高)",moment_context,ref_value);
            }
        } else {
            ref_value = GetAvgSugarValue(lastest_in_duration.getJSONArray(key));
            ref_degree = 3.0;
            suggestion = String.format("最近三次%s血糖平均值%.2f", moment_context,ref_value);
        }

        references.getJSONObject("values").put(key,ref_value);
        references.getJSONObject("degrees").put(key,ref_degree);
        if ( suggestion != null )
            suggestions.add(suggestion);
        return true;
    }

    public static String TrustedLevel(Double degree) {
        if ( degree >= 3.0 ) return "很高";
        else if ( degree >= 2.0 ) return "高";
        else if ( degree >= 1.0 ) return "中";
        else if ( degree >= 0.5 ) return "低";
        else return "很低";
    }

    public static void GetReferenceValueAndDegreeA1C(List<Quota> a1cQuotaList, Long change_days, List<String> suggestions, JSONObject reference) {

        Double ref_a1c_degree = null;
        Double ref_a1c = null;
        Double ref_a1c_value = null;
        Double ref_a1c_todate = null;
        String suggestion = null;
        if ( a1cQuotaList == null || a1cQuotaList.size() == 0) {
            ref_a1c = 5.0;
            ref_a1c_degree = 0.2;
        } else {
            Quota a1cQuota =a1cQuotaList.get(0);
            ref_a1c_todate = (30-TimeUtils.dateDiffToNow(a1cQuota.getMeasure_date())) / 20.0;
            ref_a1c_value = (change_days - TimeUtils.dateDiffToNow(a1cQuota.getMeasure_date()) - 15L)/30.0;
            if ( ref_a1c_todate > 0.0 && ref_a1c_value > 0.0 ) {
                ref_a1c_degree = ref_a1c_todate * ref_a1c_value;
                if ( ref_a1c_degree >= 3.0 ) ref_a1c_degree = 3.0;
                else if ( ref_a1c_degree >= 2.0 ) ref_a1c_degree = 2.0;
                else if ( ref_a1c_degree >= 1.0 ) ref_a1c_degree = 1.0;
                else if ( ref_a1c_degree >= 0.5 ) ref_a1c_degree = 0.5;
                else ref_a1c_degree = 0.2;
            }
        }
        if ( a1cQuotaList != null && a1cQuotaList.size() > 0) {
            Quota a1cQuota = a1cQuotaList.get(0);
            String monitor_date = a1cQuota.getMeasure_date().toString();
            if ( ref_a1c_value <= 0.0 ) suggestion = String.format("A1c可反映过去2-3个月血糖控制的平均水平，因此如需判断现行治疗方案是否合适，需在稳定治疗2-3个月以后在进行A1c检测。本次A1c的检测日期%s距最近的治疗方案调整日期%s太近，甚至是在之前，这对进一步调整治疗方案没有参考意义。建议在%s前后再查一次A1c。",
                    monitor_date,TimeUtils.getDate(change_days*-1),TimeUtils.getDate(90L-change_days));
            else if ( ref_a1c_todate <= 0.0 ) suggestion = String.format("最近的A1c是一个月前所查，系统将不再据此做出评价。");
            else if ( ref_a1c_value > 0.0 && ref_a1c_degree > 0.0 ) {
                ref_a1c = JSONObject.parseObject(a1cQuota.getRecord()).getDouble("a1c");
                suggestion = String.format("%s的糖化血红蛋白%.2f（可参考度：%s）",
                        monitor_date,ref_a1c,TrustedLevel(ref_a1c_degree));
            }
        }
        reference.getJSONObject("values").put("a1c",ref_a1c);
        reference.getJSONObject("degrees").put("a1c",ref_a1c_degree);

        if (suggestion != null)  suggestions.add(suggestion);
    }



    public static JSONObject GetReferenceValueAndDegree(CurusDriver driver,
                                                        Long account_id,
                                                        Long patient_id,
                                                        String patientName,
                                                        Long last_change_days,
                                                        JSONObject moniter_interval,
                                                        List<String> suggestions) {

        JSONObject values_in_ref_duration = new JSONObject();
        QuotaServiceUtils.listQuotas(driver,Math.min(7L,last_change_days),account_id,patient_id,QuotaConst.QUOTA_BS,null,values_in_ref_duration);
        JSONObject values_lastest = driver.quotaDao.selectLastestBSQuota(account_id,patient_id);

        JSONObject reference_value_degree = new JSONObject();
        reference_value_degree.put("values",new JSONObject());
        reference_value_degree.put("degrees",new JSONObject());

        {
            if ( ! GetRefBSAndDegreeKF (patientName,moniter_interval.getLong("kf"),
                    values_lastest,values_in_ref_duration,suggestions,reference_value_degree) )
                return reference_value_degree;
        }

        if ( suggestions != null ) {
            GetRefBSAndDegreeH(patientName, moniter_interval.getLong("zch"), "zch", values_lastest, values_in_ref_duration, suggestions, reference_value_degree);
            GetRefBSAndDegreeQ(patientName, moniter_interval.getLong("wfq"), "wfq", values_lastest, values_in_ref_duration, suggestions, reference_value_degree);
            GetRefBSAndDegreeH(patientName, moniter_interval.getLong("wfh"), "wfh", values_lastest, values_in_ref_duration, suggestions, reference_value_degree);
            GetRefBSAndDegreeQ(patientName, moniter_interval.getLong("wcq"), "wcq", values_lastest, values_in_ref_duration, suggestions, reference_value_degree);
            GetRefBSAndDegreeH(patientName, moniter_interval.getLong("wch"), "wch", values_lastest, values_in_ref_duration, suggestions, reference_value_degree);
            GetRefBSAndDegreeQ(patientName, moniter_interval.getLong("sq"), "sq", values_lastest, values_in_ref_duration, suggestions, reference_value_degree);
            GetRefBSAndDegreeQ(patientName, moniter_interval.getLong("yj"), "yj", values_lastest, values_in_ref_duration, suggestions, reference_value_degree);
            GetRefBSAndDegreeH(patientName, moniter_interval.getLong("ydq"), "ydq", values_lastest, values_in_ref_duration, suggestions, reference_value_degree);
            GetRefBSAndDegreeQ(patientName, moniter_interval.getLong("ydh"), "ydh", values_lastest, values_in_ref_duration, suggestions, reference_value_degree);


            List<Quota> a1cQuotaList = driver.quotaDao.selectLastestQuota(account_id, patient_id, QuotaConst.QUOTA_A1C_ID, 1L);
            //ALC
            GetReferenceValueAndDegreeA1C(a1cQuotaList, last_change_days, suggestions, reference_value_degree);

            suggestions.add("其他时点的血糖，在没有有效的结果之前暂按正常或参考空腹血糖水平处理。");
        }

        return  reference_value_degree;
    }

    public static JSONObject estimate(CurusDriver driver,
                                      Long account_id,
                                      Long patient_id) {
        JSONObject responseData = new JSONObject();

        Map<String,DrugInfo> drugInfoMap = new HashMap<String, DrugInfo>();
        Map<String,Double> drugCompRelationMap = new HashMap<String, Double>();
        Map<String,DrugComp> drugCompMap = new HashMap<String, DrugComp>();
        Map<String,PatientUseDrug> patientUseDrugMap = new HashMap<String, PatientUseDrug>();
        DrugServiceUtils.GetUseDrugAndDrugComp(driver,patient_id,drugInfoMap,drugCompRelationMap,drugCompMap,patientUseDrugMap);

        JSONObject moniter_interval = GetMonitorInterval(driver,account_id,patient_id,DrugServiceUtils.CompType(null,drugCompRelationMap,drugCompMap,"胰岛素")!=null);

        String patient_name = PatientServiceUtils.GetPatientName(driver,patient_id);

        Long lastChanges = GetQuotaChangeDays(driver,account_id,patient_id);

        List<String> reference_suggestion = new ArrayList<String>();
        JSONObject reference_value_degree = GetReferenceValueAndDegree(driver,account_id,patient_id,patient_name,lastChanges,moniter_interval,reference_suggestion);
        UpdateMoniterInterval(driver,patient_id,moniter_interval);

        Double BMI = SWeightSerivceUtils.GetBMI(driver,account_id,patient_id);
        Double PA =  SWeightSerivceUtils.GetActEnergy(driver, account_id, patient_id);

        List<String> level_suggestion = GetRefLevelSuggestion(patient_name,reference_value_degree,(BMI<22||PA>60),patientUseDrugMap,drugInfoMap, drugCompRelationMap,drugCompMap,moniter_interval);

        responseData.put("level_suggestion",level_suggestion);
        responseData.put("reference_suggestion",reference_suggestion);

        return responseData;
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
        JSONObject moniter_interval = GetMonitorInterval(driver,account_id,patient_id,null);
        if ( moniter_interval == null )
            moniter_interval = GetMonitorInterval(driver,account_id,patient_id,DrugServiceUtils.CompType(driver,patient_id,"胰岛素")!=null);
        Long change_lastday = GetQuotaChangeDays(driver,account_id,patient_id);
        JSONObject reference = GetReferenceValueAndDegree(driver,account_id,patient_id,null,change_lastday,moniter_interval,null);
        Double ref_sugar = reference.getJSONObject("values").containsKey("kf") == true ?
                reference.getJSONObject("values").getDouble("kf") : null;

        if ( ref_sugar == null ) {
            responseData.put("message","请进行血糖测量");
            return;
        }

        if ( diet == null ) {

            Double BMI = SWeightSerivceUtils.GetBMI(driver,account_id,patient_id);
            diet_decrease = Math.max((BMI-22)*0.05,0.0);
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
