package com.curus.utils.service.drug;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.curus.dao.CurusDriver;
import com.curus.dao.drug.DrugCompRelationDao;
import com.curus.model.database.*;
import com.curus.utils.QuotaUtils;
import com.curus.utils.TypeUtils;
import com.curus.utils.constant.CommonConst;
import com.curus.utils.constant.DrugConst;
import com.curus.utils.constant.QuotaConst;
import com.curus.utils.service.DrugUtils;
import com.curus.utils.service.supervise.bdsugar.SBdSugarServiceUtils;
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

    public static void GetUseDrugAndDrugComp(CurusDriver driver,
                                             Long patient_id,
                                             Map<String, DrugInfo> drugInfoMap,
                                             Map<String, Double> drugCompRelationMap,
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
                        drugCompRelationMap.put(patientUseDrug.getDrug_id()+"$"+drugCompRelation.getComp_id(), drugCompRelation.getComp_dosis());
                    }

                    DrugComp drugComp = driver.drugCompDao.selectByCompId(drugCompRelation.getComp_id());

                    if ( drugComp != null && drugCompMap != null ) {
                        drugCompMap.put(drugComp.getComp_id(),drugComp);
                    }
                }
            }
        }
    }


    static public Set<String> CompType(Set<String> drugCompSet,
                                       Map<String,Double> drugCompRelationMap,
                                       Map<String,DrugComp> drugCompMap,
                                       String type) {
        Integer comp_type = DrugUtils.GetCompTypeId(type);
        if ( comp_type == null ) return null;
        Set<String> newDrugCompSet = new HashSet<String>();

        if ( drugCompSet == null ) { //init
            for ( Map.Entry<String,Double> entry : drugCompRelationMap.entrySet() ) {
                String[] ids = entry.getKey().split("$");
                DrugComp drugComp = drugCompMap.get(ids[1]);

                if ( drugComp.getComp_type().compareTo(comp_type) == 0 )
                    newDrugCompSet.add(entry.getKey());
            }
        } else {

            for ( String drug_id_comp_id : drugCompSet ) {
                String[] ids = drug_id_comp_id.split("$");
                DrugComp drugComp = drugCompMap.get(ids[1]);
                if ( drugComp.getComp_type().compareTo(comp_type) == 0 )
                    newDrugCompSet.add(drug_id_comp_id);
            }
        }
        return newDrugCompSet;
    }


    static public Set<String> CompAim(Set<String> drugCompSet,
                                      Map<String,Double> drugCompRelationMap,
                                      Map<String,DrugComp> drugCompMap,
                                      String aim) {
        Integer comp_aim = DrugUtils.GetCompAimId(aim);
        Set<String> newDrugCompSet = new HashSet<String>();
        if ( drugCompSet == null ) {
            for ( Map.Entry<String,Double> entry : drugCompRelationMap.entrySet() ) {
                String ids[] = entry.getKey().split("$");
                DrugComp drugComp = drugCompMap.get(ids[1]);
                if ( Integer.parseInt(drugComp.getComp_aim()) == comp_aim ) {
                    newDrugCompSet.add(entry.getKey());
                }
            }
        } else {
            for ( String drug_id_comp_id : drugCompSet ) {
                String[] ids = drug_id_comp_id.split("$");
                DrugComp drugComp = drugCompMap.get(ids[1]);
                if ( Integer.parseInt(drugComp.getComp_aim()) == comp_aim ) {
                    newDrugCompSet.add(drug_id_comp_id);
                }
            }
        }
        return  newDrugCompSet;
    }



    static public Map<String,String> DrugTime(Set<String> drugCompSet,
                                              Map<String,Double> drugCompRelationMap,
                                              Map<String,PatientUseDrug> patientUseDrugMap,
                                              String begin, String end) {
        Integer drugBegin = QuotaConst.SUB_QUOTA_IDS.get(begin).intValue();
        Integer drugEnd = QuotaConst.SUB_QUOTA_IDS.get(end).intValue();

        Map<String,String> newDrugTimeMap = new HashMap<String, String>();

        if ( drugCompSet == null ) {
            for ( Map.Entry<String,Double> entry : drugCompRelationMap.entrySet() ) {
                String[] ids = entry.getKey().split("$");
                PatientUseDrug patientUseDrug = patientUseDrugMap.get(ids[0]);

                JSONObject use_policy = JSONObject.parseObject(patientUseDrug.getUse_policy());
                JSONArray use_points = use_policy.getJSONArray("points");
                for ( int i = 0; i < use_points.size(); ++ i ) {
                    JSONObject use_point = use_points.getJSONObject(i);
                    Integer use_id = use_point.getInteger("pointId");
                    if ( use_id.compareTo(drugEnd) <= 0 && use_id.compareTo(drugBegin) >= 0 ) {
                        newDrugTimeMap.put(entry.getKey(), QuotaUtils.getSubQuotaName(use_id.longValue()));
                    }
                }
            }
        } else {
            for ( String drug_id_comp_id : drugCompSet ) {
                String[] ids = drug_id_comp_id.split("$");
                PatientUseDrug patientUseDrug = patientUseDrugMap.get(ids[0]);
                JSONObject use_policy = JSONObject.parseObject(patientUseDrug.getUse_policy());
                JSONArray  use_points = use_policy.getJSONArray("points");
                for ( int i = 0; i < use_points.size(); ++ i ) {
                    JSONObject use_point = use_points.getJSONObject(i);
                    Integer use_id = use_point.getInteger("pointId");
                    if ( use_id.compareTo(drugEnd) <= 0 && use_id.compareTo(drugBegin) >= 0 )
                        newDrugTimeMap.put(drug_id_comp_id, QuotaUtils.getSubQuotaName(use_id.longValue()));
                }
            }
        }
        return  newDrugTimeMap;
    }

    static public Map<String,Integer> DrugTech(Set<String> drugCompSet,
                                               Map<String,Double> drugCompRelationMap,
                                               Map<String,DrugInfo> drugInfoMap) {
        Map<String,Integer> drugCompTechMap = new HashMap<String, Integer>();
        if ( drugCompSet == null ) {
            for ( Map.Entry<String,Double> entry : drugCompRelationMap.entrySet() ) {
                DrugInfo drugInfo = drugInfoMap.get(entry.getKey());
                drugCompTechMap.put(entry.getKey(), drugInfo.getTech());
            }
        } else {
            for ( String drug_id_comp_id : drugCompSet ) {
                String[] ids = drug_id_comp_id.split("$");
                drugCompTechMap.put(drug_id_comp_id, drugInfoMap.get(ids[0]).getTech());
            }
        }
        return drugCompTechMap;
    }

    static public Set<String> CompProcess(Map<String,Integer> drugCompTechMap,
                                          Map<String,Double> drugCompRelationMap,
                                          Map<String,DrugComp> drugCompMap,
                                          int process) {
        Set<String> newDrugCompSet = new HashSet<String>();

        if ( drugCompTechMap == null ) {
            for ( Map.Entry<String,Double> entry : drugCompRelationMap.entrySet() ) {
                String[] ids = entry.getKey().split("$");
                DrugComp drugComp = drugCompMap.get(ids[0]);
                if ( drugComp.getComp_process() == process ) {
                    newDrugCompSet.add(entry.getKey());
                }
            }
        } else {
            for ( Map.Entry<String,Integer> entry : drugCompTechMap.entrySet()) {
                String[] ids = entry.getKey().split("$");
                DrugComp drugComp = drugCompMap.get(ids[1]);
                Integer  comp_process = drugComp.getComp_process();
                if ( entry.getValue() == 1 ) {
                    comp_process += 1;
                    if ( comp_process > 5 ) comp_process = 5;
                } else if ( entry.getValue() == 2 ) {
                    comp_process = Math.max(4,comp_process);
                }

                if ( comp_process == process ) {
                    newDrugCompSet.add(entry.getKey());
                }
            }
        }

        return newDrugCompSet;
    }

    static public String GetProductionName(Set<String> drugCompSet,
                                           Map<String,DrugInfo> drugInfoMap) {
        Iterator<String> iterator = drugCompSet.iterator();
        String[] ids = iterator.next().split("$");
        return drugInfoMap.get(ids[0]).getProduct_name();
    }

    static public String GetProductionName(Map.Entry<String,String> entry,
                                           Map<String,DrugInfo> drugInfoMap) {
        String[] ids = entry.getKey().split("$");
        return drugInfoMap.get(ids[0]).getProduct_name();
    }

    static public String GetMomentContext(Map.Entry<String,String> entry) {
        return SBdSugarServiceUtils.GetMomentContext(entry.getValue());
    }
}
