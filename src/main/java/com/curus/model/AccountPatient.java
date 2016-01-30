package com.curus.model;

import java.io.Serializable;

/**
 * Created by stupid-coder on 25/1/16.
 *
 * */
public class AccountPatient implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id ;
    private Long account_id;
    private Long patient_id;
    private Integer is_self;
    private Integer is_super_validate;
    private Integer is_patient_validate;
    private Long role_id;
    private Long appellation_id;
    private Long status;

    public AccountPatient(Long account_id, Long patient_id) {
        this.account_id = account_id;
        this.patient_id = patient_id;
        this.is_self = null;
        this.id = null;
        this.is_super_validate = null;
        this.is_patient_validate = null;
        this.role_id = null;
        this.appellation_id = null;
        this.status = null;
    }

    public AccountPatient(Long account_id, Long patient_id, Integer is_self, Integer is_super_validate, Integer is_patient_validate, Long role_id, Long appellation_id) {
        this.id = null;
        this.status = null;
        this.account_id = account_id;
        this.patient_id = patient_id;
        this.is_self = is_self;
        this.is_super_validate = is_super_validate;
        this.is_patient_validate = is_patient_validate;
        this.role_id = role_id;
        this.appellation_id = appellation_id;
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

    public Integer getIs_self() {
        return is_self;
    }

    public void setIs_self(Integer is_self) {
        this.is_self = is_self;
    }

    public Integer getIs_super_validate() {
        return is_super_validate;
    }

    public void setIs_super_validate(Integer is_super_validate) {
        this.is_super_validate = is_super_validate;
    }

    public Integer getIs_patient_validate() {
        return is_patient_validate;
    }

    public void setIs_patient_validate(Integer is_patient_validate) {
        this.is_patient_validate = is_patient_validate;
    }

    public Long getRole_id() {
        return role_id;
    }

    public void setRole_id(Long role_id) {
        this.role_id = role_id;
    }

    public Long getAppellation_id() {
        return appellation_id;
    }

    public void setAppellation_id(Long appellation_id) {
        this.appellation_id = appellation_id;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "AccountPatient{" +
                "id=" + id +
                ", account_id=" + account_id +
                ", patient_id=" + patient_id +
                ", is_self=" + is_self +
                ", is_super_validate=" + is_super_validate +
                ", is_patient_validate=" + is_patient_validate +
                ", role_id=" + role_id +
                ", appellation_id=" + appellation_id +
                ", status=" + status +
                '}';
    }
}
