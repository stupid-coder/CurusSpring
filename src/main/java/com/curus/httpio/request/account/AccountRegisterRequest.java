package com.curus.httpio.request.account;

import java.io.Serializable;

/**
 * Created by stupid-coder on 23/1/16.
 */
public class AccountRegisterRequest implements Serializable {
    private String phone = null;
    private String code = null;
    private String passwd = null;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    @Override
    public String toString() {
        return "AccountRegisterRequest{" +
                "phone='" + phone + '\'' +
                ", code='" + code + '\'' +
                ", passwd='" + passwd + '\'' +
                '}';
    }
}
