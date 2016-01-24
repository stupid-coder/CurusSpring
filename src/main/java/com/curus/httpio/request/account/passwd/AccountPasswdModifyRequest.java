package com.curus.httpio.request.account.passwd;

import java.io.Serializable;

/**
 * Created by stupid-coder on 24/1/16.
 */
public class AccountPasswdModifyRequest implements Serializable{
    private String token;

    private String old_passwd;

    private String new_passwd;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getOld_passwd() {
        return old_passwd;
    }

    public void setOld_passwd(String old_passwd) {
        this.old_passwd = old_passwd;
    }

    public String getNew_passwd() {
        return new_passwd;
    }

    public void setNew_passwd(String new_passwd) {
        this.new_passwd = new_passwd;
    }

    @Override
    public String toString() {
        return "ModifyRequest{" +
                "token='" + token + '\'' +
                ", old_passwd='" + old_passwd + '\'' +
                ", new_passwd='" + new_passwd + '\'' +
                '}';
    }
}
