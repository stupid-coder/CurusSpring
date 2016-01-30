package com.curus.utils;

import com.curus.utils.constant.AppellationConst;

import java.util.Map;

/**
 * Created by stupid-coder on 26/1/16.
 */
public class AppellationUtils {
    static public Long getAppellationId(String appellation) {
        if ( appellation == null ) return AppellationConst.APPELLATION_INVALIDE;
        for ( Map.Entry<String,Long> entry : AppellationConst.APPELLATION_IDS.entrySet() ) {
            if ( appellation.compareTo(entry.getKey()) == 0 ) return entry.getValue();
        }
        return AppellationConst.APPELLATION_INVALIDE;
    }

    static public String getAppellationName(Long appellation_id) {
        for ( Map.Entry<String,Long> entry : AppellationConst.APPELLATION_IDS.entrySet() ) {
            if ( entry.getValue().compareTo(appellation_id) == 0 ) return entry.getKey();
        }
        return null;
    }

}
