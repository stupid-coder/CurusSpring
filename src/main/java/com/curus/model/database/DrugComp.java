package com.curus.model.database;

import java.io.Serializable;

/**
 * Created by stupid-coder on 5/29/16.
 */
public class DrugComp implements Serializable {

    private static final long serialVersionUID = 1L;

    // 成分ID
    private String comp_id;
    // 成分名
    private String comp_name;
    // 成分英文名
    private String comp_ename;
    // 效果
    private String comp_aim;
    // 成分分类
    private Integer comp_type;

    public Integer getComp_type() {
        return comp_type;
    }

    public void setComp_type(Integer comp_type) {
        this.comp_type = comp_type;
    }

    public String getComp_aim() {
        return comp_aim;
    }

    public void setComp_aim(String comp_aim) {
        this.comp_aim = comp_aim;
    }

    public String getComp_ename() {
        return comp_ename;
    }

    public void setComp_ename(String comp_ename) {
        this.comp_ename = comp_ename;
    }

    public String getComp_name() {
        return comp_name;
    }

    public void setComp_name(String comp_name) {
        this.comp_name = comp_name;
    }

    public String getComp_id() {
        return comp_id;
    }

    public void setComp_id(String comp_id) {
        this.comp_id = comp_id;
    }

    @Override
    public String toString() {
        return "DrugComp{" +
                "comp_id='" + comp_id + '\'' +
                ", comp_name='" + comp_name + '\'' +
                ", comp_ename='" + comp_ename + '\'' +
                ", comp_aim='" + comp_aim + '\'' +
                ", comp_type=" + comp_type +
                '}';
    }
}
