package com.curus.model.record;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import jdk.nashorn.internal.parser.JSONParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by stupid-coder on 28/2/16.
 */
public class SuperviseWeightListRecord {

    private List<Map<String,String>> weight_lossposition;

    public SuperviseWeightListRecord() {
    }

    public SuperviseWeightListRecord(JSONObject jo) {
        this.load(jo);
    }

    public void load(JSONObject jo) {
        weight_lossposition = new ArrayList<Map<String,String>>();
        Double count = 0.0;
        for ( String key : jo.keySet() ) {
            count += jo.getDouble(key);
        }
        for ( String key : jo.keySet() ) {
            Map<String,String> record = new HashMap<String,String>();
            record.put("weight",key);
            record.put("percent",Double.toString(jo.getDouble(key)/count));
            weight_lossposition.add(record);
        }
    }

    public List<Map<String, String>> getWeight_lossposition() {
        return weight_lossposition;
    }

    public void setWeight_lossposition(List<Map<String, String>> weight_lossposition) {
        this.weight_lossposition = weight_lossposition;
    }

    @Override
    public String toString() {
        return "SuperviseWeightListRecord{" +
                "weight_lossposition=" + weight_lossposition +
                '}';
    }
}
