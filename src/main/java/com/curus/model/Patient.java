package com.curus.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * Created by stupid-coder on 25/1/16.
 *
 *
 *
 `id` int(11) NOT NULL AUTO_INCREMENT,
 `name` varchar(30) NOT NULL,
 `gender` tinyint(1) NOT NULL,
 `birth` date NOT NULL,
 `id_number` varchar(18) NOT NULL,
 `phone` bigint(20) NOT NULL,
 `address` varchar(20) DEFAULT NULL,
 `create_time` datetime DEFAULT NULL,
 `weixin` varchar(30) DEFAULT NULL,
 `qq` smallint(6) DEFAULT NULL,
 `email` varchar(30) DEFAULT NULL,
 `other_contact` varchar(30) DEFAULT NULL,
 `can_phone_push` tinyint(1) NOT NULL,
 `can_app_push` tinyint(1) NOT NULL,
 `can_weixin_push` tinyint(1) NOT NULL,
 `can_qq_push` tinyint(1) NOT NULL,
 `can_email_push` tinyint(1) NOT NULL,
 */
public class Patient implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private Integer gender;
    private Date birth;
    private String id_number;
    private String phone;
    private String address;
    private Timestamp create_time;
    private String weixin;
    private String qq;
    private String email;
    private String other_contact;
    private Integer can_phone_push;
    private Integer can_app_push;
    private Integer can_weixin_push;
    private Integer can_qq_push;
    private Integer can_email_push;

    @Override
    public String toString() {
        return "Patient{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", gender=" + gender +
                ", birth=" + birth +
                ", id_number='" + id_number + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", create_time=" + create_time +
                ", weixin='" + weixin + '\'' +
                ", qq='" + qq + '\'' +
                ", email='" + email + '\'' +
                ", other_contact='" + other_contact + '\'' +
                ", can_phone_push=" + can_phone_push +
                ", can_app_push=" + can_app_push +
                ", can_weixin_push=" + can_weixin_push +
                ", can_qq_push=" + can_qq_push +
                ", can_email_push=" + can_email_push +
                '}';
    }

    public Patient() {}

    public Patient(String name, Integer gender, Date birth, String id_number, String phone, String address, Timestamp create_time, String weixin, String qq, String email, String other_contact) {
        this.id = null;
        this.name = name;
        this.gender = gender;
        this.birth = birth;
        this.id_number = id_number;
        this.phone = phone;
        this.address = address;
        this.create_time = create_time;
        this.weixin = weixin;
        this.qq = qq;
        this.email = email;
        this.other_contact = other_contact;
    }

    public Integer getCan_phone_push() {
        return can_phone_push;
    }

    public void setCan_phone_push(Integer can_phone_push) {
        this.can_phone_push = can_phone_push;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Timestamp getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Timestamp create_time) {
        this.create_time = create_time;
    }

    public String getWeixin() {
        return weixin;
    }

    public void setWeixin(String weixin) {
        this.weixin = weixin;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOther_contact() {
        return other_contact;
    }

    public void setOther_contact(String other_contact) {
        this.other_contact = other_contact;
    }

    public Integer getCan_app_push() {
        return can_app_push;
    }

    public void setCan_app_push(Integer can_app_push) {
        this.can_app_push = can_app_push;
    }

    public Integer getCan_qq_push() {
        return can_qq_push;
    }

    public void setCan_qq_push(Integer can_qq_push) {
        this.can_qq_push = can_qq_push;
    }


    public Integer getCan_weixin_push() {
        return can_weixin_push;
    }

    public void setCan_weixin_push(Integer can_weixin_push) {
        this.can_weixin_push = can_weixin_push;
    }

    public Integer getCan_email_push() {
        return can_email_push;
    }

    public void setCan_email_push(Integer can_email_push) {
        this.can_email_push = can_email_push;
    }

}
