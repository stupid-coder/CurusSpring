package com.curus.dao.patient;

import com.curus.dao.BaseDao;
import com.curus.model.database.PatientUseDrug;
import com.curus.utils.TypeUtils;

import javax.sql.DataSource;
import java.util.List;

/**
 * Created by stupid-coder on 5/29/16.
 */
public class PatientUseDrugDao extends BaseDao<PatientUseDrug> {

    public PatientUseDrugDao(DataSource dataSource) {
        this.setDataSource(dataSource);
    }

    public List<PatientUseDrug> selectAll(Long patient_id,
                                          Integer last) {
        return selectAll(TypeUtils.getWhereHashMap("patient_id",patient_id,"last",last));
    }

    public PatientUseDrug selectDrug(Long patient_id,
                                     String drug_id,
                                     Integer latest) {
        return select(TypeUtils.getWhereHashMap("patient_id",patient_id,
                "drug_id",drug_id,
                "last",latest));
    }
}
