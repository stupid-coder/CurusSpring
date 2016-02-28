package com.curus.dao.quota;

import com.curus.dao.BaseDao;
import com.curus.model.database.Quota;
import com.curus.utils.TimeUtils;
import com.curus.utils.TypeUtils;
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
                                                   Long cate_id, Date date) {
        RowMapper<Quota> rowMapper = BeanPropertyRowMapper.newInstance(Quota.class);
        return getJdbcTemplate().query(String.format("SELECT * FROM %s WHERE account_id = ? AND patient_id = ? AND quota_cat_id = ? AND measure_date >= ? ORDER by measure_date DESC", tableName),
                rowMapper, account_id, patient_id, cate_id, date);
    }

    public List<Quota> selectByMeasureDateLastDays(Long account_id, Long patient_id,
                                                 Long cate_id, Long lastdays) {
        RowMapper<Quota> rowMapper = BeanPropertyRowMapper.newInstance(Quota.class);
        return getJdbcTemplate().query(String.format("SELECT * FROM %s WHERE account_id = ? AND patient_id = ? AND quota_cat_id = ? AND measure_date >= ? ORDER by measure_date DESC", tableName),
                rowMapper, account_id, patient_id, cate_id, TimeUtils.getDate(lastdays*-1L));
    }

    public Quota selectByMeasureDate(Long account_id, Long patient_id, Long quota_id, Date date) {
        return select(TypeUtils.getWhereHashMap("account_id",account_id,"patient_id",patient_id,"quota_cat_id",quota_id,"measure_date",date));
    }

    public List<Quota> selectLastestQuota(Long account_id, Long patient_id, Long quota_id, Long limits) {
        RowMapper<Quota> rowMapper = BeanPropertyRowMapper.newInstance(Quota.class);
        return getJdbcTemplate().query(String.format("SELECT * FROM %s WHERE account_id = ? AND patient_id = ? AND quota_cat_id = ? ORDER BY measure_date DESC LIMIT %d",tableName,limits),
                rowMapper,account_id,patient_id,quota_id);
    }

    public int insert(Long account_id, Long patient_id, Long quota_id, Date date, String value) {
        Quota quota = new Quota(account_id,patient_id,date,quota_id, value);
        return insert(quota);
    }
}
