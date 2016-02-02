package com.curus.dao.patient;

import com.curus.dao.BaseDao;
import com.curus.model.Patient;
import com.curus.utils.TimeUtils;
import com.curus.utils.TypeUtils;

import java.sql.Date;

/**
 * Created by stupid-coder on 25/1/16.
 */
public class PatientDao extends BaseDao<Patient> {

    public Patient select(String id_nubmer) {
        return select(TypeUtils.getWhereHashMap("id_number",id_nubmer));
    }

}
