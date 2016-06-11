package com.curus.utils.service.supervise.bdsugar;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.curus.model.database.DrugComp;
import com.curus.model.database.DrugInfo;
import com.curus.model.database.PatientUseDrug;
import com.curus.utils.QuotaUtils;
import com.curus.utils.service.DrugUtils;
import com.curus.utils.service.drug.DrugServiceUtils;

import java.math.MathContext;
import java.util.List;
import java.util.Map;

/**
 * Created by stupid-coder on 6/11/16.
 */
public class SBdSugarAlgorithmUtils {

    static public Double GetAbsValues(JSONObject levels) {
        Double sum = 0.0;

        for ( String key : levels.keySet() ) {
            sum += Math.abs(levels.getDouble(key));
        }

        return sum;
    }

    static public boolean GetOk(JSONObject levels, JSONObject xggs) {
        Double sum = 0.0;
        for ( String key : levels.keySet() ) {
            Double xggs_value = xggs.containsKey(key) ? xggs.getDouble(key) : 0.0;
            if ( xggs_value + levels.getDouble(key) < 0.0 ) sum += 1.0;
        }
        return sum >= 0.0;
    }

    static public Double GetSum(JSONObject xggs) {
        Double sum = 0.0;
        for ( String key : xggs.keySet() ) {
            sum += xggs.getDouble(key);
        }
        return sum;
    }

    static public void GetSuggestion(String patientName, JSONObject algorithm,
                                     Map<String,PatientUseDrug> patientUseDrugMap,
                                     Map<String,DrugInfo> drugInfoMap,
                                     List<String> suggestions) {
        String best_drug_id_time = null;
        Double best_score = null;
        for ( String drug_id_time : algorithm.keySet() ) {
            String[] ids = drug_id_time.split("#");

            JSONObject result = algorithm.getJSONObject(drug_id_time);
            if ( result.getBoolean("ok") ) {
                Double tzqsxgs = result.getDouble("tzqsxgs");
                if ( tzqsxgs > 0.0 ) {
                    Double score = result.getDouble("xggs") / tzqsxgs;
                    if ( best_score == null || score.compareTo(best_score) > 0 ) {
                        best_score = score;
                        best_drug_id_time = drug_id_time;
                    }
                }
            }
        }

        if ( best_drug_id_time != null ) {
            JSONObject result = algorithm.getJSONObject(best_drug_id_time);
            suggestions.add(String.format("以下药物治疗建议综合了各时点的血糖变化，且以饮食运动习惯基本保持不变为前提，适用于饮食运动改善已无空间或有困难者。如果%s可以加大饮食运动干预强度，建议先不调整药物治疗方案甚至可以适当减少药物用量。为安全起见，目前的药物治疗建议仅限于剂量的调整，且每次只选择一个药物，渐进调整，从而达到安全控制血糖的目标，同时注意按提示监测各时点血糖变化。本药物治疗方案的调整需在医生指导下完成。",
                    patientName));
            Double jltz = result.getDouble("jltz");

            if ( jltz == 0.0 ) { suggestions.add("目前血糖综合控制良好，无需调整现有治疗方案。"); return; }

            String [] ids = best_drug_id_time.split("#");
            DrugInfo drugInfo = drugInfoMap.get(ids[0]);
            String unit = DrugUtils.GetFormContext(drugInfo.getForm());
            Integer hour = Integer.parseInt(ids[1]);
            PatientUseDrug patientUseDrug = patientUseDrugMap.get(ids[0]);
            JSONArray policy = JSONObject.parseObject(patientUseDrug.getUse_policy()).getJSONArray("points");
            boolean ok = true;
            if ( jltz < 0 ) {
                for ( int i = 0; i < policy.size(); ++ i ) {
                    JSONObject point = policy.getJSONObject(i);
                    Integer pointId = point.getInteger("pointId");
                    Integer value = point.getInteger("value");
                    if ( value == null ) continue;
                    if ( Math.abs(pointId - hour) < 2 ) {
                        if ( point.getInteger("value") > 1 ) {
                            ok = false;
                            suggestions.add(String.format("%s使用的%s从现在的%d%s减少1%s至%d%s。",
                                    QuotaUtils.getSubQuotaTimeName(pointId), drugInfo.getProduct_name(),
                                    value, unit, unit, value - 1, unit));
                        } else if ( value == 1 ) {
                            ok = false;
                            suggestions.add(String.format("停服%s的%s。",QuotaUtils.getSubQuotaTimeName(pointId),drugInfo.getProduct_name()));
                        }
                    }
                }
            } else {
                for ( int i = 0; i < policy.size(); ++ i ) {
                    JSONObject point = policy.getJSONObject(i);
                    Integer pointId = point.getInteger("pointId");
                    Integer value = point.getInteger("value");
                    if ( value == null ) {
                        ok = false;
                        suggestions.add(String.format("请在%s加服1%s%s。",
                                QuotaUtils.getSubQuotaTimeName(hour),unit,drugInfo.getProduct_name()));
                        continue;
                    }
                    if ( Math.abs(pointId-hour) < 2 ) {
                        if ( value+1 <= drugInfo.getMax_once() ) {
                            ok = false;
                            suggestions.add(String.format("%s使用的%s从现在的%d %s增加1%s至%d %s。",
                                    QuotaUtils.getSubQuotaTimeName(pointId),drugInfo.getProduct_name(),value,
                                    unit,unit,value+1,unit));
                        } else if ( value+1 > drugInfo.getMax_once() ){
                            ok = false;
                            suggestions.add(String.format("应该继续加大%s的%s的剂量，但目前该时刻的用量已达药商建议的最大剂量，是继续加量还是换药请需咨询医生。",
                                    QuotaUtils.getSubQuotaTimeName(pointId),drugInfo.getProduct_name()));
                        }
                    }
                }
            }
            if ( ok ) suggestions.add("目前血糖综合控制良好，无需调整现有治疗方案。");
        } else {
            suggestions.add("目前血糖综合控制无法优化。如有疑问，请咨询医生!");
        }

    }

