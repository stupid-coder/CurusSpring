package com.curus.model.record;

/**
 * Created by stupid-coder on 3/2/16.
 */
public class QuotaHeightRecord {

    private Double height;

    public QuotaHeightRecord() {
    }

    public QuotaHeightRecord(Double height) {
        this.height = height;
    }

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
