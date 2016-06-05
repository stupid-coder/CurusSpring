package com.curus.utils.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by stupid-coder on 6/6/16.
 */
public class BdSugarConst {

    public static Map<String,Long> SugarMonitorConfig = new HashMap<String, Long>() {{
        put("眼底与视力",360L);
        put("足动脉搏动",120L);
        put("神经病变",120L);
        put("体重",30L);
        put("BMI",120L);
        put("血压",30L);
        put("空腹/餐后血糖",30L);
        put("糖化血红蛋白",120L);
        put("血脂",360L);
        put("尿白蛋白",360L);
        put("肌酐/BUN",360L);
        put("肝功能",360L);
        put("心电图",360L);
        put("尿常规",120L);
    }};
}
