package com.curus.httpio.request.quota;

/**
 * Created by stupid-coder on 3/2/16.
 */
public class QuotaAddRequest {
    private String token;
    private String date;
    private String cate;
    private String patient_id;
    private String value;

    public String getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(String patient_id) {
        this.patient_id = patient_id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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
                ", date='" + date + '\'' +
                ", cate='" + cate + '\'' +
                ", patient_id='" + patient_id + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
