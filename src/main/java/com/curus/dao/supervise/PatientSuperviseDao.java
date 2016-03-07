package com.curus.dao.supervise;


import com.curus.dao.BaseDao;
import com.curus.dao.CurusDriver;
import com.curus.model.database.PatientSupervise;
import com.curus.utils.TypeUtils;
import com.curus.utils.constant.CommonConst;
import com.curus.utils.constant.QuotaConst;
import com.curus.utils.service.patient.PatientServiceUtils;

/**
 * Created by stupid-coder on 27/2/16.
 */
public class PatientSuperviseDao extends BaseDao<PatientSupervise> {
    public PatientSupervise selectLastSupervise(Long account_id, Long patient_id,
                                                    Long quota_cat_id) {
        return select(TypeUtils.getWhereHashMap("account_id", account_id, "patient_id", patient_id, "last", CommonConst.TRUE, "quota_cat_id", quota_cat_id));
    }
}
