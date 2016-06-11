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
                        drugCompRelationMap.put(patientUseDrug.getDrug_id()+"#"+drugCompRelation.getComp_id(), drugCompRelation.getComp_dosis());
                    }

                    DrugComp drugComp = driver.drugCompDao.selectByCompId(drugCompRelation.getComp_id());

                    if ( drugComp != null && drugCompMap != null ) {
                        drugCompMap.put(drugComp.getComp_id(),drugComp);
                    }
                }
            }
        }
    }


    static public Map<String,Integer> CompType(Map<String,Integer> compTypeMap,
                                               Map<String,Double> drugCompRelationMap,
                                               Map<String,DrugComp> drugCompMap,
                                               String type) {
        Integer comp_type = DrugUtils.GetCompTypeId(type);

        Map<String,Integer> newCompTypeMap = new HashMap<String, Integer>();

        if ( compTypeMap == null ) { //init
            for ( Map.Entry<String,Double> entry : drugCompRelationMap.entrySet() ) {
                String[] ids = entry.getKey().split("#");
                DrugComp drugComp = drugCompMap.get(ids[1]);
                if ( comp_type == null ) {
                    newCompTypeMap.put(entry.getKey(),drugComp.getComp_type());
                } else if ( drugComp.getComp_type().compareTo(comp_type) == 0 ) {
                    newCompTypeMap.put(entry.getKey(), comp_type);
                }
            }
        } else {

            for ( Map.Entry<String,Integer> entry : compTypeMap.entrySet() ) {
                String[] ids = entry.getKey().split("#");
                DrugComp drugComp = drugCompMap.get(ids[1]);
                if ( type == null ) {
                    newCompTypeMap.put(entry.getKey(),entry.getValue());
                } else if ( drugComp.getComp_type().compareTo(comp_type) == 0 ) {
                    newCompTypeMap.put(entry.getKey(), comp_type);
                }
            }
        }
        return newCompTypeMap;
    }


    static public Map<String,Integer> CompAim(Map<String,Integer> compAimMap,
                                              Map<String,Double> drugCompRelationMap,
                                              Map<String,DrugComp> drugCompMap,
                                              String aim) {
        Integer comp_aim = DrugUtils.GetCompAimId(aim);

        Map<String,Integer> newCompAimMap = new HashMap<String, Integer>();
        if ( compAimMap == null ) {
            for ( Map.Entry<String,Double> entry : drugCompRelationMap.entrySet() ) {
                String ids[] = entry.getKey().split("#");
                DrugComp drugComp = drugCompMap.get(ids[1]);
                if ( comp_aim == null ) {
                    newCompAimMap.put(entry.getKey(),DrugUtils.GetCompAimId(drugComp.getComp_aim()));
                } else if ( Integer.parseInt(drugComp.getComp_aim()) == comp_aim ) {
                    newCompAimMap.put(entry.getKey(),comp_aim);
                }
            }
        } else {
            for ( Map.Entry<String,Integer> entry : compAimMap.entrySet() ) {
                String[] ids = entry.getKey().split("#");
                DrugComp drugComp = drugCompMap.get(ids[1]);
                if ( Integer.parseInt(drugComp.getComp_aim()) == comp_aim ) {
                    newCompAimMap.put(entry.getKey(), comp_aim);
                }
            }
        }
        return  newCompAimMap;
    }



    static public Map<String,JSONArray> DrugTime(Map<String,Integer> drugCompMap,
                                                 Map<String,Double> drugCompRelationMap,
                                                 Map<String,PatientUseDrug> patientUseDrugMap,
                                                 Integer begin, Integer end) {

        Map<String,JSONArray> newDrugTimeMap = new HashMap<String, JSONArray>();

        if ( drugCompMap == null ) {
            for ( Map.Entry<String,Double> entry : drugCompRelationMap.entrySet() ) {
                String[] ids = entry.getKey().split("#");
                PatientUseDrug patientUseDrug = patientUseDrugMap.get(ids[0]);

                JSONObject use_policy = JSONObject.parseObject(patientUseDrug.getUse_policy());
                JSONArray use_points = use_policy.getJSONArray("points");
                if ( begin == null && end == null ) newDrugTimeMap.put(entry.getKey(),use_points);
                else {
                    JSONArray ok_points = new JSONArray();
                    for (int i = 0; i < use_points.size(); ++i) {
                        JSONObject use_point = use_points.getJSONObject(i);
                        Integer use_id = use_point.getInteger("pointId");
                        if (use_id.compareTo(begin) <= 0 && use_id.compareTo(end) >= 0) {
                            ok_points.add(use_point);
                        }
                    }
                    if (ok_points.size() > 0)
                        newDrugTimeMap.put(entry.getKey(), ok_points);
                }
            }
        } else {
            for ( Map.Entry<String,Integer> entry : drugCompMap.entrySet() ) {
                String[] ids = entry.getKey().split("#");
                PatientUseDrug patientUseDrug = patientUseDrugMap.get(ids[0]);
                JSONObject use_policy = JSONObject.parseObject(patientUseDrug.getUse_policy());
                JSONArray  use_points = use_policy.getJSONArray("points");
                if ( begin == null && end == null )
                    newDrugTimeMap.put(entry.getKey(),use_points);
                else {
                    JSONArray ok_points = new JSONArray();
                    for (int i = 0; i < use_points.size(); ++i) {
                        JSONObject use_point = use_points.getJSONObject(i);
                        Integer use_id = use_point.getInteger("pointId");
                        if (use_id.compareTo(begin) >= 0 && use_id.compareTo(end) <= 0)
                            ok_points.add(use_point);

                    }
                    if ( ok_points.size() > 0)
                        newDrugTimeMap.put(entry.getKey(), ok_points);
                }
            }
        }
        return  newDrugTimeMap;
    }

    static public Map<String,Integer> DrugTech(Map<String,Integer> drugCompMap,
                                               Map<String,Double> drugCompRelationMap,
                                               Map<String,DrugInfo> drugInfoMap) {
        Map<String,Integer> drugCompTechMap = new HashMap<String, Integer>();
        if ( drugCompMap == null ) {
            for ( Map.Entry<String,Double> entry : drugCompRelationMap.entrySet() ) {
                String[] ids = entry.getKey().split("#");
                DrugInfo drugInfo = drugInfoMap.get(ids[0]);
                drugCompTechMap.put(entry.getKey(), drugInfo.getTech());
            }
        } else {
            for ( Map.Entry<String,Integer> entry : drugCompMap.entrySet() ) {
                String[] ids = entry.getKey().split("#");
                drugCompTechMap.put(entry.getKey(), drugInfoMap.get(ids[0]).getTech());
            }
        }
        return drugCompTechMap;
    }

    static public Map<String,Integer> CompProcess(Map<String,Integer> drugCompTechMap,
                                                  Map<String,Double> drugCompRelationMap,
                                                  Map<String,DrugComp> drugCompMap,
                                                  Integer process) {
        Map<String,Integer> newDrugCompMap = new HashMap<String, Integer>();

        if ( drugCompTechMap == null ) {
            for ( Map.Entry<String,Double> entry : drugCompRelationMap.entrySet() ) {
                String[] ids = entry.getKey().split("#");
                DrugComp drugComp = drugCompMap.get(ids[0]);
                if ( process == null ) newDrugCompMap.put(entry.getKey(),drugComp.getComp_process());
                else if ( drugComp.getComp_process() == process ) newDrugCompMap.put(entry.getKey(),drugComp.getComp_process());
            }
        } else {
            for ( Map.Entry<String,Integer> entry : drugCompTechMap.entrySet()) {
                String[] ids = entry.getKey().split("#");
                DrugComp drugComp = drugCompMap.get(ids[1]);
                Integer  comp_process = drugComp.getComp_process();
                if ( entry.getValue() == 1 ) {
                    comp_process += 1;
                    if ( comp_process >= 4 ) comp_process = 4;
                } else if ( entry.getValue() == 2 ) {
                    comp_process = 4;
                }
                if ( process == null ) newDrugCompMap.put(entry.getKey(), drugComp.getComp_process());
                else if ( comp_process.compareTo(process) == 0 ) newDrugCompMap.put(entry.getKey(), process);
            }
        }

        return newDrugCompMap;
    }

    static public String GetProductionName(Map<String,Integer> drugCompMap,
                                           Map<String,DrugInfo> drugInfoMap) {
        String drug_id_comp_id = drugCompMap.keySet().iterator().next();
        String[] ids = drug_id_comp_id.split("#");
        return drugInfoMap.get(ids[0]).getProduct_name();
    }

    static public String GetProductionName(Map<String,JSONArray> drugCompMap,
                                           Map<String,DrugInfo> drugInfoMap,
                                           String jsonArray) {
        String drug_id_comp_id = drugCompMap.keySet().iterator().next();
        String[] ids = drug_id_comp_id.split("#");
        return drugInfoMap.get(ids[0]).getProduct_name();
    }


    static public Map<String,Integer> CheckType(Map<String,Integer> last_result,
                                        Map<String,DrugInfo> drugInfoMap,
                                        Map<String,Double> compRelateMap,
                                        Map<String,DrugComp> drugCompMap,
                                        Map<String,PatientUseDrug> patientUseDrugMap,
                                        List<String> suggestions,
                                        String patientName,
                                        String moment,
                                        Integer process,
                                        String type) {

        if ( last_result == null )
            last_result = DrugServiceUtils.CompType(null,compRelateMap,drugCompMap, type);
        if ( last_result.size() != 0 && suggestions != null )
            suggestions.add(String.format("特殊用药提醒： %s 目前正在使用%s（%s)，它可抑制蔗糖和淀粉类食物的吸收，因此用传统食物缓解低血糖的效果较差。建议家中常备葡萄糖或蜂蜜，其缓解低血糖的效果更佳。",
                    patientName, type, DrugServiceUtils.GetProductionName(last_result,drugInfoMap)));
        return last_result;
    }

    static public Map<String,Integer> CheckAim (Map<String,Integer> last_result,
                                        Map<String,DrugInfo> drugInfoMap,
                                        Map<String,Double> compRelateMap,
                                        Map<String,DrugComp> drugCompMap,
                                        Map<String,PatientUseDrug> patientUseDrugMap,
                                        List<String> suggestions,
                                        String patientName,
                                        String moment,
                                        Integer process,
                                        String aim) {

        if ( last_result == null ) {
            last_result = DrugServiceUtils.CompAim(null, compRelateMap, drugCompMap, aim);
        }
        /*
        if ( last_result.size() == 0 && suggestions != null )
            suggestions.add(String.format("特殊用药提醒： %s 目前正在使用%s（%s)，它可抑制蔗糖和淀粉类食物的吸收，因此用传统食物缓解低血糖的效果较差。建议家中常备葡萄糖或蜂蜜，其缓解低血糖的效果更佳。",
                    patientName, aim, DrugServiceUtils.GetProductionName(last_result,drugInfoMap)));
        */
        return last_result;
    }

    static public Map<String,Integer> CheckProcessType(Map<String,Integer> last_result,
                                               Map<String,DrugInfo> drugInfoMap,
                                               Map<String,Double> compRelateMap,
                                               Map<String,DrugComp> drugCompMap,
                                               Map<String,PatientUseDrug> patientUseDrugMap,
                                               List<String> suggestions,
                                               String patientName,
                                               String moment,
                                               Integer process,
                                               String type) {


        if ( last_result == null )
            last_result = DrugServiceUtils.CompProcess(DrugServiceUtils.DrugTech(DrugServiceUtils.CompType(null, compRelateMap,drugCompMap, type),compRelateMap,drugInfoMap),compRelateMap,drugCompMap,process);
        if ( last_result.size() != 0  && suggestions != null ) {
            suggestions.add(String.format("特殊用药提醒：目前患者正在使用的%s属%s降糖药或含有该成分，它极易导致低血糖，建议首先对其减量或停用，再尽早咨询医生，同时连续监测低血糖发生情况。",
                    DrugServiceUtils.GetProductionName(last_result,drugInfoMap),type));
        }
        return last_result;
    }

    static public String GetMomentContext(Map<String,JSONArray> timeDrugs) {
        JSONArray use_points = timeDrugs.entrySet().iterator().next().getValue();

        return QuotaUtils.getSubQuotaTimeName(use_points.getJSONObject(0).getInteger("pointId"));
    }

    static public Map<String,JSONArray> CheckTimeProcessType(Map<String,JSONArray> last_result,
                                                             Map<String,DrugInfo> drugInfoMap,
                                                             Map<String,Double> compRelateMap,
                                                             Map<String,DrugComp> drugCompMap,
                                                             Map<String,PatientUseDrug> patientUseDrugMap,
                                                             List<String> suggestions,
                                                             String patientName,
                                                             String moment,
                                                             Integer begin, Integer end,
                                                             Integer process,
                                                             String type) {


        if ( last_result == null )
            last_result = DrugServiceUtils.DrugTime(DrugServiceUtils.CompProcess(DrugServiceUtils.DrugTech(DrugServiceUtils.CompType(null, compRelateMap, drugCompMap, type), compRelateMap, drugInfoMap), compRelateMap, drugCompMap, process), compRelateMap, patientUseDrugMap,begin,end);
        if ( last_result.size() != 0 && suggestions != null ) {
            String product_name = GetProductionName(last_result,drugInfoMap,"JSONArray");
            String use_time = GetMomentContext(last_result);

            suggestions.add(String.format("特殊用药提醒：目前患者正在使用的%s属%s降糖药或含有该成分，它很可能是导致%s低血糖的罪魁祸首，建议首先对%s使用的%s进行减量或停用，再尽早咨询医生，同时连续监测低血糖发生情况。",
                    product_name,type,use_time,SBdSugarServiceUtils.GetMomentContext(moment),product_name));
        }
        return last_result;
    }

    static public JSONObject GetDrugsInfo(Map<String,PatientUseDrug> patientUseDrugMap,
                                            Map<String,DrugInfo> drugInfoMap,
                                            Map<String,Double> drugCompRelationMap,
                                            Map<String,DrugComp> drugCompMap) {
        JSONObject drugsUseInfo = new JSONObject();
        for ( Map.Entry<String,PatientUseDrug> patientUseDrugEntry : patientUseDrugMap.entrySet() ) {
            JSONObject drug = new JSONObject();
            String drug_id = patientUseDrugEntry.getKey();
            DrugInfo drugInfo = drugInfoMap.get(drug_id);
            drug.put("use",drugInfo.getUse());

            JSONArray time = JSONObject.parseObject(patientUseDrugEntry.getValue().getUse_policy()).getJSONArray("points");
            drug.put("time",time);

            if ( drugInfo.getTech() == 2 ) {
                drug.put("process","4");
            } else {
                String process = null;
                String dosis = null;
                for ( Map.Entry<String,Double> drugCompRelationEntry : drugCompRelationMap.entrySet() ) {
                    String[] ids = drugCompRelationEntry.getKey().split("#");
                    if ( ids[0].compareTo(drug_id) != 0 ) continue;
                    String comp_id = ids[1];
                    DrugComp drugComp = drugCompMap.get(comp_id);
                    Integer comp_process = drugComp.getComp_process()+drugInfo.getTech();
                    process = (process == null ? comp_process.toString() : process+"#"+comp_process);
                    dosis = (dosis == null ? drugCompRelationEntry.getValue().toString() : dosis+"#"+drugCompRelationEntry.getValue());
                }
                drug.put("process",process);
                drug.put("dosis",dosis);
            }
            drugsUseInfo.put(drug_id,drug);
        }
        return drugsUseInfo;
    }


}

