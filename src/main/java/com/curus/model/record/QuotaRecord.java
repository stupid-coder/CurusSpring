package com.curus.model.record;

import com.alibaba.fastjson.JSONObject;
import com.curus.utils.constant.QuotaConst;

/**
 * Created by stupid-coder on 28/2/16.
 */
public class QuotaRecord {

    static public String GetQuotaRecord(Long id, String record) {
        if ( id.compareTo(QuotaConst.QUOTA_WEIGHT_ID) == 0 ) return JSONObject.parseObject(record, QuotaWeightRecord.class).RecordString();
        if ( id.compareTo(QuotaConst.QUOTA_HEIGHT_ID) == 0 ) return JSONObject.parseObject(record, QuotaHeightRecord.class).RecordString();
        if ( id.compareTo(QuotaConst.QUOTA_DIET_ID) == 0 ) return JSONObject.parseObject(record, QuotaDietRecord.class).RecordString();
        if ( id.compareTo(QuotaConst.QUOTA_ACT_ID) == 0 ) return JSONObject.parseObject(record, QuotaActivityRecord.class).RecordString();
        return null;
    }

}
