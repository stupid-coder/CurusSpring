package com.curus.utils.service.drug;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.curus.dao.CurusDriver;
import com.curus.dao.drug.DrugCompRelationDao;
import com.curus.model.database.*;
import com.curus.utils.TypeUtils;
import com.curus.utils.constant.CommonConst;
import com.curus.utils.constant.DrugConst;
import com.curus.utils.constant.QuotaConst;
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

    public static DrugInfo CompAim(Map<String,DrugInfo> drugInfoMap,
                                   Map<String,DrugComp> drugCompMap,
                                   String aim) {
        if ( drugCompMap == null || drugCompMap.size() == 0 ) return null;

        Integer comp_aim = DrugUtils.GetCompAimId(aim);
        for ( Map.Entry<String,DrugComp> entry : drugCompMap.entrySet() ) {
            if ( entry.getValue().getComp_type().compareTo(comp_aim) == 0 ) return drugInfoMap.get(entry.getKey());
        }

        return null;
    }

    public static DrugInfo CompType(Map<String,DrugInfo> drugInfoMap,
                                    Map<String,DrugComp> drugCompMap,
                                    String type) {
        Integer comp_type = DrugUtils.GetCompTypeId(type);

        for ( Map.Entry<String,DrugComp> entry : drugCompMap.entrySet() ) {
            if ( entry.getValue().getComp_type().compareTo(comp_type) == 0 ) {
                if ( drugInfoMap.containsKey(entry.getValue()) ) return drugInfoMap.get(entry.getKey());
            }
        }
        return null;
    }

    public static String CompType(CurusDriver driver, Long patient_id, String type) {
        Integer comp_type = DrugUtils.GetCompTypeId(type);
        if ( comp_type == null ) return null;

        List<PatientUseDrug> patientUseDrugList = driver.patientUseDrugDao.selectAll(patient_id, CommonConst.TRUE);

        if ( patientUseDrugList == null ) return null;

        for ( PatientUseDrug patientUseDrug : patientUseDrugList ) {
            List<DrugCompRelation> drugCompRelationList = driver.drugCompRelationDao.selectByDrugId(patientUseDrug.getDrug_id());
            for (DrugCompRelation drugCompRelation : drugCompRelationList) {
                DrugComp drugComp = driver.drugCompDao.select(TypeUtils.getWhereHashMap("comp_id",drugCompRelation.getComp_id()));
                if ( drugComp == null ) continue;
                if ( comp_type.compareTo(drugComp.getComp_type()) == 0 ) return drugCompRelation.getDrug_id();
            }
        }
        return null;
    }

    public static DrugInfo DrugTechCompProcessType(Map<String,DrugInfo> drugInfoMap,
                                                   Map<String,DrugComp> drugCompMap,
                                                   Integer process,
                                                   String type) {
        Integer comp_type = DrugUtils.GetCompTypeId(type);

        for ( Map.Entry<String,DrugComp> entry : drugCompMap.entrySet() ) {
            if ( entry.getValue().getComp_type().compareTo(comp_type) == 0 ) {
                DrugInfo drugInfo = drugInfoMap.get(entry.getKey());
                Integer comp_process = entry.getValue().getComp_process();
                if ( drugInfo.getTech() == 0 ) { // 普通
                    if ( comp_process.compareTo(process) == 0 ) return drugInfo;
                } else if ( drugInfo.getTech() == 1 ) { // 缓释
                    if ( comp_process + 1 == process) return drugInfo;
                } else if ( drugInfo.getTech() == 2 ) { // 控释
                    return drugInfo;
                }
            }
        }
        return null;
    }

    public static DrugInfo DrugTechCompProcessTypeTime(Map<String,PatientUseDrug> patientUseDrugMap,
                                                       Map<String,DrugInfo> drugInfoMap,
                                                       Map<String,DrugComp> drugComMap,
                                                       Integer process,
                                                       String type,
                                                       Integer timeBegin,
                                                       Integer timeEnd) {
        DrugInfo drugInfo = DrugTechCompProcessType(drugInfoMap,drugComMap,process,type);
        if ( drugInfo != null ) {
            PatientUseDrug patientUseDrug = patientUseDrugMap.get(drugInfo.getDrug_id());
            JSONObject policy = JSONObject.parseObject(patientUseDrug.getUse_policy());
        }
        return null;
    }

    public static void GetUseDrugAndDrugComp(CurusDriver driver,
                                             Long patient_id,
                                             Map<String, DrugInfo> drugInfoMap,
                                             Map<String, Map<String, Double>> drugCompRelationMap,
                                             Map<String, DrugComp> drugCompMap,
                                             Map<String, PatientUseDrug> patientUseDrugMap) {

        List<PatientUseDrug> patientUseDrugList = driver.patientUseDrugDao.selectAll(patient_id,CommonConst.TRUE);
        if ( patientUseDrugList == null || patientUseDrugList.size() == 0 ) return;

        for ( PatientUseDrug patientUseDrug : patientUseDrugList ) {
            DrugInfo drugInfo = driver.drugInfoDao.select(patientUseDrug.getDrug_id());

            if ( drugInfo == null ) continue;
            if ( drugInfoMap != null ) drugInfoMap.put(drugInfo.getDrug_id(), drugInfo);

            patientUseDrugMap.put(patientUseDrug.getDrug_id(),patientUseDrug);

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


    public Set<String> CompType(Set<String> drugSet,
                                 Map<String,DrugComp> drugCompMap,
                                 String type) {
        Integer comp_type = DrugUtils.GetCompTypeId(type);
        if ( comp_type == null ) return null;
        Set<String> newDrugSet = new HashSet<String>();

        if ( drugSet == null ) { //init
            for ( Map.Entry<String,DrugComp> entry : drugCompMap.entrySet() ) {
                if ( entry.getValue().getComp_type().compareTo(comp_type) == 0 )
                    newDrugSet.add(entry.getKey());
            }
        } else {

            for ( String drug_id : drugSet ) {
                DrugComp drugComp = drugCompMap.get(drug_id);
                if ( drugComp.getComp_type().compareTo(comp_type) == 0 )
                    newDrugSet.add(drug_id);
            }
        }
        return newDrugSet;
    }


    public Set<String> CompAim(Set<String> drugSet,
                               Map<String,DrugComp> drugCompMap,
                               String aim) {
        Integer comp_aim = DrugUtils.GetCompAimId(aim);
        Set<String> newDrugSet = new HashSet<String>();
        if ( drugSet == null ) {
            for ( Map.Entry<String,DrugComp> entry : drugCompMap.entrySet() ) {
                if ( Integer.parseInt(entry.getValue().getComp_aim()) == comp_aim ) {
                    newDrugSet.add(entry.getKey());
                }
            }
        } else {
            for ( String drug_id : drugSet ) {
                DrugComp drugComp = drugCompMap.get(drug_id);
                if ( Integer.parseInt(drugComp.getComp_aim()) == comp_aim ) {
                    newDrugSet.add(drug_id);
                }
            }
        }
        return  newDrugSet;
    }

    public Set<String> DrugTime(Set<String> drugSet,
                                Map<String,PatientUseDrug> patientUseDrugMap,
                                String begin, String end) {
        Integer drugBegin = QuotaConst.SUB_QUOTA_IDS.get(begin).intValue();
        Integer drugEnd = QuotaConst.SUB_QUOTA_IDS.get(end).intValue();

        Set<String> newDrugSet = new HashSet<String>();

        if ( drugSet == null ) {
            for ( Map.Entry<String,PatientUseDrug> entry : patientUseDrugMap.entrySet() ) {
                PatientUseDrug patientUseDrug = entry.getValue();
                JSONObject use_policy = JSONObject.parseObject(patientUseDrug.getUse_policy());
            }
        } else {
            for ( String drug_id : drugSet ) {
                PatientUseDrug drugComp = patientUseDrugMap.get(drug_id);
            }
        }
        return  newDrugSet;
    }
}
