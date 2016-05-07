package com.curus.utils.service.supervise.bdpressure;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.curus.dao.CurusDriver;
import com.curus.httpio.request.supervise.bdpressure.SBdPressureAddSuperviseRequest;
import com.curus.httpio.request.supervise.bdpressure.SBdPressureNonmedRequest;
import com.curus.httpio.response.supervise.bdpressure.SBdPressureEstimateSuperviseResponseData;
import com.curus.model.database.PatientSupervise;
import com.curus.model.database.PatientSuperviseList;
import com.curus.model.database.Quota;
import com.curus.utils.TimeUtils;
import com.curus.utils.TypeUtils;
import com.curus.utils.constant.CommonConst;
import com.curus.utils.constant.QuotaConst;
import com.curus.utils.service.supervise.food.SFoodServiceUtils;
import com.curus.utils.service.supervise.weight.SWeightSerivceUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by stupid-coder on 7/3/16.
 */
public class SBdPressureServiseUtils {
    static private Log logger = LogFactory.getLog(SBdPressureServiseUtils.class);

    static private List<Quota> GetBdPressureQuoaList(CurusDriver driver, Long account_id,
                                           Long patient_id, Long size) {
        return driver.quotaDao.selectLastestQuota(account_id, patient_id, QuotaConst.QUOTA_BP_ID, size);
    }

    static private List<JSONObject> GetBdPressureJSONList(CurusDriver driver, Long account_id,
                                            Long patient_id, Long size) {
        List<Quota> quotaList = GetBdPressureQuoaList(driver, account_id, patient_id, size);
        List<JSONObject> retJson = new ArrayList<JSONObject>();
        for ( Quota quota : quotaList ) {
            retJson.add(JSONObject.parseObject(quota.getRecord()));
        }
        return retJson;
    }

    static private JSONObject GetLastBdPressure(CurusDriver driver, Long account_id,
                                                      Long patient_id) {
        List<JSONObject> jsonObjectList = GetBdPressureJSONList(driver, account_id, patient_id, 1L);
        if ( jsonObjectList.size()>0 ) return jsonObjectList.get(0);
        else return null;
    }

    static public JSONObject BdPressureNonmedLoss(CurusDriver driver, Long account_id,
                                              SBdPressureNonmedRequest request) {
        JSONObject jo = new JSONObject();
        JSONObject bdpressure = GetLastBdPressure(driver, account_id, request.getPatient_id());
        Double value = Double.parseDouble(request.getValue());
        if ( request.getMode().compareToIgnoreCase(QuotaConst.QUOTA_WEIGHT) == 0 ) {
            WeightLossPressure(driver, account_id, request.getPatient_id(), value, bdpressure, jo);
        } else if ( request.getMode().compareToIgnoreCase(QuotaConst.QUOTA_FOOD) == 0 ) {
            FoodLossPressure(driver, account_id, request.getPatient_id(), value, bdpressure, jo);
        } else if ( request.getMode().compareToIgnoreCase(QuotaConst.QUOTA_ACT) == 0 ) {
            ActivityLossPressure(driver,account_id,request.getPatient_id(),value, bdpressure, jo);
        }
        return jo;
    }

    static public void WeightLossPressure(CurusDriver driver, Long account_id,
                                          Long patient_id, Double query_wtloss,
                                          JSONObject bdpressure, JSONObject bplossResult) {
        Long bploss = 0L;
        Double wtloss = 0.0;

        if ( bdpressure != null ) {
            Double sbp = bdpressure.getDouble("sbloodpre");
            Double wtloss_max = SWeightSerivceUtils.WeightLossTips(driver, account_id, patient_id);
            if ( query_wtloss.compareTo(wtloss_max) < 0 ) {
                wtloss = query_wtloss;
            } else {
                wtloss = wtloss_max;
            }
            bploss = Math.min(Math.round(Math.min(Math.max(sbp-120,0), Math.round(wtloss * 2 * sbp / 160))), 20);
        }
        bplossResult.put("status", wtloss);
        bplossResult.put("value", bploss);
    }

