package com.curus.httpio.request.account.passwd;

import java.io.Serializable;

/**
 * Created by stupid-coder on 24/1/16.
 */
public class AccountPasswdForgetRequest implements Serializable {

    private String phone;

    private String passwd;

    private String code;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "ForgetRequest{" +
                "phone='" + phone + '\'' +
                ", passwd='" + passwd + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
