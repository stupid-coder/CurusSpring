package com.curus.httpio.request.quota;

/**
 * Created by stupid-coder on 3/2/16.
 */
public class QuotaAddRequest {
    private String token;
    private Long measure_date;
    private String cate;
    private String subcate;
    private Long patient_id;
    private String quota;


    public String getSubcate() {
        return subcate;
    }

    public void setSubcate(String subcate) {
        this.subcate = subcate;
    }

    public Long getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(Long patient_id) {
        this.patient_id = patient_id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getMeasure_date() {
        return measure_date;
    }

    public void setMeasure_date(Long measure_date) {
        this.measure_date = measure_date;
    }

    public String getCate() {
        return cate;
    }

    public void setCate(String cate) {
        this.cate = cate;
    }

    public String getQuota() {
        return quota;
    }

    public void setQuota(String quota) {
        this.quota = quota;
    }

    @Override
    public String toString() {
        return "QuotaAddRequest{" +
                "token='" + token + '\'' +
                ", measure_date=" + measure_date +
                ", cate='" + cate + '\'' +
                ", subcate='" + subcate + '\'' +
                ", patient_id=" + patient_id +
                ", quota='" + quota + '\'' +
                '}';
    }
}
