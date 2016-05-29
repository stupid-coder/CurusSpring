package com.curus.controllers;

import com.curus.dao.CurusDriver;
import com.curus.httpio.request.drug.DrugDirectionsGetRequest;
import com.curus.httpio.request.drug.DrugSearchRequest;
import com.curus.httpio.request.drug.PatientUseDrugListRequest;
import com.curus.httpio.request.drug.PatientUseDrugUpdateRequest;
import com.curus.httpio.response.ResponseBase;
import com.curus.services.drug.DrugDirectionGetService;
import com.curus.services.drug.DrugSearchService;
import com.curus.services.drug.PatientUseDrugListService;
import com.curus.services.drug.PatientUseDrugUpdateService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by stupid-coder on 5/29/16.
 */
@Controller
@RequestMapping(value="/drug")
public class DrugController  {

    private static CurusDriver driver = CurusDriver.getCurusDriver();

    @RequestMapping(value="/list",method= RequestMethod.POST, consumes = "application/json")
    public @ResponseBody
    ResponseBase list(@RequestBody PatientUseDrugListRequest request) {
        PatientUseDrugListService service = new PatientUseDrugListService(request,driver);
        return service.process();
    }

    @RequestMapping(value="/update",method= RequestMethod.POST, consumes = "application/json")
    public @ResponseBody ResponseBase update(@RequestBody PatientUseDrugUpdateRequest request) {
        PatientUseDrugUpdateService service = new PatientUseDrugUpdateService(request,driver);
        return service.process();
    }

    @RequestMapping(value="/search",method= RequestMethod.POST, consumes = "application/json")
    public @ResponseBody ResponseBase update(@RequestBody DrugSearchRequest request) {
        DrugSearchService service = new DrugSearchService(request,driver);
        return service.process();
    }

    @RequestMapping(value="/direction",method= RequestMethod.POST, consumes = "application/json")
    public @ResponseBody ResponseBase direction(@RequestBody DrugDirectionsGetRequest request) {
        DrugDirectionGetService service = new DrugDirectionGetService(request,driver);
        return service.process();
    }
}