    static public void FoodLossPressure(CurusDriver driver, Long account_id,
                                        Long patient_id, Double query_food,
                                        JSONObject bdpressure, JSONObject bplossResult) {
        Long bploss = 0L;
        Long foodinc = 0L;

        if ( bdpressure != null ) {
            Double sbp = bdpressure.getDouble("sbloodpre");
            Long max_inc = Math.round((100-SFoodServiceUtils.CalculateFoodScore(driver,account_id,patient_id))*0.9);
            if ( query_food < max_inc.doubleValue() ) foodinc = query_food.longValue();
            else foodinc = max_inc;
            bploss = Math.round(Math.min(Math.max(sbp - 120, 0), Math.min(foodinc / 2 * sbp / 160, 20)));
        }
        bplossResult.put("value",bploss);
        bplossResult.put("status",foodinc);
    }

    static public Double CalculateActivity(CurusDriver driver, Long account_id,
                                           Long patient_id) {
        List<Quota> quotaList = driver.quotaDao.selectLastestQuota(account_id,patient_id,QuotaConst.QUOTA_ACT_ID,1L);
        if ( quotaList.size() > 0 ) {
            return SWeightSerivceUtils.CalculateActivityEnergy(JSONObject.parseObject(quotaList.get(0).getRecord()));
        } else {
            return 0.0;
        }
    }

    static public void ActivityLossPressure(CurusDriver driver, Long account_id,
                                            Long patient_id, Double query_act,
                                            JSONObject bdpressure, JSONObject bplossResult) {
        Long bploss = 0L;
        Long actinc = 0L;

        if ( bdpressure != null ) {
            Double sbp = bdpressure.getDouble("sbloodpre");
            Long max_act = Math.round(Math.min(Math.max((40 - CalculateActivity(driver, account_id, patient_id))*0.8, 0), 20));
            if ( query_act < max_act.doubleValue() )  actinc = query_act.longValue();
            else actinc = max_act;
            bploss = Math.round(Math.min(Math.min(Math.max(sbp - 120, 0), actinc / 4 * sbp / 160), 20));
        }

        bplossResult.put("value",bploss);
        bplossResult.put("status",actinc);
    }

    static public int AddSupervise(CurusDriver driver,
                                    Long account_id, SBdPressureAddSuperviseRequest request) {
        PatientSupervise patientSupervise = driver.patientSuperviseDao.selectLastSupervise(account_id,request.getPatient_id(),QuotaConst.QUOTA_BP_ID);
        if ( patientSupervise == null ) {
            patientSupervise = new PatientSupervise();
            patientSupervise.setAccount_id(account_id);
            patientSupervise.setPatient_id(request.getPatient_id());
            patientSupervise.setQuota_cat_id(QuotaConst.QUOTA_BP_ID);
        } else {
            patientSupervise.setLast(CommonConst.FALSE);
            driver.patientSuperviseDao.update(patientSupervise,"id");
            patientSupervise.setId(null);
        }

        JSONObject policy = new JSONObject();
        policy.put("lossweight",request.getLossweight());
        policy.put("dihealthscore",request.getDihealthscore());
        policy.put("phyactivity",request.getPhyactivity());
        patientSupervise.setLast(CommonConst.TRUE);
        patientSupervise.setCreate_time(TimeUtils.getTimestamp());
        patientSupervise.setPolicy(policy.toJSONString());
        JSONObject bdp = GetLastBdPressure(driver,account_id,request.getPatient_id());
        SBdPressurePosition(driver, Double.parseDouble(patientSupervise.getCurrent()==null?"-1.0":patientSupervise.getCurrent()),GetBPLevel(bdp));
        patientSupervise.setCurrent(GetBPLevel(bdp).toString());
        return driver.patientSuperviseDao.insert(patientSupervise);
    }

