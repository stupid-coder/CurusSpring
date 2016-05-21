package com.curus.controllers;

import com.curus.dao.CurusDriver;
import com.curus.httpio.request.im.ImListRequest;
import com.curus.httpio.response.ResponseBase;
import com.curus.services.im.ImListService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by stupid-coder on 5/21/16.
 */

@Controller
@RequestMapping("/im")
public class ImController {

    private static CurusDriver driver = CurusDriver.getCurusDriver().getCurusDriver();

    @RequestMapping(value="/list", method= RequestMethod.POST, consumes="application/json")
    public @ResponseBody
    ResponseBase list(@RequestBody ImListRequest request) {
        ImListService service = new ImListService(request,driver);
        return service.process();
    }
}
