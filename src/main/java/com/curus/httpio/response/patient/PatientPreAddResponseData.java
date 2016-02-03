package com.curus.httpio.response.patient;

import com.curus.model.AccountPatient;
import com.curus.model.Patient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stupid-coder on 25/1/16.
 */

public class PatientPreAddResponseData {

    public class PatientSubData {
        private String name;
        private Integer gender;
        private String phone;
        private String birth;
        private String address;

        public PatientSubData(Patient patient) {
            this.name = patient.getName();
            this.gender = patient.getGender();
            this.phone = patient.getPhone();
            this.birth = patient.getBirth().toString();
            this.address = patient.getAddress();
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getGender() {
            return gender;
        }

        public void setGender(Integer gender) {
            this.gender = gender;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getBirth() {
            return birth;
        }

        public void setBirth(String birth) {
            this.birth = birth;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        @Override
        public String toString() {
            return "PatientSubData{" +
                    "name='" + name + '\'' +
                    ", gender=" + gender +
                    ", phone='" + phone + '\'' +
                    ", birth='" + birth + '\'' +
                    ", address='" + address + '\'' +
                    '}';
        }
    }
    public class ManagerSubData {
        private String role_name;
        private String account_name;

        public ManagerSubData(AccountPatient ap) {
            this.role_name = ap.getRole_id().toString();
            this.account_name = ap.getAccount_id().toString();
        }

        public String getRole_name() {
            return role_name;
        }

        public void setRole_name(String role_name) {
            this.role_name = role_name;
        }

        public String getAccount_name() {
            return account_name;
        }

        public void setAccount_name(String account_name) {
            this.account_name = account_name;
        }

        @Override
        public String toString() {
            return "ManagerSubData{" +
                    "role_name='" + role_name + '\'' +
                    ", account_name='" + account_name + '\'' +
                    '}';
        }
    }

    private String code;
    private Long is_exist;
    private Long under_manager;
    private PatientSubData patient;
    private List<ManagerSubData> managers;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getIs_exist() {
        return is_exist;
    }

    public void setIs_exist(Long is_exist) {
        this.is_exist = is_exist;
    }

    public Long getUnder_manager() {
        return under_manager;
    }

    public void setUnder_manager(Long under_manager) {
        this.under_manager = under_manager;
    }

    public PatientSubData getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = new PatientSubData(patient);
    }

    public List<ManagerSubData> getManagers() {
        return managers;
    }

    public void setManagers(List<AccountPatient> ap) {
        if (ap != null) {
            this.managers = new ArrayList<ManagerSubData>();
            for (int i = 0; i < ap.size(); ++i) {
                managers.add(new ManagerSubData(ap.get(i)));
            }
        } else this.managers = null;
    }

    @Override
    public String toString() {
        return "PatientPreAddResponseData{" +
                "code='" + code + '\'' +
                ", is_exist=" + is_exist +
                ", under_manager=" + under_manager +
                ", patient=" + patient +
                ", managers=" + managers +
                '}';
    }
}
