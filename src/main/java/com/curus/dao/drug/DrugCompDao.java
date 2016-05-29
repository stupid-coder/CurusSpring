package com.curus.dao.drug;

import com.curus.dao.BaseDao;
import com.curus.model.database.DrugComp;
import com.curus.utils.TypeUtils;

import javax.sql.DataSource;

/**
 * Created by stupid-coder on 5/29/16.
 */
public class DrugCompDao extends BaseDao<DrugComp> {
    public DrugCompDao(DataSource dataSource) {
        this.setDataSource(dataSource);
    }

    public DrugComp selectByCompId(String comp_id) {
        return select(TypeUtils.getWhereHashMap("comp_id",comp_id));
    }
}
