package com.workmarket.api.v2.worker.controllers;

import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.ApiBaseController;
import com.workmarket.api.v2.worker.model.CompleteDTO;
import com.workmarket.api.v2.worker.service.XAssignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import javax.validation.ValidationException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Api(tags = {"Assignments"})
@Controller("CompleteController")
@RequestMapping("/worker/v2/assignments")
public class CompleteController extends ApiBaseController {

	private static final org.apache.commons.logging.Log logger = org.apache.commons.logging.LogFactory.getLog(
					CompleteController.class);

	@Autowired private XAssignment xAssignment;

	@ApiOperation("Complete an assignment")
	@RequestMapping(value = "/{workNumber}/complete", method = POST, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV2Response postCompleteAssignment(@PathVariable("workNumber") final String workNumber,
																@Valid @RequestBody final CompleteDTO completeDTO,
																//@RequestParam(value = "onBehalfOf", required = false) Long onBehalfOf,
																final BindingResult bindingResult) throws Exception {

		Long onBehalfOf = null;

		if(bindingResult.hasErrors()) {
			throw new BindException(bindingResult);
		}
		return ApiV2Response.OK(xAssignment.complete(getCurrentUser(),
																								 workNumber,
																								 completeDTO,
																								 onBehalfOf,
																								 bindingResult));
	}

	@ApiOperation("[DEPRECATED] Complete an assignment")
	@RequestMapping(value = "/complete/{workNumber}", method = POST, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV2Response postCompleteAssignmentDeprecated(@PathVariable("workNumber") final String workNumber,
																					@Valid @RequestBody final CompleteDTO completeDTO,
																					//@RequestParam(value = "onBehalfOf", required = false) Long onBehalfOf,
																					final BindingResult bindingResult) throws Exception {

		Long onBehalfOf = null;

		return ApiV2Response.OK(xAssignment.complete(getCurrentUser(),
																								 workNumber,
																								 completeDTO,
																								 onBehalfOf,
																								 bindingResult));
	}
}
