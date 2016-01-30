package com.curus.utils.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by stupid-coder on 27/1/16.
 */
public class QuotaConst {

    static public String QUOTA_SMOKE = "smoke";
    static public String QUOTA_HEIGHT = "height";
    static public String QUOTA_WEIGHT = "weight";
    static public String QUOTA_ACT = "act";
    static public String QUOTA_BP = "bp";
    static public String QUOTA_BS = "bs";
    static public String QUOTA_BF = "bf";
    static public String QUOTA_DIET = "diet";
    static public Long QUOTA_UNKNOW_ID = 0L;
    static public Map<String,Long> QUOTA_IDS = new HashMap<String, Long>() {{
        put(QUOTA_SMOKE,1L); put(QUOTA_HEIGHT,2L); put(QUOTA_WEIGHT,3L);
        put(QUOTA_ACT,4L); put(QUOTA_BP,5L); put(QUOTA_BS,6L);
        put(QUOTA_BF,7L); put(QUOTA_DIET,8L);
    }};

    static public Map<String,Long> QUOTA_TYPE = new HashMap<String, Long>() {{
        put("ph",1L); put("lf",2L);
    }};
}
