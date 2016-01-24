package com.curus.httpio.response.account;

/**
 * Created by stupid-coder on 24/1/16.
 */
public class AccountLoginResponseData {
    private String token;
    private Integer is_exp_user;
    private Long uid;

    public AccountLoginResponseData(String token, Integer is_exp_user, Long uid) {
        this.token = token;
        this.is_exp_user = is_exp_user;
        this.uid = uid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getIs_exp_user() {
        return is_exp_user;
    }

    public void setIs_exp_user(Integer is_exp_user) {
        this.is_exp_user = is_exp_user;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "LoginData{" +
                "token='" + token + '\'' +
                ", is_exp_user=" + is_exp_user +
                ", uid=" + uid +
                '}';
    }
}
