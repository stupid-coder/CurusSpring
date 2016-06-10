package com.curus.model.database;

import java.io.Serializable;

/**
 * Created by stupid-coder on 6/9/16.
 */
public class InternalData implements Serializable {
    private Long id;
    private Long patient_id;
    private Integer cate;
    private String data;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(Long patient_id) {
        this.patient_id = patient_id;
    }

    public Integer getCate() {
        return cate;
    }

    public void setCate(Integer cate) {
        this.cate = cate;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "InternalData{" +
                "id=" + id +
                ", patient_id=" + patient_id +
                ", cate=" + cate +
                ", data='" + data + '\'' +
                '}';
    }
}
