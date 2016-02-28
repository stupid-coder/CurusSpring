package com.curus.httpio.request.supervise.weight;

import java.util.List;
import java.util.Map;

/**
 * Created by stupid-coder on 27/2/16.
 */
public class SWeightPretestRequest {

    private String token;
    private Long patient_id;
    private Double weight_loss;
    private Long lunch;
    private Long dinner;
    private Long snacks;
    private Long fatink;
    private List<Map<String,Long>> sports;


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(Long patient_id) {
        this.patient_id = patient_id;
    }

    public Double getWeight_loss() {
        return weight_loss;
    }

    public void setWeight_loss(Double weight_loss) {
        this.weight_loss = weight_loss;
    }

    public Long getLunch() {
        return lunch;
    }

    public void setLunch(Long lunch) {
        this.lunch = lunch;
    }

    public Long getDinner() {
        return dinner;
    }

    public void setDinner(Long dinner) {
        this.dinner = dinner;
    }

    public Long getSnacks() {
        return snacks;
    }

    public void setSnacks(Long snacks) {
        this.snacks = snacks;
    }

    public Long getFatink() {
        return fatink;
    }

    public void setFatink(Long fatink) {
        this.fatink = fatink;
    }

    public List<Map<String, Long>> getSports() {
        return sports;
    }

    public void setSports(List<Map<String, Long>> sports) {
        this.sports = sports;
    }

    @Override
    public String toString() {
        return "SWeightPretestRequest{" +
                "token='" + token + '\'' +
                ", patient_id=" + patient_id +
                ", weight_loss=" + weight_loss +
                ", lunch=" + lunch +
                ", dinner=" + dinner +
                ", snacks=" + snacks +
                ", fatink=" + fatink +
                ", sports=" + sports +
                '}';
    }
}

