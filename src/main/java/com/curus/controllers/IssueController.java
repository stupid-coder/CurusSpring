package com.curus.controllers;

import com.curus.dao.CurusDriver;
import com.curus.httpio.request.issue.IssueListRequest;
import com.curus.httpio.response.ResponseBase;
import com.curus.services.issue.IssueListService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by stupid-coder on 28/1/16.
 */

@Controller
@RequestMapping(value="/issue")
public class IssueController {

    private static CurusDriver driver = CurusDriver.getCurusDriver();

    @RequestMapping(value="/preadd",method= RequestMethod.POST, consumes = "application/json")
    public @ResponseBody
    ResponseBase register(@RequestBody IssueListRequest request) {
        IssueListService service = new IssueListService(request,driver);
        return service.process();
    }

}
