package com.curus.httpio.response.supervise.weight;

/**
 * Created by stupid-coder on 27/2/16.
 */
public class SWeightPretestResponseData {

    private Double wtloss;
    private String evaluation;

    public Double getWtloss() {
        return wtloss;
    }

    public void setWtloss(Double wtloss) {
        this.wtloss = wtloss;
    }

    public String getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(String evaluation) {
        this.evaluation = evaluation;
    }

    @Override
    public String toString() {
        return "SWeightPretestResponseData{" +
                "wtloss=" + wtloss +
                ", evaluation='" + evaluation + '\'' +
                '}';
    }
}
