package com.curus.model.database;

import com.curus.utils.TimeUtils;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * Created by stupid-coder on 27/1/16.
 */
public class Quota implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long account_id;
    private Long patient_id;
    private Date measure_date;
    private Long quota_cat_id;
    private String record;
    private String sub_cat;

    public Quota() {
    }

    public Quota(Long account_id, Long patient_id, Date measure_date, Long quota_cat_id, String record) {
        this.account_id = account_id;
        this.patient_id = patient_id;
        this.measure_date = measure_date;
        this.quota_cat_id = quota_cat_id;
        this.record = record;
        this.id = null;
        this.sub_cat = null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAccount_id() {
        return account_id;
    }

    public void setAccount_id(Long account_id) {
        this.account_id = account_id;
    }

    public Long getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(Long patient_id) {
        this.patient_id = patient_id;
    }

    public Date getMeasure_date() {
        return measure_date;
    }

    public void setMeasure_date(Date measure_date) {
        this.measure_date = measure_date;
    }

    public Long getQuota_cat_id() {
        return quota_cat_id;
    }

    public void setQuota_cat_id(Long quota_cat_id) {
        this.quota_cat_id = quota_cat_id;
    }

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }

    public String getSub_cat() {
        return sub_cat;
    }

    public void setSub_cat(String sub_cat) {
        this.sub_cat = sub_cat;
    }

    @Override
    public String toString() {
        return "Quota{" +
                "id=" + id +
                ", account_id=" + account_id +
                ", patient_id=" + patient_id +
                ", measure_date=" + measure_date +
                ", quota_cat_id=" + quota_cat_id +
                ", record='" + record + '\'' +
                ", sub_cat='" + sub_cat + '\'' +
                '}';
    }
}
