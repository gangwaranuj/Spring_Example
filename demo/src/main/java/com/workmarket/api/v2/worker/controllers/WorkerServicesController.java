package com.workmarket.api.v2.worker.controllers;

import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.ApiBaseController;
import com.workmarket.api.exceptions.MessageSourceApiException;
import com.workmarket.api.v2.worker.model.ScreeningWithPaymentDTO;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.screening.*;
import com.workmarket.domains.payments.service.AccountRegisterService;
import com.workmarket.service.business.PricingService;
import com.workmarket.service.business.ScreeningService;
import com.workmarket.service.business.dto.PaymentDTO;
import com.workmarket.service.business.dto.ScreeningDTO;
import com.workmarket.service.exception.account.InsufficientFundsException;
import com.workmarket.utility.DateUtilities;
import com.workmarket.web.validators.PaymentValidator;
import com.workmarket.web.validators.ScreeningValidator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Api(tags = {"worker services", "worker", "services"})
@RequestMapping("/worker/v2/services")
@Controller(value = "workerServiceController")
public class WorkerServicesController extends ApiBaseController {

	private static final org.apache.commons.logging.Log logger = org.apache.commons.logging.LogFactory.getLog(
					WorkerServicesController.class);

	@Autowired private ScreeningValidator screeningValidator;
	@Autowired private PaymentValidator paymentValidator;
	@Autowired private PricingService pricingService;
	@Autowired private ScreeningService screeningService;
	@Autowired @Qualifier("accountRegisterServicePrefundImpl")
	private AccountRegisterService accountRegisterServicePrefundImpl;

