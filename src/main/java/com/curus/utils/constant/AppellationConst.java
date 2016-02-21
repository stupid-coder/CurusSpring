package com.curus.utils.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by stupid-coder on 26/1/16.
 */
public class AppellationConst {

    static public Long APPELLATION_INVALIDE = 0L;
    static public String APPELLATION_SELF = "self";
    static public Map<String,Long> APPELLATION_IDS = new HashMap<String,Long>() {{
        put("self",1L); put("zu",2L); put("fu",3L); put("tong",4L); put("wan",5L);
        put("other",6L);
    }};

}
