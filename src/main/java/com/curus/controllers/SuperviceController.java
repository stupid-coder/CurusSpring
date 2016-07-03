package com.curus.controllers;

import com.alibaba.fastjson.JSONObject;
import com.curus.dao.CurusDriver;
import com.curus.httpio.request.supervise.SuperviseGetRequest;
import com.curus.httpio.request.supervise.SuperviseListRequest;
import com.curus.httpio.request.supervise.bdpressure.SBdPressureAddSuperviseRequest;
import com.curus.httpio.request.supervise.bdpressure.SBdPressureEstimateSuperviseRequest;
import com.curus.httpio.request.supervise.bdpressure.SBdPressureNonmedRequest;
import com.curus.httpio.request.supervise.bdsugar.SBdSugarEstimateRequest;
import com.curus.httpio.request.supervise.bdsugar.SBdSugarNonmedRequest;
import com.curus.httpio.request.supervise.smoke.SSmokeAddRequest;
import com.curus.httpio.request.supervise.smoke.SSmokeAddSuperviseRequest;
import com.curus.httpio.request.supervise.smoke.SSmokeEstimateSuperviseRequest;
import com.curus.httpio.request.supervise.smoke.SSmokeListRequest;
import com.curus.httpio.request.supervise.weight.SWeightAddRequest;
import com.curus.httpio.request.supervise.weight.SWeightEstimateRequest;
import com.curus.httpio.request.supervise.weight.SWeightLossTipsRequst;
import com.curus.httpio.request.supervise.weight.SWeightPretestRequest;
import com.curus.httpio.response.ResponseBase;
import com.curus.services.supervise.SuperviseGetService;
import com.curus.services.supervise.SuperviseListService;
import com.curus.services.supervise.bdpressure.SBdPressureAddSuperviseService;
import com.curus.services.supervise.bdpressure.SBdPressureEstimateSuperviseService;
import com.curus.services.supervise.bdpressure.SBdPressureNonmedService;
import com.curus.services.supervise.bdsugar.SBdSugarAddService;
import com.curus.services.supervise.bdsugar.SBdSugarEstimateService;
import com.curus.services.supervise.bdsugar.SBdSugarNonmedService;
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

    @RequestMapping(value="/list",method=RequestMethod.POST, consumes="application/json")
    public @ResponseBody
    ResponseBase List(@RequestBody SuperviseListRequest request) {
        SuperviseListService service = new SuperviseListService(request,driver);
        return service.process();
    }

    @RequestMapping(value="/get",method=RequestMethod.POST, consumes="application/json")
    public @ResponseBody
    ResponseBase Get(@RequestBody SuperviseGetRequest request) {
        SuperviseGetService service = new SuperviseGetService(request,driver);
        return service.process();
    }

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
    ResponseBase SmokeEstimate(@RequestBody SSmokeEstimateSuperviseRequest request) {
        SSmokeEstimateSuperviseService service = new SSmokeEstimateSuperviseService(request,driver);
        return service.process();
    }

    @RequestMapping(value="/smoke/add",method= RequestMethod.POST, consumes = "application/json")
    public @ResponseBody
    ResponseBase SmokeList(@RequestBody SSmokeAddSuperviseRequest request) {
        SSmokeAddSuperviseServise service = new SSmokeAddSuperviseServise(request,driver);
        return service.process();
    }

    @RequestMapping(value="/bdpressure/add",method= RequestMethod.POST, consumes = "application/json")
    public @ResponseBody
    ResponseBase BdPressureAdd(@RequestBody SBdPressureAddSuperviseRequest request) {
        SBdPressureAddSuperviseService service = new SBdPressureAddSuperviseService(request,driver);
        return service.process();
    }

    @RequestMapping(value="/bdpressure/estimate",method= RequestMethod.POST, consumes = "application/json")
    public @ResponseBody
    ResponseBase BdPressureAdd(@RequestBody SBdPressureEstimateSuperviseRequest request) {
        SBdPressureEstimateSuperviseService service = new SBdPressureEstimateSuperviseService(request,driver);
        return service.process();
    }

    @RequestMapping(value="/bdpressure/nonmed",method= RequestMethod.POST, consumes = "application/json")
    public @ResponseBody
    ResponseBase BdPressureNonmed(@RequestBody SBdPressureNonmedRequest request) {
        SBdPressureNonmedService service = new SBdPressureNonmedService(request,driver);
        return service.process();
    }

    @RequestMapping(value="/bdsugar/nonmed",method=RequestMethod.POST, consumes = "application/json")
    public @ResponseBody
    ResponseBase BdSugarNonmed(@RequestBody SBdSugarNonmedRequest request) {
        SBdSugarNonmedService service = new SBdSugarNonmedService(request,driver);
        return service.process();
    }

    @RequestMapping(value="/bdsugar/estimate",method=RequestMethod.POST, consumes = "application/json")
    public @ResponseBody
    ResponseBase BdSugarEstimate(@RequestBody SBdSugarEstimateRequest request) {
        SBdSugarEstimateService service = new SBdSugarEstimateService(request,driver);
        return service.process();
    }

    @RequestMapping(value="/bdsugar/add",method=RequestMethod.POST, consumes = "application/json")
    public @ResponseBody
    ResponseBase BdSugarAdd(@RequestBody JSONObject request) {
        SBdSugarAddService service = new SBdSugarAddService(request,driver);
        return service.process();
    }




}
