package com.curus.httpio.response.supervise;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stupid-coder on 13/4/16.
 */
public class SuperviseListResponseData {
    private List<String> quotaList;

    public SuperviseListResponseData() {
        quotaList = new ArrayList<String>();
    }

    public void addQuotaList(String quotaName) {
        quotaList.add(quotaName);
    }

    public List<String> getQuotaList() {
        return quotaList;
    }

    public void setQuotaList(List<String> quotaList) {
        this.quotaList = quotaList;
    }

    @Override
    public String toString() {
        return "SuperviseListResponseData{" +
                "quotaList=" + quotaList +
                '}';
    }
}
