package com.curus.services.patient;

import com.curus.dao.CurusDriver;
import com.curus.httpio.request.patient.PatientInfoRequest;
import com.curus.httpio.response.ErrorData;
import com.curus.httpio.response.patient.PatientInfoResponseData;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by stupid-coder on 7/5/16.
 */
public class PatientInfoService {

    private Log logger = LogFactory.getLog(PatientInfoService.class);

    private PatientInfoRequest request;
    private PatientInfoResponseData responseData;
    private CurusDriver driver;
    private ErrorData errorData;




}
