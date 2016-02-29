package com.curus.httpio.response.supervise.weight;

/**
 * Created by stupid-coder on 29/2/16.
 */
public class SWeightLossTipsResponseData {

    private Double weight_losstips;

    public SWeightLossTipsResponseData(Double weight_losstips) {
        this.weight_losstips = weight_losstips;
    }

    public Double getWeight_losstips() {
        return weight_losstips;
    }

    public void setWeight_losstips(Double weight_losstips) {
        this.weight_losstips = weight_losstips;
    }

    @Override
    public String toString() {
        return "SWeightLossTipsResponseData{" +
                "weight_losstips=" + weight_losstips +
                '}';
    }
}
