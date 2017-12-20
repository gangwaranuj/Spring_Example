package com.workmarket.web.controllers.requirementsets;

import com.workmarket.service.business.WeekdaySerializerService;
import com.workmarket.service.business.WeekdayService;
import com.workmarket.web.controllers.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value= "/weekdays")
public class WeekdaysController extends BaseController {
	@Autowired private WeekdayService service;
	@Autowired private WeekdaySerializerService jsonService;

	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	String list() {
		return jsonService.toJson(service.findAll());
	}
}
