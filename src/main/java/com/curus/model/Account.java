package com.curus.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * Created by stupid-coder on 23/1/16.
 */
public class Account implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String phone = null;

    private Integer is_exp_user = 1;

    private Timestamp create_time = null;

    private String name = "";

    private String passwd = null;

    private Integer gender = 1;

    private Date birth = null;

    private String id_number = null;

    private String address = null;

    private String other_contact = null;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getIs_exp_user() {
        return is_exp_user;
    }

    public void setIs_exp_user(Integer is_exp_user) {
        this.is_exp_user = is_exp_user;
    }

    public Timestamp getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Timestamp create_time) {
        this.create_time = create_time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Date getBirth() {
        return birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }

    public String getId_number() {
        return id_number;
    }

    public void setId_number(String id_number) {
        this.id_number = id_number;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOther_contact() {
        return other_contact;
    }

    public void setOther_contact(String other_contact) {
        this.other_contact = other_contact;
    }

    @Override
    public String toString() {
        return "Account{" +
                "phone=" + phone +
                ", is_exp_user=" + is_exp_user +
                ", create_time=" + create_time +
                ", name='" + name + '\'' +
                ", passwd='" + passwd + '\'' +
                ", gender=" + gender +
                ", birth=" + birth +
                ", id_number='" + id_number + '\'' +
                ", address='" + address + '\'' +
                ", other_contact='" + other_contact + '\'' +
                '}';
    }
}
