package com.curus.httpio.response.patient;

import com.curus.model.database.AccountPatient;
import com.curus.model.database.Patient;
import com.curus.model.database.Quota;
import com.curus.utils.AppellationUtils;
import com.curus.utils.RoleUtils;
import com.curus.utils.service.quota.QuotaServiceUtils;

/**
 * Created by stupid-coder on 7/5/16.
 */
public class PatientInfoResponseData {

    private String phone;
    private String appellation;
    private String name;
    private String address;
    private Double height;
    private String role;

    public PatientInfoResponseData(Patient patient, AccountPatient accountPatient, Quota height) {
        this.phone = patient.getPhone();
        this.appellation = AppellationUtils.getAppellationName(accountPatient.getAppellation_id());
        this.name = patient.getName();
        this.address = patient.getAddress();
        this.height = QuotaServiceUtils.getHeight(height.getRecord());
        this.role = RoleUtils.getRoleName(accountPatient.getRole_id());
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAppellation() {
        return appellation;
    }

    public void setAppellation(String appellation) {
        this.appellation = appellation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "PatientInfoResponseData{" +
                "phone='" + phone + '\'' +
                ", appellation='" + appellation + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", height=" + height +
                ", role='" + role + '\'' +
                '}';
    }
}
