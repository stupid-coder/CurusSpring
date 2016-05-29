package com.curus.httpio.request.drug;

import java.io.Serializable;

/**
 * Created by stupid-coder on 5/29/16.
 */
public class DrugSearchRequest implements Serializable {

    private String gov_id;
    private String product_name;
    private String manu_name;

    public String getGov_id() {
        return gov_id;
    }

    public void setGov_id(String gov_id) {
        this.gov_id = gov_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getManu_name() {
        return manu_name;
    }

    public void setManu_name(String manu_name) {
        this.manu_name = manu_name;
    }

    @Override
    public String toString() {
        return "DrugSearchRequest{" +
                "gov_id='" + gov_id + '\'' +
                ", product_name='" + product_name + '\'' +
                ", manu_name='" + manu_name + '\'' +
                '}';
    }
}
