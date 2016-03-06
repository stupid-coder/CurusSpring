package com.curus.utils.service.supervise.bdpressure;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.curus.dao.CurusDriver;
import com.curus.httpio.request.supervise.bdpressure.SBdPressureNonmedRequest;
import com.curus.model.database.Account;
import com.curus.model.database.Quota;
import com.curus.utils.constant.QuotaConst;
import com.curus.utils.service.quota.QuotaServiceUtils;
import com.curus.utils.service.supervise.food.SFoodServiceUtils;
import com.curus.utils.service.supervise.weight.SWeightSerivceUtils;

import java.util.List;

/**
 * Created by stupid-coder on 7/3/16.
 */
public class BdPressureServiseUtils {

    static private JSONObject GetBdpressure(CurusDriver driver, Long account_id,
                                            Long patient_id) {
        List<Quota> quotaList = driver.quotaDao.selectLastestQuota(account_id, patient_id, QuotaConst.QUOTA_BP_ID, 1L);
        if ( quotaList.size() == 0 ) {
            return JSONObject.parseObject(quotaList.get(0).getRecord());
        } else {
            return null;
        }
    }

    static public String BdPressureNonmedLoss(CurusDriver driver, Account account,
                                              SBdPressureNonmedRequest request) {
        JSONObject jo = new JSONObject();
        if ( request.getMode().compareToIgnoreCase(QuotaConst.QUOTA_WEIGHT) == 0 ) {
            WeightLossPressure(driver, account, request.getPatient_id(), request.getValue(), jo);
        } else if ( request.getMode().compareToIgnoreCase(QuotaConst.QUOTA_FOOD) == 0 ) {
            FoodLossPressure(driver, account, request.getPatient_id(), request.getValue(), jo);
        } else if ( request.getMode().compareToIgnoreCase(QuotaConst.QUOTA_ACT) == 0 ) {
            ActivityLossPressure(driver,account,request.getPatient_id(),request.getValue(), jo);
        }
        return jo.toJSONString();
    }

    static public void WeightLossPressure(CurusDriver driver, Account account,
                                            Long patient_id, Long query_wtloss, JSONObject bplossResult) {
        Long bploss = 0L;
        Long wtloss = 0L;
        JSONObject bdpressure = GetBdpressure(driver,account.getId(),patient_id);
        if ( bdpressure != null ) {
            Double sbp = bdpressure.getDouble("sbloodpre")-120;
            Long wtloss_max = Math.round(SWeightSerivceUtils.WeightLossTips(driver, account.getId(), patient_id));
            if ( query_wtloss.compareTo(wtloss_max) < 0 ) {
                wtloss = query_wtloss;
            } else {
                wtloss = wtloss_max;
            }
            bploss = Math.min(Math.round(Math.min(Math.max(sbp-120,0), Math.round(wtloss * 2 * sbp / 160))), 20);
        }
        bplossResult.put("wtloss", wtloss);
        bplossResult.put("bploss", bploss);
    }

    static public void FoodLossPressure(CurusDriver driver, Account account,
                                          Long patient_id, Long query_food, JSONObject bplossResult) {
        JSONObject bdpressure = GetBdpressure(driver,account.getId(),patient_id);
        Long bploss = 0L;
        Long foodinc = 0L;

        if ( bdpressure != null ) {
            Double sbp = bdpressure.getDouble("sbloodpre");
            Long max_inc = Math.round(Math.min(100-SFoodServiceUtils.CalculateFoodScore(driver,account.getId(),patient_id),0));
            if ( query_food.compareTo(max_inc) < 0 ) foodinc = query_food;
            else foodinc = max_inc;
            bploss = Math.round(Math.min(Math.max(sbp - 120, 0), Math.min(foodinc / 2 * sbp / 160, 20)));
        }
        bplossResult.put("bploss",bploss);
        bplossResult.put("foodinc",foodinc);
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

    static public void ActivityLossPressure(CurusDriver driver, Account account,
                                            Long patient_id, Long query_act, JSONObject bplossResult) {
        JSONObject bdpressure = GetBdpressure(driver, account.getId(), patient_id);
        Long bploss = 0L;
        Long actinc = 0L;

        if ( bdpressure != null ) {
            Double sbp = bdpressure.getDouble("sbloodpre");
            Long max_act = Math.round(Math.min(Math.max(40 - CalculateActivity(driver, account.getId(), patient_id), 0), 20));
            if ( query_act.compareTo(max_act) < 0 )  actinc = query_act;
            else actinc = max_act;
            bploss = Math.round(Math.min(Math.min(Math.max(sbp - 120, 0), Math.max(40 - actinc, 0) / 4 * sbp / 160), 20));
        }

        bplossResult.put("bploss",bploss);
        bplossResult.put("actinc",actinc);
    }
}

