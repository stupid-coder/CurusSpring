package com.curus.model.record;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by stupid-coder on 28/2/16.
 */
public class SuperviseWeightPolicy {
    private QuotaActivityRecord activity;
    private QuotaDietRecord diet;


    public String RecordString(QuotaActivityRecord quotaActivityRecord, QuotaDietRecord quotaDietRecord) {
        setActivity(quotaActivityRecord);
        setDiet(quotaDietRecord);
        return JSONObject.toJSONString(this);
    }

    @Override
    public String toString() {
        return "SuperviseWeightPolicy{" +
                "activity=" + activity +
                ", diet=" + diet +
                '}';
    }

    public QuotaActivityRecord getActivity() {
        return activity;
    }

    public void setActivity(QuotaActivityRecord activity) {
        this.activity = activity;
    }

    public QuotaDietRecord getDiet() {
        return diet;
    }

    public void setDiet(QuotaDietRecord diet) {
        this.diet = diet;
    }

}
