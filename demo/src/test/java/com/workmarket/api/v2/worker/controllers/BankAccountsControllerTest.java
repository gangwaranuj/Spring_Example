package com.workmarket.api.v2.worker.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.workmarket.api.BaseApiControllerTest;
import com.workmarket.api.exceptions.BadRequestApiException;
import com.workmarket.api.exceptions.ResourceNotFoundException;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.model.ApiBankAccountDTO;
import com.workmarket.api.v2.model.ApiBankRoutingDTO;
import com.workmarket.api.v2.model.VerifyPaymentAccountDTO;
import com.workmarket.api.v2.validators.ApiBankAccountValidator;
import com.workmarket.api.v2.worker.fulfillment.BankAccountsFulfillmentProcessor;
import com.workmarket.api.v2.worker.fulfillment.FulfillmentPayloadDTO;
import com.workmarket.api.v2.worker.service.ApiBankRoutingSuggestionService;
import com.workmarket.api.v2.worker.service.BankAccountsService;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.authentication.services.SecurityContextFacade;
import com.workmarket.domains.banking.util.BankRoutingUtil;
import com.workmarket.domains.model.banking.AbstractBankAccount;
import com.workmarket.domains.model.banking.BankAccount;
import com.workmarket.domains.model.banking.BankAccountType;
import com.workmarket.domains.model.banking.PayPalAccount;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.payments.model.BankAccountDTO;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;
import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.text.SimpleDateFormat;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@org.junit.runner.RunWith(org.mockito.runners.MockitoJUnitRunner.class)
public class BankAccountsControllerTest extends BaseApiControllerTest {

	private static final TypeReference<ApiV2Response<ApiBankAccountDTO>> BANK_ACCOUNT_RESPONSE_TYPE = new TypeReference<ApiV2Response<ApiBankAccountDTO>>() {};
	private static final TypeReference<ApiV2Response<Void>> BANK_VERIFY_RESPONSE_TYPE = new TypeReference<ApiV2Response<Void>>() {};
	private static final TypeReference<ApiV2Response<ApiBankRoutingDTO>> BANK_ROUTING_RESPONSE_TYPE = new TypeReference<ApiV2Response<ApiBankRoutingDTO>>() {};

	@InjectMocks private BankAccountsController controller = new BankAccountsController();
	@Mock private ApiBankRoutingSuggestionService apiBankRoutingSuggestionService;
	@Mock private BankAccountsFulfillmentProcessor fulfillmentProcessor;
	@Mock private ApiBankAccountValidator bankAccountValidator;
	@Mock private SecurityContextFacade securityContextFacade;
	@Mock private BankAccountsService bankAccountsService;
	@Mock private MessageBundleHelper messageHelper;

	@Before
	public void setup() throws Exception {
		super.setup(controller);
		when(messageHelper.getMessage("api.worker.v2.unable_to_validate")).thenReturn(
						"We are unable to validate your request, please refers to results for more details.");
	}

	@Test
	public void testCreatePayPalAccount() throws Exception {
		final PayPalAccount bankAccount = Mockito.mock(PayPalAccount.class);
		final ExtendedUserDetails currentUser = createCurrentUser();
		final ApiBankAccountDTO dto = new ApiBankAccountDTO.Builder()
			.setType(ApiBankAccountDTO.Type.PPA)
			.setCountry(Country.CANADA)
			.setName("foo@bar.com")
			.build();

		when(bankAccount.getType())
			.thenReturn(AbstractBankAccount.PAYPAL);

		when(bankAccount.getCountry())
			.thenReturn(Country.CANADA_COUNTRY);

		when(bankAccount.getAccountDescription())
			.thenReturn("foo@bar.com");

		when(bankAccount.getConfirmedFlag())
			.thenReturn(true);

		when(bankAccount.getId())
			.thenReturn(123L);

		when(securityContextFacade.getCurrentUser())
			.thenReturn(currentUser);

		when(bankAccountsService.saveBankAccount(anyLong(), any(BankAccountDTO.class)))
			.thenReturn(bankAccount);

		final MvcResult result = sendSaveRequest(dto)
			.andExpect(status().isOk())
			.andReturn();

		final ApiV2Response<ApiBankAccountDTO> response = expectApiV2Response(result, BANK_ACCOUNT_RESPONSE_TYPE);

		assertEquals(1, response.getResults().size());
		expectStatusCode(HttpStatus.OK.value(), response.getMeta());

		final ApiBankAccountDTO resultPayload = response.getResults().get(0);

		assertTrue(resultPayload.getVerified());
		assertEquals(new Long(123L), resultPayload.getId());
		assertEquals("foo@bar.com", resultPayload.getName());
		assertEquals(Country.CANADA, resultPayload.getCountry());
		assertEquals(ApiBankAccountDTO.Type.PPA, resultPayload.getType());
	}

