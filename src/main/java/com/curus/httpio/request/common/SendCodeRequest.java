package com.curus.httpio.request.common;

import java.io.Serializable;

/**
 * Created by stupid-coder on 23/1/16.
 */
public class SendCodeRequest implements Serializable {

    private String cate;

    private String phone;

    private String token;

    private String id_number;

    public String getCate() {
        return cate;
    }

    public void setCate(String cate) {
        this.cate = cate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

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

    @Override
    public String toString() {
        return "SendCodeRequest {" +
                "cate='" + cate + '\'' +
                ", phone='" + phone + '\'' +
                ", token='" + token + '\'' +
                ", id_number='" + id_number + '\'' +
                '}';
    }
}
