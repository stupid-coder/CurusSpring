package com.curus.httpio.request.patient;

/**
 * Created by stupid-coder on 28/1/16.
 */
public class PatientListRequest {

    private String token;

    public PatientListRequest(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "PatientListRequest{" +
                "token='" + token + '\'' +
                '}';
    }
}
