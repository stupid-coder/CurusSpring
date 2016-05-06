package com.curus.utils.service.supervise.weight;

import com.alibaba.fastjson.JSONObject;
import com.curus.dao.CurusDriver;
import com.curus.httpio.request.supervise.weight.SWeightAddRequest;
import com.curus.httpio.request.supervise.weight.SWeightEstimateRequest;
import com.curus.httpio.request.supervise.weight.SWeightPretestRequest;
import com.curus.httpio.response.supervise.weight.SWeightEstimateResponseData;
import com.curus.httpio.response.supervise.weight.SWeightPretestResponseData;
import com.curus.model.database.Patient;
import com.curus.model.database.PatientSupervise;
import com.curus.model.database.PatientSuperviseList;
import com.curus.model.database.Quota;
import com.curus.model.record.*;
import com.curus.utils.TimeUtils;
import com.curus.utils.TypeUtils;
import com.curus.utils.constant.ActivityConst;
import com.curus.utils.constant.CommonConst;

import com.curus.utils.constant.QuotaConst;
import com.curus.utils.service.quota.QuotaServiceUtils;

import java.util.*;


/**
 * Created by stupid-coder on 28/2/16.
 */
public class SWeightSerivceUtils {

    public static JSONObject GetUpdateWeightSupervise(CurusDriver driver, Long account_id, Long patient_id, Integer get) {
        PatientSupervise patientSupervise = driver.patientSuperviseDao.select(TypeUtils.getWhereHashMap("account_id", account_id, "patient_id", patient_id, "quota_cat_id", QuotaConst.QUOTA_WEIGHT_ID, "last", CommonConst.TRUE));
        JSONObject info = null;
        if ( get.compareTo(CommonConst.TRUE) == 0 ) { info = new JSONObject(); }
        Double curwt = QuotaServiceUtils.getLastWeight(driver, account_id, patient_id);
        Double curht = QuotaServiceUtils.getLastHeight(driver,account_id,patient_id)/100;
        Double initwt = curwt;
        Double expwtloss = 0.0;
        Double oldwtloss = 0.0;
        Double curwtloss = 0.0;
        Long days = 0L;

        if ( patientSupervise != null ) {

            initwt = QuotaServiceUtils.getWeight(patientSupervise.getInitial());
            curwtloss = initwt - curwt;
            if ( info != null ) {
                expwtloss = QuotaServiceUtils.getWeightLoss(patientSupervise.getTarget());
                oldwtloss = QuotaServiceUtils.getWeightLoss(patientSupervise.getCurrent());
                days = TimeUtils.timestampDiff(TimeUtils.getTimestamp(), patientSupervise.getCreate_time());
            }

            if ( curwtloss.compareTo(oldwtloss) != 0 ) {
                patientSupervise.setCurrent(QuotaServiceUtils.getWeightLossQuota(curwtloss));
                driver.patientSuperviseDao.update(patientSupervise, "id");
            }
        }

        if ( info != null ) {
            info.put("curwt",curwt);
            info.put("curht",curht);
            info.put("initwt",initwt);
            info.put("expwtloss",expwtloss);
            info.put("oldwtloss",oldwtloss);
            info.put("curwtloss",curwtloss);
            info.put("days",days);
            info.put("supervise",patientSupervise);
        }

        return info;
    }
    public static Double CalculateDietEnergy(JSONObject diet) {
        if ( diet != null ) {
            return diet.getLong("lunch") * 0.8 + diet.getLong("dinner") * 1.2 + diet.getLong("snacks") / 7.0 * 3 * 1.2 + diet.getLong("fatink") / 4.0 * 3;
        } else return 0.0;
    }
    public static Double CalculateAvgDietEnergyByDays(CurusDriver driver, Long account_id, Long patient_id, Long days) {
        Double diet_energy  = 0.0;
        List<Quota> dietList = driver.quotaDao.selectByMeasureDateLastDays(account_id,patient_id,
                QuotaConst.QUOTA_DIET_ID, days);
        for ( Quota diet : dietList ) {
            JSONObject dietRecord = JSONObject.parseObject(diet.getRecord());
            diet_energy += CalculateDietEnergy(dietRecord);
        }
        if ( dietList != null && dietList.size() != 0 ) return diet_energy /= dietList.size();
        else return null;
    }
    public static Double CalculateActivityEnergy( JSONObject activity) {
        Double energy = 0.0;
        for ( String key : activity.keySet() ) {
            if ( ActivityConst.ACTIVITY_ENERGY.containsKey(key) )
                energy += activity.getLong(key) / ActivityConst.ACTIVITY_ENERGY.get(key);
        }
        return energy;
    }
    public static Double CalculateAvgActivityEnergyByDays(CurusDriver driver, Long account_id, Long patient_id, Long days) {
        Double act_energy = 0.0;
        List<Quota> actList = driver.quotaDao.selectByMeasureDateLastDays(account_id,patient_id,
                QuotaConst.QUOTA_ACT_ID, days);
        for ( Quota act : actList ) {
            act_energy += CalculateActivityEnergy(JSONObject.parseObject(act.getRecord()));
        }
        if ( actList != null && actList.size() != 0 ) return act_energy /= actList.size();
        else return null;
    }
    public static Double CalculateLastSuperviseWeightLoss(CurusDriver driver, Long account_id, Long patient_id, Double currentWeight) {
        Double dayloss = 0.0;
        PatientSupervise patientSupervise = driver.patientSuperviseDao.selectLastSupervise(account_id,patient_id,QuotaConst.QUOTA_WEIGHT_ID);
        if (patientSupervise != null) {
            Long duration = TimeUtils.timestampDiff(TimeUtils.getTimestamp(), patientSupervise.getCreate_time());
            Double initWeight = QuotaServiceUtils.getWeight(patientSupervise.getInitial());
            Double wtloss = initWeight - currentWeight;
            if ( duration.compareTo(30L) < 0 ) {
                if ( wtloss.compareTo(0.0) > 0 ) {
                    dayloss = wtloss*(65-2*duration)/duration/(64-duration);
                } else {
                    dayloss = wtloss/duration;
                }

            }
            JSONObject jo = new JSONObject(); jo.put("weight_loss",wtloss);
            patientSupervise.setCurrent(jo.toJSONString());
            driver.patientSuperviseDao.update(patientSupervise,"id");
        }
        return dayloss;
    }
    public static Double CalculateDietWeightLoss(CurusDriver driver, Long account_id, Long patient_id, SWeightPretestRequest request) {
        Double oldDietSum = 0.0;
        Double requestDietSum = 0.0;
        JSONObject oldDiet;

        List<Quota> dietQuotaList = driver.quotaDao.selectLastestQuota(account_id,patient_id,QuotaConst.QUOTA_DIET_ID,1L);
        if ( dietQuotaList == null || dietQuotaList.size() == 0) {
            QuotaServiceUtils.addQuota(driver,account_id,patient_id,QuotaConst.QUOTA_DIET,null,null,QuotaConst.QUOTA_INIT.get(QuotaConst.QUOTA_DIET));
            oldDiet = JSONObject.parseObject(QuotaConst.QUOTA_INIT.get(QuotaConst.QUOTA_DIET));
        } else {
            oldDiet = JSONObject.parseObject(dietQuotaList.get(0).getRecord());
        }
        oldDietSum = CalculateDietEnergy(oldDiet);
        requestDietSum = CalculateDietEnergy(JSONObject.parseObject(request.getDiet()));
        if ( oldDietSum.compareTo(0.0) > 0 )
            return (oldDietSum - requestDietSum) / oldDietSum * 2000 / 60 / 6.5 / 1000 / 0.35;
        else return 0.0;
    }
    public static Double CalculateActivityLoss(CurusDriver driver, Long account_id, Long patient_id, SWeightPretestRequest request, Double currentWeight, SWeightPretestResponseData responseData) {
        if (request.getActivity().compareTo("[]") == 0) return 0.0;
        List<Quota> quotaList = driver.quotaDao.selectLastestQuota(account_id,patient_id,QuotaConst.QUOTA_ACT_ID,1L);
        Quota quota;
        Double old_energy = 0.0; Double request_energy = 0.0;
        if ( quotaList == null || quotaList.size() == 0 ) {
            QuotaServiceUtils.addQuota(driver,account_id,patient_id,QuotaConst.QUOTA_ACT,null,null,QuotaConst.QUOTA_INIT.get(QuotaConst.QUOTA_ACT));
            quota = new Quota(); quota.setRecord(QuotaConst.QUOTA_INIT.get(QuotaConst.QUOTA_ACT));
        } else { quota = quotaList.get(0); }

        old_energy = CalculateActivityEnergy(JSONObject.parseObject(quota.getRecord()));
        request_energy = CalculateActivityEnergy(JSONObject.parseObject(request.getActivity()));

        if ( request_energy.compareTo(20.0) <= 0 ) responseData.setEvaluation("注意:减重过多地依赖饮食控制,对健康不利, 建议适当增加运动量。但减重计划可行!可以发布计划,系统会给予必要提醒和评估，也会得到其他管理者的帮助和督促。");
        return (request_energy - old_energy) * 0.525 / 7 * currentWeight / 6.5 / 1000 / 0.35 * 2;
    }
    public static Double Pretest(CurusDriver driver, Long account_id,  SWeightPretestRequest request, SWeightPretestResponseData responseData) {
        Long patient_id = request.getPatient_id();
        Double currentWeight = QuotaServiceUtils.getLastWeight(driver,account_id,patient_id);
        Double dayloss = 0.0;
        Double dietLoss = CalculateDietWeightLoss(driver, account_id, patient_id, request);
        Double actLoss = CalculateActivityLoss(driver, account_id, patient_id, request, currentWeight, responseData);
        dayloss = CalculateLastSuperviseWeightLoss(driver, account_id, patient_id, currentWeight) + dietLoss + actLoss;
        responseData.setWtloss(dayloss * 30 * (64 -30) / 63);
        return responseData.getWtloss();
    }
    public static Integer AddSupervise(CurusDriver driver, Long account_id, SWeightAddRequest request) {
        List<Quota> quotaList = driver.quotaDao.selectLastestQuota(account_id,request.getPatient_id(), QuotaConst.QUOTA_WEIGHT_ID, 1L);
        PatientSupervise patientSupervise = driver.patientSuperviseDao.selectLastSupervise(account_id,request.getPatient_id(),QuotaConst.QUOTA_WEIGHT_ID);

        if ( patientSupervise == null ) {
            patientSupervise = new PatientSupervise();
            patientSupervise.setAccount_id(account_id);
            patientSupervise.setPatient_id(request.getPatient_id());
            patientSupervise.setQuota_cat_id(QuotaConst.QUOTA_WEIGHT_ID);
        }
        else {
            patientSupervise.setLast(CommonConst.FALSE);
            driver.patientSuperviseDao.update(patientSupervise,"id");
            WeightLossList(driver,JSONObject.parseObject(patientSupervise.getCurrent()).getDouble("weight_loss"),0.0);
        }
        JSONObject jo = new JSONObject();
        patientSupervise.setCreate_time(TimeUtils.getTimestamp());
        patientSupervise.setEnd_date(TimeUtils.getDate(30L));
        patientSupervise.setInitial(quotaList.get(0).getRecord());
        jo.put("weight_loss", request.getWeight_loss());
        patientSupervise.setTarget(jo.toJSONString());
        jo.put("weight_loss", 0.0);
        patientSupervise.setCurrent(jo.toJSONString());
        patientSupervise.setLast(CommonConst.TRUE);
        jo.clear();
        jo.put("activity", request.getActivity().compareTo("[]") == 0 ? null : JSONObject.parseObject(request.getActivity()));
        jo.put("diet", JSONObject.parseObject(request.getDiet()));
        patientSupervise.setPolicy(jo.toJSONString());
        patientSupervise.setResult(null);
        patientSupervise.setId(null);
        return driver.patientSuperviseDao.insert(patientSupervise);
    }
    public static double BMI(Double height, Double weight) { return weight / height / height; }
    public static String BMIEvaluation(Double height, Double weight) {
        double bmi = BMI(height,weight);
        if ( bmi < 18.5 ) {
            return String.format("根据我国成人体重判定标准，【管理对象】目前的体重过低（BMI<18.5kg/m2)，需要吃动结合使体重增加。达到健康体重尚需增重至少 %f 公斤。",18.5*height*height-weight);
        } else if ( bmi < 24.0 ) {
            return String.format("根据我国成人体重判定标准，【管理对象】目前的体重处在正常范围内（BMI在18.5-24之间），请注意保持。");
        } else if ( bmi < 28.0 ) {
            return String.format("根据我国成人体重判定标准，您目前的体重超重（BMI在24-28之间），需要减重 %f 公斤才能达到正常水平。减重的诀窍就是“少吃多动”。少吃主要是减少摄入总量，尤其是高能量或高油脂食物的摄入；多动主要是指时间超过40分钟的中高强度的有氧运动。",weight-24.0*height*height);
        } else  return String.format("根据我国成人体重判定标准，您目前的体重已处在肥胖水平（BMI≥28），您需要减重 %f 公斤才能达到正常水平。减重的诀窍是“少吃多动”。少吃主要是减少摄入总量，尤其是高能量或高油脂食物的摄入；多动主要是指时间超过40分钟的中高强度的有氧运动。",weight-24*height*height);
    }
    public static String WeightLossEvaluation(CurusDriver driver, Long account_id, Long patient_id,
                                              Double curwtloss, Double targetloss, Long days,
                                              PatientSupervise patientSupervise) {
        if ( patientSupervise == null ) return null;
        if ( days >= 30L ) return "体重管理计划已经超过30天，请重新添加新的体重管理方案";

        Double expectwtloss = targetloss * days * (64 - days) / 30 / ( 64 - 30 );
        Double losspercent = expectwtloss == 0.0 ? 0.0 : curwtloss / expectwtloss;

        if ( losspercent >= 1.0 && losspercent < 1.3 ) return "减重很有成效，继续坚持！";
        if ( losspercent >= 0.7 && losspercent < 1.0 ) return "减重有成效，但还需再加把劲才能顺利达到既定目标，无需变更计划！";

        Double plan_diet = 0.0;
        Double plan_act = 0.0;


        JSONObject policy = JSONObject.parseObject(patientSupervise.getPolicy());

        plan_diet = CalculateDietEnergy(policy.getJSONObject("diet"));
        plan_act = CalculateActivityEnergy(policy.getJSONObject("activity"));
        Double real_diet = CalculateAvgDietEnergyByDays(driver,account_id,patient_id,days);
        Double real_act = CalculateAvgActivityEnergyByDays(driver,account_id,patient_id,days);

        if ( real_diet == null ) real_diet = plan_diet;
        if ( real_act == null ) real_act = plan_act;

        Double diet_act_percent = 0.5 * plan_act / real_act + 0.5 * plan_diet / real_diet;
        if ( real_act == 0.0 && real_diet == 0.0 ) diet_act_percent = 1.0;

        if ( losspercent >= 0.0 && losspercent < 0.7 )
            if (diet_act_percent.compareTo(0.7) >= 0 && diet_act_percent.compareTo(1.3) < 0 ) return "饮食运动在按计划执行，也有一定成效，但目前减重速度难达预期，建议加强干预力度或重新制定计划！";
            else if ( diet_act_percent.compareTo(0.7) < 0 ) return "饮食运动计划执行较差，若计划执行有困难，请重新调整！，如有决心再按计划执行，可不必更改计划";

        if ( losspercent > 1.3 )
            if ( diet_act_percent.compareTo(1.3) <= 0 ) return "体重下降速度过快，之前饮食运动计划不太适合，建议重新制定，系统会根据情况作出调整！";
            else if ( diet_act_percent.compareTo(1.3) > 0 ) return "与计划相比，实际饮食控制和运动强度有点太过，导致体重下降过快，不利于健康，建议按计划执行！";

        return "体重不降反升，还请认真对待，避免各种心脑肾和关节等健康问题的发生！";
    }

