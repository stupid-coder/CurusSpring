package com.curus.httpio.request.drug;

import java.io.Serializable;

/**
 * Created by stupid-coder on 5/29/16.
 */
public class DrugDirectionsGetRequest implements Serializable {

    private String drug_id;

    public String getDrug_id() {
        return drug_id;
    }

    public void setDrug_id(String drug_id) {
        this.drug_id = drug_id;
    }

    @Override
    public String toString() {
        return "DrugDirectionsGetRequest{" +
                "drug_id='" + drug_id + '\'' +
                '}';
    }
}