    static public JSONObject SugarDrugsRegular(JSONObject levels, Integer hour) { // 短效单药
        JSONObject drug_result = new JSONObject();
        JSONObject levels_result = JSONObject.parseObject(levels.toJSONString());
        JSONObject levels_xggs = new JSONObject();
        Double jltz = 0.0;

        if ( hour == 7 ) { // 早上
            Double wfq = levels.getDouble("wfq");

            if ( wfq <= 0 ) jltz = wfq*2;
            else jltz = wfq*2-1;

            levels_xggs.put("zch", Math.min(jltz,levels.getDouble("zch")));
            levels_xggs.put("wfq",wfq <= 0.0 ? jltz - 2 * wfq : jltz);

            levels_result.put("zch",levels.getDouble("zch")-levels_xggs.getDouble("zch"));
            levels_result.put("wfq",wfq-levels_xggs.getDouble("wfq"));

        } else if ( hour == 12 ) { // 中午

            Double wcq = levels.getDouble("wcq");

            if ( wcq <= 0 ) jltz = wcq * 2;
            else jltz = wcq * 2 - 1;


            levels_xggs.put("wfh",Math.min(jltz, levels.getDouble("wfh")));
            levels_xggs.put("wcq",wcq <= 0.0 ? jltz - 2 * wcq : jltz);


            levels_result.put("wfh",levels.getDouble("wfh")-levels_xggs.getDouble("wfh"));
            levels_result.put("wcq",wcq-levels_xggs.getDouble("wcq"));

        } else if ( hour == 17 ) { // 晚上

            Double sq = levels.getDouble("sq");

            if ( sq <= 0 ) jltz = sq * 2;
            else jltz = sq * 2 - 1;

            levels_xggs.put("wch",Math.min(jltz, levels.getDouble("wch")));
            levels_xggs.put("sq",sq <= 0.0 ? jltz - 2 * sq : jltz);

            levels_result.put("wch",levels.getDouble("wch")-levels_xggs.getDouble("wch"));
            levels_result.put("sq",sq-levels_xggs.getDouble("sq"));
        }

        drug_result.put("jltz",jltz);
        drug_result.put("hour",hour);
        drug_result.put("xggs",GetSum(levels_xggs));
        drug_result.put("tzqsxgs",GetAbsValues(levels));
        drug_result.put("ok",GetOk(levels,levels_xggs));
        return drug_result;
    }

