package com.curus.model.database;

import javax.xml.crypto.Data;
import java.io.Serializable;
import java.sql.Date;

/**
 * Created by stupid-coder on 5/29/16.
 */
public class PatientUseDrug implements Serializable {

    private Long id;
    // 管理对象ID
    private Long patient_id;
    // 药物ID
    private String drug_id;
    // 使用方式
    private String use_policy;
    // 添加时间
    private Date change_time;
    // 是否是最新方案
    private Integer last;

    public PatientUseDrug(Long patient_id, String drug_id, String use_policy, Date change_time, Integer last) {
        this.patient_id = patient_id;
        this.drug_id = drug_id;
        this.use_policy = use_policy;
        this.change_time = change_time;
        this.last = last;
    }

    public PatientUseDrug() {
    }

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

    public String getDrug_id() {
        return drug_id;
    }

    public void setDrug_id(String drug_id) {
        this.drug_id = drug_id;
    }

    public String getUse_policy() {
        return use_policy;
    }

    public void setUse_policy(String use_policy) {
        this.use_policy = use_policy;
    }

    public Date getChange_time() {
        return change_time;
    }

    public void setChange_time(Date change_time) {
        this.change_time = change_time;
    }

    public Integer getLast() {
        return last;
    }

    public void setLast(Integer last) {
        this.last = last;
    }

    @Override
    public String toString() {
        return "PatientUseDrug{" +
                "id=" + id +
                ", patient_id=" + patient_id +
                ", drug_id='" + drug_id + '\'' +
                ", use_policy='" + use_policy + '\'' +
                ", change_time=" + change_time +
                ", last=" + last +
                '}';
    }
}