	@Test
	public void testCreateAchAccountCanada() throws Exception {
		final BankAccount entity = new BankAccount();
		final Calendar createdOn = Calendar.getInstance();
		final Calendar confirmedOn = Calendar.getInstance();
		final ExtendedUserDetails currentUser = createCurrentUser();
		final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		final ApiBankAccountDTO dto = new ApiBankAccountDTO.Builder()
			.setAccountType(ApiBankAccountDTO.AccountType.CHECKING)
			.setType(ApiBankAccountDTO.Type.ACH)
			.setAccountHolder("Julius Bike")
			.setTransitBranchNumber("12345")
			.setFinancialInstNumber("789")
			.setAccountNumber("87654321")
			.setCountry(Country.CANADA)
			.setBankName("CIBC")
			.build();

		createdOn.setTime(dateFormat.parse("2017-01-01 12:12:12"));
		confirmedOn.setTime(dateFormat.parse("2017-02-02 12:12:12"));

		entity.setId(20L);
		entity.setBankName("CIBC");
		entity.setConfirmedFlag(false);
		entity.setCreatedOn(createdOn);
		entity.setConfirmedOn(confirmedOn);
		entity.setAccountNumber("xxxxxxxxxx");
		entity.setNameOnAccount("Julius Bike");
		entity.setCountry(Country.CANADA_COUNTRY);
		entity.setBankAccountType(new BankAccountType(BankAccountType.CHECKING));
		entity.setRoutingNumber(BankRoutingUtil.buildRoutingNumber( "12345", "789"));

		when(securityContextFacade.getCurrentUser())
			.thenReturn(currentUser);

		when(bankAccountsService.saveBankAccount(anyLong(), any(BankAccountDTO.class)))
			.thenReturn(entity);

		final MvcResult result = sendSaveRequest(dto)
			.andExpect(status().isOk())
			.andReturn();

		final ApiV2Response<ApiBankAccountDTO> response = expectApiV2Response(result, BANK_ACCOUNT_RESPONSE_TYPE);

		assertEquals(1, response.getResults().size());
		expectStatusCode(HttpStatus.OK.value(), response.getMeta());

		final ApiBankAccountDTO resultPayload = response.getResults().get(0);

		assertNotNull(resultPayload.getId());
		assertFalse(resultPayload.getVerified());
		assertEquals("CIBC", resultPayload.getBankName());
		assertEquals("CIBC (xxxx)", resultPayload.getName());
		assertEquals(Country.CANADA, resultPayload.getCountry());
		assertEquals(ApiBankAccountDTO.Type.ACH, resultPayload.getType());
		assertEquals("789", resultPayload.getFinancialInstNumber());
		assertEquals("12345", resultPayload.getTransitBranchNumber());
		assertEquals("Julius Bike", resultPayload.getAccountHolder());
		assertEquals("XXXXXXXXxxxx", resultPayload.getAccountNumber());
		assertEquals(new Long(createdOn.getTimeInMillis()), resultPayload.getCreatedOn());
		assertEquals(new Long(confirmedOn.getTimeInMillis()), resultPayload.getConfirmedOn());
		assertEquals(ApiBankAccountDTO.AccountType.CHECKING, resultPayload.getAccountType());
	}

	@Test
	public void testCreateAccountError() throws Exception {
		final ExtendedUserDetails currentUser = createCurrentUser();
		final ApiBankAccountDTO dto = new ApiBankAccountDTO.Builder()
			.setType(ApiBankAccountDTO.Type.PPA)
			.setCountry(Country.CANADA)
			.setName("foo@bar.com")
			.build();

		final Throwable throwable = new RuntimeException("Some error");
		final BindingResult bindingResult = new BindException(dto, "dto");

		when(securityContextFacade.getCurrentUser())
			.thenReturn(currentUser);

		when(bankAccountsService.saveBankAccount(anyLong(), any(BankAccountDTO.class)))
			.thenThrow(throwable);

		try {
			controller.createBankAccount(dto, bindingResult);
			fail("Fail to throw exception");
		} catch (Exception e) {
			assertSame(throwable, e);
		}
	}

