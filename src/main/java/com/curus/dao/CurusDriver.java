package com.curus.dao;


import com.curus.dao.account.AccountDao;
import com.curus.dao.account.AccountPatientDao;
import com.curus.dao.drug.DrugCompDao;
import com.curus.dao.drug.DrugCompRelationDao;
import com.curus.dao.drug.DrugInfoDao;
import com.curus.dao.message.MessageDao;
import com.curus.dao.patient.PatientDao;
import com.curus.dao.patient.PatientIssueDao;
import com.curus.dao.patient.PatientUseDrugDao;
import com.curus.dao.quota.QuotaDao;
import com.curus.dao.supervise.PatientSuperviseDao;
import com.curus.dao.supervise.PatientSuperviseListDao;
import com.curus.utils.SpringContextUtils;

import javax.sql.DataSource;

/**
 * Created by stupid-coder on 26/1/16.
 */

public class CurusDriver {

    private static CurusDriver instance_;

    private CurusDriver() {}

    public static synchronized CurusDriver getCurusDriver() {
        return (CurusDriver) SpringContextUtils.getBean("curusDriver");
        /*
        if ( instance_ == null )
            instance_ = (CurusDriver) SpringContextUtils.getBean("curusDriver");
        return instance_;
        */
    }

    public AccountDao accountDao;
    public PatientDao patientDao;
    public AccountPatientDao accountPatientDao;
    public PatientIssueDao patientIssueDao;
    public MessageDao messageDao;
    public QuotaDao quotaDao;
    public PatientSuperviseDao patientSuperviseDao;
    public PatientSuperviseListDao patientSuperviseListDao;
    public PatientUseDrugDao patientUseDrugDao;
    public DrugCompDao drugCompDao;
    public DrugInfoDao drugInfoDao;
    public DrugCompRelationDao drugCompRelationDao;
    public InternalDataDao internalDataDao;

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

        this.patientSuperviseDao = new PatientSuperviseDao();
        this.patientSuperviseDao.setDataSource(ds);

        this.patientSuperviseListDao = new PatientSuperviseListDao();
        this.patientSuperviseListDao.setDataSource(ds);

        this.patientUseDrugDao = new PatientUseDrugDao(ds);

        this.drugInfoDao = new DrugInfoDao(ds);

        this.drugCompDao = new DrugCompDao(ds);

        this.drugCompRelationDao = new DrugCompRelationDao(ds);

        this.internalDataDao = new InternalDataDao(ds);
    }
}
