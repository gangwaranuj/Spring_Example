package com.workmarket.api.v2.controllers;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.ImmutableList;
import com.workmarket.api.ApiBaseController;
import com.workmarket.api.exceptions.ApiException;
import com.workmarket.api.exceptions.GenericApiException;
import com.workmarket.api.exceptions.NotFoundApiException;
import com.workmarket.api.parameter.EmptyResponse;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.model.ApiCreateCompanyRequestDTO;
import com.workmarket.api.v2.model.ApiCreateCompanyResponseDTO;
import com.workmarket.api.v2.model.validator.ApiCreateCompanyRequestDTOValidator;
import com.workmarket.common.metric.MetricRegistryFacade;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.domains.model.User;
import com.workmarket.service.business.RegistrationService;
import com.workmarket.service.business.SalesforceLeadService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.UserDTO;
import com.workmarket.service.featuretoggle.FeatureEntitlementService;
import com.workmarket.service.infra.EncryptionService;
import com.workmarket.service.infra.business.AuthenticationService;

import com.workmarket.service.locale.LocaleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Api(tags = {"Account"})
@Controller("accountControllerV2")
@RequestMapping
public class AccountController extends ApiBaseController {
	private static final Log logger = LogFactory.getLog(AccountController.class);
	private static final String MESSAGE_COULD_NOT_CREATE_USER = "We were unable to register your account";

	@Autowired private FeatureEntitlementService featureEntitlementService;
	@Autowired private AuthenticationService authn;
	@Autowired private UserService userService;
	@Autowired private RegistrationService registrationService;
	@Autowired private EncryptionService encryptionService;
	@Autowired private SalesforceLeadService salesforceLeadService;
	@Autowired private MetricRegistry registry;
	@Autowired private ApiCreateCompanyRequestDTOValidator apiCreateCompanyRequestDTOValidator;
	@Autowired private LocaleService localeService;

	private MetricRegistryFacade facade;
	private Meter resendEmailMeter;

	@PostConstruct
	public void init() {
		facade = new WMMetricRegistryFacade(registry, "publiccontroller");
		resendEmailMeter = facade.meter("resend-conf-email");
	}


