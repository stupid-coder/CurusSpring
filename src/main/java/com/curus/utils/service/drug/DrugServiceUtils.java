package com.curus.utils.service.drug;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.curus.dao.CurusDriver;
import com.curus.dao.drug.DrugCompRelationDao;
import com.curus.model.database.*;
import com.curus.utils.TypeUtils;
import com.curus.utils.constant.CommonConst;
import com.curus.utils.constant.DrugConst;
import com.curus.utils.service.DrugUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;


import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;


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
                    drugCompItem.put("单位剂量",String.format("%.2f mg",drugCompRelation.getComp_dosis()));
                    drugCompItem.put("成份",DrugConst.COMP_TYPE.get(drugComp.getComp_type()));
                    drugCompItem.put("效果",DrugAim(drugComp.getComp_aim()));
                    drugCompArray.add(drugCompItem);
                }
            }
        }
        return drugCompArray;
    }

    public static String ArrayToString(String joiner,
                                JSONArray jsonArray) {
        List<String> strList = new ArrayList<String>();
        for ( int i = 0; i < jsonArray.size() ; ++i ) {
            strList.add(jsonArray.getObject(i,String.class));
        }
        return String.join(joiner,strList);
    }

    public static String DrugCompStr(JSONArray drugCompArray) {
        List<String> drugCompList = new ArrayList<String>();
        for ( int i = 0; i < drugCompArray.size(); ++ i) {
            JSONObject compItem = drugCompArray.getJSONObject(i);
            drugCompList.add(String.format("成份:%s   单位剂量:%s 效果:%s",compItem.getString("成份"),compItem.getString("单位剂量"),ArrayToString(",", compItem.getJSONArray("效果"))));
        }
        return String.join("\n",drugCompList);
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
        directions.put("成份",DrugCompStr(DrugComp(driver, drugInfo)));
        directions.put("生产单位",drugInfo.getManu_name());
        directions.put("化学名",drugInfo.getChemical_name());
        directions.put("适应症",drugInfo.getAim());
        directions.put("禁忌病情",drugInfo.getTaboo());
        directions.put("副作用/不良反应",drugInfo.getSide_effect());
        return directions;
    }

    public static List<PatientUseDrug> GetPatientUseDrug(CurusDriver driver,
                                                         Long patient_id) {
        return driver.patientUseDrugDao.selectAll(patient_id, CommonConst.TRUE);
    }

    public static boolean DrugType(CurusDriver driver,
                                   List<PatientUseDrug> patientUseDrugList,
                                   String comp) {
        Integer comp_type = DrugUtils.GetCompTypeId(comp);
        if ( comp_type == null ) return false;

        for ( PatientUseDrug patientUseDrug : patientUseDrugList ) {
            List<DrugCompRelation> drugCompRelationList = driver.drugCompRelationDao.selectByDrugId(patientUseDrug.getDrug_id());
            for (DrugCompRelation drugCompRelation : drugCompRelationList) {
                DrugComp drugComp = driver.drugCompDao.select(TypeUtils.getWhereHashMap("comp_id",drugCompRelation.getComp_id()));
                if ( drugComp == null ) continue;
                if ( comp_type.compareTo(drugComp.getComp_type()) == 0 ) return true;
            }
        }
        return false;
    }

    public static boolean CompType(Collection<DrugComp> drugCompList,
                                   String comp) {

        if ( drugCompList == null || drugCompList.size() == 0 ) return false;

        Integer comp_type = DrugUtils.GetCompTypeId(comp);
        for ( DrugComp drugComp : drugCompList ) {
            if ( drugComp.getComp_type().compareTo(comp_type) == 0 ) return true;
        }

        return false;
    }

    public static boolean CompType(CurusDriver driver,
                                   Long patient_id,
                                   String comp) {
        Map<String,DrugComp> drugCompMap = new HashMap<String, DrugComp>();
        GetUseDrugAndDrugComp(driver,patient_id,null,null,drugCompMap);
        return CompType(drugCompMap.values(),comp);
    }

    public static DrugInfo CompType(Map<String,DrugInfo> drugInfoMap,
                                    Map<String,DrugComp> drugCompMap,
                                    String comp) {
        Integer comp_type = DrugUtils.GetCompTypeId(comp);

        for ( Map.Entry<String,DrugComp> entry : drugCompMap.entrySet() ) {
            if ( entry.getValue().getComp_type().compareTo(comp_type) == 0 ) {
                if ( drugInfoMap.containsKey(entry.getValue()) ) return drugInfoMap.get(entry.getKey());
            }
        }

        return null;
    }

    public static void GetUseDrugAndDrugComp(CurusDriver driver,
                                             Long patient_id,
                                             Map<String, DrugInfo> drugInfoList,
                                             Map<String, Map<String, Double>> drugCompRelationMap,
                                             Map<String, DrugComp> drugCompMap) {

        List<PatientUseDrug> patientUseDrugList = driver.patientUseDrugDao.selectAll(patient_id,CommonConst.TRUE);
        if ( patientUseDrugList == null || patientUseDrugList.size() == 0 ) return;

        for ( PatientUseDrug patientUseDrug : patientUseDrugList ) {
            DrugInfo drugInfo = driver.drugInfoDao.select(patientUseDrug.getDrug_id());

            if ( drugInfo == null ) continue;
            if ( drugInfoList != null ) drugInfoList.put(drugInfo.getDrug_id(), drugInfo);


            List<DrugCompRelation> drugCompRelationList = driver.drugCompRelationDao.selectByDrugId(drugInfo.getDrug_id());

            if ( drugCompRelationList == null || drugCompRelationList.size() == 0 ) continue;
            else {
                for ( DrugCompRelation drugCompRelation : drugCompRelationList ) {
                    if ( drugCompRelationMap != null ) {
                        if (drugCompRelationMap.containsKey(drugCompRelation.getDrug_id()) == false) {
                            drugCompRelationMap.put(drugCompRelation.getDrug_id(), new HashMap<String, Double>());
                        }
                        Map<String, Double> relationMap = drugCompRelationMap.get(drugCompRelation.getDrug_id());
                        relationMap.put(drugCompRelation.getComp_id(), drugCompRelation.getComp_dosis());
                    }

                    DrugComp drugComp = driver.drugCompDao.selectByCompId(drugCompRelation.getComp_id());

                    if ( drugComp != null && drugCompMap != null ) {
                        drugCompMap.put(drugCompRelation.getDrug_id(),drugComp);
                    }
                }
            }
        }
    }

}
