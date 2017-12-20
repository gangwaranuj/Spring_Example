package com.workmarket.web.controllers;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/apidocs/**")
public class ApidocsController extends BaseController {
	@RequestMapping(method = RequestMethod.GET)
	public String index() {
		return "redirect:http://developer.workmarket.com";
	}
}
