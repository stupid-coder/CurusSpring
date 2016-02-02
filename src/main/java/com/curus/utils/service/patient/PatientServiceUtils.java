package com.curus.utils.service.patient;

import com.curus.dao.CurusDriver;
import com.curus.dao.account.AccountPatientDao;
import com.curus.model.Account;
import com.curus.model.AccountPatient;
import com.curus.model.Patient;
import com.curus.utils.AppellationUtils;
import com.curus.utils.RoleUtils;
import com.curus.utils.TimeUtils;
import com.curus.utils.TypeUtils;
import com.curus.utils.constant.CommonConst;
import com.curus.utils.constant.RoleConst;
import com.curus.utils.service.issue.IssueServiceUtils;

/**
 * Created by stupid-coder on 2/2/16.
 */
public class PatientServiceUtils {

    static public Patient select(CurusDriver driver,
                                 Long patient_id) {
        return driver.patientDao.select(TypeUtils.getWhereHashMap("id", patient_id));
    }

    static public Patient AddPatient(CurusDriver driver,
                                     Account account,
                                     Patient patient,
                                     String appellation) {
        Patient ret; String role;
        if ( (ret = driver.patientDao.select(patient.getId_number())) == null) {
            driver.patientDao.insert(new Patient(patient.getName(),patient.getGender(),
                    patient.getBirth(),patient.getId_number(),
                    patient.getPhone(),patient.getAddress(),
                    TimeUtils.getTimestamp(),null,null,null,patient.getOther_contact()));
            ret = driver.patientDao.select(patient.getId_number());
            role = RoleConst.ROLE_SUPER;

        } else role =RoleConst.ROLE_COMMON;

        if ( ret != null ) {
            AddAccountPatient(driver,account,ret,role,appellation);
        }

        return ret;
    }

    static public AccountPatient AddAccountPatient(CurusDriver driver,
                                                   Account account,
                                                   Patient patient,
                                                   String role,
                                                   String appellation) {
        AccountPatient accountPatient;
        if ( (accountPatient = driver.accountPatientDao.select(TypeUtils.getWhereHashMap("account_id", account.getId(),
                "patient_id", patient.getId()))) == null) {
            Integer is_self = appellation.compareTo("self") == 0 ? CommonConst.TRUE : CommonConst.FALSE;
            if (driver.accountPatientDao.insert(new AccountPatient(account.getId(),
                    patient.getId(),
                    is_self,
                    is_self,
                    is_self,
                    RoleUtils.getRoleId(role),
                    AppellationUtils.getAppellationId(appellation)))>0)
                accountPatient = driver.accountPatientDao.select(TypeUtils.getWhereHashMap("account_id", account.getId(),
                        "patient_id", patient.getId()));
        }
        return accountPatient;
    }
}
