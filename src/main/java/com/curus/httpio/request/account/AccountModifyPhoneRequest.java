package com.curus.httpio.request.account;

/**
 * Created by stupid-coder on 24/1/16.
 */
public class AccountModifyPhoneRequest {
    private String token;
    private String phone;
    private String code;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

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

    @Override
    public String toString() {
        return "ModifyPhoneRequest{" +
                "token='" + token + '\'' +
                ", phone='" + phone + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
