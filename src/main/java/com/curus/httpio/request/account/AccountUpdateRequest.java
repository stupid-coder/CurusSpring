package com.curus.httpio.request.account;

import java.io.Serializable;

/**
 * Created by stupid-coder on 24/1/16.
 */
public class AccountUpdateRequest implements Serializable {

    private String token;

    private String name;

    private String gender;

    private String birth;

    private String id_number;

    private String address;

    private String contact;

    private String weight;

    private String height;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
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

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "UpdateRequest{" +
                "token='" + token + '\'' +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", birth='" + birth + '\'' +
                ", id_number='" + id_number + '\'' +
                ", address='" + address + '\'' +
                ", contact='" + contact + '\'' +
                ", weight='" + weight + '\'' +
                ", height='" + height + '\'' +
                '}';
    }
}
