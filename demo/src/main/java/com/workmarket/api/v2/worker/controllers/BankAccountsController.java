package com.workmarket.api.v2.worker.controllers;

import com.workmarket.api.ApiJSONPayloadMap;
import com.workmarket.api.exceptions.BadRequestApiException;
import com.workmarket.api.exceptions.ResourceNotFoundException;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.ApiBaseController;
import com.workmarket.api.v2.model.ApiBankAccountDTO;
import com.workmarket.api.v2.model.ApiBankRoutingDTO;
import com.workmarket.api.v2.model.VerifyPaymentAccountDTO;
import com.workmarket.api.v2.validators.ApiBankAccountValidator;
import com.workmarket.api.v2.worker.fulfillment.BankAccountsFulfillmentProcessor;
import com.workmarket.api.v2.worker.fulfillment.FulfillmentPayloadDTO;
import com.workmarket.api.v2.worker.service.BankAccountsService;
import com.workmarket.api.v2.worker.service.ApiBankRoutingSuggestionService;
import com.workmarket.domains.model.banking.AbstractBankAccount;
import com.workmarket.domains.payments.model.BankAccountDTO;
import com.workmarket.service.exception.AccountConfirmationAttemptsExceededException;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Api(tags = {"Accounts"})
@RequestMapping("/worker/v2/accounts")
@Controller(value = "bankAccountsController")
public class BankAccountsController extends ApiBaseController {

	private static final Log logger = LogFactory.getLog(BankAccountsController.class);

	@Autowired private BankAccountsService bankAccountsService;

	@Autowired private ApiBankAccountValidator bankAccountValidator;

	@Autowired private ApiBankRoutingSuggestionService apiBankRoutingSuggestionService;

	@Autowired private BankAccountsFulfillmentProcessor bankAccountsFulfillmentProcessor;

	@ResponseBody
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@ApiOperation(value = "Create a payment account for the logged in user.")
	@RequestMapping(method = POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("(!principal.userFundsAccessBlocked) AND hasAnyRole('PERMISSION_MANAGEBANK')")
	public ApiV2Response<ApiBankAccountDTO> createBankAccount(@RequestBody final ApiBankAccountDTO dto, final BindingResult bindingResult) throws Exception {
		bankAccountValidator.validate(dto, bindingResult);

		if (bindingResult.hasErrors()) {
			throw new BindException(bindingResult);
		}

		final Long userId = getCurrentUser().getId();
		final BankAccountDTO bankAccount = dto.toBankAccountDTO();

		final AbstractBankAccount entity = bankAccountsService.saveBankAccount(userId, bankAccount);
		final ApiBankAccountDTO result = new ApiBankAccountDTO.Builder(entity).build();

		return ApiV2Response.valueWithResult(result);
	}

	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@ApiOperation(value = "List all payment account for the logged in user.")
	@RequestMapping(method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("(!principal.userFundsAccessBlocked) AND hasAnyRole('PERMISSION_MANAGEBANK')")
	@ResponseBody
	public ApiV2Response<FulfillmentPayloadDTO> getAccounts() {

		final FulfillmentPayloadDTO results = bankAccountsFulfillmentProcessor.getBankAccounts(getCurrentUser());

		final ApiJSONPayloadMap metadataBuilder = new ApiJSONPayloadMap();
		if (StringUtils.isNotBlank(results.getMessage())) {
			metadataBuilder.put(METADATA_MESSAGE_KEY, results.getMessage());
		}

		return new ApiV2Response(metadataBuilder, results.getPayload(), null);
	}

	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@ApiOperation(value = "Get a payment account for the logged in user.")
	@RequestMapping(value = "/{accountId}", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("(!principal.userFundsAccessBlocked) AND hasAnyRole('PERMISSION_MANAGEBANK')")
	@ResponseBody
	public ApiV2Response<FulfillmentPayloadDTO> getAccount(@PathVariable("accountId") final Long accountId) {

		if (accountId <= 0) {
			throw new IllegalArgumentException("A Non-valid account id was passed.");
		}

		final FulfillmentPayloadDTO results = bankAccountsFulfillmentProcessor.getBankAccount(accountId, getCurrentUser().getCompanyId());

		final ApiJSONPayloadMap metadataBuilder = new ApiJSONPayloadMap();

		if (StringUtils.isNotBlank(results.getMessage())) {
			metadataBuilder.put(METADATA_MESSAGE_KEY, results.getMessage());
		}

		return new ApiV2Response(metadataBuilder, results.getPayload(), null);
	}

	@ResponseBody
	@ApiOperation(value = "Search ACH routing numbers.")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(method = GET, value = "/routing-numbers/{country}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ApiV2Response<ApiBankRoutingDTO> searchRoutingNumbers(
		@PathVariable("country") final String country,
		@RequestParam(name = "search") final String search) {
		final List<ApiBankRoutingDTO> list = apiBankRoutingSuggestionService.suggestBankRouting(country, search);
		final ApiV2Response<ApiBankRoutingDTO> response = ApiV2Response.OK(list);

		return response;
	}

	@ResponseBody
	@ApiOperation(value = "Deactivate a payment account for the logged in user.")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(method = POST, value = "/{accountId}/deactivate", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("(!principal.userFundsAccessBlocked) AND hasAnyRole('PERMISSION_MANAGEBANK')")
	public ApiV2Response<ApiBankAccountDTO> deactivateAccount(@PathVariable("accountId") final Long accountId) throws Exception {

		if (accountId <= 0) {
			throw new ResourceNotFoundException("Account not found.");
		}

		try {
			final AbstractBankAccount account = bankAccountsService.deleteBankAccount(accountId, getCurrentUser().getCompanyId());
			final ApiBankAccountDTO result = new ApiBankAccountDTO.Builder(account).build();

			return ApiV2Response.valueWithResult(result);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Account not found.");
		}
	}

	@ResponseBody
	@ApiOperation(value = "Verify a payment account for the logged in user.")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(method = POST, value = "/{accountId}/verify", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("(!principal.userFundsAccessBlocked) AND hasAnyRole('PERMISSION_MANAGEBANK')")
	public ApiV2Response verifyAccount(@PathVariable("accountId") final Long accountId, @RequestBody final VerifyPaymentAccountDTO dto) throws Exception {
		if (accountId <= 0) {
			throw new ResourceNotFoundException("Account not found.");
		}
		BigDecimal amount1 = new BigDecimal(dto.getAmount1());
		BigDecimal amount2 = new BigDecimal(dto.getAmount2());
		try {
			boolean confirmed = bankAccountsService.confirmBankAccount(accountId, amount1, amount2, getCurrentUser().getCompanyId());
			if (confirmed) {
				return ApiV2Response.OK();
			}
		} catch (AccountConfirmationAttemptsExceededException ex) {
			throw new BadRequestApiException("Account confirmation attempts exceeded.");
		}
		throw new BadRequestApiException("Account cannot be verified.");
	}
}
