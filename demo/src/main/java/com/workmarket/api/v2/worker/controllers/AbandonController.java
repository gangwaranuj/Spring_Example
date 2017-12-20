package com.workmarket.api.v2.worker.controllers;

import com.workmarket.api.ApiBaseController;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.worker.model.AbandonAssignmentDTO;
import com.workmarket.api.v2.worker.model.validator.AbandonAssignmentDTOValidator;
import com.workmarket.api.v2.worker.service.XAssignment;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Created by michaelrothbaum on 7/24/17.
 */
@Api(tags = {"Assignments"})
@Controller("AbandonController")
@RequestMapping({"/worker/v2/assignments", "/v2/worker/assignments"})
public class AbandonController extends ApiBaseController {

	@Autowired
	private XAssignment xAssignment;
	@Autowired
	private AbandonAssignmentDTOValidator abandonAssignmentDTOValidator;

	@ApiOperation("Abandon an assignment")
	@RequestMapping(value = "/{workNumber}/abandon", method = POST, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV2Response postAbandonAssignment(
			@PathVariable("workNumber") final String workNumber,
			@Valid @RequestBody final AbandonAssignmentDTO dto,
			final BindingResult bindingResult) throws Exception {

		abandonAssignmentDTOValidator.validate(dto.getMessage(), bindingResult);

		if (bindingResult.hasErrors()) {
			throw new BindException(bindingResult);
		}

		return ApiV2Response.OK(xAssignment.abandonAssignment(getCurrentUser(), workNumber, dto));
	}
}