    static public JSONObject SugarDrugsLR(JSONObject levels, Integer hour, Double r) {
        JSONObject drug_result = new JSONObject();
        JSONObject levels_result = JSONObject.parseObject(levels.toJSONString());
        JSONObject levels_xggs = new JSONObject();
        Double jltz = 0.0;

        if ( hour == 7 ) {
            Double kf = levels.getDouble("kf");
            Double wfq = levels.getDouble("wfq");
            Double wcq = levels.getDouble("wcq");
            Double sq = levels.getDouble("sq");
            Double yj = levels.getDouble("yj");

            jltz = Math.min(kf,(1-r)*wfq); jltz = Math.min(jltz,wcq); jltz = Math.min(jltz,sq);
            jltz = Math.min(jltz,yj);

            if ( jltz <= 0.0 ) jltz = jltz/(1-r)*2;
            else jltz = jltz/(1-r)*2-1;

            levels_xggs.put("kf",kf<=0.0?jltz-2*kf:jltz);
            levels_xggs.put("zch",Math.min(jltz/(1-r),levels.getDouble("zch")));
            levels_xggs.put("wfq",wfq<=0?jltz/(1-r)-2*wfq:jltz/(1-r));
            levels_xggs.put("wfh",Math.min(jltz,levels.getDouble("wfh")));
            levels_xggs.put("wcq", wcq <= 0 ? jltz - 2 * wcq : jltz);
            levels_xggs.put("wch",Math.min(levels.getDouble("wch"),jltz));
            levels_xggs.put("sq",sq<=0?jltz-2*sq:jltz);
            levels_xggs.put("yj",yj<=0?jltz-2*yj:jltz);

        } else if ( hour == 12 ) {

            Double kf = levels.getDouble("kf");
            Double wfq = levels.getDouble("wfq");
            Double wcq = levels.getDouble("wcq");
            Double sq = levels.getDouble("sq");
            Double yj = levels.getDouble("yj");

            jltz = Math.min(kf,wfq); jltz = Math.min(jltz,(1-r)*wcq); jltz = Math.min(jltz,sq);
            jltz = Math.min(jltz,yj);

            if ( jltz <= 0.0 ) jltz = jltz/(1-r)*2;
            else jltz = jltz/(1-r)*2-1;

            levels_xggs.put("kf",kf<=0.0?jltz-2*kf:jltz);
            levels_xggs.put("zch",Math.min(jltz,levels.getDouble("zch")));
            levels_xggs.put("wfq",wfq<=0?jltz-2*wfq:jltz);
            levels_xggs.put("wfh",Math.min(jltz/(1-r),levels.getDouble("wfh")));
            levels_xggs.put("wcq", wcq <= 0 ? jltz/(1-r) - 2 * wcq : jltz/(1-r));
            levels_xggs.put("wch",Math.min(levels.getDouble("wch"),jltz));
            levels_xggs.put("sq",sq<=0?jltz-2*sq:jltz);
            levels_xggs.put("yj",yj<=0?jltz-2*yj:jltz);

        } else if ( hour == 17 ) {

            Double kf = levels.getDouble("kf");
            Double wfq = levels.getDouble("wfq");
            Double wcq = levels.getDouble("wcq");
            Double sq = levels.getDouble("sq");
            Double yj = levels.getDouble("yj");

            jltz = Math.min(kf,wfq); jltz = Math.min(jltz,wcq); jltz = Math.min(jltz,(1-r)*sq);
            jltz = Math.min(jltz,yj);

            if ( jltz <= 0.0 ) jltz = jltz/(1-r)*2;
            else jltz = jltz/(1-r)*2-1;

            levels_xggs.put("kf",kf<=0.0?jltz-2*kf:jltz);
            levels_xggs.put("zch",Math.min(jltz,levels.getDouble("zch")));
            levels_xggs.put("wfq",wfq<=0?jltz-2*wfq:jltz);
            levels_xggs.put("wfh",Math.min(jltz/(1-r),levels.getDouble("wfh")));
            levels_xggs.put("wcq", wcq <= 0 ? jltz - 2 * wcq : jltz);
            levels_xggs.put("wch",Math.min(levels.getDouble("wch"),jltz/(1-r)));
            levels_xggs.put("sq",sq<=0?jltz/(1-r)-2*sq:jltz/(1-r));
            levels_xggs.put("yj",yj<=0?jltz-2*yj:jltz);

        }

        drug_result.put("jltz",jltz);
        drug_result.put("hour",hour);
        drug_result.put("xggs",GetSum(levels_xggs));
        drug_result.put("tzqsxgs",GetAbsValues(levels));
        drug_result.put("ok",GetOk(levels,levels_xggs));
        return drug_result;
    }

