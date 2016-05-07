package com.curus.httpio.request.patient;

/**
 * Created by stupid-coder on 7/5/16.
 */
public class PatientInfoRequest {

    private String token;
    private Long patient_id;
    private String phone;
    private String appellation;
    private String name;
    private String address;
    private Double height;
    private String role;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAppellation() {
        return appellation;
    }

    public void setAppellation(String appellation) {
        this.appellation = appellation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "PatientInfoRequest{" +
                "token='" + token + '\'' +
                ", patient_id=" + patient_id +
                ", phone='" + phone + '\'' +
                ", appellation='" + appellation + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", height='" + height + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
