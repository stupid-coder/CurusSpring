package com.curus.httpio.response.account;

/**
 * Created by stupid-coder on 24/1/16.
 */
public class AccountIDetailResponseData {
    private String name;
    private String phone;

    public AccountIDetailResponseData(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "IdetailData{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
