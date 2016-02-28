package com.curus.model.record;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by stupid-coder on 3/2/16.
 */
public class QuotaHeightRecord {

    private Double height;

    public QuotaHeightRecord() {}

    public QuotaHeightRecord(Double height) { this.height = height; }

    public String RecordString() {
        return JSONObject.toJSONString(this);
    }

    public String RecordString(Double height) { setHeight(height); return JSONObject.toJSONString(this); }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "QuotaHeightRecord{" +
                "height=" + height +
                '}';
    }
}
