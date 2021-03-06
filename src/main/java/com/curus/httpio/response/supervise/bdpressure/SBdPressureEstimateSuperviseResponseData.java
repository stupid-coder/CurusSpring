package com.curus.httpio.response.supervise.bdpressure;

/**
 * Created by stupid-coder on 7/3/16.
 */
public class SBdPressureEstimateSuperviseResponseData {

    private String bptimelines;
    private String nonmed_suggestion;
    private String position;
    private String positionindex;
    private String position_suggestion;
    private String nonmed_status;

    public String getPosition_suggestion() {
        return position_suggestion;
    }

    public void setPosition_suggestion(String position_suggestion) {
        this.position_suggestion = position_suggestion;
    }

    public String getPositionindex() {
        return positionindex;
    }

    public void setPositionindex(String positionindex) {
        this.positionindex = positionindex;
    }

    public String getBptimelines() {
        return bptimelines;
    }

    public void setBptimelines(String bptimelines) {
        this.bptimelines = bptimelines;
    }

    public String getNonmed_suggestion() {
        return nonmed_suggestion;
    }

    public void setNonmed_suggestion(String nonmed_suggestion) {
        this.nonmed_suggestion = nonmed_suggestion;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getNonmed_status() {
        return nonmed_status;
    }

    public void setNonmed_status(String nonmed_status) {
        this.nonmed_status = nonmed_status;
    }

    @Override
    public String toString() {
        return "SBdPressureEstimateSuperviseResponseData{" +
                "bptimelines='" + bptimelines + '\'' +
                ", nonmed_suggestion='" + nonmed_suggestion + '\'' +
                ", position='" + position + '\'' +
                ", positionindex='" + positionindex + '\'' +
                ", position_suggestion='" + position_suggestion + '\'' +
                ", nonmed_status='" + nonmed_status + '\'' +
                '}';
    }
}
