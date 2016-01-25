package com.curus.controllers;

import com.curus.httpio.request.patient.PatientAddRequest;
import com.curus.httpio.request.patient.PatientPreAddRequest;
import com.curus.httpio.response.ResponseBase;
import com.curus.services.patient.PatientAddService;
import com.curus.services.patient.PatientPreAddService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by stupid-coder on 25/1/16.
 */

@Controller
@RequestMapping(value="/patient")
public class PatientController {

    @RequestMapping(value="/preadd",method= RequestMethod.POST, consumes = "application/json")
    public @ResponseBody
    ResponseBase register(@RequestBody PatientPreAddRequest request) {
        PatientPreAddService service = new PatientPreAddService(request);
        return service.process();
    }

    @RequestMapping(value="/add",method= RequestMethod.POST, consumes = "application/json")
    public @ResponseBody
    ResponseBase register(@RequestBody PatientAddRequest request) {
        PatientAddService service = new PatientAddService(request);
        return service.process();
    }

}
