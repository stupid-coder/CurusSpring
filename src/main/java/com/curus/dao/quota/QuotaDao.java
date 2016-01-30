package com.curus.dao.quota;

import com.alibaba.fastjson.JSONObject;
import com.curus.dao.BaseDao;
import com.curus.model.Quota;
import com.curus.utils.QuotaUtils;
import com.curus.utils.TimeUtils;
import com.curus.utils.constant.QuotaConst;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by stupid-coder on 27/1/16.
 */
public class QuotaDao extends BaseDao<Quota> {

    public int initQuota(Long account_id, Long patient_id,Map<String,String> quotas) {
        Long id = 0L;
        List<Quota> quotaList = new ArrayList<Quota>();
        for ( Map.Entry<String,String> entry : quotas.entrySet() ) {
            if ((id = QuotaUtils.getQuotaIds(entry.getKey())) != QuotaConst.QUOTA_UNKNOW_ID) {
                Quota quota = new Quota(account_id,patient_id, TimeUtils.getTimestamp(),TimeUtils.getTimestamp(),
                        id, JSONObject.toJSONString(entry));
                quotaList.add(quota);
            }
        }
        return insert(quotaList);
    }

}
