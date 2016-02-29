package com.curus.httpio.request.supervise.weight;

import java.util.List;
import java.util.Map;

/**
 * Created by stupid-coder on 27/2/16.
 */
public class SWeightPretestRequest {

    private String token;
    private Long patient_id;
    private Double weight_loss;
    private String diet;
    private String sports;


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(Long patient_id) {
        this.patient_id = patient_id;
    }

    public Double getWeight_loss() {
        return weight_loss;
    }

    public void setWeight_loss(Double weight_loss) {
        this.weight_loss = weight_loss;
    }

    public String getDiet() {
        return diet;
    }

    public void setDiet(String diet) {
        this.diet = diet;
    }

    public String getSports() {
        return sports;
    }

    public void setSports(String sports) {
        this.sports = sports;
    }

    @Override
    public String toString() {
        return "SWeightPretestRequest{" +
                "token='" + token + '\'' +
                ", patient_id=" + patient_id +
                ", weight_loss=" + weight_loss +
                ", diet='" + diet + '\'' +
                ", sports='" + sports + '\'' +
                '}';
    }
}

