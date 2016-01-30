package com.curus.httpio.request.issue;

/**
 * Created by stupid-coder on 28/1/16.
 */
public class IssueListRequest {

    private String token;
    private String patient_id;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(String patient_id) {
        this.patient_id = patient_id;
    }

    @Override
    public String toString() {
        return "IssueListRequest{" +
                "token='" + token + '\'' +
                ", patient_id='" + patient_id + '\'' +
                '}';
    }
}
