package com.curus.httpio.response;

import com.curus.utils.constant.ErrorConst;

/**
 * Created by stupid-coder on 23/1/16.
 */
public class ErrorData {

    private String error_code = null;
    private String error_msg = null;

    public ErrorData(String code, String msg) {
        this.error_code = code;
        this.error_msg = msg;
    }

    public ErrorData(int status_error_idx) {
        this.error_code = ErrorConst.GetErrorCode(status_error_idx);
        this.error_msg = ErrorConst.GetErrorMsg(status_error_idx);
    }

    public ErrorData(int status_error_idx, String para) {
        this.error_code = ErrorConst.GetErrorCode(status_error_idx);
        this.error_msg = String.format(ErrorConst.GetErrorMsg(status_error_idx),para);
    }
    public String getError_code() {
        return this.error_code;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }

    public String getError_msg() {
        return this.error_msg;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }
}