    static public String GetMonitorFreq(Double sbp, Date bpmeasure, Date drugupdatedays) {
        if ( bpmeasure == null ) {
            return "没有有效的血压值,请首先测量血压!";
        }
        Date curdate = TimeUtils.getDate();
        Long monitordays = 0L;
        Long bpmonitordays  = Math.round(225854.0*Math.pow(Math.E, -0.063 * sbp));

        if ( drugupdatedays != null ) {
            monitordays = TimeUtils.dateDiff(drugupdatedays, curdate);
            if ( monitordays.compareTo(bpmonitordays) > 0 ) monitordays = bpmonitordays;
        } else monitordays = bpmonitordays;

        Long noncheckdays = TimeUtils.dateDiff(bpmeasure, curdate) - 1L;
        logger.info(String.format("GetMonitorFreq sbp:%f\tnoncheckdays:%d\tmonitordays:%d\tbpmonitordays:%d\n", sbp,noncheckdays,monitordays,bpmonitordays));
        if ( noncheckdays.compareTo(monitordays) > 0 ) return "上次血压测量太久了,据此给出的评价和建议没有意义, 建议更新血压等监测指标后再进行评估!";
        else if ( noncheckdays >= 2L && noncheckdays.compareTo(monitordays) < 0 ) return String.format("本次评价所依据的血压值是【%d】天前测得,最好使用当天数据进行评价!",noncheckdays);
        else if ( noncheckdays == 1L ) return "根据今天测得的血压值,评估指导意见如下!";
        return "都没有满足的条件怎么办";
    }
    static public Long GetMeasureFreq(JSONObject curbdp, JSONObject lastbdp, Integer isDrug, Long drugUpdateDays) {
        if (curbdp == null) return 1L; // 没有测量值
        Double curlevel = GetBPLevel(curbdp);
        Double lastlevel = lastbdp == null ? curlevel : GetBPLevel(lastbdp);
        if ( isDrug > 0 ) { // 服用药物
            if ( curlevel < 1.0 && drugUpdateDays > 7L )
                return 30L;
            else if ( curlevel == 1.0 && drugUpdateDays > 7L )
                return 14L;
            else if ( curlevel == 2.0 && drugUpdateDays > 7L || curbdp.getDouble("dbloodpre") < 60.0 )
                return 7L;
            else if ( curlevel == 3.0 && drugUpdateDays <= 7L )
                return 1L;
        } else { // 未服用药物
            if ( curlevel == 0.0 && lastlevel == 0.0 )
                return 365L;
            else if ( curlevel == 0.0  && lastlevel >= 0.5 )
                return 180L;
            else if ( curlevel == 0.5 && lastlevel <= 0.5 )
                return 90L;
            else if ( curlevel == 0.5 && lastlevel >= 1.0 || curlevel == 1.0 && lastlevel <= 1.0 )
                return 30L;
            else if ( curlevel == 1.0 && lastlevel >= 2.0 )
                return 14L;
            else if ( curlevel == 2.0 && lastlevel <= 2.0 )
                return 7L;
            else if ( curlevel == 2.0 && lastlevel == 3.0 || curlevel ==3.0 )
                return 1L;
        }
        return 1L;
    }
    static public Double GetBPLevel(JSONObject bdpJson) {
        if ( bdpJson == null ) return -1.0;

        Double sbp = bdpJson.getDouble("sbloodpre");
        Double dbp = bdpJson.getDouble("dbloodpre");
        if ( sbp < 120.0 && dbp < 80.0 ) return 0.0;
        if ( sbp < 140.0 && dbp < 90.0 ) return 0.5;
        if ( sbp < 160.0 && dbp < 100.0) return 1.0;
        if ( sbp < 180.0 && dbp < 110.0) return 2.0;
        else return 3.0;

    }

