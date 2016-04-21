package com.curus.httpio.request.supervise;

/**
 * Created by stupid-coder on 21/4/16.
 */
public class SuperviseGetRequest {
    private String token;
    private Long patient_id;
    private String cate;

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

    public String getCate() {
        return cate;
    }

    public void setCate(String cate) {
        this.cate = cate;
    }

    @Override
    public String toString() {
        return "SuperviseGetRequest{" +
                "token='" + token + '\'' +
                ", patient_id=" + patient_id +
                ", cate='" + cate + '\'' +
                '}';
    }
}
