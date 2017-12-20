package com.workmarket.api.v2.employer.settings.controllers;

import com.workmarket.api.ApiBaseController;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.employer.settings.models.CompanyWorkersDTO;
import com.workmarket.api.v2.employer.settings.services.CompanyWorkersService;
import com.workmarket.common.core.RequestContext;
import com.workmarket.service.exception.authentication.InvalidAclRoleException;
import com.workmarket.service.web.WebRequestContext;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.thrift.core.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Company workers controller.
 */
@Api(tags = "Company Profile")
@Controller("companyWorkersController")
@RequestMapping(value = {"/v2/employer/settings/profile", "/employer/v2/settings/profile"})
public class CompanyWorkersController extends ApiBaseController {

	@Autowired
	private WebRequestContextProvider webRequestContextProvider;
	@Autowired
	private CompanyWorkersService companyWorkersService;

	@ApiOperation(value = "List company workers")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@ResponseBody
	@RequestMapping(
		value = "/{id}/workers",
		method = GET,
		produces = APPLICATION_JSON_VALUE
	)
	public ApiV2Response<CompanyWorkersDTO> getCompanyWorkers(
		@RequestParam(value = "offset", required = false) final Integer offset,
		@RequestParam(value = "limit", required = false) final Integer limit,
		@ApiParam(name = "id", required = true)
		@PathVariable("id") String id) throws ValidationException, InvalidAclRoleException {

		final WebRequestContext requestContext = webRequestContextProvider.getWebRequestContext();
		return ApiV2Response.valueWithResult(companyWorkersService.getWorkers(offset, limit, id));
	}

	@ResponseBody
	@RequestMapping(
		value = "/workers",
		method = GET,
		produces = APPLICATION_JSON_VALUE
	)
	public ApiV2Response<CompanyWorkersDTO> getCompanyWorkers(
		@RequestParam(value = "offset", required = false) final Integer offset,
		@RequestParam(value = "limit", required = false) final Integer limit) throws ValidationException,
		InvalidAclRoleException {

		final WebRequestContext requestContext = webRequestContextProvider.getWebRequestContext();
		return ApiV2Response.valueWithResult(companyWorkersService.getWorkers(offset, limit));
	}
}
