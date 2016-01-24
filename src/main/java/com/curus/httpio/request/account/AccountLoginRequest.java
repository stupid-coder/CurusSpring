package com.curus.httpio.request.account;

import java.io.Serializable;

/**
 * Created by stupid-coder on 24/1/16.
 */
public class AccountLoginRequest implements Serializable {

    private String phone;
    private String passwd;
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getPasswd() {
        return passwd;
    }
    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }
    @Override
    public String toString() {
        return "LoginRequest{" +
                "phone='" + phone + '\'' +
                ", passwd='" + passwd + '\'' +
                '}';
    }
}
