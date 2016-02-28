package com.curus.model.database;

import java.util.List;

/**
 * Created by stupid-coder on 28/2/16.
 */
public class PatientSuperviseList {

    private Long id;
    private Long quota_cate_id;
    private String list;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getQuota_cate_id() {
        return quota_cate_id;
    }

    public void setQuota_cate_id(Long quota_cate_id) {
        this.quota_cate_id = quota_cate_id;
    }

    public String getList() {
        return list;
    }

    public void setList(String list) {
        this.list = list;
    }
}
