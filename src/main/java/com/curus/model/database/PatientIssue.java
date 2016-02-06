package com.curus.model.database;

import java.io.Serializable;
import java.sql.Date;

/**
 * Created by stupid-coder on 27/1/16.
 */
public class PatientIssue implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long patient_id;
    private Long issue_id;
    private Integer status;

    public PatientIssue() {
    }

    public PatientIssue(Long patient_id, Long issue_id, Integer status) {
        this.patient_id = patient_id;
        this.issue_id = issue_id;
        this.status = status;
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

    public Long getIssue_id() {
        return issue_id;
    }

    public void setIssue_id(Long issue_id) {
        this.issue_id = issue_id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "PatientIssue{" +
                "id=" + id +
                ", patient_id=" + patient_id +
                ", issue_id=" + issue_id +
                ", status=" + status +
                '}';
    }
}
