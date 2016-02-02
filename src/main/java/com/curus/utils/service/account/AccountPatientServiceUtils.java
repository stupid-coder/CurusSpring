package com.curus.utils.service.account;

import com.curus.dao.CurusDriver;
import com.curus.model.AccountPatient;
import com.curus.utils.TypeUtils;
import com.curus.utils.constant.CommonConst;

import java.util.List;

/**
 * Created by stupid-coder on 2/2/16.
 */
public class AccountPatientServiceUtils {

    static public List<AccountPatient> selectValidate(CurusDriver driver,
                                                      Long account_id) {
        return driver.accountPatientDao.selectAll(TypeUtils.getWhereHashMap(
                "account_id",account_id,
                "is_super_validate", CommonConst.TRUE,
                "is_patient_validate",CommonConst.TRUE
        ));
    }

}
