package com.curus.dao.drug;

import com.curus.dao.BaseDao;
import com.curus.model.database.DrugCompRelation;
import com.curus.utils.TypeUtils;

import javax.sql.DataSource;
import java.util.List;

/**
 * Created by stupid-coder on 5/29/16.
 */
public class DrugCompRelationDao extends BaseDao<DrugCompRelation> {

    public DrugCompRelationDao( DataSource dataSource) {
        this.setDataSource(dataSource);
    }

    public List<DrugCompRelation> selectByDrugId(String drug_id) {
        return selectAll(TypeUtils.getWhereHashMap("drug_id",drug_id));
    }

}
