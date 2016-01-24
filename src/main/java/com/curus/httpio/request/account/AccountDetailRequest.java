package com.curus.httpio.request.account;

import java.io.Serializable;

/**
 * Created by stupid-coder on 24/1/16.
 */
public class AccountDetailRequest implements Serializable{
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "AccountDetailRequest{" +
                "token='" + token + '\'' +
                '}';
    }
}