    public static String WeightLossIndex(Double wl) {
        if ( wl == null ) return null;
        if ( wl < 3 ) return "0";
        else if ( wl < 5) return "3";
        else if ( wl < 10) return "5";
        else if ( wl < 15) return "10";
        else return "15";
    }
    public static SuperviseWeightListRecord WeightLossList(CurusDriver driver, Double old, Double cur) {
        PatientSuperviseList superviseList = driver.patientSuperviseListDao.select(TypeUtils.getWhereHashMap("quota_cat_id",QuotaConst.QUOTA_WEIGHT_ID));
        if ( superviseList == null ) {
            superviseList = new PatientSuperviseList();
            superviseList.setQuota_cat_id(QuotaConst.QUOTA_WEIGHT_ID);
            superviseList.setList( JSONObject.toJSONString(new HashMap<String,Double>() {{
                put("0",0.0); put("3",0.0); put("5",0.0); put("10",0.0); put("15",0.0);
            }}) );
            driver.patientSuperviseListDao.insert(superviseList);
            superviseList = driver.patientSuperviseListDao.select(TypeUtils.getWhereHashMap("quota_cat_id",QuotaConst.QUOTA_WEIGHT_ID));
        }
        JSONObject listRecordJson = JSONObject.parseObject(superviseList.getList());

        String oldIndex = WeightLossIndex(old);
        String curIndex = WeightLossIndex(cur);

        if ( oldIndex != null ) {
            Double count = listRecordJson.getDouble(oldIndex);
            if ( count.compareTo(1.0) >= 0 ) listRecordJson.put(oldIndex, count - 1);
        }
        if ( curIndex != null ) {
            Double count = listRecordJson.getDouble(curIndex);
            listRecordJson.put(curIndex, count + 1);
        }
        superviseList.setList(listRecordJson.toJSONString());
        driver.patientSuperviseListDao.update(superviseList,"id");

        return new SuperviseWeightListRecord(listRecordJson);
    }
    public static SWeightEstimateResponseData EstimateWeightLoss(CurusDriver driver, Long account_id, SWeightEstimateRequest request) {

        SWeightEstimateResponseData responseData = new SWeightEstimateResponseData();

        JSONObject weightLossInfo = GetUpdateWeightSupervise(driver,account_id,request.getPatient_id(), CommonConst.TRUE);
        responseData.setDay_wermanagerment(weightLossInfo.getLong("days"));
        responseData.setWeight_loss(weightLossInfo.getDouble("curwtloss"));
        responseData.setWeight_change(WeightLossIndex(responseData.getWeight_loss()));
        responseData.setWeight_lossposition(WeightLossList(driver, weightLossInfo.getDouble("oldwtloss"), weightLossInfo.getDouble("curwtloss")));
        responseData.setBMI(BMI(weightLossInfo.getDouble("curht"), weightLossInfo.getDouble("curwt")));
        responseData.setBMI_evaluation(BMIEvaluation(weightLossInfo.getDouble("curht"),weightLossInfo.getDouble("curwt")));
        responseData.setWe_loss_evaluation(WeightLossEvaluation(driver, account_id,request.getPatient_id(),
                weightLossInfo.getDouble("curwtloss"),weightLossInfo.getDouble("expwtloss"),weightLossInfo.getLong("days"),weightLossInfo.getObject("supervise",PatientSupervise.class)));
        return responseData;

    }
    public static Double WeightLossTips(CurusDriver driver, Long account_id, Long patient_id) {

        Patient patient = driver.patientDao.select(TypeUtils.getWhereHashMap("id",patient_id));

        Double weight = QuotaServiceUtils.getLastWeight(driver, account_id, patient_id);
        Double height = QuotaServiceUtils.getLastHeight(driver, account_id, patient_id);
        height /= 100;
        Double standweight = patient.getGender().compareTo(CommonConst.GENDER_MALE) == 0 ? 23 * height * height : 21 * height * height;
        return Math.max(0, (weight-standweight)/standweight)*10;
    }
}
