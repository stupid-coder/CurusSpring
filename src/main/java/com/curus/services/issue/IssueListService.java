package com.curus.services.issue;

import com.curus.dao.CurusDriver;
import com.curus.httpio.request.issue.IssueListRequest;
import com.curus.httpio.response.ErrorData;
import com.curus.httpio.response.ResponseBase;
import com.curus.httpio.response.issue.IssueListResponseData;
import com.curus.model.database.PatientIssue;
import com.curus.utils.LogUtils;
import com.curus.utils.TypeUtils;
import com.curus.utils.constant.ErrorConst;
import com.curus.utils.constant.StatusConst;
import com.curus.utils.validate.ValueValidate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stupid-coder on 28/1/16.
 */
public class IssueListService {

    private Log logger = LogFactory.getLog(IssueListService.class);

    private IssueListRequest request;
    private IssueListResponseData responseData;
    private CurusDriver driver;
    private ErrorData errorData;

    public IssueListService(IssueListRequest request, CurusDriver driver) {
        this.request = request;
        this.driver = driver;
        this.responseData = null;
        this.errorData = null;
    }

    private ErrorData validate() {
        if ( (errorData = ValueValidate.valueExistValidate(request.getToken(),"token")) != null) {
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (errorData = ValueValidate.valueExistValidate(request.getPatient_id(),"patient_id")) !=null) {
            logger.warn(LogUtils.Msg(errorData,request));
        }
        return errorData;
    }

    private ErrorData issueList() {
        List<IssueListResponseData.IssueListItem> issueItemList = new ArrayList<IssueListResponseData.IssueListItem>();
        List<PatientIssue> patientIssueList;
        if ( (patientIssueList = driver.patientIssueDao.selectAll(TypeUtils.getWhereHashMap("patient_id", request.getPatient_id()))) == null) {
            errorData = new ErrorData(ErrorConst.IDX_PATIENTNOTEXIST_ERROR);
            logger.warn(LogUtils.Msg(errorData,request));
        } else {
            for ( PatientIssue patientIssut : patientIssueList) {
            }
        }
        return errorData;
    }

    public ResponseBase process() {
        if ( validate() == null && issueList() == null) {
            logger.info(LogUtils.Msg("Success to List Issue",request,responseData));
            return new ResponseBase(StatusConst.OK,responseData);
        } else return new ResponseBase(StatusConst.ERROR,errorData);
    }


}
