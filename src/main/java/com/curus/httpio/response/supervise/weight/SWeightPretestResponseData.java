package com.curus.httpio.response.supervise.weight;

/**
 * Created by stupid-coder on 27/2/16.
 */
public class SWeightPretestResponseData {

    private Double wtloss;
    private String prompt;

    public Double getWtloss() {
        return wtloss;
    }

    public void setWtloss(Double wtloss) {
        this.wtloss = wtloss;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    @Override
    public String toString() {
        return "SWeightPretestResponseData{" +
                "wtloss=" + wtloss +
                ", prompt='" + prompt + '\'' +
                '}';
    }
}
