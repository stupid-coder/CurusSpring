package com.curus.httpio.response.supervise;

import com.curus.model.database.PatientSupervise;
import com.curus.utils.TimeUtils;

/**
 * Created by stupid-coder on 21/4/16.
 */
public class SuperviseGetResponseData {
    private Long create_time;
    private Long end_date;
    private String initial;
    private String target;
    private String current;
    private String policy;
    private String result;

    public SuperviseGetResponseData(PatientSupervise patientSupervise) {
        this.create_time = TimeUtils.m2t(patientSupervise.getCreate_time().getTime());
        this.end_date = patientSupervise.getEnd_date() != null ? TimeUtils.date2Long(patientSupervise.getEnd_date()) : null;
        this.initial = patientSupervise.getInitial();
        this.target = patientSupervise.getTarget();
        this.current = patientSupervise.getCurrent();
        this.policy = patientSupervise.getPolicy();
        this.result = patientSupervise.getResult();
    }

    public Long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Long create_time) {
        this.create_time = create_time;
    }

    public Long getEnd_date() {
        return end_date;
    }

    public void setEnd_date(Long end_date) {
        this.end_date = end_date;
    }

    public String getInitial() {
        return initial;
    }

    public void setInitial(String initial) {
        this.initial = initial;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "SuperviseGetResponseData{" +
                "create_time=" + create_time +
                ", end_date=" + end_date +
                ", initial='" + initial + '\'' +
                ", target='" + target + '\'' +
                ", current='" + current + '\'' +
                ", policy='" + policy + '\'' +
                ", result='" + result + '\'' +
                '}';
    }
}
