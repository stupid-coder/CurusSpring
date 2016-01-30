package com.curus.httpio.request.patient;

/**
 * Created by stupid-coder on 28/1/16.
 */
public class PatientPushConfigRequest {

    private String token;
    private String patient_id;
    private String can_phone_push;
    private String can_app_push;
    private String can_weixin_push;
    private String can_qq_push;
    private String can_email_push;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(String patient_id) {
        this.patient_id = patient_id;
    }

    public String getCan_phone_push() {
        return can_phone_push;
    }

    public void setCan_phone_push(String can_phone_push) {
        this.can_phone_push = can_phone_push;
    }

    public String getCan_app_push() {
        return can_app_push;
    }

    public void setCan_app_push(String can_app_push) {
        this.can_app_push = can_app_push;
    }

    public String getCan_weixin_push() {
        return can_weixin_push;
    }

    public void setCan_weixin_push(String can_weixin_push) {
        this.can_weixin_push = can_weixin_push;
    }

    public String getCan_qq_push() {
        return can_qq_push;
    }

    public void setCan_qq_push(String can_qq_push) {
        this.can_qq_push = can_qq_push;
    }

    public String getCan_email_push() {
        return can_email_push;
    }

    public void setCan_email_push(String can_email_push) {
        this.can_email_push = can_email_push;
    }

    @Override
    public String toString() {
        return "PatientPushConfigRequest{" +
                "token='" + token + '\'' +
                ", patient_id='" + patient_id + '\'' +
                ", can_phone_push='" + can_phone_push + '\'' +
                ", can_app_push='" + can_app_push + '\'' +
                ", can_weixin_push='" + can_weixin_push + '\'' +
                ", can_qq_push='" + can_qq_push + '\'' +
                ", can_email_push='" + can_email_push + '\'' +
                '}';
    }
}
