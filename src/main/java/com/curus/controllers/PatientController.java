package com.curus.controllers;

import com.curus.dao.CurusDriver;
import com.curus.httpio.request.patient.*;
import com.curus.httpio.response.ResponseBase;
import com.curus.httpio.response.patient.PatientListResponseData;
import com.curus.services.patient.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by stupid-coder on 25/1/16.
 */

@Controller
@RequestMapping(value="/patient")
public class PatientController {

    private static CurusDriver driver = CurusDriver.getCurusDriver();

    @RequestMapping(value="/preadd",method= RequestMethod.POST, consumes = "application/json")
    public @ResponseBody
    ResponseBase preadd(@RequestBody PatientPreAddRequest request) {
        PatientPreAddService service = new PatientPreAddService(request,driver);
        return service.process();
    }

    @RequestMapping(value="/add",method= RequestMethod.POST, consumes = "application/json")
    public @ResponseBody
    ResponseBase add(@RequestBody PatientAddRequest request) {
        PatientAddService service = new PatientAddService(request,driver);
        return service.process();
    }

    @RequestMapping(value="/list",method= RequestMethod.POST, consumes = "application/json")
    public @ResponseBody
    ResponseBase list(@RequestBody PatientListRequest request) {
        PatientListService service = new PatientListService(request,driver);
        return service.process();
    }

    @RequestMapping(value="/relieve",method= RequestMethod.POST, consumes = "application/json")
    public @ResponseBody
    ResponseBase relieve(@RequestBody PatientRelieveRequest request) {
        PatientRelieveService service = new PatientRelieveService(request,driver);
        return service.process();
    }

    @RequestMapping(value="/push_config",method= RequestMethod.POST, consumes = "application/json")
    public @ResponseBody
    ResponseBase push_config(@RequestBody PatientPushConfigRequest request) {
        PatientPushConfigService service = new PatientPushConfigService(request,driver);
        return service.process();
    }


    @RequestMapping(value="/info",method= RequestMethod.POST)
    public @ResponseBody
    ResponseBase info(@RequestBody PatientInfoRequest request) {
        PatientInfoService service = new PatientInfoService(request,driver);
        return service.process();
    }


    @RequestMapping(value="/agree",method= RequestMethod.GET)
    public @ResponseBody
    ResponseBase agree(@RequestParam Long aid, @RequestParam Long pid) {
        PatientAgreeService service = new PatientAgreeService(aid,pid,driver);
        return service.process();
    }


}
