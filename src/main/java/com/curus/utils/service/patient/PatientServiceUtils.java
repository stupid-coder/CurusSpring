package com.curus.utils.service.patient;

import com.curus.dao.CurusDriver;
import com.curus.model.database.Account;
import com.curus.model.database.AccountPatient;
import com.curus.model.database.Message;
import com.curus.model.database.Patient;
import com.curus.utils.AppellationUtils;
import com.curus.utils.RoleUtils;
import com.curus.utils.TimeUtils;
import com.curus.utils.TypeUtils;
import com.curus.utils.constant.CommonConst;
import com.curus.utils.constant.MessageConst;
import com.curus.utils.constant.QuotaConst;
import com.curus.utils.constant.RoleConst;
import com.curus.utils.service.account.AccountPatientServiceUtils;
import com.curus.utils.service.quota.QuotaServiceUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.validation.constraints.Null;
import java.util.List;

/**
 * Created by stupid-coder on 2/2/16.
 */
public class PatientServiceUtils {

    private static Log logger = LogFactory.getLog(PatientServiceUtils.class);

    static public Patient select(CurusDriver driver,
                                 Long patient_id) {
        return driver.patientDao.select(TypeUtils.getWhereHashMap("id", patient_id));
    }

    static public Patient select(CurusDriver driver,
                                 String id_number) {
        return driver.patientDao.select(TypeUtils.getWhereHashMap("id_number", id_number));
    }

    static public void SendMessageToSuper(CurusDriver driver,
                                          Account account,
                                          Patient patient) {
        List<AccountPatient> accountPatientList;

        if ( (accountPatientList = AccountPatientServiceUtils.selectSuper(driver, patient.getId())) != null) {
            for ( AccountPatient accountPatient : accountPatientList ) {
                driver.messageDao.insert(new Message(CommonConst.SYSNAME, accountPatient.getAccount_id(), MessageConst.ADD_PATIENT_TITLE,
                        String.format(MessageConst.ADD_PATIENT_CONTENT, account.getName(), patient.getName(), patient.getName()),
                        String.format(MessageConst.ADD_PATIENT_AGREE_LINK, patient.getId(), account.getId())));
            }
        }
    }

    static public Patient AddPatient(CurusDriver driver,
                                     Account account,
                                     Patient patient,
                                     AccountPatient accountPatient,
                                     String appellation) {
        String role;
        if ( patient.getId() == null || (patient = select(driver,patient.getId_number())) == null) {
            driver.patientDao.insert(new Patient(patient.getName(),patient.getGender(),
                    patient.getBirth(),patient.getId_number(),
                    patient.getPhone(),patient.getAddress(),
                    TimeUtils.getTimestamp(),null,null,null,patient.getOther_contact()));
            patient = select(driver,patient.getId_number());
            role = RoleConst.ROLE_SUPER;

        } else { role =RoleConst.ROLE_COMMON; }

        if ( patient != null ) {
            AddAccountPatient(driver,account,patient,accountPatient,role,appellation);
        }

        return patient;
    }

    static public AccountPatient AddAccountPatient(CurusDriver driver,
                                                   Account account,
                                                   Patient patient,
                                                   AccountPatient accountPatient,
                                                   String role,
                                                   String appellation) {
        Integer is_self = account.getId_number().compareTo(patient.getId_number()) == 0 ? CommonConst.TRUE : CommonConst.FALSE;
        if ( accountPatient == null) {
            if (driver.accountPatientDao.insert(new AccountPatient(account.getId(),
                    patient.getId(),
                    is_self,
                    role.compareTo(RoleConst.ROLE_SUPER) == 0 ? CommonConst.TRUE : CommonConst.FALSE,
                    CommonConst.TRUE,
                    RoleUtils.getRoleId(role),
                    AppellationUtils.getAppellationId(appellation)))>0) {
                accountPatient = driver.accountPatientDao.select(TypeUtils.getWhereHashMap("account_id", account.getId(),
                        "patient_id", patient.getId()));
                QuotaServiceUtils.initQuota(driver,account.getId(),patient.getId(), QuotaConst.QUOTA_INIT);
            }
            else accountPatient = null;
        } else if ( accountPatient.getIs_super_validate().compareTo(CommonConst.TRUE) == 0) {
            accountPatient.setIs_self(is_self);
            accountPatient.setRole_id(RoleUtils.getRoleId(role));
            accountPatient.setAppellation_id(AppellationUtils.getAppellationId(appellation));
        }

        if (role.compareTo(RoleConst.ROLE_COMMON) == 0){
            // Send Message To Super
            SendMessageToSuper(driver,account,patient);
        }
        return accountPatient;
    }
}
