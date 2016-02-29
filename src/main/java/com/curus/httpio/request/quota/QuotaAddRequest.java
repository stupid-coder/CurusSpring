package com.curus.httpio.request.quota;

/**
 * Created by stupid-coder on 3/2/16.
 */
public class QuotaAddRequest {
    private String token;
    private Long measure_date;
    private String cate;
    private Long patient_id;
    private String value;

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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "QuotaAddRequest{" +
                "token='" + token + '\'' +
                ", measure_date='" + measure_date + '\'' +
                ", cate='" + cate + '\'' +
                ", patient_id='" + patient_id + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
