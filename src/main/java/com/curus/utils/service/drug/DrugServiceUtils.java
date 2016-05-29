package com.curus.utils.service.drug;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.curus.dao.CurusDriver;
import com.curus.model.database.DrugComp;
import com.curus.model.database.DrugCompRelation;
import com.curus.model.database.DrugInfo;
import com.curus.utils.constant.DrugConst;


import java.util.ArrayList;
import java.util.List;


/**
 * Created by stupid-coder on 5/29/16.
 */
public class DrugServiceUtils {


    public static JSONArray DrugAim(String aim) {
        JSONArray aimArray = new JSONArray();
        String[] aims = aim.split(",");
        for ( String item : aims ) {
            aimArray.add(DrugConst.DRUG4ILLNESS.get(Integer.parseInt(item)));
        }
        return aimArray;
    }

    public static JSONArray DrugComp(CurusDriver driver,
                                     DrugInfo drugInfo) {
        JSONArray drugCompArray = new JSONArray();
        List<DrugCompRelation> drugCompRelationList = driver.drugCompRelationDao.selectByDrugId(drugInfo.getDrug_id());
        if ( drugCompRelationList != null ) {
            for ( DrugCompRelation drugCompRelation : drugCompRelationList ) {
                DrugComp drugComp = driver.drugCompDao.selectByCompId(drugCompRelation.getComp_id());
                if (drugComp != null) {
                    JSONObject drugCompItem = new JSONObject();
                    drugCompItem.put("单位剂量",String.format("%f mg",drugCompRelation.getComp_dosis()));
                    drugCompItem.put("成份",DrugConst.COMP_TYPE.get(drugComp.getComp_type()));
                    drugCompItem.put("效果",DrugAim(drugComp.getComp_aim()));
                    drugCompArray.add(drugCompItem);
                }
            }
        }
        return drugCompArray;
    }

    public static JSONObject DrugDirections(CurusDriver driver,
                                            DrugInfo drugInfo) {
        JSONObject directions = new JSONObject();
        directions.put("国药准字",drugInfo.getGov_id());
        directions.put("产品名称",drugInfo.getProduct_name());
        directions.put("通用名称",drugInfo.getProduct_name());
        directions.put("商品名称",drugInfo.getTrade_name());
        directions.put("剂型", DrugConst.DRUG_FORM.get(drugInfo.getForm()));
        directions.put("规格",drugInfo.getSpec());
        directions.put("成份",DrugComp(driver, drugInfo));
        directions.put("生产单位",drugInfo.getManu_name());
        directions.put("化学名",drugInfo.getChemical_name());
        directions.put("适应症",drugInfo.getFor_illness());
        directions.put("禁忌病情",drugInfo.getTaboo());
        directions.put("副作用/不良反应",drugInfo.getSide_effect());
        return directions;
    }
}