	@Test
	public void testSearchRoutingNumbers() throws Exception {
		final String search = "0110";
		final String country = Country.USA;

		final ApiBankRoutingDTO r1 = new ApiBankRoutingDTO.Builder()
			.setBankName("FEDERAL RESERVE BANK")
			.setAddress("1000 PEACHTREE ST N.E")
			.setRoutingNumber("011000015")
			.setPostalCode("30309")
			.setCity("ATLANTA")
			.setCountry("USA")
			.setState("GA")
			.setId(1L)
			.build();

		final ApiBankRoutingDTO r2 = new ApiBankRoutingDTO.Builder()
			.setBankName("STATE STREET BANK")
			.setAddress("8001 VILLA PARK DRIVE")
			.setRoutingNumber("011000138")
			.setPostalCode("23228")
			.setCity("HENRICO")
			.setCountry("USA")
			.setState("VA")
			.setId(2L)
			.build();

		when(apiBankRoutingSuggestionService.suggestBankRouting(eq(country), eq(search)))
			.thenReturn(Arrays.asList(r1, r2));

		final MvcResult result = sendGetRequest("/worker/v2/accounts/routing-numbers/{country}?search={search}", country, search)
			.andExpect(status().isOk())
			.andReturn();

		final ApiV2Response<ApiBankRoutingDTO> response = expectApiV2Response(result, BANK_ROUTING_RESPONSE_TYPE);

		assertEquals(2, response.getResults().size());
		expectStatusCode(HttpStatus.OK.value(), response.getMeta());

		final ApiBankRoutingDTO routing1 = response.getResults().get(0);
		final ApiBankRoutingDTO routing2 = response.getResults().get(1);

		assertEquals(new Long(1), routing1.getId());
		assertEquals("GA", routing1.getState());
		assertEquals("USA", routing1.getCountry());
		assertEquals("ATLANTA", routing1.getCity());
		assertEquals("30309", routing1.getPostalCode());
		assertEquals("011000015", routing1.getRoutingNumber());
		assertEquals("FEDERAL RESERVE BANK", routing1.getBankName());
		assertEquals("1000 PEACHTREE ST N.E", routing1.getAddress());

		assertEquals(new Long(2), routing2.getId());
		assertEquals("VA", routing2.getState());
		assertEquals("USA", routing2.getCountry());
		assertEquals("HENRICO", routing2.getCity());
		assertEquals("23228", routing2.getPostalCode());
		assertEquals("011000138", routing2.getRoutingNumber());
		assertEquals("STATE STREET BANK", routing2.getBankName());
		assertEquals("8001 VILLA PARK DRIVE", routing2.getAddress());
	}

	@Test
	public void getAccount_withValidRequest_returnsValidResponse() {

		final FulfillmentPayloadDTO processorResult = createBankAccountResponse();

		when(fulfillmentProcessor.getBankAccount(anyLong(), eq(user.getCompany().getId()))).thenReturn(processorResult);

		final ApiV2Response apiResponse = controller.getAccount(1L);

		verify(fulfillmentProcessor, times(1)).getBankAccount(1L, user.getCompany().getId());

		expectStatusCode(HttpStatus.OK.value(), apiResponse.getMeta());

		assertEquals(1, apiResponse.getResults().size());

		final ApiBankAccountDTO resultPayload = (ApiBankAccountDTO) apiResponse.getResults().get(0);

		assertEquals(new Long(6453), resultPayload.getId());
		assertEquals("My Bank (XXXX)", resultPayload.getName());
	}

	@Test
	public void getAccount_withInvalidRequest_returnsErrorResponse() {

		try {

			final ApiV2Response response = controller.getAccount(-5L);
			fail("Expected an IllegalArgumentException to be thrown.");
		}
		catch (final IllegalArgumentException iae) {

			assertEquals("A Non-valid account id was passed.", iae.getMessage());
		}
		catch (final Exception e) {

			fail("Expected an IllegalArgumentException to be thrown.");
		}
		finally {

			verify(fulfillmentProcessor, never()).getBankAccount(1L, user.getCompany().getId());
		}
	}

