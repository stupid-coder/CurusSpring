package com.curus.utils.service;

import com.curus.utils.constant.DrugConst;

import java.util.Map;

/**
 * Created by stupid-coder on 6/6/16.
 */
public class DrugUtils {

    static public Integer GetCompTypeId(String name) {
        for ( Map.Entry<Integer,String> entry : DrugConst.COMP_TYPE.entrySet() ) {
            if ( entry.getValue().compareTo(name) == 0 ) return entry.getKey();
        }
        return null;
    }
}
