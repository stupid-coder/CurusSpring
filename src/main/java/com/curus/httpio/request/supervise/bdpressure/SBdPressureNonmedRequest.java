package com.curus.httpio.request.supervise.bdpressure;

/**
 * Created by stupid-coder on 7/3/16.
 */
public class SBdPressureNonmedRequest {
    private String token;
    private Long patient_id;
    private Double diet;
    private Double weight;
    private Double activity;

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

    public Double getDiet() {
        return diet;
    }

    public void setDiet(Double diet) {
        this.diet = diet;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getActivity() {
        return activity;
    }

    public void setActivity(Double activity) {
        this.activity = activity;
    }

    @Override
    public String toString() {
        return "SBdPressureNonmedRequest{" +
                "token='" + token + '\'' +
                ", patient_id=" + patient_id +
                ", diet=" + diet +
                ", weight=" + weight +
                ", activity=" + activity +
                '}';
    }
}
