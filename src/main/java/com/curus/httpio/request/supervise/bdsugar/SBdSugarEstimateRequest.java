package com.curus.httpio.request.supervise.bdsugar;

import java.io.Serializable;

/**
 * Created by stupid-coder on 6/7/16.
 */
public class SBdSugarEstimateRequest implements Serializable {
    private String token;
    private Long patient_id;

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

    @Override
    public String toString() {
        return "SBdSugarEstimateRequest{" +
                "token='" + token + '\'' +
                ", patient_id=" + patient_id +
                '}';
    }
}
