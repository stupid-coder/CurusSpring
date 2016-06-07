package com.curus.httpio.request.drug;

import java.io.Serializable;
import java.sql.Date;

/**
 * Created by stupid-coder on 5/29/16.
 */
public class PatientUseDrugUpdateRequest implements Serializable {

    private String token;
    private Long patient_id;
    private String drug_id;
    private String use_policy;
    private String change_time;

    public String getUse_policy() {
        return use_policy;
    }

    public void setUse_policy(String use_policy) {
        this.use_policy = use_policy;
    }

    public String getChange_time() {
        return change_time;
    }

    public void setChange_time(String change_time) {
        this.change_time = change_time;
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

    public String getDrug_id() {
        return drug_id;
    }

    public void setDrug_id(String drug_id) {
        this.drug_id = drug_id;
    }

    @Override
    public String toString() {
        return "PatientUseDrugUpdateRequest{" +
                "change_time=" + change_time +
                ", use_policy='" + use_policy + '\'' +
                ", drug_id='" + drug_id + '\'' +
                ", patient_id=" + patient_id +
                ", token='" + token + '\'' +
                '}';
    }
}
