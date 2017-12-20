package com.workmarket.api.v2.employer.settings.controllers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.workmarket.api.ApiBaseController;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.employer.settings.models.CreateUserDTO;
import com.workmarket.api.v2.employer.settings.models.UserDTO;
import com.workmarket.api.v2.employer.settings.services.UserSettingService;
import com.workmarket.service.business.UserService;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.web.forms.user.ReassignUserForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Api(tags = "User Settings")
@Controller("userSettingController")
@RequestMapping("/employer/v2/settings/users")
public class UserSettingController extends ApiBaseController {

	@Autowired UserSettingService userSettingService;
	@Autowired UserService userService;

	@ApiOperation(value = "Get user")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@PreAuthorize("hasAnyRole('ACL_ADMIN','ROLE_WM_ADMIN','ROLE_WM_EMPLOYEE_MGMT', 'ROLE_SUPERUSER')")
	@ResponseBody
	@RequestMapping(
		value = {"/{userNumber}"},
		method = GET,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	public ApiV2Response<UserDTO> getUser(@ApiParam(name = "userNumber", required = true) @PathVariable String userNumber) throws Exception {
		UserDTO userDTO = userSettingService.get(userNumber);
		return ApiV2Response.valueWithResult(userDTO);
	}

	@ApiOperation(value = "Get all users")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@ResponseBody
	@RequestMapping(
		method = GET,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	public ApiV2Response<Map> getUsers(@RequestParam String companyId, @RequestParam String[] fields) throws Exception {
		ImmutableList<Map> projectedAllActiveCompanyUsers = userService.getProjectedAllActiveCompanyUsers(companyId, fields);
		return ApiV2Response.valueWithResults(projectedAllActiveCompanyUsers);
	}

	@ApiOperation(value = "Save user")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@PreAuthorize("hasAnyRole('ACL_ADMIN','ROLE_WM_ADMIN','ROLE_WM_EMPLOYEE_MGMT','ROLE_SUPERUSER')")
	@ResponseBody
	@RequestMapping(
		method = {POST},
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	public ApiV2Response<UserDTO> postCreateUser(@RequestBody CreateUserDTO builder) throws Exception {
		UserDTO userDTO = userSettingService.create(builder);
		return ApiV2Response.valueWithResult(userDTO);
	}

	@ApiOperation(value = "Update user")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@PreAuthorize("hasAnyRole('ACL_ADMIN','ROLE_WM_ADMIN','ROLE_WM_EMPLOYEE_MGMT','ROLE_SUPERUSER')")
	@ResponseBody
	@RequestMapping(
		value = {"/{userNumber}"},
		method = {POST},
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	public ApiV2Response<UserDTO> postUpdateUser(@ApiParam(name = "userNumber", required = true) @PathVariable String userNumber, @RequestBody UserDTO.Builder builder) throws Exception {
		UserDTO userDTO = userSettingService.update(userNumber, builder.build());
		return ApiV2Response.valueWithResult(userDTO);
	}

	@ApiOperation(value = "Verify user")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@PreAuthorize("hasAnyRole('ACL_ADMIN','ROLE_WM_ADMIN','ROLE_WM_EMPLOYEE_MGMT','ROLE_SUPERUSER')")
	@ResponseBody
	@RequestMapping(
		value = {"/{userNumber}/verification"},
		method = GET,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	public ApiV2Response<Map<String, String>> getVerifyUser(@ApiParam(name = "userNumber", required = true) @PathVariable String userNumber) throws Exception {
		Map<String, String> map = ImmutableMap.of(
			"isLastDispatcher", String.valueOf(userService.isLastDispatcher(userNumber)),
			"isLastAdmin", String.valueOf(userService.isLastAdmin(userNumber))
		);
		ImmutableList<Map<String, String>> result = ImmutableList.of(map);
		return ApiV2Response.valueWithResults(result);
	}

	@ApiOperation(value = "Deactivate user")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@PreAuthorize("hasAnyRole('ACL_ADMIN','ROLE_WM_ADMIN','ROLE_WM_EMPLOYEE_MGMT','ROLE_SUPERUSER')")
	@ResponseBody
	@RequestMapping(
		value = {"/{userNumber}/deactivation"},
		method = PUT,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	public ApiV2Response<UserDTO> putDeactivateUser(final @RequestBody ReassignUserForm reassignUserForm) throws ValidationException {
		final UserDTO userDTO = userSettingService.deactivate(reassignUserForm);
		return ApiV2Response.valueWithResult(userDTO);
	}
}
