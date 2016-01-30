package com.curus.utils;

import com.curus.utils.constant.RoleConst;

import java.util.Map;

/**
 * Created by stupid-coder on 28/1/16.
 */
public class RoleUtils {

    static public String getRoleName(Long id) {
        for (Map.Entry<String,Long> entry : RoleConst.ROLE_IDS.entrySet() ) {
            if ( id.compareTo(entry.getValue()) == 0 ) return entry.getKey();
        }
        return null;
    }
}
