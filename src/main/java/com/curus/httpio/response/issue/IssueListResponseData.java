package com.curus.httpio.response.issue;

import com.curus.model.PatientIssue;

import java.util.List;

/**
 * Created by stupid-coder on 28/1/16.
 */
public class IssueListResponseData {

    public class IssueListItem {
        private Long patient_id;
        private Long issue_id;
        private String issue_category;
        private String patient_name;

        public IssueListItem(Long patient_id, Long issue_id, String issue_category, String patient_name) {
            this.patient_id = patient_id;
            this.issue_id = issue_id;
            this.issue_category = issue_category;
            this.patient_name = patient_name;
        }

        public Long getPatient_id() {
            return patient_id;
        }

        public void setPatient_id(Long patient_id) {
            this.patient_id = patient_id;
        }

        public Long getIssue_id() {
            return issue_id;
        }

        public void setIssue_id(Long issue_id) {
            this.issue_id = issue_id;
        }

        public String getIssue_category() {
            return issue_category;
        }

        public void setIssue_category(String issue_category) {
            this.issue_category = issue_category;
        }

        public String getPatient_name() {
            return patient_name;
        }

        public void setPatient_name(String patient_name) {
            this.patient_name = patient_name;
        }

        @Override
        public String toString() {
            return "IssueListResponseData{" +
                    "patient_id=" + patient_id +
                    ", issue_id=" + issue_id +
                    ", issue_category='" + issue_category + '\'' +
                    ", patient_name='" + patient_name + '\'' +
                    '}';
        }
    }

    private List<IssueListItem> illness;

    public IssueListResponseData(List<IssueListItem> illness) {
        this.illness = illness;
    }

    public List<IssueListItem> getIllness() {
        return illness;
    }

    public void setIllness(List<IssueListItem> illness) {
        this.illness = illness;
    }

    @Override
    public String toString() {
        return "IssueListResponseData{" +
                "illness=" + illness +
                '}';
    }
}
