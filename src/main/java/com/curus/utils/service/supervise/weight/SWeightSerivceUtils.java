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
import com.sun.tools.internal.xjc.reader.TypeUtil;

import java.util.*;


/**
 * Created by stupid-coder on 28/2/16.
 */
public class SWeightSerivceUtils {

    public static Double CalculateDietEnergy(Long lunch, Long dinner, Long snacks, Long fatink) {
        return lunch * 0.8 + dinner * 1.2 + snacks / 7.0 * 3 * 1.2 + fatink / 4.0 * 3;
    }
    public static Double CalculateActivityEnergy( Iterator<Map<String,Long>> iter) {
        Double energy = 0.0;
        while ( iter.hasNext() ) {
            Map<String,Long> sportitem = iter.next();
            energy += sportitem.get("sport_value") * ActivityConst.ACTIVITY_ENERGY.get(sportitem.get("sport_id"));
        }
        return energy;
    }
    public static Double CalculateLastSuperviseWeightLoss(CurusDriver driver, Long account_id, Long patient_id, Double currentWeight) {
        Double dayloss = 0.0;
        PatientSupervise patientSupervise = driver.patientSuperviseDao.select(TypeUtils.getWhereHashMap("account_id", account_id, "patient_id", patient_id, "quota_cate_id", QuotaConst.QUOTA_WEIGHT_ID, "last", CommonConst.TRUE));
        if (patientSupervise != null) {
            Long duration = TimeUtils.timestampDiff(TimeUtils.getTimestamp(), patientSupervise.getCreate_time());
            Double initWeight = QuotaServiceUtils.getWeight(patientSupervise.getInitial());
            if ( duration <= 30 ) {
                Double wtloss = initWeight - currentWeight;
                if ( wtloss > 0 ) {
                    dayloss = wtloss*(65-2*duration)/duration/(64-duration);
                } else {
                    dayloss = wtloss/duration;
                }
            }
        }
        return dayloss;
    }
    public static Double CalculateDietWeightLoss(CurusDriver driver, Long account_id, Long patient_id, SWeightPretestRequest request) {
        Double oldDietSum = 0.0;
        Double requestDietSum = 0.0;
        QuotaDietRecord oldDiet;

        List<Quota> dietQuotaList = driver.quotaDao.selectLastestQuota(account_id,patient_id,QuotaConst.QUOTA_DIET_ID,1L);
        if ( dietQuotaList == null ) {
            QuotaServiceUtils.addQuota(driver,account_id,patient_id,QuotaConst.QUOTA_DIET,null,QuotaConst.QUOTA_INIT.get(QuotaConst.QUOTA_DIET));
            oldDiet = JSONObject.parseObject(QuotaConst.QUOTA_INIT.get(QuotaConst.QUOTA_DIET),QuotaDietRecord.class);
        } else {
            oldDiet = JSONObject.parseObject(dietQuotaList.get(0).getRecord(), QuotaDietRecord.class);
        }
        oldDietSum = CalculateDietEnergy(oldDiet.getLunch(),oldDiet.getDinner(), oldDiet.getSnacks(), oldDiet.getFatink());
        requestDietSum = CalculateDietEnergy(request.getLunch(),request.getDinner(),request.getSnacks(),request.getFatink());
        return (oldDietSum - requestDietSum) / oldDietSum * 2000 / 60 / 6.5 / 1000 / 0.35;
    }
    public static Double CalculateActivityLoss(CurusDriver driver, Long account_id, Long patient_id, SWeightPretestRequest request, Double currentWeight, SWeightPretestResponseData responseData) {
        List<Quota> quotaList = driver.quotaDao.selectLastestQuota(account_id,patient_id,QuotaConst.QUOTA_ACT_ID,1L);
        Quota quota;
        Double old_energy = 0.0; Double request_energy = 0.0;
        if ( quotaList == null && quotaList.size() == 0 ) {
            QuotaServiceUtils.addQuota(driver,account_id,patient_id,QuotaConst.QUOTA_ACT,null,QuotaConst.QUOTA_INIT.get(QuotaConst.QUOTA_DIET));
            quota = new Quota(); quota.setRecord(QuotaConst.QUOTA_INIT.get(QuotaConst.QUOTA_DIET));
        } else { quota = quotaList.get(0); }

        QuotaActivityRecord quotaActivityRecord = JSONObject.parseObject(quota.getRecord(),QuotaActivityRecord.class);
        old_energy = CalculateActivityEnergy(quotaActivityRecord.getSports().iterator());
        request_energy = CalculateActivityEnergy(request.getSports().iterator());

        if ( request_energy <= 20 ) responseData.setPrompt("减重过多地依赖饮食控制，对健康不利，建议适当增加运动量。");
        return (request_energy - old_energy) * 0.525 / 7 * currentWeight / 6.5 / 1000 / 0.35 * 2;
    }
    public static Double Pretest(CurusDriver driver, Long account_id,  SWeightPretestRequest request, SWeightPretestResponseData responseData) {
        Long patient_id = request.getPatient_id();

        List<Quota> quota = driver.quotaDao.selectLastestQuota(account_id, patient_id, QuotaConst.QUOTA_WEIGHT_ID,1L);
        Double currentWeight = QuotaServiceUtils.getWeight(quota.get(0).getRecord());
        Double dayloss = 0.0;
        Double dietLoss = CalculateDietWeightLoss(driver, account_id, patient_id, request);
        Double actLoss = CalculateActivityLoss(driver, account_id, patient_id, request, currentWeight, responseData);
        dayloss = CalculateLastSuperviseWeightLoss(driver, account_id, patient_id, currentWeight) + dietLoss + actLoss;
        responseData.setWtloss(dayloss * 30 * (64 -30) / 63);
        return responseData.getWtloss();
    }
    public static Integer AddSupervise(CurusDriver driver, Long account_id, SWeightAddRequest request) {
        List<Quota> quotaList = driver.quotaDao.selectLastestQuota(account_id,request.getPatient_id(), QuotaConst.QUOTA_WEIGHT_ID, 1L);

        PatientSupervise patientSupervise = driver.patientSuperviseDao.select(TypeUtils.getWhereHashMap("account_id",account_id,"patient_id",request.getPatient_id(),"last",CommonConst.TRUE,"quota_cat_id",QuotaConst.QUOTA_WEIGHT_ID));

        if ( patientSupervise == null ) {
            patientSupervise = new PatientSupervise();
            patientSupervise.setAccount_id(account_id);
            patientSupervise.setPatient_id(request.getPatient_id());
            patientSupervise.setQuota_cat_id(QuotaConst.QUOTA_WEIGHT_ID);
        }
        else {
            patientSupervise.setLast(CommonConst.FALSE);
            driver.patientSuperviseDao.update(patientSupervise,"id");
        }

        patientSupervise.setCreate_time(TimeUtils.getTimestamp());
        patientSupervise.setEnd_date(TimeUtils.getDate(30L));
        patientSupervise.setInitial(quotaList.get(0).getRecord());
        patientSupervise.setTarget(new SuperviseWeightLossRecord().RecordString(request.getWeight_loss()));
        patientSupervise.setCurrent(new SuperviseWeightLossRecord().RecordString(0.0));
        patientSupervise.setLast(CommonConst.TRUE);
        patientSupervise.setPolicy(new SuperviseWeightPolicy().RecordString(new QuotaActivityRecord(request.getSports()),new QuotaDietRecord(request.getLunch(),request.getDinner(),request.getSnachs(),request.getFatink())));
        patientSupervise.setResult(null);

        return driver.patientSuperviseDao.insert(patientSupervise);
    }
    public static Double BMI(Double height, Double weight) { return weight / height / height; }
    public static String BMIEvaluation(Double height, Double weight) {
        Double bmi = BMI(height,weight);
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
        Double expectwtloss = targetloss * days * (64 - days) / 30 / ( 64 - 30 );
        Double losspercent = curwtloss / expectwtloss;
        if ( losspercent >= 1.0 && losspercent < 1.3 ) return "减重很有成效，继续坚持！";
        if ( losspercent >= 0.7 && losspercent < 1.0 ) return "减重有成效，但还需再加把劲才能顺利达到既定目标，无需变更计划！";

        Double plan_diet = 0.0;
        Double plan_act = 0.0;

        if ( patientSupervise != null ) {
            SuperviseWeightPolicy policy = JSONObject.parseObject(patientSupervise.getPolicy(), SuperviseWeightPolicy.class);
            plan_diet = CalculateDietEnergy(policy.getDiet().getLunch(),policy.getDiet().getDinner(),policy.getDiet().getSnacks(),policy.getDiet().getFatink());
            plan_act = CalculateActivityEnergy(policy.getActivity().getSports().iterator());
        }

        Double real_diet = 0.0;
        Double real_act = 0.0;

        if ( patientSupervise != null ) {
            List<Quota> dietList = driver.quotaDao.selectByMeasureDateLastDays(account_id,patient_id,
                    QuotaConst.QUOTA_DIET_ID, days);
            for ( Quota diet : dietList ) {
                QuotaDietRecord dietRecord = JSONObject.parseObject(diet.getRecord(),QuotaDietRecord.class);
                real_diet += CalculateDietEnergy(dietRecord.getLunch(),dietRecord.getDinner(),dietRecord.getSnacks(),dietRecord.getFatink());
            }
            if ( dietList != null && dietList.size() != 0 ) real_diet /= dietList.size();
            else real_diet = plan_diet;
            List<Quota> actList = driver.quotaDao.selectByMeasureDateLastDays(account_id,patient_id,
                    QuotaConst.QUOTA_ACT_ID, days);
            for ( Quota act : actList ) {
                QuotaActivityRecord actRecord = JSONObject.parseObject(act.getRecord(),QuotaActivityRecord.class);
                real_act += CalculateActivityEnergy(actRecord.getSports().iterator());
            }
            if ( actList != null && actList.size() != 0 ) real_act /= dietList.size();
            else real_act = plan_act;
        }
        Double diet_act_percent = 0.5 * plan_act / real_act + 0.5 * plan_diet / real_diet;
        if ( losspercent >= 0.0 && losspercent < 0.7 )
            if (diet_act_percent >= 0.7 && diet_act_percent < 1.3 ) return "饮食运动在按计划执行，也有一定成效，但目前减重速度难达预期，建议加强干预力度或重新制定计划！";
            else if ( diet_act_percent < 0.7 ) return "饮食运动计划执行较差，若计划执行有困难，请重新调整！，如有决心再按计划执行，可不必更改计划";

        if ( losspercent > 1.3 )
            if ( diet_act_percent <= 1.3 ) return "体重下降速度过快，之前饮食运动计划不太适合，建议重新制定，系统会根据情况作出调整！";
            else if ( diet_act_percent > 1.3) return "与计划相比，实际饮食控制和运动强度有点太过，导致体重下降过快，不利于健康，建议按计划执行！";

        return "体重不降反升，还请认真对待，避免各种心脑肾和关节等健康问题的发生！";
    }
    public static String WeightLossIndex(Double wl) {
        if ( wl < 3 ) return "0";
        else if ( wl < 5) return "3";
        else if ( wl < 10) return "5";
        else if ( wl < 15) return "10";
        else return "15";
    }
    public static SuperviseWeightListRecord WeightLossList(CurusDriver driver, Double old, Double cur) {
        PatientSuperviseList superviseList = driver.patientSuperviseListDao.select(TypeUtils.getWhereHashMap("quota_cate_id",QuotaConst.QUOTA_WEIGHT_ID));
        if ( superviseList == null ) {
            superviseList = new PatientSuperviseList();
            superviseList.setQuota_cate_id(QuotaConst.QUOTA_WEIGHT_ID);
            superviseList.setList( JSONObject.toJSONString(new HashMap<String,Double>() {{
                put("0",0.0); put("3",0.0); put("5",0.0); put("10",0.0); put("15",0.0);
            }}) );
            driver.patientSuperviseListDao.insert(superviseList);
        }
        JSONObject listRecordJson = JSONObject.parseObject(superviseList.getList());

        String oldIndex = WeightLossIndex(old);
        String curIndex = WeightLossIndex(cur);
        if ( oldIndex.compareTo(curIndex) != 0 ) {
            Double count = listRecordJson.getDouble(oldIndex);
            if (count > 0) listRecordJson.put(oldIndex, count - 1);
            count = listRecordJson.getDouble(curIndex);
            listRecordJson.put(curIndex, count + 1);
        }
        return new SuperviseWeightListRecord(listRecordJson);
    }
    public static void EstimateWeightLoss(CurusDriver driver, Long account_id, SWeightEstimateRequest request, SWeightEstimateResponseData responseData) {
        List<Quota> quotaList = driver.quotaDao.selectLastestQuota(account_id,request.getPatient_id(),QuotaConst.QUOTA_WEIGHT_ID,1L);
        QuotaWeightRecord currentWeightRecord = JSONObject.parseObject(quotaList.get(0).getRecord(), QuotaWeightRecord.class);
        quotaList = driver.quotaDao.selectLastestQuota(account_id, request.getPatient_id(),QuotaConst.QUOTA_HEIGHT_ID,1L);
        QuotaHeightRecord currentHeightRecord = JSONObject.parseObject(quotaList.get(0).getRecord(),QuotaHeightRecord.class);

        PatientSupervise patientSupervise = driver.patientSuperviseDao.select(TypeUtils.getWhereHashMap("account_id",account_id,"patient_id",request.getPatient_id(),"quota_cat_id",QuotaConst.QUOTA_WEIGHT_ID,"last", CommonConst.TRUE));
        PatientSuperviseList superviseList;
        responseData = new SWeightEstimateResponseData();


        Double curwtloss = 0.0;
        Double oldwtloss = 0.0;
        Double expectloss =0.0;
        Double curwt = currentWeightRecord.getWeight();
        Double curht = currentHeightRecord.getHeight();
        Double initwt = curwt;
        Long days = 0L;

        if ( patientSupervise != null ) {
            QuotaWeightRecord initWeightRecord = JSONObject.parseObject(patientSupervise.getInitial(),QuotaWeightRecord.class);
            initwt = initWeightRecord.getWeight();

            SuperviseWeightLossRecord targetWeightLossRecord = JSONObject.parseObject(patientSupervise.getTarget(),SuperviseWeightLossRecord.class);
            expectloss = targetWeightLossRecord.getWeight_loss();

            SuperviseWeightLossRecord oldWeightLossRecord =  JSONObject.parseObject(patientSupervise.getCurrent(), SuperviseWeightLossRecord.class);
            oldwtloss = oldWeightLossRecord.getWeight_loss();

            days = TimeUtils.timestampDiff(TimeUtils.getTimestamp(),patientSupervise.getCreate_time());

            curwtloss = initwt - curwt;
            if ( curwtloss.compareTo(oldwtloss) != 0 ) {
                patientSupervise.setCurrent(currentWeightRecord.RecordString());
                driver.patientSuperviseDao.update(patientSupervise, "id");
            }
        } else {
            curwtloss = initwt - curwt;
        }

        responseData.setDay_wermanagerment(days);
        responseData.setWeight_loss(curwtloss);
        responseData.setWeight_change(WeightLossIndex(curwtloss));
        responseData.setWeight_lossposition(WeightLossList(driver, oldwtloss, curwtloss));
        responseData.setBMI(BMI(curht,curwt));
        responseData.setBMI_evaluation(BMIEvaluation(curht, curwt));
        responseData.setWe_loss_evaluation(WeightLossEvaluation(driver, account_id,request.getPatient_id(),curwtloss,expectloss,days,patientSupervise));

    }
    public static Double WeightLossTips(CurusDriver driver, Long account_id, Long patient_id) {

        Patient patient = driver.patientDao.select(TypeUtils.getWhereHashMap("id",patient_id));

        List<Quota> weightList = driver.quotaDao.selectLastestQuota(account_id, patient_id, QuotaConst.QUOTA_WEIGHT_ID, 1L);
        List<Quota> heightList = driver.quotaDao.selectLastestQuota(account_id, patient_id, QuotaConst.QUOTA_HEIGHT_ID,1L);
        Double weight = JSONObject.parseObject(weightList.get(0).getRecord(), QuotaWeightRecord.class).getWeight();
        Double height = JSONObject.parseObject(heightList.get(0).getRecord(), QuotaHeightRecord.class).getHeight();
        Double standweight = patient.getGender() == CommonConst.GENDER_MALE ? 23 * height * height : 21 * height *height;
        return Math.max(0, (weight-standweight)/standweight)*10;
    }
}
