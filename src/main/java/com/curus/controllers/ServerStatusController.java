package com.curus.controllers;

import com.curus.utils.constant.CommonConst;
import com.curus.utils.constant.MessageConst;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import javax.servlet.http.HttpServletRequest;


@Controller
@RequestMapping("/")
public class ServerStatusController {
	@RequestMapping(method = RequestMethod.GET)
	public String status(HttpServletRequest request,ModelMap model) throws Exception {
		model.addAttribute("message", String.format("IP:%s,PORT:%d",request.getLocalAddr(),request.getServerPort()));
		CommonConst.SYSIP = request.getLocalAddr();
		CommonConst.SYSPORT = request.getLocalPort();
		MessageConst.ADD_PATIENT_AGREE_LINK = String.format("{\"link\":\"http://%s:%d/patient/agree?pid=%%d&aid=%%d\"}",CommonConst.SYSIP,CommonConst.SYSPORT);
		return "server-status";
	}
}