	@Test
	public void getAccounts_withValidRequest_returnsValidResponse() {

		final FulfillmentPayloadDTO processorResult = createBankAccountsResponse();

		when(fulfillmentProcessor.getBankAccounts((ExtendedUserDetails) anyObject())).thenReturn(processorResult);

		final ApiV2Response apiResponse = controller.getAccounts();

		assertEquals(HttpStatus.OK.value(), getMetaInt("code", apiResponse.getMeta()));

		assertEquals(2, apiResponse.getResults().size());
		assertNull(apiResponse.getPagination());

		final ApiBankAccountDTO resultPayload1 = (ApiBankAccountDTO) apiResponse.getResults().get(0);
		final ApiBankAccountDTO resultPayload2 = (ApiBankAccountDTO) apiResponse.getResults().get(1);

		assertEquals(new Long(6453), resultPayload1.getId());
		assertEquals("My Bank", resultPayload1.getBankName());
		assertEquals(Country.USA, resultPayload1.getCountry());
		assertEquals("Foo", resultPayload1.getAccountHolder());
		assertEquals("My Bank (XXXX)", resultPayload1.getName());
		assertEquals("011000138", resultPayload1.getRoutingNumber());
		assertEquals("0123456789", resultPayload1.getAccountNumber());
		assertEquals(ApiBankAccountDTO.Type.ACH, resultPayload1.getType());

		assertEquals(new Long(2), resultPayload2.getId());
		assertEquals("My CC", resultPayload2.getBankName());
		assertEquals(Country.USA, resultPayload2.getCountry());
		assertEquals("Bar", resultPayload2.getAccountHolder());
		assertEquals("My CC (XXXX)", resultPayload2.getName());
		assertEquals("022000138", resultPayload2.getRoutingNumber());
		assertEquals("987654321", resultPayload2.getAccountNumber());
		assertEquals(ApiBankAccountDTO.Type.ACH, resultPayload2.getType());
	}

	@Test
	public void deactivateAccounts_withValidRequest_returnsValidResponse() throws Exception {
		final PayPalAccount bankAccount = Mockito.mock(PayPalAccount.class);
		final ExtendedUserDetails currentUser = createCurrentUser();

		when(bankAccount.getType())
				.thenReturn(AbstractBankAccount.PAYPAL);

		when(bankAccount.getCountry())
				.thenReturn(Country.CANADA_COUNTRY);

		when(bankAccount.getAccountDescription())
				.thenReturn("foo@bar.com");

		when(bankAccount.getConfirmedFlag())
				.thenReturn(true);

		when(bankAccount.getId())
				.thenReturn(123L);

		when(securityContextFacade.getCurrentUser())
				.thenReturn(currentUser);

		when(bankAccountsService.deleteBankAccount(anyLong(), anyLong()))
				.thenReturn(bankAccount);

		final MvcResult result = sendDeactivateRequest(bankAccount.getId())
				.andExpect(status().isOk())
				.andReturn();

		final ApiV2Response<ApiBankAccountDTO> response = expectApiV2Response(result, BANK_ACCOUNT_RESPONSE_TYPE);

		assertEquals(1, response.getResults().size());
		expectStatusCode(HttpStatus.OK.value(), response.getMeta());

		final ApiBankAccountDTO resultPayload = response.getResults().get(0);

		assertTrue(resultPayload.getVerified());
		assertEquals(new Long(123L), resultPayload.getId());
		assertEquals("foo@bar.com", resultPayload.getName());
		assertEquals(Country.CANADA, resultPayload.getCountry());
		assertEquals(ApiBankAccountDTO.Type.PPA, resultPayload.getType());
	}

	@Test
	public void deactivateAccount_withInvalidAccountId_returnsErrorResponse() {

		try {

			final ApiV2Response response = controller.deactivateAccount(-5L);
			fail("Expected a ResourceNotFound exception");
		}
		catch (final ResourceNotFoundException ex) {

			assertEquals("Account not found.", ex.getMessage());
		}
		catch (final Exception e) {

			fail("Expected a ResourceNotFound to be thrown.");
		}
		finally {
			verify(bankAccountsService, never()).deleteBankAccount(anyLong(), anyLong());
		}
	}


	@Test
	public void deactivateAccount_withAccountNotFound_returnsErrorResponse() {
		when(bankAccountsService.deleteBankAccount(anyLong(), anyLong()))
				.thenThrow(new EntityNotFoundException());
		try {

			final ApiV2Response response = controller.deactivateAccount(34L);
			fail("Expected a ResourceNotFoundException to be thrown.");
		}
		catch (final ResourceNotFoundException ex) {

			assertEquals("Account not found.", ex.getMessage());
		}
		catch (final Exception e) {

			fail("Expected a ResourceNotFoundException to be thrown.");
		}
		finally {
			verify(bankAccountsService, atMost(1)).deleteBankAccount(anyLong(), anyLong());
		}
	}

