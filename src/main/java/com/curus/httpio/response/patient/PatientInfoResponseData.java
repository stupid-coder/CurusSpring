package com.curus.httpio.response.patient;

/**
 * Created by stupid-coder on 7/5/16.
 */
public class PatientInfoResponseData {

    private String phone;
    private String appellation;
    private String name;
    private String address;
    private String height;

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

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "PatientInfoResponseData{" +
                "phone='" + phone + '\'' +
                ", appellation='" + appellation + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", height='" + height + '\'' +
                '}';
    }
}
