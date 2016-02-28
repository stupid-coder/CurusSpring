package com.curus.model.database;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * Created by stupid-coder on 27/1/16.
 */
public class PatientSupervise implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long account_id;
    private Long patient_id;
    private Long quota_cat_id;
    private Timestamp create_time;
    private Date end_date;
    private String initial;
    private String target;
    private String current;
    private Integer last;
    private String policy;
    private String result;

    @Override
    public String toString() {
        return "PatientSupervice{" +
                "id=" + id +
                ", account_id=" + account_id +
                ", patient_id=" + patient_id +
                ", quota_cat_id=" + quota_cat_id +
                ", create_time=" + create_time +
                ", end_date=" + end_date +
                ", initial='" + initial + '\'' +
                ", target='" + target + '\'' +
                ", current='" + current + '\'' +
                ", last=" + last +
                ", policy='" + policy + '\'' +
                ", result='" + result + '\'' +
                '}';
    }

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }

    public Long getAccount_id() {
        return account_id;
    }

    public void setAccount_id(Long account_id) {
        this.account_id = account_id;
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

    public Long getQuota_cat_id() {
        return quota_cat_id;
    }

    public void setQuota_cat_id(Long quota_cat_id) {
        this.quota_cat_id = quota_cat_id;
    }

    public Timestamp getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Timestamp create_time) {
        this.create_time = create_time;
    }

    public Date getEnd_date() {
        return end_date;
    }

    public void setEnd_date(Date end_date) {
        this.end_date = end_date;
    }

    public String getInitial() {
        return initial;
    }

    public void setInitial(String initial) {
        this.initial = initial;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public Integer getLast() {
        return last;
    }

    public void setLast(Integer last) {
        this.last = last;
    }

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

}
