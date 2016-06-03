package com.curus.httpio.request.drug;

/**
 * Created by stupid-coder on 6/3/16.
 */
public class PatientUseDrugDeleteRequest {
    private String token;
    private Long patient_id;
    private String drug_id;

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
        return "PatientUseDrugDeleteRequest{" +
                "token='" + token + '\'' +
                ", patient_id=" + patient_id +
                ", drug_id='" + drug_id + '\'' +
                '}';
    }
}
