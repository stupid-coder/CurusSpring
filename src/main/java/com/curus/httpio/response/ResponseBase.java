package com.curus.httpio.response;

import com.alibaba.fastjson.JSON;

/**
 * Created by stupid-coder on 23/1/16.
 */
public class ResponseBase {
    private Integer status = null;
    private Object data = null;

    public ResponseBase(Integer status, Object data) {
        this.status = status;
        this.data = data;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String toString() {
        return JSON.toJSONString(this);
    }
}
