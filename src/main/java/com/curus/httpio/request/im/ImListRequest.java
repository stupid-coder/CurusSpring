package com.curus.httpio.request.im;

/**
 * Created by stupid-coder on 5/21/16.
 */
public class ImListRequest {
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "ImListRequest{" +
                "token='" + token + '\'' +
                '}';
    }
}
