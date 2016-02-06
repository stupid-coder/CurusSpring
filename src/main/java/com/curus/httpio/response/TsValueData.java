package com.curus.httpio.response;

import java.sql.Timestamp;

/**
 * Created by stupid-coder on 5/2/16.
 */
public class TsValueData implements Comparable<TsValueData> {

    private Long  measure_time;
    private String value;

    public TsValueData(Long measure_time, String value) {
        this.measure_time = measure_time;
        this.value = value;
    }

    public Long getMeasure_time() {
        return measure_time;
    }

    public void setMeasure_time(Long measure_time) {
        this.measure_time = measure_time;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int compareTo(TsValueData o) {
        return measure_time.compareTo(o.getMeasure_time());
    }

    @Override
    public String toString() {
        return "TsValueData{" +
                "measure_time=" + measure_time +
                ", value='" + value + '\'' +
                '}';
    }
}
