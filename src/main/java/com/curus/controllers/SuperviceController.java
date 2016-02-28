package com.curus.controllers;

import com.curus.dao.CurusDriver;
import com.curus.httpio.request.supervise.weight.SWeightAddRequest;
import com.curus.httpio.request.supervise.weight.SWeightPretestRequest;
import com.curus.httpio.response.ResponseBase;
import com.curus.services.supervise.weight.SWeightAddService;
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
@RequestMapping(value="/supervice")
public class SuperviceController {
    private static CurusDriver driver = CurusDriver.getCurusDriver();

    @RequestMapping(value="/weight/pretest",method= RequestMethod.POST, consumes="application/json")
    public @ResponseBody
    ResponseBase pretest(@RequestBody SWeightPretestRequest request) {
        SWeightPretestService service = new SWeightPretestService(request,driver);
        return service.process();
    }


    @RequestMapping(value="/weight/add",method= RequestMethod.POST, consumes = "application/json")
    public @ResponseBody
    ResponseBase add(@RequestBody SWeightAddRequest request) {
        SWeightAddService service = new SWeightAddService(request,driver);
        return service.process();
    }

}
