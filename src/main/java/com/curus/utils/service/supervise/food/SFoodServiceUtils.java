package com.curus.utils.service.supervise.food;

import com.alibaba.fastjson.JSONObject;
import com.curus.dao.CurusDriver;
import com.curus.model.database.Quota;
import com.curus.utils.constant.QuotaConst;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by stupid-coder on 4/3/16.
 */
public class SFoodServiceUtils {
    static public Map<String,Double> FOOD_SCORES = new HashMap<String,Double>() {{
        put("quitelot",5.0); put("many",4.0); put("commonly",3.0); put("less",2.0); put("veryseldom",1.0);
    }};
    public static Double GetScoreByName(String key,String value) {
        if ( !FOOD_SCORES.containsKey(value) ) return 0.0;
        Double based_score = FOOD_SCORES.get(value);

        if ( key.compareTo("salt") == 0 ) return (6.0-based_score * 2.0);
        else if ( key.compareTo("meat") == 0 ) return (6.0-based_score);
        else return based_score;
    }
    public static Double CalculateFoodScore(JSONObject foodJson) {
        Double score = 0.0;
        for ( String key : foodJson.keySet() ) {
            score += GetScoreByName(key,foodJson.getString(key));
        }
        return score;
    }
    public static  Double CalculateFoodScore(CurusDriver driver,
                                             Long account_id,
                                             Long patient_id) {
        List<Quota> quotaList = driver.quotaDao.selectLastestQuota(account_id,patient_id, QuotaConst.QUOTA_FOOD_ID,1L);
        if ( quotaList.size() != 0 )
            return CalculateFoodScore(JSONObject.parseObject(quotaList.get(0).getRecord()));
        else return 0.0;
    }
}
