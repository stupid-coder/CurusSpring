package com.curus.model.record;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by stupid-coder on 28/2/16.
 */
public class SuperviseWeightLossRecord {

    private Double weight_loss;

    public String RecordString(Double weight_loss) { setWeight_loss(weight_loss); return JSONObject.toJSONString(this); }


    public Double getWeight_loss() {
        return weight_loss;
    }

    public void setWeight_loss(Double weight_loss) {
        this.weight_loss = weight_loss;
    }

    @Override
    public String toString() {
        return "QuotaRecordWeightLoss{" +
                "weight_loss=" + weight_loss +
                '}';
    }
}
