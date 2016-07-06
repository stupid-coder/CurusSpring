package com.curus.dao.quota;

import com.alibaba.fastjson.JSONObject;
import com.curus.dao.BaseDao;
import com.curus.model.database.Quota;
import com.curus.utils.QuotaUtils;
import com.curus.utils.TimeUtils;
import com.curus.utils.TypeUtils;
import com.curus.utils.constant.QuotaConst;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by stupid-coder on 27/1/16.
 */
public class QuotaDao extends BaseDao<Quota> {

    public List<Quota> selectByMeasureDateLastDays(Long account_id, Long patient_id,
                                                   Long quota_id, Long subquota_id, Long lastdays) {
        RowMapper<Quota> rowMapper = BeanPropertyRowMapper.newInstance(Quota.class);
        if ( subquota_id != null )
            return getJdbcTemplate().query(String.format("SELECT * FROM %s WHERE account_id = ? AND patient_id = ? AND quota_cat_id = ? AND sub_cat = ? AND measure_date >= ? ORDER by measure_date DESC", tableName), rowMapper, account_id, patient_id, quota_id, subquota_id, TimeUtils.getDate(lastdays*-1L));
        else return getJdbcTemplate().query(String.format("SELECT * FROM %s WHERE account_id = ? AND patient_id = ? AND quota_cat_id = ? AND measure_date >= ? ORDER by measure_date DESC", tableName), rowMapper, account_id, patient_id, quota_id, TimeUtils.getDate(lastdays*-1L));
    }
    public List<Quota> selectByMeasureDateLastDays(Long account_id, Long patient_id,
                                                   Long quota_id, Long lastdays) {
        RowMapper<Quota> rowMapper = BeanPropertyRowMapper.newInstance(Quota.class);
        return getJdbcTemplate().query(String.format("SELECT * FROM %s WHERE account_id = ? AND patient_id = ? AND quota_cat_id = ? AND measure_date >= ? ORDER by measure_date DESC", tableName), rowMapper, account_id, patient_id, quota_id, TimeUtils.getDate(lastdays*-1L));
    }

    public List<Quota> selectAfterMeasureDate(Long account_id, Long patient_id,
                                           Long quota_id, Date date) {
        RowMapper<Quota> rowMapper = BeanPropertyRowMapper.newInstance(Quota.class);
        return getJdbcTemplate().query(String.format("SELECT * FROM %s WHERE account_id = ? AND patient_id = ? AND quota_cat_id = ? AND measure_date >= ? ORDER by measure_date", tableName), rowMapper, account_id, patient_id, quota_id, date);
    }

    public Quota selectByMeasureDate(Long account_id, Long patient_id, Long quota_id, Long subquota_id, Date date) {
        if ( subquota_id != null )
            return select(TypeUtils.getWhereHashMap("account_id",account_id,"patient_id",patient_id,"quota_cat_id",quota_id,"sub_cat",subquota_id,"measure_date",date));
        else return select(TypeUtils.getWhereHashMap("account_id",account_id,"patient_id",patient_id,"quota_cat_id",quota_id,"measure_date",date));
    }

    public List<Quota> selectLastestQuota(Long account_id, Long patient_id, Long quota_id, Long limits) {
        RowMapper<Quota> rowMapper = BeanPropertyRowMapper.newInstance(Quota.class);
        return getJdbcTemplate().query(String.format("SELECT * FROM %s WHERE account_id = ? AND patient_id = ? AND quota_cat_id = ? ORDER BY measure_date DESC LIMIT %d",tableName,limits),
                rowMapper,account_id,patient_id,quota_id);
    }

    public JSONObject selectLastestBSQuota(Long account_id, Long patient_id) {
        JSONObject bsquota = new JSONObject();
        RowMapper<Quota> rowMapper = BeanPropertyRowMapper.newInstance(Quota.class);
        for ( Long sub_cat = 1L; sub_cat <=  QuotaConst.SUB_QUOTA_IDS.size(); ++ sub_cat) {
            List<Quota> quotaList = getJdbcTemplate().query(String.format("SELECT measure_date,record FROM %s WHERE account_id = ? AND patient_id=? AND quota_cat_id=? AND sub_cat = ? ORDER BY measure_date DESC LIMIT 1", tableName), rowMapper, account_id, patient_id, QuotaConst.QUOTA_BS_ID, sub_cat);
            if (quotaList != null && quotaList.size() == 1)
                bsquota.put(QuotaUtils.getSubQuotaName(sub_cat), quotaList.get(0));
        }
        return bsquota;
    }

    public int insert(Long account_id, Long patient_id, Long quota_id, Long subquota_id, Date date, String value) {
        Quota quota = new Quota(account_id,patient_id,date,quota_id, value);
        if (subquota_id != null) quota.setSub_cat(subquota_id);
        return insert(quota);
    }

}
