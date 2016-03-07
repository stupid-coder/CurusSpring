package com.curus.utils.service.supervise.smoke;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.curus.dao.CurusDriver;
import com.curus.httpio.response.supervise.smoke.SSmokeEstimateSuperviseResponseData;
import com.curus.model.database.PatientSupervise;
import com.curus.model.database.PatientSuperviseList;
import com.curus.model.database.Quota;
import com.curus.utils.TimeUtils;
import com.curus.utils.TypeUtils;
import com.curus.utils.constant.CommonConst;
import com.curus.utils.constant.QuotaConst;
import com.curus.utils.service.quota.QuotaServiceUtils;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by stupid-coder on 5/3/16.
 */
public class SSmokeServiseUtils {

    public static int addSupervise(CurusDriver driver, Long account_id, Long patient_id, String smplan) {
        Quota smokeQuota = QuotaServiceUtils.getLastSmokeQuota(driver, account_id, patient_id);
        PatientSupervise patientSupervise = driver.patientSuperviseDao.selectLastSupervise(account_id, patient_id, QuotaConst.QUOTA_SMOKE_ID);
        if ( patientSupervise == null ) {
            patientSupervise = new PatientSupervise();
            patientSupervise.setAccount_id(account_id);
            patientSupervise.setPatient_id(patient_id);
            patientSupervise.setQuota_cat_id(QuotaConst.QUOTA_SMOKE_ID);
            patientSupervise.setLast(CommonConst.TRUE);
        } else {
            patientSupervise.setLast(CommonConst.FALSE);
            position(driver, Long.parseLong(patientSupervise.getCurrent()),0L);
            driver.patientSuperviseDao.update(patientSupervise,"id");
            patientSupervise.setId(null);
        }
        position(driver,patientSupervise.getCurrent() == null ? null : Long.parseLong(patientSupervise.getCurrent()),0L);
        patientSupervise.setCreate_time(TimeUtils.getTimestamp());
        patientSupervise.setInitial(smokeQuota.getRecord());
        patientSupervise.setTarget(smplan);
        patientSupervise.setPolicy(smplan);
        patientSupervise.setCurrent("0");
        patientSupervise.setLast(CommonConst.TRUE);
        return driver.patientSuperviseDao.insert(patientSupervise);

    }

    public static Double dayMoney(String record) {
        JSONObject jo = JSONObject.parseObject(record);
        return  jo.getDouble("price") * jo.getLong("number") / 20.0;
    }

    public static Double calculateMoney(List<Quota> quotaList, Date begin, Date end) {

        Double money = 0.0;
        //if ( quotaList.size() != 0 ) money = (TimeUtils.dateDiff(begin,quotaList.get(0).getMeasure_date())-1)*dayMoney(quotaList.get(0).getRecord());
        Quota quota = quotaList.get(0);

        for ( int i = 0; i < quotaList.size(); ++ i ) {
            money += (TimeUtils.dateDiff(begin,quotaList.get(i).getMeasure_date())-1) * dayMoney(quota.getRecord());
            quota = quotaList.get(i);
            begin = quota.getMeasure_date();
        }

        if ( quotaList.size() != 0 ) {
            money += TimeUtils.dateDiff(begin,end) * dayMoney(quota.getRecord());
        }
        return money;
    }

    public static Double sv_money(CurusDriver driver, Long account_id, Long patient_id,
                                  String init,
                                  Date create) {
        JSONObject initSmoke = JSONObject.parseObject(init);
        List<Quota> quotaList = driver.quotaDao.selectAfterMeasureDate(account_id, patient_id, QuotaConst.QUOTA_SMOKE_ID,create);
        return dayMoney(init) * TimeUtils.dateDiff(create,TimeUtils.getDate()) - calculateMoney(quotaList, create,TimeUtils.getDate());
    }

    public static String positionIndex(Long days) {
        if ( days.compareTo(7L) < 0 ) return "0";
        if ( days.compareTo(30L) < 0 ) return "7";
        if ( days.compareTo(90L) < 0 ) return "30";
        if ( days.compareTo(180L) < 0 ) return "90";
        if ( days.compareTo(365L) < 0 ) return "180";
        if ( days.compareTo(730L) < 0 ) return "365";
        if ( days.compareTo(1195L) < 0 ) return "730";
        else return "1195";
    }
    public static String position(CurusDriver driver, Long oldays, Long newdays) {
        PatientSuperviseList superviseList = driver.patientSuperviseListDao.select(TypeUtils.getWhereHashMap("quota_cat_id", QuotaConst.QUOTA_SMOKE_ID));
        if ( superviseList == null ) {
            superviseList = new PatientSuperviseList();
            superviseList.setQuota_cat_id(QuotaConst.QUOTA_SMOKE_ID);
            superviseList.setList(JSONObject.toJSONString(new HashMap<String, Double>() {{
                put("0", 0.0);
                put("7", 0.0);
                put("30", 0.0);
                put("90", 0.0);
                put("180", 0.0);
                put("365", 0.0);
                put("730", 0.0);
                put("1195", 0.0);
            }}));
            driver.patientSuperviseListDao.insert(superviseList);
            superviseList = driver.patientSuperviseListDao.select(TypeUtils.getWhereHashMap("quota_cat_id", QuotaConst.QUOTA_SMOKE_ID));
        }
        JSONObject list = JSONObject.parseObject(superviseList.getList());

        if ( oldays != null ) {
            String index = positionIndex(oldays);
            Double count = list.getDouble(index);
            if ( count.compareTo(0.0) > 0 ) list.put(index,count-1);
        }

        if ( newdays != null ) {
            String index = positionIndex(newdays);
            Double count = list.getDouble(index);
            list.put(index,count+1);
        }

        superviseList.setList(list.toJSONString());
        driver.patientSuperviseListDao.update(superviseList,"id");

        if ( newdays != null ) {
            Double sum = 0.0;
            for ( String key : list.keySet() ) {
                sum += list.getDouble(key);
            }
            if ( sum.compareTo(0.0) == 0 ) sum = 1.0;
            JSONArray position = new JSONArray();
            for (String key : list.keySet()) {
                JSONObject item = new JSONObject();
                item.put("time",key);
                item.put("percent", list.getDouble(key)/sum);
                position.add(item);
            }
            return position.toJSONString();
        }
        return null;
    }

    public static SSmokeEstimateSuperviseResponseData estimate(CurusDriver driver,
                                                               Long account_id,
                                                               Long patient_id) {
        PatientSupervise patientSupervise = driver.patientSuperviseDao.selectLastSupervise(account_id, patient_id, QuotaConst.QUOTA_SMOKE_ID);
        SSmokeEstimateSuperviseResponseData responseData = new SSmokeEstimateSuperviseResponseData();

        Long days = 0L;
        String st_goal = null;
        Double sv_money = 0.0;
        if ( patientSupervise != null ) {
            days = TimeUtils.timestampDiff(patientSupervise.getCreate_time(),TimeUtils.getTimestamp());
            st_goal = patientSupervise.getTarget();
            sv_money = sv_money(driver, account_id, patient_id, patientSupervise.getInitial(),new Date(patientSupervise.getCreate_time().getTime()));
            patientSupervise.setCurrent(days.toString());
            driver.patientSuperviseDao.update(patientSupervise,"id");
            responseData.setLossposition(position(driver,Long.parseLong(patientSupervise.getCurrent()),days));
        }
        responseData.setDays(days);
        responseData.setSt_goal(st_goal);
        responseData.setSv_money(sv_money);
        return responseData;
    }

}