    static public String SBdPressurePosition(CurusDriver driver, Double lastlevel, Double curlevel) {
        PatientSuperviseList superviseList = driver.patientSuperviseListDao.select(TypeUtils.getWhereHashMap("quota_cat_id",QuotaConst.QUOTA_BP_ID));
        if (superviseList == null) {
            superviseList = new PatientSuperviseList();
            superviseList.setQuota_cat_id(QuotaConst.QUOTA_BP_ID);
            superviseList.setList( JSONObject.toJSONString(new HashMap<String,Double>() {{
                put("0.0",0.0); put("0.5",0.0); put("1.0",0.0); put("2.0",0.0); put("3.0",0.0);
            }}));
            driver.patientSuperviseListDao.insert(superviseList);
            superviseList = driver.patientSuperviseListDao.select(TypeUtils.getWhereHashMap("quota_cat_id",QuotaConst.QUOTA_BP_ID));
        }

        JSONObject list = JSONObject.parseObject(superviseList.getList());

        if ( lastlevel != null && lastlevel != -1.0 ) {
            Double count = list.getDouble(lastlevel.toString());
            if ( count.compareTo(1.0) >= 0 ) list.put(lastlevel.toString(),count-1);
        }

        if ( curlevel != null && curlevel != -1.0 ) {
            list.put(curlevel.toString(),list.getDouble(curlevel.toString())+1);
        }
        if ( curlevel != null && curlevel != -1.0 || lastlevel != null && lastlevel != -1.0 ) {
            superviseList.setList(list.toJSONString());
            driver.patientSuperviseListDao.update(superviseList, "id");
        }


        JSONArray jsonArray = new JSONArray();

        Double sum = 0.0;

        for ( String key : list.keySet() ) {
            sum += list.getDouble(key);
        }

        for ( String key : list.keySet() ) {
            JSONObject item = new JSONObject();
            item.put("level", key);
            item.put("percent", sum == 0.0 ? 0.0 : list.getDouble(key) / sum);
            jsonArray.add(item);
        }
        return jsonArray.toJSONString();
    }

