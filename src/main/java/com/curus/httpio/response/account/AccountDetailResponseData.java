package com.curus.httpio.response.account;

import com.curus.model.Account;

import java.io.Serializable;

/**
 * Created by stupid-coder on 24/1/16.
 */
public class AccountDetailResponseData implements Serializable {

    private Account detail;

    public AccountDetailResponseData(Account detail) {
        this.detail = detail;
    }

    public Account getDetail() {
        return detail;
    }

    public void setDetail(Account detail) {
        this.detail = detail;
    }

    @Override
    public String toString() {
        return "AccountDetailResponse{" +
                "detail=" + detail +
                '}';
    }
}
