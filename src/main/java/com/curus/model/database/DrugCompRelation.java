package com.curus.model.database;

import java.io.Serializable;

/**
 * Created by stupid-coder on 5/29/16.
 */
public class DrugCompRelation implements Serializable {

    private static final long serialVersionUID = 1L;
    // 主键ID
    private String dc_id;
    // 药品ID
    private String drug_id;
    //
    private String comp_id;
    //
    private Double comp_dosis;

    public String getDc_id() {
        return dc_id;
    }

    public void setDc_id(String dc_id) {
        this.dc_id = dc_id;
    }

    public String getDrug_id() {
        return drug_id;
    }

    public void setDrug_id(String drug_id) {
        this.drug_id = drug_id;
    }

    public String getComp_id() {
        return comp_id;
    }

    public void setComp_id(String comp_id) {
        this.comp_id = comp_id;
    }

    public Double getComp_dosis() {
        return comp_dosis;
    }

    public void setComp_dosis(Double comp_dosis) {
        this.comp_dosis = comp_dosis;
    }

    @Override
    public String toString() {
        return "DrugCompRelation{" +
                "dc_id='" + dc_id + '\'' +
                ", drug_id='" + drug_id + '\'' +
                ", comp_id='" + comp_id + '\'' +
                ", comp_dosis=" + comp_dosis +
                '}';
    }
}
