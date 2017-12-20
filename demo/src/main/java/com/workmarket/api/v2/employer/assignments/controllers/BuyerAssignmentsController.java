package com.workmarket.api.v2.employer.assignments.controllers;

import com.workmarket.api.ApiBaseController;
import com.workmarket.api.exceptions.GenericApiException;
import com.workmarket.api.exceptions.ResourceNotFoundException;
import com.workmarket.api.exceptions.UnauthorizedException;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.employer.assignments.models.AcceptOnBehalfDTO;
import com.workmarket.web.exceptions.HttpException401;
import com.workmarket.web.exceptions.HttpException404;
import com.workmarket.web.exceptions.ValidationException;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.WorkDetailsControllerHelperService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(tags = {"Assignments"})
@Controller
@RequestMapping(value = {"/v2/employer/assignments", "/v2/assignments"})
public class BuyerAssignmentsController extends ApiBaseController {
	@Autowired WorkDetailsControllerHelperService workDetailsControllerHelperService;
	@Autowired @Qualifier("acceptOnBehalfDTOValidator") Validator validator;

	@InitBinder
	protected void initBinder(final WebDataBinder binder) {
		binder.setValidator(validator);
	}

	@ApiOperation(value = "Accept work on behalf of the given worker")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value = "/{workNumber}/accept_work_on_behalf_of",
									method = RequestMethod.POST,
									produces = MediaType.APPLICATION_JSON_VALUE)
	public
	@ResponseBody
	ApiV2Response postAcceptWorkOnBehalfOfWorker(@ApiParam(name = "workNumber", required = true) @PathVariable(value = "workNumber") String workNumber,
																		 @Valid @RequestBody AcceptOnBehalfDTO dto,
																		 HttpServletRequest req,
																		 HttpServletResponse res,
																		 BindingResult result) throws Exception {

		if (result.hasErrors()) {
			throw new ValidationException(result.getAllErrors());
		}

		try {
			AjaxResponseBuilder response = workDetailsControllerHelperService.acceptWorkOnBehalf(workNumber,
																																													 dto.getNote(),
																																													 dto.getUserNumber(),
																																													 req,
																																													 null,
																																													 getCurrentUser());
			if (!response.isSuccessful()) {
				throw new GenericApiException("Unable to accept work on behalf", response.getMessages());
			}
		}
		catch (HttpException404 ex) {
			throw new ResourceNotFoundException(ex.getMessage());
		}
		catch (HttpException401 ex) {
			throw new UnauthorizedException(ex.getMessage());
		}

		return ApiV2Response.OK();
	}
}
