package com.curus.controllers;

import com.curus.dao.CurusDriver;
import com.curus.httpio.request.account.*;
import com.curus.httpio.request.account.passwd.AccountPasswdForgetRequest;
import com.curus.httpio.request.account.passwd.AccountPasswdModifyRequest;
import com.curus.httpio.response.ResponseBase;
import com.curus.services.account.*;
import com.curus.services.account.passwd.AccountPasswdForgetService;
import com.curus.services.account.passwd.AccountPasswdModifyService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by stupid-coder on 23/1/16.
 */

@Controller
@RequestMapping(value="/account")
public class AccountController {

    private static CurusDriver driver = CurusDriver.getCurusDriver();

    @RequestMapping(value="/register",method= RequestMethod.POST, consumes = "application/json")
    public @ResponseBody ResponseBase register(@RequestBody AccountRegisterRequest request) {
        AccountRegisterService service = new AccountRegisterService(request,driver);
        return service.process();
    }

    @RequestMapping(value="/login",method=RequestMethod.POST, consumes="application/json")
    public @ResponseBody ResponseBase login(@RequestBody AccountLoginRequest request) {
        AccountLoginService service = new AccountLoginService(request,driver);
        return service.process();
    }

    @RequestMapping(value="/update",method=RequestMethod.POST, consumes="application/json")
    public @ResponseBody ResponseBase login(@RequestBody AccountUpdateRequest request) {
        AccountUpdateService service = new AccountUpdateService(request,driver);
        return service.process();
    }

    @RequestMapping(value="/modify_phone",method=RequestMethod.POST, consumes="application/json")
    public @ResponseBody ResponseBase login(@RequestBody AccountModifyPhoneRequest request) {
        AccountModifyPhoneService service = new AccountModifyPhoneService(request,driver);
        return service.process();
    }

    @RequestMapping(value="/logout",method=RequestMethod.POST,consumes="application/json")
    public @ResponseBody ResponseBase logout(@RequestBody AccountLogoutRequest request) {
        AccountLogoutService service = new AccountLogoutService(request,driver);
        return service.process();
    }

    @RequestMapping(value="/detail",method=RequestMethod.POST,consumes="application/json")
    public @ResponseBody String logout(@RequestBody AccountDetailRequest request) {
        AccountDetailService service = new AccountDetailService(request,driver);
        return service.process().toString();
    }

    @RequestMapping(value="/idetail",method=RequestMethod.POST,consumes="application/json")
    public @ResponseBody String logout(@RequestBody AccountIDetailRequest request) {
        AccountIDetailService service = new AccountIDetailService(request,driver);
        return service.process().toString();
    }

    @RequestMapping(value="/passwd/modify",method=RequestMethod.POST,consumes="application/json")
    public @ResponseBody ResponseBase passwd_modify(@RequestBody AccountPasswdModifyRequest request) {
        AccountPasswdModifyService service = new AccountPasswdModifyService(request,driver);
        return service.process();
    }

    @RequestMapping(value="/passwd/forget",method=RequestMethod.POST,consumes="application/json")
    public @ResponseBody ResponseBase passwd_modify(@RequestBody AccountPasswdForgetRequest request) {
        AccountPasswdForgetService service = new AccountPasswdForgetService(request,driver);
        return service.process();
    }


}
