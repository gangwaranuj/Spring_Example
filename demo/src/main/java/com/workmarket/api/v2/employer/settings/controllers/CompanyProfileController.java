package com.workmarket.api.v2.employer.settings.controllers;

import com.workmarket.api.ApiBaseController;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.employer.settings.models.CompanyProfileDTO;
import com.workmarket.api.v2.employer.settings.services.CompanyProfileService;
import com.workmarket.api.v2.employer.settings.services.CompanyPublicProfileService;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.exception.HostServiceException;
import com.workmarket.service.exception.asset.AssetTransformationException;
import com.workmarket.service.exception.authentication.InvalidAclRoleException;
import com.workmarket.thrift.core.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;

@Api(tags = "Profile")
@Controller("companyProfileController")
@RequestMapping(value = {"/v2/employer/settings/profile", "/employer/v2/settings/profile"})
public class CompanyProfileController extends ApiBaseController {

	@Autowired private CompanyProfileService companyProfileService;
	@Autowired private CompanyPublicProfileService companyPublicProfileService;
	@Autowired private CompanyService companyService;

	@ApiOperation(value = "Create company profile")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@ResponseBody
	@RequestMapping(
		method = {POST},
		produces = APPLICATION_JSON_VALUE
	)
	@PreAuthorize("hasAnyRole('ACL_ADMIN','ROLE_WM_ADMIN','ROLE_WM_EMPLOYEE_MGMT')")
	public ApiV2Response<CompanyProfileDTO> postCreateCompanyProfile(@RequestBody CompanyProfileDTO builder)
		throws ValidationException, InvalidAclRoleException, HostServiceException, IOException, AssetTransformationException {
		CompanyProfileDTO companyProfileDTO = companyProfileService.saveOrUpdate(builder);
		return ApiV2Response.valueWithResult(companyProfileDTO);
	}

	@ApiOperation(value = "Update company profile")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@ResponseBody
	@RequestMapping(
			method = {PUT},
			produces = APPLICATION_JSON_VALUE
	)
	@PreAuthorize("hasAnyRole('ACL_ADMIN','ROLE_WM_ADMIN','ROLE_WM_EMPLOYEE_MGMT')")
	public ApiV2Response<CompanyProfileDTO> putUpdateCompanyProfile(@RequestBody CompanyProfileDTO builder)
			throws ValidationException, InvalidAclRoleException, HostServiceException, IOException, AssetTransformationException {
		CompanyProfileDTO companyProfileDTO = companyProfileService.saveOrUpdate(builder);
		return ApiV2Response.valueWithResult(companyProfileDTO);
	}

	@ApiOperation(value = "Get company profile for logged in user")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@ResponseBody
	@RequestMapping(
		method = GET,
		produces = APPLICATION_JSON_VALUE
	)
	public ApiV2Response<CompanyProfileDTO> getUserCompanyProfile() throws ValidationException, InvalidAclRoleException {
		return ApiV2Response.valueWithResult(companyProfileService.get());
	}

	@ApiOperation(value = "Get company profile")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@ResponseBody
	@RequestMapping(
		value = "/{id}",
		method = GET,
		produces = APPLICATION_JSON_VALUE
	)
	public ApiV2Response<CompanyProfileDTO> getCompanyProfile(@ApiParam(name = "id", required = true) @PathVariable("id") String id) throws ValidationException, InvalidAclRoleException {

		if (id != null && id.length() >= 36) {
			com.workmarket.domains.model.Company company = companyService.findByUUID(id);
			final String companyNumber = company.getCompanyNumber();
			return ApiV2Response.valueWithResult(companyProfileService.get(companyNumber));
		}
		else {
			return ApiV2Response.valueWithResult(companyProfileService.get(id));
		}
	}

	@ApiOperation(value = "Get public company profile")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@ResponseBody
	@RequestMapping(
		value = "/public/{id}",
		method = GET,
		produces = APPLICATION_JSON_VALUE
	)
	public ApiV2Response<CompanyProfileDTO> getPublicCompanyProfile(@ApiParam(name = "id", required = true) @PathVariable("id") String id) throws ValidationException, InvalidAclRoleException {

		if (id != null && id.length() >= 36) {
			com.workmarket.domains.model.Company company = companyService.findByUUID(id);
			final String companyNumber = company.getCompanyNumber();
			return ApiV2Response.valueWithResult(companyPublicProfileService.get(companyNumber));
		}
		else {
			return ApiV2Response.valueWithResult(companyPublicProfileService.get(id));
		}
	}

	@ApiOperation(value = "Follow company profile")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@ResponseBody
	@RequestMapping(
			value = "/{id}/follower",
			method = POST,
			produces = APPLICATION_JSON_VALUE
	)
	public ApiV2Response postFollowCompany(@ApiParam(name = "id", required = true) @PathVariable("id") String id) throws ValidationException, InvalidAclRoleException {

		if (id != null && id.length() >= 36) {
			com.workmarket.domains.model.Company company = companyService.findByUUID(id);
			final String companyNumber = company.getCompanyNumber();
			companyProfileService.follow(companyNumber);
			return ApiV2Response.OK();
		}

		else {
			companyProfileService.follow(id);
			return ApiV2Response.OK();
		}
	}

	@ApiOperation(value = "Unfollow company profile")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@ResponseBody
	@RequestMapping(
			value = "/{id}/follower",
			method = DELETE,
			produces = APPLICATION_JSON_VALUE
	)
	public ApiV2Response deleteFollowCompany(@ApiParam(name = "id", required = true) @PathVariable("id") String id) throws ValidationException, InvalidAclRoleException {

		if (id != null && id.length() >= 36) {
			com.workmarket.domains.model.Company company = companyService.findByUUID(id);
			final String companyNumber = company.getCompanyNumber();
			companyProfileService.unfollow(companyNumber);
			return ApiV2Response.OK();
		}

		else {
			companyProfileService.unfollow(id);
			return ApiV2Response.OK();
		}
	}
}
