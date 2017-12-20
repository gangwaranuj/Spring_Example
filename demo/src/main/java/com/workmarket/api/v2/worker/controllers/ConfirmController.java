package com.workmarket.api.v2.worker.controllers;

import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.ApiBaseController;
import com.workmarket.api.v2.worker.service.XAssignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Api(tags = {"Assignments"})
@Controller("ConfirmController")
@RequestMapping("/worker/v2/assignments")
public class ConfirmController extends ApiBaseController {

	private static final org.apache.commons.logging.Log logger = org.apache.commons.logging.LogFactory.getLog(
					ConfirmController.class);

	@Autowired private XAssignment xAssignment;

	@ApiOperation("Confirm an assignment")
	@RequestMapping(value = "/{workNumber}/confirm", method = POST, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV2Response postConfirmAssignment(@PathVariable("workNumber") final String workNumber) {

		return ApiV2Response.OK(xAssignment.confirm(getCurrentUser(), workNumber));
	}

	@ApiOperation("[DEPRECATED] Confirm an assignment")
	@Deprecated
	@RequestMapping(value = "/confirm/{workNumber}", method = POST, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV2Response postConfirmAssignmentDeprecated(@PathVariable("workNumber") final String workNumber) {

		return ApiV2Response.OK(xAssignment.confirm(getCurrentUser(), workNumber));
	}
}
