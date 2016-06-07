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
import com.curus.utils.service.DrugUtils;
import com.curus.utils.service.drug.DrugServiceUtils;
import com.curus.utils.service.patient.PatientServiceUtils;
import com.curus.utils.service.quota.QuotaServiceUtils;
import com.curus.utils.service.supervise.weight.SWeightSerivceUtils;

import java.util.ArrayList;
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

    public static void MonitorIntervalSuggestionMomentByValue(JSONObject interval,
                                                       JSONObject suggestion,
                                                       String moment,
                                                       JSONObject moment_degree,
                                                       boolean BMI_ACT_NO,
                                                       List<String> low_degree)
    {

        String moment_context = GetMoment(moment);
        String moment_suggestion = null;
        Double ref_degree = moment_degree.getDouble("ref_degree");
        Double ref_bs = moment_degree.getDouble("ref_bs");
        Long default_interval = interval.getLong(moment);
        if ( ref_degree >= 1.0 ) {
            if (ref_bs < 7.8) {
                moment_suggestion = String.format("%s血糖正常，必要时再按照提示对%s血糖进行监测。",moment_context,moment_context);
                default_interval = Math.min(365L, 4 * default_interval);
            } else if ( ref_bs < 11.1 ) {
                if ( BMI_ACT_NO )
                    moment_suggestion = String.format("%s血糖偏高（糖耐量减低），但尚未达到糖尿病水平。减重、控制饮食或增加身体活动仍有较大的降糖空间，建议借助本系统提供的“体重管理”和“身体活动管理”功能模块进行非药物干预。尤其注意适当减少能量摄入，增加%s的运动量，并按提示监测%s血糖！",
                            moment_context,moment_context,moment_context);
                else moment_suggestion = String.format("%s血糖偏高（糖耐量减低），但尚未达到糖尿病水平。已有较大的非药物干预力度，请继续保持，并按提示监测%s血糖。",moment_context,moment_context);
                default_interval = Math.min(180L, 2 * default_interval);
            } else {
                if ( BMI_ACT_NO )
                    moment_suggestion = String.format("%s血糖已达糖尿病水平。减重、控制饮食或增加身体活动仍有较大的降糖空间，建议借助本系统提供的“体重管理”和“身体活动管理”功能模块进行非药物干预。尤其注意适当减少能量摄入，增加%s的运动量，并按提示监测%s血糖，如持续升高建议及早就医！",
                            moment_context,moment_context,moment_context);
                else
                    moment_suggestion = String.format("%s血糖已达糖尿病水平。单纯非药物干预似乎很难使%s的血糖达标，建议尽早就医，并按提示监测%s血糖。",moment_context,moment_context,moment_context);
                default_interval = Math.min(30L,default_interval/2L);
            }
        } else {
            low_degree.add(moment_context);
        }

        if ( moment_suggestion != null ) {
            interval.put(moment,default_interval);
            suggestion.put(moment,moment_suggestion);
        }
    }

    public static String GetSuggestion(String moment, Double level, boolean BMI_ACT_NO) {
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
    public static void MonitorIntervalSuggestionMomentByLevel(JSONObject interval,
                                                              JSONObject suggestion,
                                                              String moment,
                                                              JSONObject moment_degree,
                                                              boolean BMI_ACT_NO,
                                                              List<String> low_degree)
    {
        String moment_context = GetMoment(moment);
        String moment_suggestion = null;
        Double ref_degree = moment_degree.getDouble("ref_degree");
        Double ref_bs = moment_degree.getDouble("ref_bs");
        Double ref_level = BdSugarLevel(moment,ref_bs);
        Long default_interval = interval.getLong(moment);

        if ( ref_degree >= 1.0 ) {
            if ( ref_level == -2.0 ) {
                moment_suggestion = GetSuggestion(moment,ref_level,BMI_ACT_NO);
                default_interval = 7L;
            } else if ( ref_level <= 0.0 ) {
                moment_suggestion = GetSuggestion(moment,ref_level,BMI_ACT_NO);
                default_interval = 4*default_interval;
            } else {
                moment_suggestion = GetSuggestion(moment,ref_level,BMI_ACT_NO);
                default_interval = Math.min(30L,default_interval/2L);
            }

            interval.put(moment,default_interval);
            suggestion.put(moment,moment_suggestion);

        } else {
            low_degree.add(moment_context);
        }
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

        boolean UseYDS = patientUseDrugList == null ? false :
                DrugServiceUtils.DrugType(driver, patientUseDrugList, DrugUtils.GetCompTypeId("胰岛素"));
        if ( UseYDS ) {
            interval.put("kf", 1L);
            for ( Map.Entry<String,Long> entry : QuotaConst.SUB_QUOTA_IDS.entrySet() ) {
                interval.put(entry.getKey(),Math.min(interval.getLong(entry.getKey()),30L));
            }
        }

        if (ref_bs_degree == null || !ref_bs_degree.containsKey("kf") ) {
            return interval;
        }

        String patientName = PatientServiceUtils.GetPatientName(driver,patient_id);
        interval.put("suggestion",new JSONObject());
        JSONObject suggestion = interval.getJSONObject("suggestion");

        Double BMI = SWeightSerivceUtils.GetBMI(driver,account_id,patient_id);
        Double act_energy = SWeightSerivceUtils.GetActEnergy(driver,account_id,patient_id);
        boolean BMI_ACT_NO = ( BMI >= 24.0 || act_energy < 50.0);
        if (patientUseDrugList == null) { // 未用药


            List<String> low_degree = new ArrayList<String>();
            { // kf
                String moment = "kf";
                String moment_suggestion = null;
                JSONObject moment_degree = ref_bs_degree.getJSONObject(moment);
                Double ref_degree = moment_degree.getDouble("ref_degree");
                Double ref_bs = moment_degree.getDouble("ref_bs");
                Double ref_level = SBdSugarServiceUtils.BdSugarLevel(moment,ref_bs);
                Long default_interval = interval.getLong(moment);
                if ( ref_degree >= 1.0 ) {
                    if ( ref_level ==  -2.0 ) {
                        moment_suggestion = "早晨出现了低血糖！建议规律早餐或适当提前早餐时间或在晨起后吃些水果或零食，平时做好应对低血糖的准备，并按提示监测空腹血糖，如再次发生低血糖或出现低血糖症状，建议尽早看医生。";
                        default_interval = 7L;
                    } else if (ref_level <= 0.0 ) {
                        moment_suggestion = "空腹血糖处在正常水平，请保持健康的生活方式，按提示监测空腹血糖。";
                        interval.put("kf",Math.min(365L,default_interval));
                    } else if (ref_level == 0.5 ) {
                        moment_suggestion = "空腹血糖偏高（空腹血糖受损），建议强化健康生活方式，并按提示监测空腹血糖。";
                        if ( BMI_ACT_NO )
                            moment_suggestion = String.format("%s通过减重、控制饮食或增加身体活动，%s仍有很大空间可使血糖恢复正常或延迟进展为糖尿病！研究显示，超重肥胖者减重5%或强化生活方式干预可使糖尿病的发病率减少50%；低能量摄入（少吃同时结合低能量食物的摄入）和高能量消耗（主要通过有氧运动）的降糖作用均不亚于降糖药物。对超重肥胖者建议利用“体重管理”工具帮助少吃多动，双管齐下；对体重正常或偏瘦者可利用“身体活动管理”维持正常的糖代谢水平。", moment_suggestion, patientName);
                        default_interval = Math.min(90L,default_interval*2);
                    } else if (ref_level == 1.0) {
                        moment_suggestion = "空腹血糖达糖尿病水平！请按提示监测空腹血糖。";
                        if ( BMI_ACT_NO )
                            moment_suggestion = moment_suggestion+"空腹血糖长期得不到控制会导致心脑眼肾等脏器并发症。目前"+patientName+"在减重、控制饮食或增加身体活动等方面仍存在较大的降糖空间，强烈建议加大非药物干预力度，努力使血糖恢复正常！研究显示，超重肥胖者减重5%或强化生活方式干预可使糖尿病的发病率减少50%；低能量摄入（少吃同时结合低能量食物的摄入）和高能量消耗（主要通过有氧运动）的降糖作用均不亚于降糖药物。对超重肥胖者建议利用“体重管理”工具帮助少吃多动，双管齐下；对体重正常或偏瘦者可利用“身体活动管理”维持正常的糖代谢水平。";
                        else
                            moment_suggestion = moment_suggestion+"目前"+patientName+"已有较好饮食控制及身体活动强度，单纯非药物治疗很难使血糖降至正常水平，建议咨询医师看是否需要尽快启动药物治疗。";
                        default_interval = 30L;
                    } else if (ref_level == 1.5) {
                        if ( BMI_ACT_NO )
                            moment_suggestion = "空腹血糖已进展至糖尿病较高水平，长期得不到控制会导致心脑眼肾等脏器并发症。目前"+patientName+"在减重、控制饮食或增加身体活动等方面仍存在较大的降糖空间，强烈建议加大非药物干预力度，努力使血糖恢复正常！同时按提示监测空腹血糖变化。研究显示，低能量摄入（少吃同时结合低能量食物的摄入）和高能量消耗（主要通过有氧运动）的降糖作用均不亚于降糖药物。";
                        else
                            moment_suggestion =  "虽经努力，空腹血糖水平仍然较高，且非药物干预的效果已接近极限，建议尽早就诊接受药物治疗，并按提示监测空腹血糖。";
                        default_interval = 15L;
                    } else if (ref_level >= 2.0) {
                        if ( BMI_ACT_NO )
                            moment_suggestion = "空腹血糖水平过高！通过严格的非药物干预"+patientName+"虽仍有较大的降糖空间，但仍建议在强化饮食控制和身体活动的情况下尽快就医，并按提示监测空腹血糖变化。";
                        else
                            moment_suggestion = "空腹血糖非常严峻，单纯非药物干预不可能使血糖得到控制。建议尽早看医生接受药物治疗，之前每周至少测量一次空腹血糖！";
                        default_interval = 7L;
                    }
                    interval.put(moment,default_interval);
                    suggestion.put(moment,moment_suggestion);
                } else {
                    low_degree.add(GetMoment(moment));
                }
            }

            MonitorIntervalSuggestionMomentByValue(interval,suggestion,"zch",ref_bs_degree.getJSONObject("zch"),BMI_ACT_NO,low_degree);
            MonitorIntervalSuggestionMomentByLevel(interval,suggestion,"wfq",ref_bs_degree.getJSONObject("wfq"),BMI_ACT_NO,low_degree);
            MonitorIntervalSuggestionMomentByValue(interval,suggestion,"wfh",ref_bs_degree.getJSONObject("wfh"),BMI_ACT_NO,low_degree);
            MonitorIntervalSuggestionMomentByLevel(interval,suggestion,"wcq",ref_bs_degree.getJSONObject("wcq"),BMI_ACT_NO,low_degree);
            MonitorIntervalSuggestionMomentByValue(interval,suggestion,"wch",ref_bs_degree.getJSONObject("wch"),BMI_ACT_NO,low_degree);
            MonitorIntervalSuggestionMomentByLevel(interval,suggestion,"sq",ref_bs_degree.getJSONObject("sq"),BMI_ACT_NO,low_degree);

            {
                String moment = "yj";
                String moment_suggestion = null;
                JSONObject degree = ref_bs_degree.getJSONObject(moment);
                Double ref_degree = degree.getDouble("ref_degree");
                Double ref_bs = degree.getDouble("ref_bs");
                Double ref_level = BdSugarLevel(moment,ref_bs);
                Long default_interval = interval.getLong(moment);

                if ( ref_degree >= 1.0 ) {
                    if ( ref_level == -2 ) {
                        default_interval = 7L;
                        moment_suggestion = GetSuggestion(moment,ref_level,BMI_ACT_NO);
                    } else {
                        default_interval = 4 * default_interval;
                        moment_suggestion = GetSuggestion(moment,ref_level,BMI_ACT_NO);
                    }
                    interval.put(moment,default_interval);
                    suggestion.put(moment,moment_suggestion);
                } else {
                    low_degree.add(GetMoment(moment));
                }
            }

            MonitorIntervalSuggestionMomentByLevel(interval,suggestion,"ydh",ref_bs_degree.getJSONObject("ydh"),BMI_ACT_NO,low_degree);
            suggestion.put("low_degree",String.format("以上未对可参考度较低的时点血糖%s进行评价。",low_degree));
        } else { // 用药
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

    public static boolean GetRefBSAndDegreeKF(Long interval,
                                              String key,
                                              JSONObject lastest_quotas,
                                              JSONObject lastest_in_duration,
                                              JSONObject suggestion,
                                              JSONObject degree) {
        JSONObject kfbs = new JSONObject();
        String kf_suggestion;
        if ( !lastest_in_duration.containsKey(key) ) { // ref_duration 内无记录
            if ( lastest_quotas.containsKey(key) ) {
                Quota quota = lastest_quotas.getObject(key,Quota.class);
                if (TimeUtils.dateDiffToNow(quota.getMeasure_date()) > interval) {
                    suggestion.put(key,String.format("患者%s天内一直没有记录%s血糖变化，系统无法进行评估！",interval,GetMoment(key)));
                    return false;
                } else {
                    Double ref_bs = JSONObject.parseObject(quota.getRecord()).getDouble("sugarvalue");
                    kfbs.put("ref_bs",ref_bs);
                    if ( ref_bs <= 2.8 ) {
                        kfbs.put("ref_degree",1.0);
                        kf_suggestion = String.format("%s%s低血糖(可参考度：中)",TimeUtils.DateFormat(quota.getMeasure_date()),GetMoment(key));
                    } else if ( ref_bs < 11.1 ) {
                        kfbs.put("ref_degree",0.5);
                        kf_suggestion = String.format("近期无%s血糖记录，暂用%s的血糖值替代(可参考度：低)",GetMoment(key),TimeUtils.DateFormat(quota.getMeasure_date()));
                    } else {
                        kfbs.put("ref_degree",1.0);
                        kf_suggestion = String.format("近期无%s血糖记录，暂用%s的血糖值替代(可参考度：中)",GetMoment(key),TimeUtils.DateFormat(quota.getMeasure_date()));
                    }
                }
            } else {
                suggestion.put(key, String.format("患者%s天内一直没有记录%s血糖变化，系统无法进行评估！", interval,GetMoment(key)));
                return false;
            }
        } else if ( lastest_in_duration.getJSONArray(key).size() == 1 ){ // ref_duration 1 条记录
            JSONObject lastest_quota = lastest_in_duration.getJSONArray(key).getJSONObject(0);
            Double ref_bs = lastest_quota.getDouble("sugarvalue");
            kfbs.put("ref_bs",ref_bs);
            String date_string = TimeUtils.DateFormat(TimeUtils.parseDate(lastest_quota.getLong("measure_date")));
            if ( ref_bs <= 2.8 ) {
                kfbs.put("ref_degree",3.0);
                kf_suggestion = String.format("%s%s低血糖(可参考度：很高)",date_string,GetMoment(key));
            } else if ( ref_bs < 11.1 ) {
                kfbs.put("ref_degree",1.0);
                kf_suggestion = String.format("%s%s血糖%.2f(可参考度：中)",date_string,GetMoment(key),ref_bs);
            } else if ( ref_bs < 13.9 ) {
                kfbs.put("ref_degree",2.0);
                kf_suggestion = String.format("%s%s血糖%.2f(可参考度：高)",date_string,GetMoment(key),ref_bs);
            } else {
                kfbs.put("ref_degree",3.0);
                kf_suggestion = String.format("%s%s血糖%.2f(可参考度：很高)",date_string,GetMoment(key),ref_bs);
            }
        } else if ( lastest_in_duration.getJSONArray(key).size() == 2 ) { // ref_duration 2 条记录
            Double lastest_bs = lastest_in_duration.getJSONArray(key).getJSONObject(0).getDouble("sugarvalue");
            Double ref_bs = GetAvgSugarValue(lastest_in_duration.getJSONArray(key));
            kfbs.put("ref_bs",ref_bs);
            if ( lastest_bs < 11.1 ) {
                kfbs.put("ref_degree",2.0);
                kf_suggestion = String.format("最近两次%s血糖平均值%.2f(可参考度：高)",GetMoment(key),ref_bs);
            } else {
                kfbs.put("ref_degree",3.0);
                kf_suggestion = String.format("最近两次%s血糖平均值%.2f(可参考度：很高)",GetMoment(key),ref_bs);
            }
        } else {
            Double ref_bs = GetAvgSugarValue(lastest_in_duration.getJSONArray(key));
            kfbs.put("ref_bs",ref_bs);
            kfbs.put("ref_degree",3.0);
            kf_suggestion = String.format("最近三次%s血糖平均值%.2f(可参考度：很高)",GetMoment(key),ref_bs);
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
                String date_string = TimeUtils.DateFormat(item.getMeasure_date());
                if ( lastest_bs < 16.6 ) {
                    zchbs.put("ref_degree",0.5);
                    ref_bs = lastest_bs;
                    zch_suggestion = String.format("近期无%s血糖，暂用%s的%s血糖%.2f替代(可参考度：低)",GetMoment(key),date_string,GetMoment(key),ref_bs);
                } else {
                    zchbs.put("ref_degree",1.0);
                    ref_bs = lastest_bs;
                    zch_suggestion = String.format("近期无%s血糖，暂用%s的%s血糖%.2f替代(可参考度：中)",GetMoment(key),date_string,GetMoment(key),ref_bs);
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
            String date_string = TimeUtils.DateFormat(TimeUtils.parseDate(lastest_quota.getLong("measure_date")));
            if ( ref_bs < 16.6 ) {
                zchbs.put("ref_degree",1.0);
                zch_suggestion = String.format("%s%s血糖%.2f(可参考度：中)",date_string,GetMoment(key),ref_bs);
            } else {
                zchbs.put("ref_degree",2.0);
                zch_suggestion = String.format("%s%s血糖%.2f(可参考度：高)",date_string,GetMoment(key),ref_bs);
            }
        } else if (lastest_in_duration.getJSONArray(key).size() == 2 ) {
            JSONObject lastest_quota = lastest_in_duration.getJSONArray(key).getJSONObject(0);
            ref_bs = GetAvgSugarValue(lastest_in_duration.getJSONArray(key));
            if (lastest_quota.getDouble("sugarvalue") < 16.6) {
                zchbs.put("ref_degree",2.0);
                zch_suggestion = String.format("最近两次%s血糖平均值%.2f(可参考度：高)",GetMoment(key),ref_bs);
            } else {
                zchbs.put("ref_degree",3.0);
                zch_suggestion = String.format("最近两次%s血糖平均值%.2f(可参考度：很高)",GetMoment(key),ref_bs);
            }
        } else {
            ref_bs = GetAvgSugarValue(lastest_in_duration.getJSONArray(key));
            zchbs.put("ref_degree",3.0);
            zch_suggestion = String.format("最近三次%s血糖平均值%.2f",GetMoment(key),ref_bs);
        }
        degree.put(key,zchbs);
        if ( zch_suggestion != null )
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
                String date_string = TimeUtils.DateFormat(item.getMeasure_date());
                if ( lastest_bs <= 2.8 ) {
                    qbs.put("ref_degree",1.0);
                    ref_bs = lastest_bs;
                    zch_suggestion = String.format("%s%s低血糖(可参考度：中)",date_string,GetMoment(key));
                } else if ( lastest_bs < 11.1 && degree.getJSONObject("kf").getDouble("ref_degree") <= 1.0 ) {
                    qbs.put("ref_degree",0.5);
                    ref_bs = lastest_bs;
                    zch_suggestion = String.format("近期无%s血糖记录，暂用%s的%s血糖%.2f替代(可参考度：低)",GetMoment(key),date_string,GetMoment(key),ref_bs);
                } else if ( lastest_bs < 11.1 && degree.getJSONObject("kf").getDouble("ref_degree") > 1.0 ) {
                    ref_bs = kfbs;
                    qbs.put("ref_degree",0.5);
                    zch_suggestion = String.format("近期无%s血糖记录，暂用%s%s血糖%.2f替代(可参考度：低)",GetMoment(key),date_string,GetMoment("kf"),ref_bs);
                } else {
                    ref_bs = lastest_bs;
                    qbs.put("ref_degree",1.0);
                    zch_suggestion = String.format("近期无%s记录，暂用%s%s血糖%.2f替代(可参考度：中)",GetMoment(key),date_string,GetMoment(key),ref_bs);

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
            String date_string = TimeUtils.DateFormat(TimeUtils.parseDate(lastest_quota.getLong("measure_date")));
            if ( ref_bs <= 2.8 ) {
                qbs.put("ref_degree",3.0);
                zch_suggestion = String.format("%s%s血糖低血糖(可参考度：很高)",date_string,GetMoment(key));
            } else if ( ref_bs < 11.1 ){
                qbs.put("ref_degree", 1.0);
                zch_suggestion = String.format("%s%s血糖%.2f(可参考度：中)",date_string,GetMoment(key),ref_bs);
            } else if ( ref_bs < 13.9 ){
                qbs.put("ref_degree", 2.0);
                zch_suggestion = String.format("%s%s血糖%.2f(可参考度：高)",date_string, GetMoment(key), ref_bs);
            } else {
                qbs.put("ref_degree",3.0);
                zch_suggestion = String.format("%s%s血糖%.2f(可参考度：很高)",date_string,GetMoment(key),ref_bs);
            }
        } else if (lastest_in_duration.getJSONArray(key).size() == 2 ) {
            JSONObject lastest_quota = lastest_in_duration.getJSONArray(key).getJSONObject(0);
            ref_bs = GetAvgSugarValue(lastest_in_duration.getJSONArray(key));
            if (lastest_quota.getDouble("sugarvalue") < 11.1) {
                qbs.put("ref_degree",2.0);
                zch_suggestion = String.format("最近两次%s血糖平均值%.2f(可参考度：高)",GetMoment(key),ref_bs);
            } else {
                qbs.put("ref_degree",3.0);
                zch_suggestion = String.format("最近两次%s血糖平均值%.2f(可参考度：很高)",GetMoment(key),ref_bs);
            }
        } else {
            ref_bs = GetAvgSugarValue(lastest_in_duration.getJSONArray(key));
            qbs.put("ref_degree",3.0);
            zch_suggestion = String.format("最近三次%s血糖平均值%.2f",GetMoment(key),ref_bs);
        }
        degree.put(key,qbs);
        if ( zch_suggestion != null )
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
                suggestion = String.format("%s的糖化血红蛋白%.2f（可参考度：%s）",
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
        JSONObject BSquotas = quotas.containsKey("list")?quotas.getJSONObject("list"):quotas;

        JSONObject lastestQuotaList = driver.quotaDao.selectLastestBSQuota(account_id,patient_id);

        degree.put("suggestion",new JSONObject());
        JSONObject suggestion = degree.getJSONObject("suggestion");
        suggestion.put("patient_name", PatientServiceUtils.GetPatientName(driver, patient_id));


        if (!GetRefBSAndDegreeKF(interval.getLong("kf"),"kf",lastestQuotaList,BSquotas,suggestion,degree))
            return degree;
        GetRefBSAndDegreeH(interval.getLong("zch"), "zch", lastestQuotaList, BSquotas, suggestion, degree);
        GetRefBSAndDegreeQ(interval.getLong("wfq"), "wfq", lastestQuotaList, BSquotas, suggestion, degree);
        GetRefBSAndDegreeH(interval.getLong("wfh"), "wfh", lastestQuotaList, BSquotas, suggestion, degree);
        GetRefBSAndDegreeQ(interval.getLong("wcq"), "wcq", lastestQuotaList, BSquotas, suggestion, degree);
        GetRefBSAndDegreeH(interval.getLong("wch"), "wch", lastestQuotaList, BSquotas, suggestion, degree);
        GetRefBSAndDegreeQ(interval.getLong("sq"), "sq", lastestQuotaList, BSquotas, suggestion, degree);
        GetRefBSAndDegreeQ(interval.getLong("yj"), "yj", lastestQuotaList, BSquotas, suggestion, degree);
        GetRefBSAndDegreeH(interval.getLong("ydq"), "ydq", lastestQuotaList, BSquotas, suggestion, degree);
        GetRefBSAndDegreeQ(interval.getLong("ydh"), "ydh", lastestQuotaList, BSquotas, suggestion, degree);

        //ALC
        GetRefAndDegreeALC(driver,account_id,patient_id,change_days,degree);

        suggestion.put("last","其他时点的血糖，在没有有效的结果之前暂按正常或参考空腹血糖水平处理。");
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
        JSONObject ref_degree = GetRefAndDegreeTotal(driver,account_id,patient_id);
        Double ref_sugar = ref_degree.containsKey("kf") == true ?
                ref_degree.getJSONObject("kf").getDouble("ref_bs") : null;

        if ( ref_sugar == null ) return;

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
