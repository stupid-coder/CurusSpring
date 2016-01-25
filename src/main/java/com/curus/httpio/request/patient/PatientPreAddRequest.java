package com.curus.httpio.request.patient;

import java.io.Serializable;

/**
 * Created by stupid-coder on 25/1/16.
 */
public class PatientPreAddRequest implements Serializable {
    private String token;

    private String id_number;

    private String phone;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getId_number() {
        return id_number;
    }

    public void setId_number(String id_number) {
        this.id_number = id_number;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "PatientPreAddRequest{" +
                "token='" + token + '\'' +
                ", id_number='" + id_number + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
