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
        if (patientSupervise == null) {
            patientSupervise = new PatientSupervise();
            patientSupervise.setAccount_id(account_id);
            patientSupervise.setPatient_id(patient_id);
            patientSupervise.setQuota_cat_id(QuotaConst.QUOTA_SMOKE_ID);
            patientSupervise.setLast(CommonConst.TRUE);
        } else {
            patientSupervise.setLast(CommonConst.FALSE);
            position(driver, Long.parseLong(patientSupervise.getCurrent()), 0L);
            driver.patientSuperviseDao.update(patientSupervise, "id");
            patientSupervise.setId(null);
        }
        position(driver, patientSupervise.getCurrent() == null ? null : Long.parseLong(patientSupervise.getCurrent()), 0L);
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
        return jo.getDouble("price") * jo.getDouble("smoke") / 20.0;
    }

    public static Double calculateMoney(String init, List<Quota> quotaList, Date begin, Date end) {

        Double money = 0.0;

        for (int i = 0; i < quotaList.size(); ++i) {
            money += (TimeUtils.dateDiff(begin, quotaList.get(i).getMeasure_date()) - 1) * dayMoney(init);
            init = quotaList.get(i).getRecord();
            begin = quotaList.get(i).getMeasure_date();
        }

        if (quotaList.size() != 0) {
            money += TimeUtils.dateDiff(begin, end) * dayMoney(init);
        }
        return money;
    }

    public static Double sv_money(CurusDriver driver, Long account_id, Long patient_id,
                                  String init,
                                  Date create) {

        List<Quota> quotaList = driver.quotaDao.selectAfterMeasureDate(account_id, patient_id, QuotaConst.QUOTA_SMOKE_ID, create);
        if (quotaList.size() == 0) return 0.0;
        return dayMoney(init) * TimeUtils.dateDiff(create, TimeUtils.getDate()) - calculateMoney(init, quotaList, create, TimeUtils.getDate());
    }

    public static String positionIndex(Long days) {
        if (days == null || days == 0L) return null;
        if (days.compareTo(7L) < 0) return "0";
        if (days.compareTo(30L) < 0) return "7";
        if (days.compareTo(90L) < 0) return "30";
        if (days.compareTo(180L) < 0) return "90";
        if (days.compareTo(365L) < 0) return "180";
        if (days.compareTo(730L) < 0) return "365";
        if (days.compareTo(1195L) < 0) return "730";
        else return "1195";
    }

    public static String position(CurusDriver driver, Long oldays, Long newdays) {
        PatientSuperviseList superviseList = driver.patientSuperviseListDao.select(TypeUtils.getWhereHashMap("quota_cat_id", QuotaConst.QUOTA_SMOKE_ID));
        if (superviseList == null) {
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

        if (oldays != null && oldays != 0) {
            String index = positionIndex(oldays);
            Double count = list.getDouble(index);
            if (count.compareTo(0.0) > 0) list.put(index, count - 1);
        }

        if (newdays != null && newdays != 0) {
            String index = positionIndex(newdays);
            Double count = list.getDouble(index);
            list.put(index, count + 1);
        }

        if (newdays != null && newdays != 0 || oldays != null && oldays != 0) {
            superviseList.setList(list.toJSONString());
            driver.patientSuperviseListDao.update(superviseList, "id");
        }


        Double sum = 0.0;
        for (String key : list.keySet()) {
            sum += list.getDouble(key);
        }
        if (sum.compareTo(0.0) == 0) sum = 1.0;
        JSONArray position = new JSONArray();
        for (String key : list.keySet()) {
            JSONObject item = new JSONObject();
            item.put("time", key);
            item.put("percent", list.getDouble(key) / sum);
            position.add(item);
        }
        return position.toJSONString();


    }

    public static Double recovery(CurusDriver driver, Long account_id, Long patient_id)
    {
        List<Quota> quotaList = driver.quotaDao.selectLastestQuota(account_id,patient_id, QuotaConst.QUOTA_SMOKE_ID,1L);
        Double no_smoke_hours = 0.0;
        if ( quotaList.size() != 0 ) {
            Quota quota = quotaList.get(0);
            JSONObject record = JSONObject.parseObject(quota.getRecord());
            if ( record.getDouble("smoke") == 0 ) {
                no_smoke_hours = TimeUtils.timestampDiffHour(record.getTimestamp("timestamp"));
            }
        }

        return no_smoke_hours;
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
            responseData.setLossposition(
                    position(driver,Long.parseLong(patientSupervise.getCurrent()),days));
            patientSupervise.setCurrent(days.toString());
            driver.patientSuperviseDao.update(patientSupervise,"id");
        } else {
            responseData.setLossposition(position(driver, null, null));
        }
        responseData.setPositionindex(positionIndex(days));
        responseData.setDays(days);
        responseData.setSt_goal(st_goal);
        responseData.setSv_money(sv_money);
        responseData.setNo_smoke_hour(recovery(driver,account_id,patient_id));
        return responseData;
    }

}
