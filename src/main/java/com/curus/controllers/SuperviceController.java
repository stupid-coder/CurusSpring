package com.curus.controllers;

import com.curus.dao.CurusDriver;
import com.curus.httpio.request.supervise.smoke.SSmokeAddRequest;
import com.curus.httpio.request.supervise.smoke.SSmokeAddSuperviseRequest;
import com.curus.httpio.request.supervise.smoke.SSmokeEstimateSuperviseRequest;
import com.curus.httpio.request.supervise.smoke.SSmokeListRequest;
import com.curus.httpio.request.supervise.weight.SWeightAddRequest;
import com.curus.httpio.request.supervise.weight.SWeightEstimateRequest;
import com.curus.httpio.request.supervise.weight.SWeightLossTipsRequst;
import com.curus.httpio.request.supervise.weight.SWeightPretestRequest;
import com.curus.httpio.response.ResponseBase;
import com.curus.services.supervise.smoke.SSmokeAddService;
import com.curus.services.supervise.smoke.SSmokeAddSuperviseServise;
import com.curus.services.supervise.smoke.SSmokeEstimateSuperviseService;
import com.curus.services.supervise.smoke.SSmokeListService;
import com.curus.services.supervise.weight.SWeightAddService;
import com.curus.services.supervise.weight.SWeightEstimateService;
import com.curus.services.supervise.weight.SWeightLossTipsService;
import com.curus.services.supervise.weight.SWeightPretestService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by stupid-coder on 28/2/16.
 */

@Controller
@RequestMapping(value="/supervise")
public class SuperviceController {
    private static CurusDriver driver = CurusDriver.getCurusDriver();

    @RequestMapping(value="/weight/pretest",method= RequestMethod.POST, consumes="application/json")
    public @ResponseBody
    ResponseBase WeightPretest(@RequestBody SWeightPretestRequest request) {
        SWeightPretestService service = new SWeightPretestService(request,driver);
        return service.process();
    }


    @RequestMapping(value="/weight/add",method= RequestMethod.POST, consumes = "application/json")
    public @ResponseBody
    ResponseBase WeightAdd(@RequestBody SWeightAddRequest request) {
        SWeightAddService service = new SWeightAddService(request,driver);
        return service.process();
    }

    @RequestMapping(value="/weight/estimate",method= RequestMethod.POST, consumes = "application/json")
    public @ResponseBody
    ResponseBase WeightEstimate(@RequestBody SWeightEstimateRequest request) {
        SWeightEstimateService service = new SWeightEstimateService(request,driver);
        return service.process();
    }

    @RequestMapping(value="/weight/tips",method= RequestMethod.POST, consumes = "application/json")
    public @ResponseBody
    ResponseBase WeightTips(@RequestBody SWeightLossTipsRequst request) {
        SWeightLossTipsService service = new SWeightLossTipsService(request,driver);
        return service.process();
    }


    @RequestMapping(value="/smoke/estimate",method= RequestMethod.POST, consumes = "application/json")
    public @ResponseBody
    ResponseBase SmokeAdd(@RequestBody SSmokeEstimateSuperviseRequest request) {
        SSmokeEstimateSuperviseService service = new SSmokeEstimateSuperviseService(request,driver);
        return service.process();
    }

    @RequestMapping(value="/smoke/add",method= RequestMethod.POST, consumes = "application/json")
    public @ResponseBody
    ResponseBase SmokeList(@RequestBody SSmokeAddSuperviseRequest request) {
        SSmokeAddSuperviseServise service = new SSmokeAddSuperviseServise(request,driver);
        return service.process();
    }


}
