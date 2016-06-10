package com.curus.dao.patient;

import com.curus.dao.BaseDao;
import com.curus.model.database.Patient;
import com.curus.model.database.PatientUseDrug;
import com.curus.model.database.Quota;
import com.curus.utils.TimeUtils;
import com.curus.utils.TypeUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;

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

    public List<PatientUseDrug> selectByMeasureDateLastDays(Long account_id,
                                                            Long patient_id,
                                                            Long lastestdays) {
        RowMapper<PatientUseDrug> rowMapper = BeanPropertyRowMapper.newInstance(PatientUseDrug.class);
        return getJdbcTemplate().query(String.format("SELECT * FROM %s WHERE patient_id = ? AND change_time >= ? ORDER by change_time DESC", tableName), rowMapper, patient_id, TimeUtils.getDate(lastestdays * -1L));
    }

}