	@ApiOperation("[NOOP]")
	@RequestMapping(method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV2Response getWorkerServices(final HttpServletRequest request) {

		return ApiV2Response.OK();
	}

	@ApiOperation("[NOOP]")
	@RequestMapping(value = "/drug-test-ok", method = POST, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV2Response postDrugTestOk(@Valid @RequestBody final ScreeningWithPaymentDTO screeningWithPaymentDTO,
																				 final BindingResult bindingResult) {

		return ApiV2Response.OK();
	}

	@ApiOperation("Request the status of a drug test")
	@RequestMapping(value = "/drug-test", method = GET, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV2Response getDrugTestStatus() throws Exception {

		final ExtendedUserDetails userDetails = getCurrentUser();

		final DrugTest screening = (DrugTest) ScreeningObjectConverter.convertScreeningResponseToMonolith(screeningService.findMostRecentDrugTest(
						userDetails.getId()));

		final Map<String, Object> map = new HashMap<>();

		if (screening == null) {

			map.put("purchase", true);
		}
		else {

			final String status = screening.getScreeningStatusType().getCode();

			map.put("purchase", (ScreeningStatusType.ERROR.equals(status) || ScreeningStatusType.PASSED.equals(status)));

			if (DateUtilities.getDaysBetween(screening.getResponseDate(),
																			 Calendar.getInstance()) > (Constants.STANDARD_CALENDAR_YEAR_DAYS)) {

				map.put("old", true);
			}

			final List<com.workmarket.screening.model.Screening> screenings = screeningService.findDrugTestsByUser(userDetails
																																																										 .getId());

			final List<Map<String, Object>> drugTests = new ArrayList<>();

			for (final com.workmarket.screening.model.Screening s : screenings) {

				DrugTest drugTest = (DrugTest) ScreeningObjectConverter.convertScreeningResponseToMonolith(s);

				Map<String, Object> _map = new HashMap<>();
				_map.put("screeningId", drugTest.getScreeningId());
				_map.put("status", drugTest.getScreeningStatusType().getCode());
				if (drugTest.getRequestDate() != null) {
					_map.put("requestDate", drugTest.getRequestDate().getTimeInMillis());
				}
				if (drugTest.getResponseDate() != null) {
					_map.put("responseDate", drugTest.getResponseDate().getTimeInMillis());
				}

				drugTests.add(_map);
			}

			map.put("previous", drugTests);
			map.put("status", status);
		}

		return ApiV2Response.OK(Arrays.asList(map));
	}

	@ApiOperation("Request a drug test")
	@RequestMapping(value = "/drug-test", method = POST, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV2Response postRequestDrugTest(@Valid @RequestBody final ScreeningWithPaymentDTO screeningWithPaymentDTO,
																			 final BindingResult bindingResult) throws Exception {

		final ExtendedUserDetails userDetails = getCurrentUser();

		final ScreeningDTO screeningDTO = screeningWithPaymentDTO.getScreening();
		final PaymentDTO paymentDTO = screeningWithPaymentDTO.getPayment();

		// Is this necessary??
		if (StringUtils.isBlank(screeningDTO.getCountry())) {

			if (!StringUtils.isBlank(userDetails.getCountry())) {
				screeningDTO.setCountry(userDetails.getCountry());
			}
			else {
				screeningDTO.setCountry(Country.USA);
			}
		}

		screeningDTO.setScreeningType(Screening.DRUG_TEST_TYPE);

		try {

			bindingResult.pushNestedPath("screening");

			ValidationUtils.invokeValidator(screeningValidator, screeningDTO, bindingResult);
		}
		finally {
			bindingResult.popNestedPath();
		}

		if (paymentDTO.isCreditCard()) {

			try {

				bindingResult.pushNestedPath("payment");

				ValidationUtils.invokeValidator(paymentValidator, paymentDTO, bindingResult);
			}

			finally {
				bindingResult.popNestedPath();
			}
		}

		if (bindingResult.hasErrors()) {

			screeningDTO.setWorkIdentificationNumber(""); // hide SSN

			throw new BindException(bindingResult);
		}

		final BigDecimal screeningCost = pricingService.findDrugTestPrice(userDetails.getCompanyId());

		if (paymentDTO.isCreditCard()) {

			paymentDTO.setAmount(screeningCost.toString());

			screeningService.requestDrugTest(userDetails.getId(), screeningDTO, paymentDTO);
		}
		else {

			try {

				screeningService.requestDrugTest(userDetails.getId(), screeningDTO);
			}
			catch (final InsufficientFundsException ex) {

				throw new MessageSourceApiException("insufficient_funds");
			}
		}

		return ApiV2Response.OK();
	}

	@ApiOperation("Request status of a background check")
	@RequestMapping(value = "/background-check", method = GET, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV2Response getBackgroundCheckStatus() throws Exception {

		ExtendedUserDetails userDetails = getCurrentUser();

		BackgroundCheck screening = (BackgroundCheck) ScreeningObjectConverter.convertScreeningResponseToMonolith(
						screeningService.findMostRecentBackgroundCheck(userDetails.getId()));

		final Map<String, Object> map = new HashMap<>();

		//map.put("accountSummary", accountRegisterServicePrefundImpl.getAccountRegisterSummaryFields(getCurrentUser().getCompanyId()));

		if (screening == null) {

			map.put("purchase", true);
		}
		else {

			final String status = screening.getScreeningStatusType().getCode();

			map.put("purchase", ScreeningStatusType.BACKGROUND_CHECK_PURCHASE_STATUS_TYPES.contains(status));

			if (DateUtilities.getDaysBetween(screening.getResponseDate(),
																			 Calendar.getInstance()) > Constants.STANDARD_CALENDAR_YEAR_DAYS) {

				map.put("old", true);
			}

			final List<com.workmarket.screening.model.Screening> screenings = screeningService.findBackgroundChecksByUser(
							userDetails.getId());

			final List<Map<String, Object>> backgroundChecks = new ArrayList<>();

			for (final com.workmarket.screening.model.Screening s : screenings) {

				BackgroundCheck backgroundCheck = (BackgroundCheck) ScreeningObjectConverter.convertScreeningResponseToMonolith(
								s);

				Map<String, Object> _map = new HashMap<>();
				_map.put("screeningId", backgroundCheck.getScreeningId());
				_map.put("status", backgroundCheck.getScreeningStatusType().getCode());
				if (backgroundCheck.getRequestDate() != null) {
					_map.put("requestDate", backgroundCheck.getRequestDate().getTimeInMillis());
				}
				if (backgroundCheck.getResponseDate() != null) {
					_map.put("responseDate", backgroundCheck.getResponseDate().getTimeInMillis());
				}

				backgroundChecks.add(_map);
			}

			map.put("previous", backgroundChecks);
			map.put("status", status);
		}

		return ApiV2Response.OK(Arrays.asList(map));
	}

	@ApiOperation("Request a background check")
	@RequestMapping(value = "/background-check", method = POST, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV2Response postBackgroundCheckRequest(@Valid @RequestBody final ScreeningWithPaymentDTO screeningWithPaymentDTO,
																							BindingResult bindingResult) throws Exception {

		final ExtendedUserDetails userDetails = getCurrentUser();

		final ScreeningDTO screeningDTO = screeningWithPaymentDTO.getScreening();
		final PaymentDTO paymentDTO = screeningWithPaymentDTO.getPayment();

		screeningDTO.setScreeningType(Screening.BACKGROUND_CHECK_TYPE);

		try {

			bindingResult.pushNestedPath("screening");

			ValidationUtils.invokeValidator(screeningValidator, screeningDTO, bindingResult);
		}
		finally {
			bindingResult.popNestedPath();
		}

		if (paymentDTO.isCreditCard()) {

			try {

				bindingResult.pushNestedPath("payment");

				ValidationUtils.invokeValidator(paymentValidator, paymentDTO, bindingResult);
			}

			finally {
				bindingResult.popNestedPath();
			}
		}

		if (bindingResult.hasErrors()) {

			screeningDTO.setWorkIdentificationNumber(""); // hide SSN

			throw new BindException(bindingResult);
		}

		// Is this necessary??
		if (StringUtils.isBlank(screeningDTO.getCountry())) {

			if (!StringUtils.isBlank(userDetails.getCountry())) {
				screeningDTO.setCountry(userDetails.getCountry());
			}
			else {
				screeningDTO.setCountry(Country.USA);
			}
		}

		final BigDecimal screeningCost = pricingService.findBackgroundCheckPrice(userDetails.getCompanyId(),
																																						 screeningDTO.getCountry());

		final BigDecimal withdrawableAmount = accountRegisterServicePrefundImpl.calculateWithdrawableCashByCompany(
						userDetails.getCompanyId());

		if (paymentDTO.isCreditCard()) {

			paymentDTO.setAmount(screeningCost.toString());

			screeningService.requestBackgroundCheck(userDetails.getId(), screeningDTO, paymentDTO);
		}
		else {

			try {

				if (withdrawableAmount.compareTo(screeningCost) < 0) {

					throw new InsufficientFundsException();
				}

				screeningService.requestBackgroundCheck(userDetails.getId(), screeningDTO);
			}
			catch (final InsufficientFundsException ex) {

				throw new MessageSourceApiException("insufficient_funds");
			}
		}

		return ApiV2Response.OK();
	}
}