    static public JSONObject SugarDrugsAlgorithm(
            Map<String,PatientUseDrug> patientUseDrugMap,
            Map<String,DrugInfo> drugInfoMap,
            Map<String,Double> drugCompRelateMap,
            Map<String,DrugComp> drugCompMap,
            JSONObject levels)
    {
        JSONObject algorithm_result = new JSONObject();

        JSONObject drugsInfo = DrugServiceUtils.GetDrugsInfo(patientUseDrugMap, drugInfoMap, drugCompRelateMap, drugCompMap);

        for ( String drug_id : drugsInfo.keySet() ) {
            JSONObject drug_info = drugsInfo.getJSONObject(drug_id);
            String[] processes = drug_info.getString("process").split("#");
            String[] dosis = drug_info.getString("dosis").split("#");
            if ( processes.length == 1 ) {
                // 单药
                if (processes[0].compareTo("1") == 0) { // rapid
                } else if (processes[0].compareTo("2") == 0 ) { // regular
                    String drug_id_time = drug_id+"#7";
                    algorithm_result.put(drug_id_time, SugarDrugsRegular(levels, 7));
                    drug_id_time = drug_id + "#12";
                    algorithm_result.put(drug_id_time, SugarDrugsRegular(levels, 12));
                    drug_id_time = drug_id + "#17";
                    algorithm_result.put(drug_id_time, SugarDrugsRegular(levels, 17));
                } else if (processes[0].compareTo("3") == 0 ) { // mid
                } else if (processes[0].compareTo("4") == 0 ) { // long
                }
            }
            else {
                // 双药
                if ( drug_info.getInteger("use") == 1 ) { // 注射
                    Double r = processes[0].compareTo("2") == 0 ? Double.parseDouble(dosis[0]) : Double.parseDouble(dosis[1]);
                    r /= 100.0;
                    String drug_id_time = drug_id + "#7";
                    algorithm_result.put(drug_id_time, SugarDrugsLR(levels, 7,r));
                    drug_id_time = drug_id + "#12";
                    algorithm_result.put(drug_id_time, SugarDrugsLR(levels, 12,r));
                    drug_id_time = drug_id + "#17";
                    algorithm_result.put(drug_id_time, SugarDrugsLR(levels, 17,r));

                }
            }
        }
        return algorithm_result;
    }
}
