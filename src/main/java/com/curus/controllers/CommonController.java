package com.curus.controllers;


import com.curus.dao.CurusDriver;
import com.curus.httpio.request.common.SendCodeRequest;
import com.curus.httpio.response.ResponseBase;
import com.curus.services.common.SendCodeService;
import com.curus.utils.SpringContextUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by stupid-coder on 23/1/16.
 */

@Controller
@RequestMapping("/common")
public class CommonController  {

    // mysql driver
    private static CurusDriver driver = CurusDriver.getCurusDriver();

    @RequestMapping(value="/send_code", method = RequestMethod.POST, consumes="application/json")
    public @ResponseBody ResponseBase send_code(@RequestBody SendCodeRequest request) {
        SendCodeService service = new SendCodeService(request);
        return service.process();
    }

}