	@Test
	public void verifyAccount_withValidRequest_returnValidResponse() throws Exception {
		BigDecimal amount1 = new BigDecimal(".02");
		BigDecimal amount2 = new BigDecimal(".03");

		try {
			when(bankAccountsService.confirmBankAccount(anyLong(), eq(amount1), eq(amount2), anyLong())).thenReturn(true);
		} catch (Exception e) {
			fail("Was not expecting exception");
		}

		final MvcResult result = sendVerifyRequest(10L, amount1, amount2)
				.andExpect(status().isOk())
				.andReturn();

		final ApiV2Response<Void> response = expectApiV2Response(result, BANK_VERIFY_RESPONSE_TYPE);
		assertEquals(HttpStatus.OK.value(), response.getMeta().getStatusCode().intValue());
	}

	@Test
	public void verifyAccount_withNotMatchingRequest_returnInValidResponse() throws Exception {
		BigDecimal amount1 = new BigDecimal(".02");
		BigDecimal amount2 = new BigDecimal(".03");

		try {
			when(bankAccountsService.confirmBankAccount(anyLong(), eq(amount1), eq(amount2), anyLong())).thenReturn(false);
		} catch (Exception e) {
			fail("Was not expecting exception");
		}
		VerifyPaymentAccountDTO dto = new VerifyPaymentAccountDTO(amount1.toString(), amount2.toString());

		try {
			controller.verifyAccount(10L, dto);
		}
		catch (BadRequestApiException e) {
			assertEquals(e.getMessage(), "Account cannot be verified.");
		}
		catch (final Exception e) {
			fail("Expected a BadRequestApiException to be thrown. Instead:" + e.toString());
		}
	}

	private FulfillmentPayloadDTO createBankAccountResponse() {

		final FulfillmentPayloadDTO response = new FulfillmentPayloadDTO();
		final ApiBankAccountDTO dto = new ApiBankAccountDTO.Builder()
			.setType(ApiBankAccountDTO.Type.ACH)
			.setName("My Bank (XXXX)")
			.setBankName("My Bank")
			.setCountry(Country.USA)
			.setId(6453L)
			.build();

		response.addResponseResult(dto);

		return response;
	}

	private FulfillmentPayloadDTO createBankAccountsResponse() {

		final FulfillmentPayloadDTO response = new FulfillmentPayloadDTO();
		final ApiBankAccountDTO dto1 = new ApiBankAccountDTO.Builder()
			.setType(ApiBankAccountDTO.Type.ACH)
			.setAccountNumber("0123456789")
			.setRoutingNumber("011000138")
			.setName("My Bank (XXXX)")
			.setCountry(Country.USA)
			.setAccountHolder("Foo")
			.setBankName("My Bank")
			.setId(6453L)
			.build();

		final ApiBankAccountDTO dto2 = new ApiBankAccountDTO.Builder()
			.setType(ApiBankAccountDTO.Type.ACH)
			.setAccountNumber("987654321")
			.setRoutingNumber("022000138")
			.setName("My CC (XXXX)")
			.setCountry(Country.USA)
			.setAccountHolder("Bar")
			.setBankName("My CC")
			.setId(2L)
			.build();

		response.addResponseResult(dto1);
		response.addResponseResult(dto2);

		return response;
	}

	private ResultActions sendSaveRequest(final ApiBankAccountDTO dto) throws Exception {
		final String bankAccountDTOJson = jackson.writeValueAsString(dto);
		final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
			.post("/worker/v2/accounts")
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.content(bankAccountDTOJson);

		return mockMvc.perform(builder);
	}

	private ResultActions sendGetRequest(final String path, final Object... urlVariables) throws Exception {
		final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(path, urlVariables)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON);

		return mockMvc.perform(builder);
	}

	private ResultActions sendDeactivateRequest(final Long accountId) throws Exception {
		final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
				.post("/worker/v2/accounts/{accountId}/deactivate", accountId)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);
		return mockMvc.perform(builder);
	}


	private ResultActions sendVerifyRequest(final Long accountId, final BigDecimal amount1, final BigDecimal amount2) throws Exception {
		VerifyPaymentAccountDTO dto = new VerifyPaymentAccountDTO(amount1.toString(), amount2.toString());
		final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
				.post("/worker/v2/accounts/{accountId}/verify", accountId)
				.content(jackson.writeValueAsString(dto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);
		return mockMvc.perform(builder);
	}

	private ExtendedUserDetails createCurrentUser() {
		final Collection<GrantedAuthority> authorities = Collections.<GrantedAuthority>emptyList();
		final ExtendedUserDetails currentUser = new ExtendedUserDetails("test", "test", authorities);

		currentUser.setId(123L);
		currentUser.setEmail("unittest@workmarket.com");

		return currentUser;
	}
}
