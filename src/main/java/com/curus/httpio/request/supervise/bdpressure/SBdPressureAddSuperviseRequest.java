package com.curus.httpio.request.supervise.bdpressure;

/**
 * Created by stupid-coder on 7/3/16.
 */
public class SBdPressureAddSuperviseRequest {

    private String token;
    private Long patient_id;
    private Double lossweight;
    private Double dihealthscore;
    private Double phyactivity;

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

    public Double getLossweight() {
        return lossweight;
    }

    public void setLossweight(Double lossweight) {
        this.lossweight = lossweight;
    }

    public Double getDihealthscore() {
        return dihealthscore;
    }

    public void setDihealthscore(Double dihealthscore) {
        this.dihealthscore = dihealthscore;
    }

    public Double getPhyactivity() {
        return phyactivity;
    }

    public void setPhyactivity(Double phyactivity) {
        this.phyactivity = phyactivity;
    }

    @Override
    public String toString() {
        return "SBdPressureAddSuperviseRequest{" +
                "token='" + token + '\'' +
                ", patient_id=" + patient_id +
                ", lossweight=" + lossweight +
                ", dihealthscore=" + dihealthscore +
                ", phyactivity=" + phyactivity +
                '}';
    }
}
