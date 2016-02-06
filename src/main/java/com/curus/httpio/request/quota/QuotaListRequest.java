package com.curus.httpio.request.quota;

/**
 * Created by stupid-coder on 5/2/16.
 */
public class QuotaListRequest {

    private String token;
    private String cate;
    private String patient_id;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCate() {
        return cate;
    }

    public void setCate(String cate) {
        this.cate = cate;
    }

    public String getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(String patient_id) {
        this.patient_id = patient_id;
    }

    @Override
    public String toString() {
        return "QuotaListRequest{" +
                "token='" + token + '\'' +
                ", cate='" + cate + '\'' +
                ", patient_id='" + patient_id + '\'' +
                '}';
    }
}
