package com.curus.httpio.request.supervise.smoke;

/**
 * Created by stupid-coder on 5/3/16.
 */
public class SSmokeAddSuperviseRequest {
    private String token;
    private Long patient_id;
    private String smplan;

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

    public String getSmplan() {
        return smplan;
    }

    public void setSmplan(String smplan) {
        this.smplan = smplan;
    }

    @Override
    public String toString() {
        return "SSmokeAddSuperviseRequest{" +
                "token='" + token + '\'' +
                ", patient_id=" + patient_id +
                ", smplan='" + smplan + '\'' +
                '}';
    }
}
