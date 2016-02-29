package com.curus.model.record;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by stupid-coder on 28/2/16.
 */
public class SuperviseWeightPolicy {
    private JSONObject activity;
    private JSONObject diet;


    public SuperviseWeightPolicy(JSONObject activity, JSONObject diet) {
        this.activity = activity;
        this.diet = diet;
    }

    public SuperviseWeightPolicy() {
    }

    public String RecordString() {
        return JSONObject.toJSONString(this);
    }

    @Override
    public String toString() {
        return "SuperviseWeightPolicy{" +
                "activity=" + activity +
                ", diet=" + diet +
                '}';
    }

    public JSONObject getActivity() {
        return activity;
    }

    public void setActivity(JSONObject activity) {
        this.activity = activity;
    }

    public JSONObject getDiet() {
        return diet;
    }

    public void setDiet(JSONObject diet) {
        this.diet = diet;
    }

}
