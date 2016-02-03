package com.curus.controllers;

import com.curus.dao.CurusDriver;
import com.curus.httpio.request.quota.QuotaAddRequest;
import com.curus.httpio.response.ResponseBase;
import com.curus.services.quota.QuotaAddService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by stupid-coder on 3/2/16.
 */

@Controller
@RequestMapping(value="/quota")
public class QuotaController {

    private static CurusDriver driver = CurusDriver.getCurusDriver();

    @RequestMapping(value="/add",method= RequestMethod.POST, consumes="application/json")
    public @ResponseBody
    ResponseBase add(@RequestBody QuotaAddRequest request) {
        QuotaAddService service = new QuotaAddService(request,driver);
        return service.process();
    }
}