    static public SBdPressureEstimateSuperviseResponseData estimate(CurusDriver driver,
                                                                    Long account_id, Long patient_id) {
        SBdPressureEstimateSuperviseResponseData responseData = new SBdPressureEstimateSuperviseResponseData();
        List<Quota> bdpreQuotaList = GetBdPressureQuoaList(driver, account_id, patient_id, 2L);

        if ( bdpreQuotaList.size() == 0 ) {
            responseData.setBptimelines("请先进行血压测量后，方可给出评价和建议！");
            responseData.setPosition(SBdPressurePosition(driver,null,null));
        } else {

            JSONObject curbdp = JSONObject.parseObject(bdpreQuotaList.get(0).getRecord());
            JSONObject lastbdp = bdpreQuotaList.size() > 1 ? JSONObject.parseObject(bdpreQuotaList.get(1).getRecord()) : null;

            responseData.setBptimelines(GetMonitorFreq(curbdp.getDouble("sbloodpre"),bdpreQuotaList.get(0).getMeasure_date(),null));
            Double cur_bplevel = GetBPLevel(curbdp);
            Double last_bplevel = lastbdp == null ? cur_bplevel : GetBPLevel(lastbdp);
            String nonmed_suggestion = "";

            if ( CommonConst.FALSE.compareTo(CommonConst.TRUE) == 0 ) { // 使用药物
            } else { // 不使用药物

                if (cur_bplevel == 0.0) {
                    if (last_bplevel == 0.0) nonmed_suggestion = "【管理对象】血压维持在理想水平,请注意保持,每年 至少测量和评价一次血压";
                    else nonmed_suggestion = "【管理对象】血压降至理想水平,请保持健康生活方式; 因上次血压较高,建议半年内至少监测血压一次";
                } else if (cur_bplevel == 0.5) {
                    if (last_bplevel == 0.0)
                        nonmed_suggestion = "本次【管理对象】血压正常偏高,且比上次略有上升,需强化健康生活方式,防止发展成为高血压,建议 3 个月 内至少测量血压一次";
                    else if (last_bplevel == 0.5)
                        nonmed_suggestion = "【管理对象】血压连续处在正常偏高水平,请务必注意培养健康生活方式,防止发展成为高血压,建议 3 个月内至少测量血压一次";
                    else nonmed_suggestion = "【管理对象】血压降至正常水平,但仍然偏高,建议继续 加大生活方式干预力度,一个月内至少测量一次血压";
                } else if (cur_bplevel == 1.0) {
                    if (last_bplevel < 1.0)
                        nonmed_suggestion = "本次【管理对象】血压已处高血压水平,可根据生活方式改善可能带来的降压效果确定血压控制策略,也可尽早咨询医生,同时建议一个月内至少测量血压一次";
                    else if (last_bplevel == 1.0)
                        nonmed_suggestion = "【管理对象】血压仍维持在一级高血压水平,如果非药物 (健康生活方式)治疗的降压空间有限或实施难度很大,建议尽早咨询医生启动药物治疗。之前每个月至少测量血压一次";
                    else
                        nonmed_suggestion = "【管理对象】血压降至一级高血压水平,如果非药物( 健康生活方式)治疗的降压空间有限或实施难度很大,建议尽早咨询医生启动药物治疗。之前每 2 周至少测量血压一次";
                } else if (cur_bplevel == 2.0) {
                    if (last_bplevel <= 1.0)
                        nonmed_suggestion = "【管理对象】血压已升至二级高血压水平,建议尽早看医生,同时根据生活方式改善的降压效果与医生共商血压控制 策略,之前每周至少测量血压一次";
                    else if (last_bplevel == 2.0)
                        nonmed_suggestion = "【管理对象】血压仍维持在二级高血压水平,建议尽早看医生,同时根据生活方式改善的降压效果与医生共商血压控制策略,之前每周至少测量血压一次";
                    else nonmed_suggestion = "【管理对象】血压虽有所下降,当仍处在二级高血压水平,建议尽早看医生,同时根据生活方式改善的降压效果与 医生共商血压控制策略,之前每天监测血压";
                } else nonmed_suggestion = "本次【管理对象】血压水平高危,建议马上去看医生, 并密切监测血压";
            }
            responseData.setNonmed_suggestion(nonmed_suggestion);
            PatientSupervise patientSupervise = driver.patientSuperviseDao.selectLastSupervise(account_id,patient_id,QuotaConst.QUOTA_BP_ID);

            if ( patientSupervise != null ) {
                Double old_bplevel = Double.parseDouble(patientSupervise.getCurrent());
                responseData.setPosition(SBdPressurePosition(driver,old_bplevel,cur_bplevel));
                patientSupervise.setCurrent(cur_bplevel.toString());
                driver.patientSuperviseDao.update(patientSupervise,"id");
            } else responseData.setPosition(SBdPressurePosition(driver,null,null));

            responseData.setPositionindex(cur_bplevel.toString());

            JSONArray nonmed = new JSONArray();
            JSONObject weight = new JSONObject();
            WeightLossPressure(driver,account_id,patient_id,100.0,curbdp,weight);
            weight.put("mode", QuotaConst.QUOTA_WEIGHT); nonmed.add(weight);

            JSONObject food = new JSONObject();
            FoodLossPressure(driver,account_id,patient_id,100.0,curbdp,food);
            food.put("mode",QuotaConst.QUOTA_FOOD); nonmed.add(food);

            JSONObject activity = new JSONObject();
            ActivityLossPressure(driver,account_id,patient_id,40.0,curbdp,activity);
            activity.put("mode",QuotaConst.QUOTA_ACT); nonmed.add(activity);

            responseData.setNonmed_status(nonmed.toJSONString());
        }

        return responseData;
    }
}

