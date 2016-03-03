package com.curus.httpio.request.quota;

/**
 * Created by stupid-coder on 5/2/16.
 */
public class QuotaListRequest {

    private String token;
    private String cate;
    private String subcate;
    private Long patient_id;
    private Long days;

    @Override
    public String toString() {
        return "QuotaListRequest{" +
                "token='" + token + '\'' +
                ", cate='" + cate + '\'' +
                ", subcate='" + subcate + '\'' +
                ", patient_id=" + patient_id +
                ", days=" + days +
                '}';
    }

    public String getSubcate() {
        return subcate;
    }

    public void setSubcate(String subcate) {
        this.subcate = subcate;
    }

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

    public Long getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(Long patient_id) {
        this.patient_id = patient_id;
    }

    public Long getDays() {
        return days;
    }

    public void setDays(Long days) {
        this.days = days;
    }
}
