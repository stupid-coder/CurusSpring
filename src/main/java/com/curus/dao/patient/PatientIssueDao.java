package com.curus.dao.patient;

import com.curus.dao.BaseDao;
import com.curus.model.database.PatientIssue;
import com.curus.utils.constant.IssueConst;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by stupid-coder on 27/1/16.
 */
public class PatientIssueDao extends BaseDao<PatientIssue> {

    public int initPatientIssue(Long patient_id) {
        List<PatientIssue> patientIssuesList = new ArrayList<PatientIssue>();
        for ( Map.Entry<String,Long> entry : IssueConst.ISSUES_IDS.entrySet() ) {
            patientIssuesList.add(new PatientIssue(patient_id, entry.getValue(),IssueConst.ISSUE_INACTIVE));
        }
        return insert(patientIssuesList);
    }

}
