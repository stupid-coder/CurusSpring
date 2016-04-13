package com.curus.httpio.request.supervise;

/**
 * Created by stupid-coder on 13/4/16.
 */
public class SuperviseListRequest {
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
        return "SuperviseListRequest{" +
                "token='" + token + '\'' +
                ", patient_id=" + patient_id +
                '}';
    }
}
