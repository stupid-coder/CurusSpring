package com.curus.utils.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by stupid-coder on 27/1/16.
 */
public class QuotaConst {

    static public String QUOTA_SMOKE = "smoke"; static public Long QUOTA_SMOKE_ID = 1L;
    static public String QUOTA_HEIGHT = "height"; static public Long QUOTA_HEIGHT_ID = 2L;
    static public String QUOTA_WEIGHT = "weight"; static public Long QUOTA_WEIGHT_ID = 3L;
    static public String QUOTA_ACT = "activity"; static public Long QUOTA_ACT_ID = 4L; // 运动
    static public String QUOTA_BP = "bdpressure"; static public Long QUOTA_BP_ID = 5L; // 血压
    static public String QUOTA_BS = "bdsugar"; static public Long QUOTA_BS_ID = 6L; // 血糖
    static public String QUOTA_BF = "bdlipid"; static public Long QUOTA_BF_ID = 7L; // 血脂
    static public String QUOTA_DIET = "diet"; static public Long QUOTA_DIET_ID =  8L; // 饮食指标
    static public String QUOTA_FOOD = "dietary"; static public Long QUOTA_FOOD_ID = 9L; // 膳食

    static public Long QUOTA_UNKNOW_ID = 0L;
    static public Map<String,Long> QUOTA_IDS = new HashMap<String, Long>() {{
        put(QUOTA_SMOKE,1L); put(QUOTA_HEIGHT,2L); put(QUOTA_WEIGHT,3L);
        put(QUOTA_ACT,4L); put(QUOTA_BP,5L); put(QUOTA_BS,6L);
        put(QUOTA_BF,7L); put(QUOTA_DIET,8L);
    }};

    static public Map<String,Long> QUOTA_TYPE = new HashMap<String, Long>() {{
        put("ph",1L); put("lf",2L);
    }};

    static public Map<String,String> QUOTA_INIT = new HashMap<String, String>() {{
        put(QUOTA_DIET,"{\"lunch\":0,\"dinner\":0,\"snacks\":0,\"fatink\":0}");
        put(QUOTA_ACT,"{\"1\":0}");
    }};
}
