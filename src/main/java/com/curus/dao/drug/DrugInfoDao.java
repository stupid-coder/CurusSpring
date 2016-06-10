package com.curus.dao.drug;

import com.curus.dao.BaseDao;
import com.curus.model.database.DrugInfo;
import com.curus.utils.TypeUtils;
import com.curus.utils.constant.CommonConst;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;


import javax.sql.DataSource;

import java.util.List;


/**
 * Created by stupid-coder on 5/29/16.
 */
public class DrugInfoDao extends BaseDao<DrugInfo> {
    public DrugInfoDao( DataSource dataSource ) {
        this.setDataSource(dataSource);
    }

    public DrugInfo select(String drug_id) {
        return select(TypeUtils.getWhereHashMap("drug_id",drug_id));
    }

    public List<DrugInfo> GetDrugInfos(List<String> drugIdList) {
        RowMapper<DrugInfo> rowMapper = BeanPropertyRowMapper.newInstance(DrugInfo.class);
        return getJdbcTemplate().query(String.format("SELECT * FROM %s WHERE drug_id in %?",tableName), rowMapper,drugIdList, CommonConst.TRUE);
    }
}
