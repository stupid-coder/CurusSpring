package com.curus.dao.drug;

import com.curus.dao.BaseDao;
import com.curus.model.database.DrugInfo;
import com.curus.utils.TypeUtils;

import javax.sql.DataSource;


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
}
