package com.workmarket.web.controllers.requirementsets;

import com.workmarket.service.business.dto.StateDTO;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.web.controllers.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping(value= "/states")
public class StatesController extends BaseController {
	@Autowired private InvariantDataService service;

	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<StateDTO> list() {
		return service.getStateDTOs();
	}
}
