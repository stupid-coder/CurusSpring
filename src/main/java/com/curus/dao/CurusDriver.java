package com.curus.dao;


import com.curus.dao.account.AccountDao;
import com.curus.dao.account.AccountPatientDao;
import com.curus.dao.message.MessageDao;
import com.curus.dao.patient.PatientDao;
import com.curus.dao.patient.PatientIssueDao;
import com.curus.dao.quota.QuotaDao;
import com.curus.utils.SpringContextUtils;

import javax.sql.DataSource;

/**
 * Created by stupid-coder on 26/1/16.
 */

public class CurusDriver {

    public static CurusDriver getCurusDriver() {
        return (CurusDriver) SpringContextUtils.getBean("curusDriver");
    }

    public AccountDao accountDao;
    public PatientDao patientDao;
    public AccountPatientDao accountPatientDao;
    public PatientIssueDao patientIssueDao;
    public MessageDao messageDao;
    public QuotaDao quotaDao;

    public CurusDriver(DataSource ds) {
        this.accountDao = new AccountDao();
        this.accountDao.setDataSource(ds);

        this.patientDao = new PatientDao();
        this.patientDao.setDataSource(ds);

        this.accountPatientDao = new AccountPatientDao();
        this.accountPatientDao.setDataSource(ds);

        this.messageDao = new MessageDao();
        this.messageDao.setDataSource(ds);

        this.patientIssueDao = new PatientIssueDao();
        this.patientIssueDao.setDataSource(ds);

        this.quotaDao = new QuotaDao();
        this.quotaDao.setDataSource(ds);
    }
}
