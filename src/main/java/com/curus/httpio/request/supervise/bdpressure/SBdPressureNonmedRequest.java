package com.curus.httpio.request.supervise.bdpressure;

/**
 * Created by stupid-coder on 7/3/16.
 */
public class SBdPressureNonmedRequest {
    private String token;
    private Long patient_id;
    private String mode;
    private String value;

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

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "SBdPressureNonmedRequest{" +
                "token='" + token + '\'' +
                ", patient_id=" + patient_id +
                ", mode='" + mode + '\'' +
                ", value=" + value +
                '}';
    }
}
