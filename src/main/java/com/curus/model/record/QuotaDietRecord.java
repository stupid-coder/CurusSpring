package com.curus.model.record;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by stupid-coder on 28/2/16.
 */
public class QuotaDietRecord {
    private Long lunch;
    private Long dinner;
    private Long snacks;
    private Long fatink;

    public QuotaDietRecord() {
    }

    public QuotaDietRecord(Long lunch, Long dinner, Long snacks, Long fatink) {
        this.lunch = lunch;
        this.dinner = dinner;
        this.snacks = snacks;
        this.fatink = fatink;
    }

    public String RecordString() { return JSONObject.toJSONString(this); }

    public Long getLunch() {
        return lunch;
    }

    public void setLunch(Long lunch) {
        this.lunch = lunch;
    }

    public Long getDinner() {
        return dinner;
    }

    public void setDinner(Long dinner) {
        this.dinner = dinner;
    }

    public Long getSnacks() {
        return snacks;
    }

    public void setSnacks(Long snacks) {
        this.snacks = snacks;
    }

    public Long getFatink() {
        return fatink;
    }

    public void setFatink(Long fatink) {
        this.fatink = fatink;
    }

    @Override
    public String toString() {
        return "QuotaDietRecord{" +
                "lunch=" + lunch +
                ", dinner=" + dinner +
                ", snacks=" + snacks +
                ", fatink=" + fatink +
                '}';
    }
}
