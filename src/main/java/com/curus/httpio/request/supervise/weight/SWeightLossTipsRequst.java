package com.curus.httpio.request.supervise.weight;

/**
 * Created by stupid-coder on 29/2/16.
 */
public class SWeightLossTipsRequst {

    private String token;
    private Long patient_id;

    @Override
    public String toString() {
        return "SWeightLossTipsRequst{" +
                "token='" + token + '\'' +
                ", patient_id=" + patient_id +
                '}';
    }

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
}
