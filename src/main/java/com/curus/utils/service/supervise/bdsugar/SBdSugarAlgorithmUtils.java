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
            //String[] ids = drug_id_time.split("#");

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
            Integer hour = ids.length == 2 ? Integer.parseInt(ids[1]) : null;
            PatientUseDrug patientUseDrug = patientUseDrugMap.get(ids[0]);
            JSONArray policy = JSONObject.parseObject(patientUseDrug.getUse_policy()).getJSONArray("points");
            boolean ok = true;
            if ( jltz < 0 ) {
                for (int i = 0; i < policy.size(); ++i) {
                    JSONObject point = policy.getJSONObject(i);
                    Integer pointId = point.getInteger("pointId");
                    Integer value = point.getInteger("value");
                    if (value == null) continue;
                    if ( hour == null || pointId == hour) {
                        if (point.getInteger("value") > 1) {
                            ok = false;
                            suggestions.add(String.format("%s使用的%s从现在的%d%s减少1%s至%d%s。",
                                    QuotaUtils.getSubQuotaTimeName(pointId), drugInfo.getProduct_name(),
                                    value, unit, unit, value - 1, unit));
                        } else if (value == 1) {
                            ok = false;
                            suggestions.add(String.format("停服%s的%s。", QuotaUtils.getSubQuotaTimeName(pointId), drugInfo.getProduct_name()));
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
                    if ( hour == null || pointId==hour ) {
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

    static public JSONObject SugarDrugsRapid(JSONObject levels, Integer hour) {
        JSONObject drug_result = new JSONObject();
        JSONObject levels_xggs = new JSONObject();
        Double jltz = 0.0;

        if ( hour == 7 ) { // 早
            Double zch = levels.getDouble("zch");
            Double min = zch;

            if ( min <= 0.0 ) jltz = min*2;
            else jltz = min*2-1;

            levels_xggs.put("zch",zch);
        } else if ( hour == 12 ) {

            Double wfh = levels.getDouble("wfh");

            Double min = wfh;

            if ( min <= 0.0 ) jltz = min*2;
            else jltz = min*2-1;

            levels_xggs.put("wfh",wfh);

        } else if ( hour == 17) {

            Double wch = levels.getDouble("wch");

            Double min = wch;

            if ( min <= 0.0 ) jltz = min*2;
            else jltz = min*2-1;

            levels_xggs.put("wch", wch);
        }

        drug_result.put("jltz",jltz);
        drug_result.put("hour",hour);
        drug_result.put("xggs",GetSum(levels_xggs));
        drug_result.put("tzqsxgs",GetAbsValues(levels));
        drug_result.put("ok",GetOk(levels,levels_xggs));
        return drug_result;
    }

    static public JSONObject SugarDrugsRegular(JSONObject levels, Integer hour) { // 短效单药
        JSONObject drug_result = new JSONObject();
        //JSONObject levels_result = JSONObject.parseObject(levels.toJSONString());
        JSONObject levels_xggs = new JSONObject();
        Double jltz = 0.0;

        if ( hour == 7 ) { // 早上
            Double wfq = levels.getDouble("wfq");
            Double min = wfq;
            if ( min <= 0 ) jltz = wfq*2;
            else jltz = wfq*2-1;

            levels_xggs.put("zch", Math.min(min,levels.getDouble("zch")));
            levels_xggs.put("wfq",wfq <= 0.0 ? min - 2 * wfq : min);

            //levels_result.put("zch",levels.getDouble("zch")-levels_xggs.getDouble("zch"));
            //levels_result.put("wfq",wfq-levels_xggs.getDouble("wfq"));

        } else if ( hour == 12 ) { // 中午

            Double wcq = levels.getDouble("wcq");
            Double min = wcq;
            if ( wcq <= 0 ) jltz = wcq * 2;
            else jltz = wcq * 2 - 1;


            levels_xggs.put("wfh",Math.min(min, levels.getDouble("wfh")));
            levels_xggs.put("wcq",wcq <= 0.0 ? min - 2 * wcq : min);


            //levels_result.put("wfh",levels.getDouble("wfh")-levels_xggs.getDouble("wfh"));
            //levels_result.put("wcq",wcq-levels_xggs.getDouble("wcq"));

        } else if ( hour == 17 ) { // 晚上

            Double sq = levels.getDouble("sq");
            Double min = sq;
            if ( sq <= 0 ) jltz = sq * 2;
            else jltz = sq * 2 - 1;

            levels_xggs.put("wch",Math.min(min, levels.getDouble("wch")));
            levels_xggs.put("sq",sq <= 0.0 ? min - 2 * sq : min);

            //levels_result.put("wch",levels.getDouble("wch")-levels_xggs.getDouble("wch"));
            //levels_result.put("sq",sq-levels_xggs.getDouble("sq"));
        }

        drug_result.put("jltz",jltz);
        drug_result.put("hour",hour);
        drug_result.put("xggs",GetSum(levels_xggs));
        drug_result.put("tzqsxgs",GetAbsValues(levels));
        drug_result.put("ok",GetOk(levels,levels_xggs));
        return drug_result;
    }

    static public JSONObject SugarDrugsMiddle(JSONObject levels, Integer hour) {
        JSONObject drug_result = new JSONObject();
        JSONObject levels_xggs = new JSONObject();
        Double jltz = 0.0;

        if ( hour == 7 ) { // 早
            Double wfq = levels.getDouble("wfq");
            Double wcq = levels.getDouble("wcq");
            Double sq = levels.getDouble("sq");

            Double min = Math.min(Math.min(wfq,wcq),sq);

            if ( min <= 0.0 ) jltz = min*2;
            else jltz = min*2-1;

            levels_xggs.put("wfq",wfq<=0.0?min-2*wfq:min);
            levels_xggs.put("wfh",Math.min(min,levels.getDouble("wfh")));
            levels_xggs.put("wch",Math.min(min,wfq));
            levels_xggs.put("wcq",wcq<=0.0?min-2*wcq:min);
            levels_xggs.put("sq",sq<=0.0?min-2*sq:min);

        } else if ( hour == 12 ) {
            Double kf = levels.getDouble("kf");
            Double wcq = levels.getDouble("wcq");
            Double sq = levels.getDouble("sq");
            Double yj = levels.getDouble("yj");

            Double min = Math.min(kf,Math.min(wcq,Math.min(sq,yj)));

            if ( min <= 0.0 ) jltz = min*2;
            else jltz = min*2-1;

            levels_xggs.put("kf",kf<=0.0?min-2*kf:min);
            levels_xggs.put("wcq",wcq<=0.0?min-2*wcq:min);
            levels_xggs.put("wch",Math.min(min,levels.getDouble("wch")));
            levels_xggs.put("sq",sq<=0.0?min-2*sq:min);
            levels_xggs.put("yj",yj<=0.0?min-2*yj:min);

        } else if ( hour == 17) {
            Double kf = levels.getDouble("kf");
            Double wfq = levels.getDouble("wfq");
            Double yj = levels.getDouble("yj");

            Double min = Math.min(kf,Math.min(wfq,yj));

            if ( min <= 0.0 ) jltz = min*2;
            else jltz = min*2-1;

            levels_xggs.put("kf",kf<=0.0?min-2*kf:min);
            levels_xggs.put("zch", Math.min(min,levels.getDouble("zch")));
            levels_xggs.put("wfq",wfq<=0.0?min-2*wfq:min);
            levels_xggs.put("yj",yj<=0.0?min-2*yj:min);
        }
        drug_result.put("jltz",jltz);
        drug_result.put("hour",hour);
        drug_result.put("xggs",GetSum(levels_xggs));
        drug_result.put("tzqsxgs",GetAbsValues(levels));
        drug_result.put("ok",GetOk(levels,levels_xggs));
        return drug_result;
    }

    static public JSONObject SugarDrugsLong(JSONObject levels) {
        JSONObject drug_result = new JSONObject();
        JSONObject levels_xggs = new JSONObject();
        Double jltz = 0.0;

        Double kf = levels.getDouble("kf");
        Double wfq = levels.getDouble("wfq");
        Double wcq = levels.getDouble("wcq");
        Double sq = levels.getDouble("sq");
        Double yj = levels.getDouble("yj");

        Double min = Math.min(kf,wfq); min = Math.min(min,wcq); min = Math.min(min,sq); min = Math.min(min,yj);

        if ( min <= 0.0 ) jltz = min*2;
        else jltz = min*2-1;

        levels_xggs.put("kf",kf<=0.0?min-2*kf:min);
        levels_xggs.put("wch",Math.min(min,levels.getDouble("wch")));
        levels_xggs.put("wfq",levels.getDouble("wfq")<=0.0?min-2*levels.getDouble("wfq"):min);
        levels_xggs.put("wfh",Math.min(min,levels.getDouble("wfh")));
        levels_xggs.put("wcq",levels.getDouble("wcq")<=0.0?min-2*levels.getDouble("wcq"):min);
        levels_xggs.put("wch",Math.min(min,levels.getDouble("wch")));
        levels_xggs.put("sq",levels.getDouble("sq")<=0.0?min-2*levels.getDouble("sq"):min);
        levels_xggs.put("yj",levels.getDouble("yj")<=0.0?min-2*levels.getDouble("yj"):min);

        drug_result.put("jltz",jltz);
        drug_result.put("xggs",GetSum(levels_xggs));
        drug_result.put("tzqsxgs",GetAbsValues(levels));
        drug_result.put("ok",GetOk(levels,levels_xggs));

        return drug_result;
    }

    static public JSONObject SugarDrugsLongRegular(JSONObject levels, Integer hour, Double r) {
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

            Double min = Math.min(kf,(1-r)*wfq); min = Math.min(min,wcq); min = Math.min(min,sq);
            min = Math.min(min,yj);

            if ( min <= 0.0 ) jltz = min/(1-r)*2;
            else jltz = min/(1-r)*2-1;

            levels_xggs.put("kf",kf<=0.0?min-2*kf:min);
            levels_xggs.put("zch",Math.min(min/(1-r),levels.getDouble("zch")));
            levels_xggs.put("wfq",wfq<=0?min/(1-r)-2*wfq:min/(1-r));
            levels_xggs.put("wfh",Math.min(min,levels.getDouble("wfh")));
            levels_xggs.put("wcq", wcq <= 0 ? min - 2 * wcq : min);
            levels_xggs.put("wch",Math.min(levels.getDouble("wch"),min));
            levels_xggs.put("sq",sq<=0?min-2*sq:min);
            levels_xggs.put("yj",yj<=0?min-2*yj:min);

        } else if ( hour == 12 ) {

            Double kf = levels.getDouble("kf");
            Double wfq = levels.getDouble("wfq");
            Double wcq = levels.getDouble("wcq");
            Double sq = levels.getDouble("sq");
            Double yj = levels.getDouble("yj");

            Double min = Math.min(kf,wfq); min = Math.min(min,(1-r)*wcq); min = Math.min(min,sq);
            min = Math.min(min,yj);

            if ( min <= 0.0 ) jltz = min/(1-r)*2;
            else jltz = min/(1-r)*2-1;

            levels_xggs.put("kf",kf<=0.0?min-2*kf:min);
            levels_xggs.put("zch",Math.min(min,levels.getDouble("zch")));
            levels_xggs.put("wfq",wfq<=0?min-2*wfq:min);
            levels_xggs.put("wfh",Math.min(min/(1-r),levels.getDouble("wfh")));
            levels_xggs.put("wcq", wcq <= 0 ? min/(1-r) - 2 * wcq : min/(1-r));
            levels_xggs.put("wch",Math.min(levels.getDouble("wch"),min));
            levels_xggs.put("sq",sq<=0?min-2*sq:min);
            levels_xggs.put("yj",yj<=0?min-2*yj:min);

        } else if ( hour == 17 ) {

            Double kf = levels.getDouble("kf");
            Double wfq = levels.getDouble("wfq");
            Double wcq = levels.getDouble("wcq");
            Double sq = levels.getDouble("sq");
            Double yj = levels.getDouble("yj");

            Double min = Math.min(kf,wfq); min = Math.min(min,wcq); min = Math.min(min,(1-r)*sq);
            min = Math.min(min,yj);

            if ( min <= 0.0 ) jltz = min/(1-r)*2;
            else jltz = min/(1-r)*2-1;

            levels_xggs.put("kf",kf<=0.0?min-2*kf:min);
            levels_xggs.put("zch",Math.min(min,levels.getDouble("zch")));
            levels_xggs.put("wfq",wfq<=0?min-2*wfq:min);
            levels_xggs.put("wfh",Math.min(min/(1-r),levels.getDouble("wfh")));
            levels_xggs.put("wcq", wcq <= 0 ? min - 2 * wcq : min);
            levels_xggs.put("wch",Math.min(levels.getDouble("wch"),min/(1-r)));
            levels_xggs.put("sq",sq<=0?min/(1-r)-2*sq:min/(1-r));
            levels_xggs.put("yj",yj<=0?min-2*yj:min);

        }

        drug_result.put("jltz",jltz);
        drug_result.put("hour",hour);
        drug_result.put("xggs",GetSum(levels_xggs));
        drug_result.put("tzqsxgs",GetAbsValues(levels));
        drug_result.put("ok",GetOk(levels,levels_xggs));
        return drug_result;
    }

    static public JSONObject SugarDrugsLongRapid(JSONObject levels, Integer hour, Double r) {
        JSONObject drug_result = new JSONObject();
        JSONObject levels_xggs = new JSONObject();
        Double jltz = 0.0;

        if ( hour == 7 ) {
            Double kf = levels.getDouble("kf");
            Double wfq = levels.getDouble("wfq");
            Double wcq = levels.getDouble("wcq");
            Double sq = levels.getDouble("sq");
            Double yj = levels.getDouble("yj");

            Double min = Math.min(kf,wfq); min = Math.min(min,wcq); min = Math.min(min,sq); min = Math.min(min,yj);

            if ( min <= 0.0 ) jltz = min/(1-r)*2;
            else jltz = min/(1-r)*2-1;

            levels_xggs.put("kf",kf<=0.0?min-2*kf:min);
            levels_xggs.put("zch",Math.min(min/(1-r),levels.getDouble("zch")));
            levels_xggs.put("wfq",wfq<=0?min-2*wfq:min);
            levels_xggs.put("wfh",Math.min(min,levels.getDouble("wfh")));
            levels_xggs.put("wcq", wcq <= 0 ? min - 2 * wcq : min);
            levels_xggs.put("wch",Math.min(levels.getDouble("wch"),min));
            levels_xggs.put("sq",sq<=0?min-2*sq:min);
            levels_xggs.put("yj",yj<=0?min-2*yj:min);

        } else if ( hour == 12 ) {

            Double kf = levels.getDouble("kf");
            Double wfq = levels.getDouble("wfq");
            Double wcq = levels.getDouble("wcq");
            Double sq = levels.getDouble("sq");
            Double yj = levels.getDouble("yj");

            Double min = Math.min(kf,wfq); min = Math.min(min,wcq); min = Math.min(min,sq);
            min = Math.min(min,yj);

            if ( min <= 0.0 ) jltz = min/(1-r)*2;
            else jltz = min/(1-r)*2-1;

            levels_xggs.put("kf",kf<=0.0?min-2*kf:min);
            levels_xggs.put("zch",Math.min(min,levels.getDouble("zch")));
            levels_xggs.put("wfq",wfq<=0?min-2*wfq:min);
            levels_xggs.put("wfh",Math.min(min/(1-r),levels.getDouble("wfh")));
            levels_xggs.put("wcq", wcq <= 0 ? min - 2 * wcq : min);
            levels_xggs.put("wch",Math.min(levels.getDouble("wch"),min));
            levels_xggs.put("sq",sq<=0?min-2*sq:min);
            levels_xggs.put("yj",yj<=0?min-2*yj:min);

        } else if ( hour == 17 ) {

            Double kf = levels.getDouble("kf");
            Double wfq = levels.getDouble("wfq");
            Double wcq = levels.getDouble("wcq");
            Double sq = levels.getDouble("sq");
            Double yj = levels.getDouble("yj");

            Double min = Math.min(kf,wfq); min = Math.min(min,wcq); min = Math.min(min,sq);
            min = Math.min(min,yj);

            if ( min <= 0.0 ) jltz = min/(1-r)*2;
            else jltz = min/(1-r)*2-1;

            levels_xggs.put("kf",kf<=0.0?min-2*kf:min);
            levels_xggs.put("zch",Math.min(min,levels.getDouble("zch")));
            levels_xggs.put("wfq",wfq<=0?min-2*wfq:min);
            levels_xggs.put("wfh",Math.min(min,levels.getDouble("wfh")));
            levels_xggs.put("wcq", wcq <= 0 ? min - 2 * wcq : min);
            levels_xggs.put("wch",Math.min(levels.getDouble("wch"),min/(1-r)));
            levels_xggs.put("sq",sq<=0?min-2*sq:min);
            levels_xggs.put("yj",yj<=0?min-2*yj:min);

        }

        drug_result.put("jltz",jltz);
        drug_result.put("hour",hour);
        drug_result.put("xggs",GetSum(levels_xggs));
        drug_result.put("tzqsxgs",GetAbsValues(levels));
        drug_result.put("ok",GetOk(levels,levels_xggs));
        return drug_result;
    }

    static public JSONObject SugarDrugsMiddleRegular(JSONObject levels, Integer hour, Double r) {
        JSONObject drug_result = new JSONObject();
        JSONObject levels_xggs = new JSONObject();
        Double jltz = 0.0;

        if ( hour == 7 ) {
            Double wfq = levels.getDouble("wfq");
            Double wcq = levels.getDouble("wcq");
            Double sq = levels.getDouble("sq");

            Double min = Math.min(wfq*(1-r),wcq); min = Math.min(min,sq);

            if ( min <= 0.0 ) jltz = min/(1-r)*2;
            else jltz = min/(1-r)*2-1;

            levels_xggs.put("zch",Math.min(min * r / (1 - r), levels.getDouble("zch")));
            levels_xggs.put("wfq",wfq<=0?min/(1-r)-2*wfq:min/(1-r));
            levels_xggs.put("wfh",Math.min(min,levels.getDouble("wfh")));
            levels_xggs.put("wcq", wcq <= 0 ? min - 2 * wcq : min);
            levels_xggs.put("wch",Math.min(levels.getDouble("wch"),min));
            levels_xggs.put("sq",sq<=0?min-2*sq:min);

        } else if ( hour == 12 ) {

            Double kf = levels.getDouble("kf");
            Double wcq = levels.getDouble("wcq");
            Double sq = levels.getDouble("sq");
            Double yj = levels.getDouble("yj");

            Double min = Math.min(kf,wcq*(1-r)); min = Math.min(min,sq); min = Math.min(min,yj);

            if ( min <= 0.0 ) jltz = min/(1-r)*2;
            else jltz = min/(1-r)*2-1;

            levels_xggs.put("kf",kf<=0.0?min-2*kf:min);
            levels_xggs.put("wfh",Math.min(min * r / (1 - r), levels.getDouble("wfh")));
            levels_xggs.put("wcq", wcq <= 0 ? min/(1-r)- 2 * wcq : min/(1-r));
            levels_xggs.put("wch",Math.min(levels.getDouble("wch"),min));
            levels_xggs.put("sq",sq<=0?min-2*sq:min);
            levels_xggs.put("yj",yj<=0?min-2*yj:min);

        } else if ( hour == 17 ) {

            Double kf = levels.getDouble("kf");
            Double zch = levels.getDouble("zch");
            Double sq = levels.getDouble("sq");
            Double yj = levels.getDouble("yj");

            Double min = Math.min(kf,zch); min = Math.min(min,(1-r)*sq); min = Math.min(min,yj);

            if ( min <= 0.0 ) jltz = min/(1-r)*2;
            else jltz = min/(1-r)*2-1;

            levels_xggs.put("kf",kf<=0.0?min-2*kf:min);
            levels_xggs.put("zch",Math.min(min,levels.getDouble("zch")));
            levels_xggs.put("wch",Math.min(levels.getDouble("wch"),min*r/(1-r)));
            levels_xggs.put("sq",sq<=0?min/(1-r)-2*sq:min/(1-r));
            levels_xggs.put("yj",yj<=0?min-2*yj:min);

        }
        drug_result.put("jltz",jltz);
        drug_result.put("hour",hour);
        drug_result.put("xggs",GetSum(levels_xggs));
        drug_result.put("tzqsxgs",GetAbsValues(levels));
        drug_result.put("ok",GetOk(levels,levels_xggs));
        return drug_result;
    }

    static public JSONObject SugarDrugsMiddleRapid(JSONObject levels, Integer hour, Double r) {
        JSONObject drug_result = new JSONObject();
        JSONObject levels_xggs = new JSONObject();
        Double jltz = 0.0;

        if ( hour == 7 ) {
            Double wfq = levels.getDouble("wfq");
            Double wcq = levels.getDouble("wcq");
            Double sq = levels.getDouble("sq");

            Double min = Math.min(wfq,wcq); min = Math.min(min,sq);

            if ( min <= 0.0 ) jltz = min/(1-r)*2;
            else jltz = min/(1-r)*2-1;

            levels_xggs.put("zch",Math.min(min * r / (1 - r), levels.getDouble("zch")));
            levels_xggs.put("wfq",wfq<=0?min-2*wfq:min);
            levels_xggs.put("wfh",Math.min(min,levels.getDouble("wfh")));
            levels_xggs.put("wcq", wcq <= 0 ? min - 2 * wcq : min);
            levels_xggs.put("wch",Math.min(levels.getDouble("wch"),min));
            levels_xggs.put("sq",sq<=0?min-2*sq:min);

        } else if ( hour == 12 ) {

            Double kf = levels.getDouble("kf");
            Double wcq = levels.getDouble("wcq");
            Double sq = levels.getDouble("sq");
            Double yj = levels.getDouble("yj");

            Double min = Math.min(kf,wcq); min = Math.min(min,wcq); min = Math.min(min,sq);

            if ( min <= 0.0 ) jltz = min/(1-r)*2;
            else jltz = min/(1-r)*2-1;

            levels_xggs.put("kf",kf<=0.0?min-2*kf:min);
            levels_xggs.put("wfh",Math.min(levels.getDouble("wfh"),min*r/(1-r)));
            levels_xggs.put("wcq",wcq<=0.0?min-2*wcq:min);
            levels_xggs.put("wch",Math.min(levels.getDouble("wch"),min));
            levels_xggs.put("sq",sq<=0?min-2*sq:min);
            levels_xggs.put("yj",yj<=0?min-2*yj:min);

        } else if ( hour == 17 ) {

            Double kf = levels.getDouble("kf");
            Double zch = levels.getDouble("zch");
            Double sq = levels.getDouble("sq");
            Double yj = levels.getDouble("yj");

            Double min = Math.min(kf,zch); min = Math.min(min,(1-r)*sq); min = Math.min(min,yj);
            min = Math.min(min,yj);

            if ( min <= 0.0 ) jltz = min/(1-r)*2;
            else jltz = min/(1-r)*2-1;

            levels_xggs.put("kf",kf<=0.0?min-2*kf:min);
            levels_xggs.put("zch",Math.min(min,zch));
            levels_xggs.put("wcq",Math.min(levels.getDouble("wcq"),min*r/(1-r)));
            levels_xggs.put("sq",sq<=0?min-2*sq:min);
            levels_xggs.put("yj",yj<=0?min-2*yj:min);

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
            String[] dosis = drug_info.getString("dosis")==null?null:drug_info.getString("dosis").split("#");
            if ( processes.length == 1 ) {
                // 单药
                if (processes[0].compareTo("1") == 0) { // rapid
                    algorithm_result.put(drug_id+"#7", SugarDrugsRapid(levels,7));
                    algorithm_result.put(drug_id+"#12", SugarDrugsRapid(levels,12));
                    algorithm_result.put(drug_id+"#17", SugarDrugsRapid(levels,17));
                } else if (processes[0].compareTo("2") == 0 ) { // regular
                    algorithm_result.put(drug_id+"#7", SugarDrugsRegular(levels, 7));
                    algorithm_result.put(drug_id+"#12", SugarDrugsRegular(levels, 12));
                    algorithm_result.put(drug_id+"#17", SugarDrugsRegular(levels, 17));
                } else if (processes[0].compareTo("3") == 0 ) { // mid
                    algorithm_result.put(drug_id+"#7",SugarDrugsMiddle(levels, 7));
                    algorithm_result.put(drug_id+"#12",SugarDrugsMiddle(levels, 12));
                    algorithm_result.put(drug_id+"#17",SugarDrugsMiddle(levels, 17));
                } else if (processes[0].compareTo("4") == 0 ) { // long
                    algorithm_result.put(drug_id,SugarDrugsLong(levels));
                }
            }
            else {
                // 双药
                int short_drug = Math.min(Integer.parseInt(processes[0]),Integer.parseInt(processes[1]));
                int long_drug = Math.max(Integer.parseInt(processes[0]), Integer.parseInt(processes[1]));

                if ( short_drug == 2 && long_drug == 4 ) {
                    if ( drug_info.getInteger("use") != 1 ) {
                        algorithm_result.put(drug_id+"#7", SugarDrugsLongRegular(levels, 7, 0.5));
                        algorithm_result.put(drug_id+"#12", SugarDrugsLongRegular(levels, 12, 0.5));
                        algorithm_result.put(drug_id+"#17", SugarDrugsLongRegular(levels, 17,0.5));
                    } else {
                        Double r = processes[0].compareTo(Integer.toString(short_drug)) == 0 ? Double.parseDouble(dosis[0]) : Double.parseDouble(dosis[1]);
                        r /= 100.0;
                        algorithm_result.put(drug_id+"#7", SugarDrugsLongRegular(levels, 7, r));
                        algorithm_result.put(drug_id+"#12", SugarDrugsLongRegular(levels, 12, r));
                        algorithm_result.put(drug_id+"#17", SugarDrugsLongRegular(levels, 17,r));
                    }
                } else if ( short_drug == 1 && long_drug == 4 ) {
                    if ( drug_info.getInteger("use") != 1 ) {
                        algorithm_result.put(drug_id+"#7", SugarDrugsLongRapid(levels, 7, 0.5));
                        algorithm_result.put(drug_id+"#12", SugarDrugsLongRapid(levels, 12, 0.5));
                        algorithm_result.put(drug_id+"#17", SugarDrugsLongRapid(levels, 17, 0.5));
                    } else {
                        Double r = processes[0].compareTo(Integer.toString(short_drug)) == 0 ? Double.parseDouble(dosis[0]) : Double.parseDouble(dosis[1]);
                        r /= 100.0;
                        algorithm_result.put(drug_id+"#7", SugarDrugsLongRapid(levels, 7, r));
                        algorithm_result.put(drug_id+"#12", SugarDrugsLongRapid(levels, 12, r));
                        algorithm_result.put(drug_id+"#17", SugarDrugsLongRapid(levels, 17, r));
                    }
                } else if ( short_drug == 2 && long_drug == 3 ) {
                    if ( drug_info.getInteger("use") != 1 ) {
                        algorithm_result.put(drug_id+"#7", SugarDrugsMiddleRegular(levels, 7, 0.5));
                        algorithm_result.put(drug_id+"#12", SugarDrugsMiddleRegular(levels, 12, 0.5));
                        algorithm_result.put(drug_id+"#17", SugarDrugsMiddleRegular(levels, 17, 0.5));
                    } else {
                        Double r = processes[0].compareTo(Integer.toString(short_drug)) == 0 ? Double.parseDouble(dosis[0]) : Double.parseDouble(dosis[1]);
                        r /= 100.0;
                        algorithm_result.put(drug_id+"#7", SugarDrugsMiddleRegular(levels, 7, r));
                        algorithm_result.put(drug_id + "#12", SugarDrugsMiddleRegular(levels, 12, r));
                        algorithm_result.put(drug_id+"#17", SugarDrugsMiddleRegular(levels, 17, r));
                    }
                } else if ( short_drug == 1 && long_drug == 3 ) {
                    if ( drug_info.getInteger("use") != 1 ) {
                        algorithm_result.put(drug_id+"#7", SugarDrugsMiddleRapid(levels, 7, 0.5));
                        algorithm_result.put(drug_id+"#12", SugarDrugsMiddleRapid(levels, 12, 0.5));
                        algorithm_result.put(drug_id+"#17", SugarDrugsMiddleRapid(levels, 17, 0.5));
                    } else {
                        Double r = processes[0].compareTo(Integer.toString(short_drug)) == 0 ? Double.parseDouble(dosis[0]) : Double.parseDouble(dosis[1]);
                        r /= 100.0;
                        algorithm_result.put(drug_id+"#7", SugarDrugsMiddleRapid(levels, 7, r));
                        algorithm_result.put(drug_id + "#12", SugarDrugsMiddleRapid(levels, 12, r));
                        algorithm_result.put(drug_id +"#17", SugarDrugsMiddleRapid(levels, 17, r));
                    }
                }
            }
        }
        return algorithm_result;
    }
}
