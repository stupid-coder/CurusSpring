package com.curus.dao.quota;

import com.curus.dao.BaseDao;
import com.curus.model.database.Quota;
import com.curus.utils.TimeUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stupid-coder on 27/1/16.
 */
public class QuotaDao extends BaseDao<Quota> {

    final Long days90ago = -90L * 24L * 3600L ;

    public List<Quota> selectByMeasureTime90Days(Long account_id, Long patient_id,
                                                 Long cate_id) {
        RowMapper<Quota> rowMapper = BeanPropertyRowMapper.newInstance(Quota.class);
        List<Quota> rs = getJdbcTemplate().query(String.format("SELECT * FROM %s WHERE account_id = ? AND patient_id = ? AND quota_cat_id = ? AND measure_time >= ?", tableName),
                rowMapper, new Object[] {account_id, patient_id, cate_id, TimeUtils.getTimestamp(days90ago)});
        if (rs.isEmpty()) return null;
        else return rs;
    }

}
