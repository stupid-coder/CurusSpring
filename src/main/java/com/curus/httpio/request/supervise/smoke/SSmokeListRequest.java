package com.curus.httpio.request.supervise.smoke;

/**
 * Created by stupid-coder on 4/3/16.
 */
public class SSmokeListRequest {

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
        return "SSmockListRequest{" +
                "token='" + token + '\'' +
                ", patient_id=" + patient_id +
                '}';
    }
}
