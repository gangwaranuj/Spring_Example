package com.workmarket.api.v2.worker.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workmarket.api.BaseApiControllerTest;
import com.workmarket.api.v2.worker.fulfillment.FulfillmentPayloadDTO;
import com.workmarket.api.v2.worker.model.ScreeningWithPaymentDTO;
import com.workmarket.domains.authentication.services.SecurityContextFacade;
import com.workmarket.domains.payments.service.AccountRegisterService;
import com.workmarket.screening.model.Screening;
import com.workmarket.screening.model.ScreeningBuilder;
import com.workmarket.service.business.PricingService;
import com.workmarket.service.business.ScreeningService;
import com.workmarket.service.business.dto.PaymentDTO;
import com.workmarket.service.business.dto.PhoneNumberDTO;
import com.workmarket.service.business.dto.ScreeningDTO;
import com.workmarket.web.validators.PaymentValidator;
import com.workmarket.web.validators.ScreeningValidator;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class WorkerServicesControllerTest extends BaseApiControllerTest {
	private static final String DRUG_TEST_STATUS_URL = "/worker/v2/services/drug-test-status";
	private static final String DRUG_TEST_URL = "/worker/v2/services/drug-test";
	private static final String BACKGROUND_CHECK_STATUS_URL = "/worker/v2/services/background-check-status";
	private static final String BACKGROUND_CHECK_URL = "/worker/v2/services/background-check";

	@InjectMocks private WorkerServicesController controller = new WorkerServicesController();
	@Mock private SecurityContextFacade securityContextFacade;
	@Mock private ScreeningValidator screeningValidator = new ScreeningValidator();
	@Mock private PaymentValidator paymentValidator = new PaymentValidator();
	@Mock private PricingService pricingService;
	@Mock private ScreeningService screeningService;
	@Mock private AccountRegisterService accountRegisterServicePrefundImpl;
	private ObjectMapper jackson = new ObjectMapper();

	private FulfillmentPayloadDTO feedResponse;

	@Before
	public void setup() throws Exception {
		super.setup(controller);

		when(screeningValidator.supports(any(Class.class))).thenReturn(true);
		when(paymentValidator.supports(any(Class.class))).thenReturn(true);
		when(pricingService.findDrugTestPrice(anyLong())).thenReturn(new BigDecimal(10));
		when(pricingService.findBackgroundCheckPrice(anyLong(), anyString()))
			.thenReturn(new BigDecimal(10));
		when(accountRegisterServicePrefundImpl.calculateWithdrawableCashByCompany(anyLong()))
			.thenReturn(new BigDecimal(1000));

		try {
			when(screeningService.requestDrugTest(anyLong(), org.mockito.Matchers.isA(ScreeningDTO.class)))
				.thenReturn(successfulDrugTestScreeningResponse());

			when(screeningService.requestBackgroundCheck(anyLong(), org.mockito.Matchers.isA(ScreeningDTO.class)))
				.thenReturn(successfulBackgroundCheckScreeningResponse());

		} catch (Exception ex) {
		}
	}

	@Ignore("API endpoint not implemented")
	public void requestDrugTestStatus_shouldReturnValidResponse() throws Exception {
		requestDrugTestStatus().andExpect(status().isOk()).andExpect(jsonPath("$.meta.code", is(200)));
	}

	@Test
	public void requestDrugTest_withInvalidRequest_shouldReturnErrorResponse() throws Exception {
		final ScreeningWithPaymentDTO screeningWithPaymentDTO = createInvalidDrugScreenDTO();

		requestSterlingScreening(DRUG_TEST_URL, screeningWithPaymentDTO)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.meta.code", is(200)));
	}

	@Test
	public void requestDrugTest_withValidRequest_shouldReturnValidResponse() throws Exception {
		final ScreeningWithPaymentDTO screeningWithPaymentDTO = createValidDrugScreenDTO();

		requestSterlingScreening(DRUG_TEST_URL, screeningWithPaymentDTO)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.meta.code", is(200)));
	}

	@Ignore("API endpoint not implemented")
	@Test
	public void requestBackgroundCheckStatus_shouldReturnValidResponse() throws Exception {
		requestBackgroundCheckStatus().andExpect(status().isOk()).andExpect(jsonPath("$.meta.code", is(200)));
	}

	@Test
	public void requestBackgroundCheck_withInvalidRequest_shouldReturnErrorResponse() throws Exception {
		final ScreeningWithPaymentDTO screeningWithPaymentDTO = createInvalidBackgroundCheckDTO();

		requestSterlingScreening(BACKGROUND_CHECK_URL, screeningWithPaymentDTO)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.meta.code", is(200)));
	}

	@Test
	public void requestBackgroundCheck_withValidRequest_shouldReturnValidResponse() throws Exception {
		final ScreeningWithPaymentDTO screeningWithPaymentDTO = createValidBackgroundCheckDTO();

		requestSterlingScreening(BACKGROUND_CHECK_URL, screeningWithPaymentDTO)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.meta.code", is(200)));
	}

	private ResultActions requestDrugTestStatus() throws Exception {
		return requestScreeningStatus(DRUG_TEST_STATUS_URL);
	}

	private ResultActions requestBackgroundCheckStatus() throws Exception {
		return requestScreeningStatus(BACKGROUND_CHECK_STATUS_URL);
	}

	private ResultActions requestScreeningStatus(final String url) throws Exception {
		return mockMvc.perform(get(url));
	}

	private ResultActions requestSterlingScreening(
		final String url,
		final ScreeningWithPaymentDTO screeningWithPaymentDTO
	) throws Exception {
		final String screeningWithPaymentJSON = jackson.writeValueAsString(screeningWithPaymentDTO);
		return mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(screeningWithPaymentJSON));
	}

	private ScreeningWithPaymentDTO createInvalidDrugScreenDTO() {
		return createScreeningWithPaymentDTO(
			createInvalidScreeningDTO(com.workmarket.domains.model.screening.Screening.DRUG_TEST_TYPE),
			createValidAccountPaymentDTO()
		);
	}

	private ScreeningWithPaymentDTO createValidDrugScreenDTO() {
		return createScreeningWithPaymentDTO(
			createValidScreeningDTO(com.workmarket.domains.model.screening.Screening.DRUG_TEST_TYPE),
			createValidAccountPaymentDTO()
		);
	}

	private ScreeningWithPaymentDTO createInvalidBackgroundCheckDTO() {
		return createScreeningWithPaymentDTO(
			createInvalidScreeningDTO(com.workmarket.domains.model.screening.Screening.BACKGROUND_CHECK_TYPE),
			createValidAccountPaymentDTO()
		);
	}

	private ScreeningWithPaymentDTO createValidBackgroundCheckDTO() {
		return createScreeningWithPaymentDTO(
			createValidScreeningDTO(com.workmarket.domains.model.screening.Screening.BACKGROUND_CHECK_TYPE),
			createValidAccountPaymentDTO()
		);
	}

	private ScreeningWithPaymentDTO createScreeningWithPaymentDTO(
		final ScreeningDTO screeningDTO,
		final PaymentDTO paymentDTO
	) {
		final ScreeningWithPaymentDTO screeningWithPaymentDTO = new ScreeningWithPaymentDTO.Builder()
			.withScreening(screeningDTO)
			.withPayment(paymentDTO)
			.build();

		return screeningWithPaymentDTO;
	}

	private ScreeningDTO createValidScreeningDTO(final String screeningType) {
		final PhoneNumberDTO phoneNumberDTO = new PhoneNumberDTO();
		phoneNumberDTO.setPhone("888-555-1234");

		final ScreeningDTO screeningDTO = new ScreeningDTO();

		//screeningDTO.setScreeningId();
		//screeningDTO.setTitle();
		screeningDTO.setFirstName("Test");
		screeningDTO.setMiddleName("E");
		screeningDTO.setLastName("User");
		//screeningDTO.setMaidenName("");
		screeningDTO.setGender("male");
		screeningDTO.setPhone(phoneNumberDTO);
		//screeningDTO.setMobilePhone();
		screeningDTO.setBirthDay(1);
		screeningDTO.setBirthMonth(1);
		screeningDTO.setBirthYear(1900);
		//screeningDTO.setDateOfBirth("");
		screeningDTO.setWorkIdentificationNumber("151-11-1111");
		screeningDTO.setEmail("test.user@email.com");
		//screeningDTO.setReferenceId();
		screeningDTO.setScreeningType(screeningType);
		//screeningDTO.setVendorName();
		//screeningDTO.setAddressId();
		screeningDTO.setAddress1("1010 Digital Lane");
		screeningDTO.setAddress2("");
		screeningDTO.setCity("New York");
		screeningDTO.setState("NY");
		screeningDTO.setPostalCode("10001");
		screeningDTO.setCountry("USA");
		//screeningDTO.setAddressTypeCode();
		//screeningDTO.setLocationTypeId();
		//screeningDTO.setDressCodeId();
		//screeningDTO.setLatitude();
		//screeningDTO.setLongitude();

		return screeningDTO;
	}

	private ScreeningDTO createInvalidScreeningDTO(final String screeningType) {
		final ScreeningDTO screeningDTO = new ScreeningDTO();
		screeningDTO.setScreeningType(screeningType);

		return screeningDTO;
	}

	private PaymentDTO createValidAccountPaymentDTO() {
		final PaymentDTO paymentDTO = new PaymentDTO();
		paymentDTO.setPaymentType("account");
		paymentDTO.setAmount("100");

		return paymentDTO;
	}

	private PaymentDTO createInvalidAccountPaymentDTO() {
		final PaymentDTO paymentDTO = new PaymentDTO();
		paymentDTO.setPaymentType("account");
		paymentDTO.setAmount("100");

		return paymentDTO;
	}

	private PaymentDTO createValidCreditCardPaymentDTO() {
		final PaymentDTO paymentDTO = new PaymentDTO();

		/*
		paymentDTO.setPaymentType("cc");
		paymentDTO.setAmount(100);
		paymentDTO.setFirstName();
		//private String firstName;
		paymentDTO.setLastName();
		//private String lastName;
		paymentDTO.setCardType();
		//private String cardType; // Values: Visa, MasterCard, Discover, Amex
		paymentDTO.setCardNumber();
		//private String cardNumber;
		paymentDTO.setCardExpirationMonth();
		//private String cardExpirationMonth;
		paymentDTO.setCardExpirationYear();
		//private String cardExpirationYear;
		paymentDTO.setCardSecurityCode();
		//private String cardSecurityCode;
		paymentDTO.setNameOnCard();
		//private String nameOnCard;
		*/

		return paymentDTO;
	}

	private PaymentDTO createInvalidCreditCardPaymentDTO() {
		final PaymentDTO paymentDTO = new PaymentDTO();

		return paymentDTO;
	}

	private Screening successfulDrugTestScreeningResponse() {
		final ScreeningBuilder builder = Screening.builder();
		final Screening screening = builder.build();

		/*
		screening.set();
		//private User user;
		screening.set();
		//private ScreeningStatusType screeningStatusType;
		screening.set();
		//private Calendar requestDate;
		screening.set();
		//private Calendar responseDate;
		screening.set();
		//private String screeningId;
		*/

		return screening;
	}

	private Screening successfulBackgroundCheckScreeningResponse() {
		final ScreeningBuilder builder = Screening.builder();
		final Screening screening = builder.build();

		/*
		screening.set();
		//private User user;
		screening.set();
		//private ScreeningStatusType screeningStatusType;
		screening.set();
		//private Calendar requestDate;
		screening.set();
		//private Calendar responseDate;
		screening.set();
		//private String screeningId;
		*/

		return screening;
	}
}
