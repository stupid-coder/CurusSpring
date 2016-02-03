package com.curus.httpio.response.patient;

/**
 * Created by stupid-coder on 25/1/16.
 */
public class PatientAddResponseData {
    private Long patient_id;

    public PatientAddResponseData() {
    }

    public PatientAddResponseData(Long patient_id) {
        this.patient_id = patient_id;
    }

    public Long getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(Long patient_id) {
        this.patient_id = patient_id;
    }

    @Override
    public String toString() {
        return "PatientAddResponseData{" +
                "patient_id=" + patient_id +
                '}';
    }
}
