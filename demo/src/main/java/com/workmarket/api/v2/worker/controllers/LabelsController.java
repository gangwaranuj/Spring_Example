package com.workmarket.api.v2.worker.controllers;

import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.ApiBaseController;
import com.workmarket.api.v2.worker.model.AddLabelDTO;
import com.workmarket.api.v2.worker.service.XAssignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Api(tags = {"Assignments"})
@Controller("LabelsController")
@RequestMapping("/worker/v2/assignments")
public class LabelsController extends ApiBaseController {

	private static final org.apache.commons.logging.Log logger = org.apache.commons.logging.LogFactory.getLog(
					LabelsController.class);

	@Autowired private XAssignment xAssignment;

	@ApiOperation("Get all labels for an assignment")
	@RequestMapping(value = "/{workNumber}/labels", method = GET, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV2Response getLabels(@PathVariable("workNumber") final String workNumber) {

		return ApiV2Response.OK(xAssignment.getLabels(getCurrentUser(), workNumber));
	}

	@ApiOperation("Add a label to an assignment")
	@RequestMapping(value = "/{workNumber}/labels/{labelId}", method = POST, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV2Response postAddLabel(@PathVariable("workNumber") final String workNumber,
																@PathVariable("labelId") final Long labelId,
																@Valid @RequestBody final AddLabelDTO addLabelDTO,
																final BindingResult bindingResult) {

		return ApiV2Response.OK(xAssignment.addLabel(getCurrentUser(),
																								 workNumber,
																								 labelId,
																								 addLabelDTO,
																								 bindingResult));
	}
}
