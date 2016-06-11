package com.curus.utils.constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by stupid-coder on 27/1/16.
 */
public class QuotaConst {

    static public String QUOTA_SMOKE = "smoke";
    static public Long QUOTA_SMOKE_ID = 1L;
    static public String QUOTA_HEIGHT = "height";
    static public Long QUOTA_HEIGHT_ID = 2L;
    static public String QUOTA_WEIGHT = "weight";
    static public Long QUOTA_WEIGHT_ID = 3L;
    static public String QUOTA_ACT = "activity";
    static public Long QUOTA_ACT_ID = 4L; // 运动
    static public String QUOTA_BP = "bdpressure";
    static public Long QUOTA_BP_ID = 5L; // 血压
    static public String QUOTA_BS = "bdsugar";
    static public Long QUOTA_BS_ID = 6L; // 血糖
    static public String QUOTA_BF = "bdlipid";
    static public Long QUOTA_BF_ID = 7L; // 血脂
    static public String QUOTA_DIET = "diet";
    static public Long QUOTA_DIET_ID = 8L; // 饮食指标
    static public String QUOTA_FOOD = "food";
    static public Long QUOTA_FOOD_ID = 9L; // 膳食
    static public String QUOTA_A1C = "a1c";
    static public Long QUOTA_A1C_ID = 10L; // 血红蛋白

    static public Long QUOTA_UNKNOW_ID = 0L;
    static public Map<String, Long> QUOTA_IDS = new HashMap<String, Long>() {{
        put(QUOTA_SMOKE, QUOTA_SMOKE_ID);
        put(QUOTA_HEIGHT, QUOTA_HEIGHT_ID);
        put(QUOTA_WEIGHT, QUOTA_WEIGHT_ID);
        put(QUOTA_ACT, QUOTA_ACT_ID);
        put(QUOTA_BP, QUOTA_BP_ID);
        put(QUOTA_BS, QUOTA_BS_ID);
        put(QUOTA_BF, QUOTA_BF_ID);
        put(QUOTA_DIET, QUOTA_DIET_ID);
        put(QUOTA_FOOD, QUOTA_FOOD_ID);
        put(QUOTA_A1C, QUOTA_A1C_ID);
    }};

    static public Map<String, Long> SUB_QUOTA_IDS = new HashMap<String, Long>() {{
        put("kf",  1L);
        put("zch", 2L);
        put("wfq", 3L);
        put("wfh", 4L);
        put("wcq", 5L);
        put("wch", 6L);
        put("sq",  7L);
        put("yj",  8L);
        put("ydq", 9L);
        put("ydh", 10L);
    }};

    static public Map<Integer,String> SUB_QUOTA_TIME_IDS = new HashMap<Integer, String>() {{
        put(6,"早餐前空腹");
        put(7,"早餐时");
        put(8,"早餐后");
        put(11,"午餐前");
        put(12, "午餐时");
        put(13,"午餐后");
        put(17,"晚餐前");
        put(18,"晚餐后");
        put(22,"睡前");
        put(0,"其他");
    }};

    static public class BdSugerLevelConfig {
        BdSugerLevelConfig(Double max_value, Double level) {
            this.max_value = max_value;
            this.level = level;
        }

        public Double max_value;
        public Double level;
    }

    static public Map<Long, List<BdSugerLevelConfig>> BS_LEVEL_CONFIG = new HashMap<Long, List<BdSugerLevelConfig>>() {{
        put(1L,new ArrayList<BdSugerLevelConfig>() {{ // 空腹前 kf
            add(new BdSugerLevelConfig(2.8, -2.0));
            add(new BdSugerLevelConfig(3.9, -1.0));
            add(new BdSugerLevelConfig(4.4, -0.5));
            add(new BdSugerLevelConfig(6.1,  0.0));
            add(new BdSugerLevelConfig(7.0,  0.5));
            add(new BdSugerLevelConfig(8.3,  1.0));
            add(new BdSugerLevelConfig(11.1, 1.5));
            add(new BdSugerLevelConfig(13.9, 2.0));
            add(new BdSugerLevelConfig(16.6, 2.5));
            add(new BdSugerLevelConfig(19.4, 3.0));
            add(new BdSugerLevelConfig(Double.MAX_VALUE, 3.5));
        }});
        put(2L,new ArrayList<BdSugerLevelConfig>() {{ // 早餐后 zch
            add(new BdSugerLevelConfig(7.8,  0.0));
            add(new BdSugerLevelConfig(10.0, 0.5));
            add(new BdSugerLevelConfig(11.1, 0.8));
            add(new BdSugerLevelConfig(Double.MAX_VALUE, 1.0));
        }});
        put(3L,new ArrayList<BdSugerLevelConfig>() {{ // 午饭前 wfq
            add(new BdSugerLevelConfig(2.8, -2.0));
            add(new BdSugerLevelConfig(3.9, -1.0));
            add(new BdSugerLevelConfig(7.8,  0.0));
            add(new BdSugerLevelConfig(8.3,  1.0));
            add(new BdSugerLevelConfig(11.1, 1.5));
            add(new BdSugerLevelConfig(13.9, 2.0));
            add(new BdSugerLevelConfig(16.6, 2.5));
            add(new BdSugerLevelConfig(19.4, 3.0));
            add(new BdSugerLevelConfig(Double.MAX_VALUE, 3.5));
        }});
        put(4L,get(2L)); // 午饭后 wfh
        put(5L,get(3L)); // 晚餐前 wcq
        put(6L,get(2L)); // 晚餐后 wch
        put(7L,get(3L)); // 睡前   sq
        put(8L,new ArrayList<BdSugerLevelConfig>() {{  // 夜间   yj
            add(new BdSugerLevelConfig(3.9, -1.0));
            add(new BdSugerLevelConfig(Double.MAX_VALUE, 0.0));
        }});
        put(9L,new ArrayList<BdSugerLevelConfig>() {{ //  运动前 ydq
            add(new BdSugerLevelConfig(10.0, 0.0));
            add(new BdSugerLevelConfig(Double.MAX_VALUE, 1.0));
        }});
        put(10L,get(3L)); // 运动后 ydh
    }};


    static public Map<String,Long> QUOTA_TYPE = new HashMap<String, Long>() {{
        put("ph",1L); put("lf",2L);
    }};

    static public Map<String,String> QUOTA_INIT = new HashMap<String, String>() {{
        put(QUOTA_DIET,"{\"lunch\":10,\"dinner\":9,\"snacks\":2,\"fatink\":3}");
        put(QUOTA_ACT,"{\"MediumWalk\":30}");
    }};

}
