package com.curus.utils.service.patient;

import com.curus.dao.CurusDriver;
import com.curus.dao.account.AccountPatientDao;
import com.curus.model.Account;
import com.curus.model.AccountPatient;
import com.curus.model.Message;
import com.curus.model.Patient;
import com.curus.utils.AppellationUtils;
import com.curus.utils.RoleUtils;
import com.curus.utils.TimeUtils;
import com.curus.utils.TypeUtils;
import com.curus.utils.constant.CommonConst;
import com.curus.utils.constant.MessageConst;
import com.curus.utils.constant.RoleConst;
import com.curus.utils.service.account.AccountPatientServiceUtils;
import com.curus.utils.service.issue.IssueServiceUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.surefire.shade.org.codehaus.plexus.util.StringUtils;

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
        return driver.patientDao.select(id_number);
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
                                     String appellation) {
        Patient ret; String role;
        if ( patient.getId() == null && (ret = select(driver,patient.getId_number())) == null) {
            driver.patientDao.insert(new Patient(patient.getName(),patient.getGender(),
                    patient.getBirth(),patient.getId_number(),
                    patient.getPhone(),patient.getAddress(),
                    TimeUtils.getTimestamp(),null,null,null,patient.getOther_contact()));
            ret = driver.patientDao.select(patient.getId_number());
            role = RoleConst.ROLE_SUPER;

        } else { ret = patient; role =RoleConst.ROLE_COMMON; }

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
                    CommonConst.TRUE,
                    RoleUtils.getRoleId(role),
                    AppellationUtils.getAppellationId(appellation)))>0)
                accountPatient = driver.accountPatientDao.select(TypeUtils.getWhereHashMap("account_id", account.getId(),
                        "patient_id", patient.getId()));
        }

        if (role.compareTo(RoleConst.ROLE_COMMON) == 0){
            // Send Message To Super
            SendMessageToSuper(driver,account,patient);
        }
        return accountPatient;
    }
}
