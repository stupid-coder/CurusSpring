package com.curus.utils.service.patient;

import com.curus.dao.CurusDriver;
import com.curus.im.ImQueryInterface;
import com.curus.model.database.Account;
import com.curus.model.database.AccountPatient;
import com.curus.model.database.Message;
import com.curus.model.database.Patient;
import com.curus.utils.*;
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

    static public String GetPatientName(CurusDriver driver, Long patient_id) {
        return select(driver, patient_id).getName();
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
                                     String appellation,
                                     String weight,
                                     String height) {
        String role  = RoleConst.ROLE_COMMON;
        if ( patient.getId() == null ) {
            driver.patientDao.insert(new Patient(patient.getName(),patient.getGender(),
                    patient.getBirth(),patient.getId_number(),
                    patient.getPhone(),patient.getAddress(),
                    TimeUtils.getTimestamp(),null,null,null,patient.getOther_contact()));
            patient = select(driver,patient.getId_number());
            role = RoleConst.ROLE_SUPER;

            ImUtils.CreateIM(patient.getId_number(),patient.getName());
        } else {
            if ( (accountPatient != null && accountPatient.getRole_id().compareTo(RoleConst.ROLE_IDS.get(RoleConst.ROLE_SUPER)) == 0) ||
                    AccountPatientServiceUtils.selectSuper(driver,patient.getId()) == null ) { // super user, update patient
                role = RoleConst.ROLE_SUPER;
                driver.patientDao.update(patient,"id");
            }
        }

        AddAccountPatient(driver,account,patient,accountPatient,role,appellation);

        if ( role.compareTo(RoleConst.ROLE_SUPER) == 0 ) {
            QuotaServiceUtils.addQuota(driver,account.getId(),patient.getId(),QuotaConst.QUOTA_WEIGHT,null,null,
                    QuotaServiceUtils.getKVJSON(QuotaConst.QUOTA_WEIGHT, weight));
            QuotaServiceUtils.addQuota(driver,account.getId(),patient.getId(),QuotaConst.QUOTA_HEIGHT,null,null,
                    QuotaServiceUtils.getKVJSON(QuotaConst.QUOTA_HEIGHT, height));
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
                    CommonConst.TRUE,
                    CommonConst.TRUE,
                    RoleUtils.getRoleId(role),
                    AppellationUtils.getAppellationId(appellation)))>0) {
                accountPatient = driver.accountPatientDao.select(TypeUtils.getWhereHashMap("account_id", account.getId(),
                        "patient_id", patient.getId()));
                QuotaServiceUtils.addQuotas(driver,account.getId(),patient.getId(), TimeUtils.getDate(), QuotaConst.QUOTA_INIT);
            }
            else return null;

            if ( is_self.compareTo(CommonConst.TRUE) != 0 ) {
                ImUtils.AddIM(account.getId_number(),patient.getId_number());
            }
        } else {
            accountPatient.setIs_self(is_self);
            accountPatient.setRole_id(RoleUtils.getRoleId(role));
            accountPatient.setAppellation_id(AppellationUtils.getAppellationId(appellation));
            driver.accountPatientDao.update(accountPatient, "id");
        }
        return accountPatient;
    }
}
