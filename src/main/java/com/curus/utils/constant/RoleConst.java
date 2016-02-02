package com.curus.utils.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by stupid-coder on 27/1/16.
 */
public class RoleConst {

    static public String ROLE_COMMON = "common";
    static public String ROLE_SUPER = "super";

    static public Map<String,Long> ROLE_IDS = new HashMap<String,Long>() {{
        put("super",1L);
        put("common",2L);
    }};


}
