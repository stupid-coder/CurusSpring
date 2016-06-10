package com.curus.model.database;

import java.io.Serializable;

/**
 * Created by stupid-coder on 5/29/16.
 */
public class DrugInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    // 药品ID
    private String drug_id;
    // 国药准号
    private String gov_id;
    // 产品名称
    private String product_name;
    // 通用名/化学名
    private String chemical_name;
    // 商品名
    private String trade_name;
    // 生产单位名
    private String manu_name;
    // 剂型
    private Integer form;
    // 工艺
    private Integer tech;
    // 规格
    private String spec;
    // 使用方法
    private Integer use;
    // 持效特征
    private Integer process;
    // 单次最低剂量
    private Double min_once;
    // 单次最高剂量
    private Double max_once;
    // 单日最低剂量
    private Double min_day;
    // 单日最高剂量
    private Double max_day;
    // 禁忌病情
    private String taboo;
    // 副作用/不良反应
    private String side_effect;
    // 适应症
    private String aim;

    public String getAim() {
        return aim;
    }

    public void setAim(String aim) {
        this.aim = aim;
    }

    public String getSide_effect() {
        return side_effect;
    }

    public void setSide_effect(String side_effect) {
        this.side_effect = side_effect;
    }

    public String getTaboo() {
        return taboo;
    }

    public void setTaboo(String taboo) {
        this.taboo = taboo;
    }

    public Double getMax_day() {
        return max_day;
    }

    public void setMax_day(Double max_day) {
        this.max_day = max_day;
    }

    public Double getMin_day() {
        return min_day;
    }

    public void setMin_day(Double min_day) {
        this.min_day = min_day;
    }

    public Double getMax_once() {
        return max_once;
    }

    public void setMax_once(Double max_once) {
        this.max_once = max_once;
    }

    public Double getMin_once() {
        return min_once;
    }

    public void setMin_once(Double min_once) {
        this.min_once = min_once;
    }

    public Integer getProcess() {
        return process;
    }

    public void setProcess(Integer process) {
        this.process = process;
    }

    public Integer getUse() {
        return use;
    }

    public void setUse(Integer use) {
        this.use = use;
    }


    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public Integer getTech() {
        return tech;
    }

    public void setTech(Integer tech) {
        this.tech = tech;
    }

    public Integer getForm() {
        return form;
    }

    public void setForm(Integer form) {
        this.form = form;
    }

    public String getManu_name() {
        return manu_name;
    }

    public void setManu_name(String manu_name) {
        this.manu_name = manu_name;
    }

    public String getTrade_name() {
        return trade_name;
    }

    public void setTrade_name(String trade_name) {
        this.trade_name = trade_name;
    }

    public String getChemical_name() {
        return chemical_name;
    }

    public void setChemical_name(String chemical_name) {
        this.chemical_name = chemical_name;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getGov_id() {
        return gov_id;
    }

    public void setGov_id(String gov_id) {
        this.gov_id = gov_id;
    }

    public String getDrug_id() {
        return drug_id;
    }

    public void setDrug_id(String drug_id) {
        this.drug_id = drug_id;
    }

    @Override
    public String toString() {
        return "DrugInfo{" +
                "drug_id='" + drug_id + '\'' +
                ", gov_id='" + gov_id + '\'' +
                ", product_name='" + product_name + '\'' +
                ", chemical_name='" + chemical_name + '\'' +
                ", trade_name='" + trade_name + '\'' +
                ", manu_name='" + manu_name + '\'' +
                ", form=" + form +
                ", tech=" + tech +
                ", spec='" + spec + '\'' +
                ", use=" + use +
                ", process=" + process +
                ", min_once=" + min_once +
                ", max_once=" + max_once +
                ", min_day=" + min_day +
                ", max_day=" + max_day +
                ", taboo='" + taboo + '\'' +
                ", side_effect='" + side_effect + '\'' +
                ", aim='" + aim + '\'' +
                '}';
    }
}
