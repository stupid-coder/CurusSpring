package com.curus.httpio.response.patient;

import com.curus.model.database.AccountPatient;
import com.curus.model.database.Patient;
import com.curus.utils.AppellationUtils;
import com.curus.utils.RoleUtils;

import java.util.List;

/**
 * Created by stupid-coder on 28/1/16.
 */
public class PatientListResponseData {


    public class PatientListInfo {
        private String patient_name;
        private String role_name;
        private Integer is_self;
        private Long patient_id;
        private String appellation_name;

        public PatientListInfo(Patient patient, AccountPatient accountPatient) {
            this.patient_name = patient.getName();
            this.role_name = RoleUtils.getRoleName(accountPatient.getRole_id());
            this.is_self = accountPatient.getIs_self();
            this.patient_id = patient.getId();
            this.appellation_name = AppellationUtils.getAppellationName(accountPatient.getAppellation_id());
        }

        public String getPatient_name() {
            return patient_name;
        }

        public void setPatient_name(String patient_name) {
            this.patient_name = patient_name;
        }

        public String getRole_name() {
            return role_name;
        }

        public void setRole_name(String role_name) {
            this.role_name = role_name;
        }

        public Integer getIs_self() {
            return is_self;
        }

        public void setIs_self(Integer is_self) {
            this.is_self = is_self;
        }

        public Long getPatient_id() {
            return patient_id;
        }

        public void setPatient_id(Long patient_id) {
            this.patient_id = patient_id;
        }

        public String getAppellation_name() {
            return appellation_name;
        }

        public void setAppellation_name(String appellation_name) {
            this.appellation_name = appellation_name;
        }

        @Override
        public String toString() {
            return "PatientListInfo{" +
                    "patient_name='" + patient_name + '\'' +
                    ", role_name='" + role_name + '\'' +
                    ", is_self=" + is_self +
                    ", patient_id=" + patient_id +
                    ", appellation_name='" + appellation_name + '\'' +
                    '}';
        }
    }

    private List<PatientListInfo> patients;

    public List<PatientListInfo> getPatients() {
        return patients;
    }

    public void setPatients(List<PatientListInfo> patients) {
        this.patients = patients;
    }

    @Override
    public String toString() {
        return "PatientListResponseData{" +
                "patients=" + patients +
                '}';
    }
}
