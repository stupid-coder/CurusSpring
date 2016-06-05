package com.curus.httpio.request.supervise.bdsugar;

import java.io.Serializable;

/**
 * Created by stupid-coder on 6/5/16.
 */
public class SBdSugarNonmedRequest implements Serializable {
    private String token;
    private Long patient_id;
    private Double diet;
    private Double activity;

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

    public Double getDiet() {
        return diet;
    }

    public void setDiet(Double diet) {
        this.diet = diet;
    }

    public Double getActivity() {
        return activity;
    }

    public void setActivity(Double activity) {
        this.activity = activity;
    }

    @Override
    public String toString() {
        return "SBdSugarNonmedRequest{" +
                "token='" + token + '\'' +
                ", patient_id=" + patient_id +
                ", diet=" + diet +
                ", activity=" + activity +
                '}';
    }
}
