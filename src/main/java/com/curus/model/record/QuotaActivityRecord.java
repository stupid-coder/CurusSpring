package com.curus.model.record;

import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by stupid-coder on 28/2/16.
 */
public class QuotaActivityRecord {

    private List<Map<String,Long>> sports;

    public QuotaActivityRecord() {
    }

    public QuotaActivityRecord(List<Map<String, Long>> sports) {
        this.sports = sports;
    }

    public List<Map<String, Long>> getSports() {
        return sports;
    }

    public void setSports(List<Map<String, Long>> sports) {
        this.sports = sports;
    }

    @Override
    public String toString() {
        return "QuotaActivityRecord{" +
                "sports=" + sports +
                '}';
    }

    public String RecordString() { return JSONObject.toJSONString(this); }


}
