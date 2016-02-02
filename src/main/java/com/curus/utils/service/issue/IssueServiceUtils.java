package com.curus.utils.service.issue;

import com.curus.dao.CurusDriver;

/**
 * Created by stupid-coder on 2/2/16.
 */
public class IssueServiceUtils {

    public static int InitIssue(CurusDriver driver, Long patient_id) {

        return  driver.patientIssueDao.initPatientIssue(patient_id);

    }

}
