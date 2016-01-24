package com.curus.httpio.request.account;

import java.io.Serializable;

/**
 * Created by stupid-coder on 24/1/16.
 */
public class AccountIDetailRequest implements Serializable {
    private String token;
    private String uid;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "IdetailRequest{" +
                "token='" + token + '\'' +
                ", uid='" + uid + '\'' +
                '}';
    }
}
