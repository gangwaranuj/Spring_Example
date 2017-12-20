package com.workmarket.api.v2.employer.settings.controllers;

import com.google.common.collect.ImmutableList;

import com.workmarket.api.ApiBaseController;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.employer.settings.models.ACHBankAccountDTO;
import com.workmarket.api.v2.employer.settings.services.BankAccountService;
import com.workmarket.thrift.core.ValidationException;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Api(tags = "Accounts")
@Controller("employerBankAccountController")
@RequestMapping(value = {"/v2/employer/settings/funds/accounts", "/employer/v2/settings/funds/accounts"})
public class BankAccountController extends ApiBaseController {

	@Autowired BankAccountService bankAccountService;

	@ApiOperation(value = "Save bank account information")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@ResponseBody
	@RequestMapping(
		method = POST,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@PreAuthorize("hasAnyRole('PERMISSION_MANAGEBANK')")
	public ApiV2Response<ACHBankAccountDTO> postBankAccountInformation(@RequestBody ACHBankAccountDTO builder) throws ValidationException, BeansException {
		ACHBankAccountDTO bankAccountDTO = bankAccountService.save(builder);
		return ApiV2Response.valueWithResult(bankAccountDTO);
	}

	@ApiOperation(value = "Get list of admins")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@ResponseBody
	@RequestMapping(
		value = "/admins",
		method = GET,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@PreAuthorize("hasAnyRole('PERMISSION_MANAGEBANK')")
	public ApiV2Response<List<String>> getAdmins() {
		List<String> users = bankAccountService.findAllAdminUserNames();
		return ApiV2Response.valueWithResult(users);
	}
}
