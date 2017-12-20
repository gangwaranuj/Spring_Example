package com.workmarket.api.v2.worker.controllers;

import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.ApiBaseController;
import com.workmarket.api.v2.worker.model.SaveCustomFieldsDTO;
import com.workmarket.api.v2.worker.service.XAssignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Api(tags = {"Assignments"})
@Controller("CustomFieldsController")
@RequestMapping("/worker/v2/assignments")
public class CustomFieldsController extends ApiBaseController {

	private static final org.apache.commons.logging.Log logger = org.apache.commons.logging.LogFactory.getLog(
					CustomFieldsController.class);

	@Autowired private XAssignment xAssignment;

	@ApiOperation("Get custom fields for an assignment")
	@RequestMapping(value = "/{workNumber}/customfields", method = GET, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV2Response getCustomFields(@PathVariable("workNumber") final String workNumber) {

		return ApiV2Response.OK(xAssignment.getCustomFields(getCurrentUser(), workNumber));
	}

	@ApiOperation("Save custom fields")
	@RequestMapping(value = "/{workNumber}/customfields", method = POST, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV2Response postSaveCustomFields(@PathVariable("workNumber") final String workNumber,
																				@Valid @RequestBody final SaveCustomFieldsDTO saveCustomFieldsDTO,
																				@RequestParam(value = "onComplete",
																														required = false,
																														defaultValue = "false") final Boolean onComplete,
																				final BindingResult bindingResult) throws Exception {

		if(bindingResult.hasErrors()) {
			throw new BindException(bindingResult);
		}

		return ApiV2Response.OK(xAssignment.saveCustomFields(getCurrentUser(),
																												 workNumber,
																												 saveCustomFieldsDTO,
																												 onComplete,
																												 bindingResult));
	}
}
