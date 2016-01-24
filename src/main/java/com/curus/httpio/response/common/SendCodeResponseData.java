package com.curus.httpio.response.common;

/**
 * Created by stupid-coder on 23/1/16.
 */
public class SendCodeResponseData {
    private String code;
    public SendCodeResponseData(String code) { this.code = code; }
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "SendCodeResponseData{" +
                "code='" + code + '\'' +
                '}';
    }
}