  @RequestMapping(
    value = "/v2/confirm_account/{encryptedId}",
    method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  @ResponseBody
  @ApiOperation(value = "Confirm an email")
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Your account has been confirmed."),
    @ApiResponse(code = 400, message = "The user you are trying to confirm does not exist."),
    @ApiResponse(code = 403, message = "The user number you are trying to confirm is invalid."),
    @ApiResponse(code = 403, message = "The email address you are trying to confirm is no longer available.")})
	public ApiV2Response<EmptyResponse> getConfirmAccount(
		@PathVariable final String encryptedId
	) {
		String userNumber = null;
		Long userId = null;

		try {
			userId = encryptionService.decryptId(encryptedId);
			userNumber = userService.findUserNumber(userId);
		} catch (EncryptionOperationNotPossibleException e) {
			logger.error("Error decrypting key [" + encryptedId + "]", e);
		}

		if (null == userNumber) {
			throw new ApiException("user.account.confirm.error_user_not_found");
		}
		else if (StringUtils.isBlank(userNumber)) {
			throw new NotFoundApiException(messageHelper.getMessage("user.account.confirm.error_user_invalid"));
		}

		User user = registrationService.confirmAndApproveAccount(userId);
		if (user == null) {
			throw new NotFoundApiException(messageHelper.getMessage("user.account.confirm.error_email_not_found"));
		}

		authn.setCurrentUser(userNumber);

		return ApiV2Response.valueWithMessage(
			messageHelper.getMessage("user.account.confirm.success"),
			HttpStatus.OK);
	}


	@RequestMapping(
		value = "/v2/resend_confirmation_email/{userNumber}",
		method = RequestMethod.GET,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ApiOperation(value = "Resend a confirm email")
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "Your confirmation email has been sent."),
		@ApiResponse(code = 400, message = "User not found"),
		@ApiResponse(code = 500, message =
			"We encountered an error while sending you a confirmation email. Please try again.")})
	@ResponseBody
	public ApiV2Response<EmptyResponse> getResendConfirmationEmail(
		@PathVariable("userNumber") final String userNumber,
		@RequestParam(value = "resetPassword", defaultValue = "false", required = false) final boolean resetPassword) {
		resendEmailMeter.mark();

		final Long userId = userService.findUserId(userNumber);
		if (userId == null) {
			throw new ApiException("users.resend_confirmation_email.user_not_found");
		}

		try {
			if (resetPassword) {
				registrationService.sendRemindConfirmationWithPasswordResetEmail(userId);
			} else {
				registrationService.sendRemindConfirmationEmail(userId);
			}
		} catch (final Exception e) {
			logger.error("Error sending email confirmation", e);
			throw new RuntimeException(messageHelper.getMessage("user.confirmation.resend.failure"));
		}

		return ApiV2Response.valueWithMessage(
			messageHelper.getMessage("user.confirmation.resend.success"),
			HttpStatus.OK);
	}

	/**
	 * Create a new user and profile with a campaign id.
	 *
	 * @param encryptedId
	 * @param dto
	 * @param bindingResult
	 * @return
	 * @throws Exception
	 */
  @ApiOperation(value = "Signup a new Employer with a campaign id")
  @ApiResponses(value = {
  	@ApiResponse(code = 200, message = MESSAGE_OK),
		@ApiResponse(code = 400, message = MESSAGE_COULD_NOT_CREATE_USER)
  })
  @RequestMapping(value = {"/v2/employer/create-account/{encryptedId}"},
		method = POST,
		produces = APPLICATION_JSON_VALUE)
  @ResponseBody
  public ApiV2Response<ApiCreateCompanyResponseDTO> postSignUpEmployer(
  	@PathVariable final String encryptedId,
  	@RequestBody final ApiCreateCompanyRequestDTO dto,
		BindingResult bindingResult) throws Exception {
    apiCreateCompanyRequestDTOValidator.validate(dto, bindingResult);

		if (bindingResult.getErrorCount() > 0) {
			throw new BindException(bindingResult);
		}

		final User user;
		boolean isBuyer = true;
		try {
			UserDTO userDto = dto.convertToUserDTO();

			if (encryptedId != null) {
				Long campaignId = encryptionService.decryptId(encryptedId);
				userDto.setRecruitingCampaignId(campaignId);
				// Recruited companies should be set as sellers by default.
				isBuyer = false;
			}
			user = registrationService.registerNew(userDto,
				null,
				dto.getCompanyName(),
				dto.convertToAddressDTO(),
				dto.convertToProfileDTO(),
				isBuyer,
				true);

			if (user == null) {
				throw new GenericApiException(MESSAGE_COULD_NOT_CREATE_USER);
			}

			if (featureEntitlementService.hasFeatureToggle(user.getId(), "locale")) {
				localeService.setPreferredLocale(user.getUuid(), dto.getLocale());
			}
		} catch (Exception e) {
			if (e instanceof BindException) {
				throw e;
			}
			throw new GenericApiException(e.getMessage());
		}

		try {
			final String authToken = salesforceLeadService.authenticateToken();
			salesforceLeadService.generateBuyerLead(
				authToken,
				dto.getFirstName(),
				dto.getLastName(),
				dto.getCompanyName(),
				dto.getWorkPhone() != null ? dto.getWorkPhone().getPhone() : "",
				dto.getAddress() != null ? dto.getAddress().getCity() : "",
				dto.getAddress() != null ? dto.getAddress().getCountry() : "",
				dto.getAddress() != null ? dto.getAddress().getPostalCode() : "",
				dto.getAddress() != null ? dto.getAddress().getState() : "",
				dto.getAddress() != null ? dto.getAddress().getAddressLine1() : "",
				dto.getUserEmail(),
				user.getCompany().getId(),
				dto.getTitle(),
				dto.getFunction()
			);
		} catch (Exception e) {
			logger.error("Error sending lead to SalesForce", e);
		}

		return ApiV2Response.OK(ImmutableList.of(new ApiCreateCompanyResponseDTO(user)));
	}

	/**
	 * Create a new user and profile without a campaign id.
	 *
	 * @param dto
	 * @param bindingResult
	 * @return
	 * @throws Exception
	 */
	@ApiOperation(value = "Signup a new Employer without a campaign id")
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = MESSAGE_OK),
		@ApiResponse(code = 400, message = MESSAGE_COULD_NOT_CREATE_USER)
	})
	@RequestMapping(value = {"/v2/employer/create-account"},
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV2Response<ApiCreateCompanyResponseDTO> postSignUpEmployerWithoutCampaignId(
		@RequestBody final ApiCreateCompanyRequestDTO dto,
		BindingResult bindingResult) throws Exception {
  	return postSignUpEmployer(null, dto, bindingResult);
	}
}
