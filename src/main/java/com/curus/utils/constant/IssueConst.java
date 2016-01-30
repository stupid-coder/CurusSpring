package com.curus.utils.constant;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by stupid-coder on 27/1/16.
 */
// 病人问题管理
public class IssueConst {
    static public Integer ISSUE_ACTIVE = 1;
    static public Integer ISSUE_INACTIVE = 0;

    static public String ISSUE_SMOKE = "smoke";
    static public String ISSUE_ACTIVITY = "activity";
    static public String ISSUE_DIET = "diet";
    static public String ISSUE_WEIGHT = "weight";
    static public String ISSUE_BP = "bp"; // 血压
    static public String ISSUE_BS = "bs"; // 血糖
    static public String ISSUE_BF = "bf"; // 血脂
    static public String ISSUE_STROKE = "stroke"; // 脑猝
    static public String ISSUE_HEART = "heart"; //冠心病
    static public String ISSUE_CKD = "ckd"; // 慢性肾病
    static public String ISSUE_COPD = "copd"; // 慢性肺
    static public String ISSUE_SLEEP = "sleep"; // 呼吸睡眠停止
    static public String ISSUE_MENTALITY = "mentality"; // 心理

    static public String GROUP_HABIT_KEY = "habit";
    static public String GROUP_PHYSIOLOGY_KEY = "physiology";
    static public String GROUP_ILLNESS_KEY = "illness";
    static public String GROUP_UNKNOWN_KEY = null;

    static public Map<String,Long> ISSUES_IDS = new HashMap<String,Long>() {{
        // 100-200L HABIT GROUP
        put(ISSUE_SMOKE,101L); put(ISSUE_ACTIVITY,102L); put(ISSUE_DIET,103L); put(ISSUE_WEIGHT,104L);
        // 200-300L PHYSIOLOGY GROUP
        put(ISSUE_BP,201L); put(ISSUE_BS,202L); put(ISSUE_BF,203L);
        // 300L-400L ILLNESS GROUP
        put(ISSUE_STROKE,301L); put(ISSUE_HEART,302L); put(ISSUE_CKD,303L); put(ISSUE_COPD,304L);
        put(ISSUE_SLEEP,305L); put(ISSUE_MENTALITY,306L);

    }};

}
