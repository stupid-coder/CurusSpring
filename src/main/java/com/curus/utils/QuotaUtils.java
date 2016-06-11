package com.curus.utils;

import com.curus.utils.constant.QuotaConst;

import java.util.Map;

/**
 * Created by stupid-coder on 28/1/16.
 */
public class QuotaUtils {

    static public Long getQuotaIds(String quota) {
        if ( quota == null ) return QuotaConst.QUOTA_UNKNOW_ID;
        else {
            for ( Map.Entry<String,Long> entry : QuotaConst.QUOTA_IDS.entrySet() ) {
                if ( quota.compareTo(entry.getKey()) == 0) return entry.getValue();
            }
        }
        return QuotaConst.QUOTA_UNKNOW_ID;
    }

    static public String getQuotaName(Long quota_id) {
        for ( Map.Entry<String,Long> entry : QuotaConst.QUOTA_IDS.entrySet() ) {
            if ( entry.getValue().compareTo(quota_id) == 0) return entry.getKey();
        }
        return null;
    }

    static public Long getSubQuotaIds(String subquota) {
        if ( subquota == null ) return null;
        else {
            if (QuotaConst.SUB_QUOTA_IDS.containsKey(subquota)) {
                return QuotaConst.SUB_QUOTA_IDS.get(subquota);
            }
        }
        return null;
    }

    static public String getSubQuotaName(Long subcat_id) {
        if ( subcat_id == null ) return null;
        else {
            for ( Map.Entry<String,Long> entry : QuotaConst.SUB_QUOTA_IDS.entrySet()) {
                if ( subcat_id.compareTo(entry.getValue()) == 0 ) return entry.getKey();
            }
        }
        return null;
    }

    static public String getSubQuotaTimeName(Integer time) {
        if ( time == null ) return null;
        if (QuotaConst.SUB_QUOTA_TIME_IDS.containsKey(time)) return QuotaConst.SUB_QUOTA_TIME_IDS.get(time);
        else return null;
    }
}
