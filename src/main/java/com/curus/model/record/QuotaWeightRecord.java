package com.curus.model.record;

/**
 * Created by stupid-coder on 3/2/16.
 */
public class QuotaWeightRecord {

    private Double weight;

    public QuotaWeightRecord() {
    }

    public QuotaWeightRecord(Double weight) {
        this.weight = weight;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "QuotaWeightRecord{" +
                "weight=" + weight +
                '}';
    }
}
