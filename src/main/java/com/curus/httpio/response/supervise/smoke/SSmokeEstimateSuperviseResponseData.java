package com.curus.httpio.response.supervise.smoke;

/**
 * Created by stupid-coder on 6/3/16.
 */
public class SSmokeEstimateSuperviseResponseData {

    private Long days;
    private Double sv_money;
    private String positionindex;
    private String lossposition;
    private String st_goal;
    private Double no_smoke_hour;

    public Double getNo_smoke_hour() {
        return no_smoke_hour;
    }

    public void setNo_smoke_hour(Double no_smoke_hour) {
        this.no_smoke_hour = no_smoke_hour;
    }

    public String getPositionindex() {
        return positionindex;
    }

    public void setPositionindex(String positionindex) {
        this.positionindex = positionindex;
    }

    public Long getDays() {
        return days;
    }

    public void setDays(Long days) {
        this.days = days;
    }

    public Double getSv_money() {
        return sv_money;
    }

    public void setSv_money(Double sv_money) {
        this.sv_money = sv_money;
    }

    public String getLossposition() {
        return lossposition;
    }

    public void setLossposition(String lossposition) {
        this.lossposition = lossposition;
    }

    public String getSt_goal() {
        return st_goal;
    }

    public void setSt_goal(String st_goal) {
        this.st_goal = st_goal;
    }

    @Override
    public String toString() {
        return "SSmokeEstimateSuperviseResponseData{" +
                "days=" + days +
                ", sv_money=" + sv_money +
                ", positionindex='" + positionindex + '\'' +
                ", lossposition='" + lossposition + '\'' +
                ", st_goal='" + st_goal + '\'' +
                ", no_smoke_hour=" + no_smoke_hour +
                '}';
    }
}
