package com.curus.httpio.response.supervise.weight;

import com.curus.model.record.SuperviseWeightListRecord;

import java.util.List;

/**
 * Created by stupid-coder on 28/2/16.
 */
public class SWeightEstimateResponseData {

    private Long day_wermanagerment;
    private String weight_change;
    private SuperviseWeightListRecord weight_lossposition;
    private Double weight_loss;
    private Double BMI;
    private String BMI_evaluation;
    private String we_loss_evaluation;

    @Override
    public String toString() {
        return "SWeightEstimateResponseData{" +
                "day_wermanagerment=" + day_wermanagerment +
                ", weight_change=" + weight_change +
                ", weight_lossposition=" + weight_lossposition +
                ", weight_loss='" + weight_loss + '\'' +
                ", BMI=" + BMI +
                ", BMI_evaluation='" + BMI_evaluation + '\'' +
                ", we_loss_evaluation='" + we_loss_evaluation + '\'' +
                '}';
    }

    public Long getDay_wermanagerment() {
        return day_wermanagerment;
    }

    public void setDay_wermanagerment(Long day_wermanagerment) {
        this.day_wermanagerment = day_wermanagerment;
    }

    public Double getWeight_loss() {
        return weight_loss;
    }

    public void setWeight_loss(Double weight_loss) {
        this.weight_loss = weight_loss;
    }

    public SuperviseWeightListRecord getWeight_lossposition() {
        return weight_lossposition;
    }

    public void setWeight_lossposition(SuperviseWeightListRecord weight_lossposition) {
        this.weight_lossposition = weight_lossposition;
    }

    public String getWeight_change() {
        return weight_change;
    }

    public void setWeight_change(String weight_change) {
        this.weight_change = weight_change;
    }

    public Double getBMI() {
        return BMI;
    }

    public void setBMI(Double BMI) {
        this.BMI = BMI;
    }

    public String getBMI_evaluation() {
        return BMI_evaluation;
    }

    public void setBMI_evaluation(String BMI_evaluation) {
        this.BMI_evaluation = BMI_evaluation;
    }

    public String getWe_loss_evaluation() {
        return we_loss_evaluation;
    }

    public void setWe_loss_evaluation(String we_loss_evaluation) {
        this.we_loss_evaluation = we_loss_evaluation;
    }
}
