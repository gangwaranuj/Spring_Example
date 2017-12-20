package com.workmarket.api.v2.employer.settings.controllers;

import com.workmarket.api.ApiBaseError;
import com.workmarket.api.v2.employer.settings.controllers.support.CreditCardPaymentMaker;
import com.workmarket.api.v2.employer.settings.models.CreditCardPaymentDTO;
import com.workmarket.api.v2.employer.settings.models.CreditCardPaymentResponseDTO;
import com.workmarket.api.v2.ApiV2BaseIT;
import com.workmarket.test.IntegrationTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MvcResult;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static com.workmarket.api.v2.employer.assignments.controllers.support.TypeReferences.errorType;
import static com.workmarket.api.v2.employer.assignments.controllers.support.TypeReferences.paymentResponseType;
import static com.workmarket.api.v2.employer.settings.controllers.support.CreditCardPaymentMaker.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class CreditCardPaymentControllerIT extends ApiV2BaseIT {

	@InjectMocks CreditCardPaymentController controller;

	private static final String ENDPOINT = "/employer/v2/settings/funds/credit_card";

	@Before
	public void setUp() throws Exception {
		login();
	}

	@Test
	public void addFundsWithNullAmount() throws Exception {
		CreditCardPaymentDTO paymentDTO = make(a(CreditCardPaymentMaker.DEFAULT_CREDIT_CARD, withNull(amount)));
		String paymentDTOJson = jackson.writeValueAsString(paymentDTO);

		MvcResult mvcResult = mockMvc.perform(doPost(ENDPOINT)
																									.content(paymentDTOJson))
						.andExpect(status().isBadRequest())
						.andReturn();

		ApiBaseError result = getFirstResult(mvcResult, errorType);
		expectApiErrorMessage(result,
													"amount",
													"There was a problem with amount you entered. Please enter a valid amount.");
	}

	@Test
	public void addFundsWithNegativeAmount() throws Exception {
		CreditCardPaymentDTO paymentDTO = make(a(CreditCardPaymentMaker.DEFAULT_CREDIT_CARD, with(amount, "-100.00")));
		String paymentDTOJson = jackson.writeValueAsString(paymentDTO);

		MvcResult mvcResult = mockMvc.perform(doPost(ENDPOINT)
																									.content(paymentDTOJson))
						.andExpect(status().isBadRequest())
						.andReturn();

		ApiBaseError result = getFirstResult(mvcResult, errorType);
		expectApiErrorMessage(result,
													"amount",
													"There was a problem with amount you entered. Please enter a valid amount.");
	}

	@Test
	public void addFundsWithEmptyCardType() throws Exception {
		CreditCardPaymentDTO paymentDTO = make(a(CreditCardPaymentMaker.DEFAULT_CREDIT_CARD, withNull(cardType)));
		String paymentDTOJson = jackson.writeValueAsString(paymentDTO);

		MvcResult mvcResult = mockMvc.perform(doPost(ENDPOINT)
																									.content(paymentDTOJson))
						.andExpect(status().isBadRequest())
						.andReturn();

		ApiBaseError result = getFirstResult(mvcResult, errorType);
		expectApiErrorMessage(result, "cardType", "Credit card type cannot be empty.");
	}

	@Test
	public void addFundsWithEmptyCardNumber() throws Exception {
		CreditCardPaymentDTO paymentDTO = make(a(CreditCardPaymentMaker.DEFAULT_CREDIT_CARD, withNull(cardNumber)));
		String paymentDTOJson = jackson.writeValueAsString(paymentDTO);

		MvcResult mvcResult = mockMvc.perform(doPost(ENDPOINT)
																									.content(paymentDTOJson))
						.andExpect(status().isBadRequest())
						.andReturn();

		ApiBaseError result = getFirstResult(mvcResult, errorType);
		expectApiErrorMessage(result, "cardNumber", "Credit card number cannot be empty.");
	}

	@Test
	public void addFundsWithEmptyExpirationMonth() throws Exception {
		CreditCardPaymentDTO paymentDTO = make(a(CreditCardPaymentMaker.DEFAULT_CREDIT_CARD, withNull(cardExpirationMonth)));
		String paymentDTOJson = jackson.writeValueAsString(paymentDTO);

		MvcResult mvcResult = mockMvc.perform(doPost(ENDPOINT)
																									.content(paymentDTOJson))
						.andExpect(status().isBadRequest())
						.andReturn();

		ApiBaseError result = getFirstResult(mvcResult, errorType);
		expectApiErrorMessage(result, "cardExpirationMonth", "Credit card expiration month cannot be empty.");
	}

	@Test
	public void addFundsWithEmptyExpirationYear() throws Exception {
		CreditCardPaymentDTO paymentDTO = make(a(CreditCardPaymentMaker.DEFAULT_CREDIT_CARD, withNull(cardExpirationYear)));
		String paymentDTOJson = jackson.writeValueAsString(paymentDTO);

		MvcResult mvcResult = mockMvc.perform(doPost(ENDPOINT)
																									.content(paymentDTOJson))
						.andExpect(status().isBadRequest())
						.andReturn();

		ApiBaseError result = getFirstResult(mvcResult, errorType);
		expectApiErrorMessage(result, "cardExpirationYear", "Credit card expiration year cannot be empty.");
	}

	@Test
	public void addFundsWithEmptySecurityCode() throws Exception {
		CreditCardPaymentDTO paymentDTO = make(a(CreditCardPaymentMaker.DEFAULT_CREDIT_CARD, withNull(cardSecurityCode)));
		String paymentDTOJson = jackson.writeValueAsString(paymentDTO);

		MvcResult mvcResult = mockMvc.perform(doPost(ENDPOINT)
																									.content(paymentDTOJson))
						.andExpect(status().isBadRequest())
						.andReturn();

		ApiBaseError result = getFirstResult(mvcResult, errorType);
		expectApiErrorMessage(result, "cardSecurityCode", "Security code cannot be empty.");
	}

	@Test
	public void addFundsWithEmptyNameOnCard() throws Exception {
		CreditCardPaymentDTO paymentDTO = make(a(CreditCardPaymentMaker.DEFAULT_CREDIT_CARD, withNull(nameOnCard)));
		String paymentDTOJson = jackson.writeValueAsString(paymentDTO);

		MvcResult mvcResult = mockMvc.perform(doPost(ENDPOINT)
																									.content(paymentDTOJson))
						.andExpect(status().isBadRequest())
						.andReturn();

		ApiBaseError result = getFirstResult(mvcResult, errorType);
		expectApiErrorMessage(result, "nameOnCard", "Name on card cannot be empty.");
	}

	@Test
	public void addFundsWithEmptyAddress1() throws Exception {
		CreditCardPaymentDTO paymentDTO = make(a(CreditCardPaymentMaker.CREDIT_CARD_WITH_EMPTY_ADDRESS1));
		String paymentDTOJson = jackson.writeValueAsString(paymentDTO);

		MvcResult mvcResult = mockMvc.perform(doPost(ENDPOINT)
																									.content(paymentDTOJson))
						.andExpect(status().isBadRequest())
						.andReturn();

		ApiBaseError result = getFirstResult(mvcResult, errorType);
		expectApiErrorMessage(result, "address1", "Address is a required field.");
	}

	@Test
	public void addFundsWithEmptyCity() throws Exception {
		CreditCardPaymentDTO paymentDTO = make(a(CreditCardPaymentMaker.CREDIT_CARD_WITH_EMPTY_CITY));
		String paymentDTOJson = jackson.writeValueAsString(paymentDTO);

		MvcResult mvcResult = mockMvc.perform(doPost(ENDPOINT)
																									.content(paymentDTOJson))
						.andExpect(status().isBadRequest())
						.andReturn();

		ApiBaseError result = getFirstResult(mvcResult, errorType);
		expectApiErrorMessage(result, "city", "This location does not have a valid city.");
	}

	@Ignore  // TODO API - let's test this, shall we ? :-P
	@Test
	public void addFundsSuccess() throws Exception {
		CreditCardPaymentDTO paymentDTO = make(a(CreditCardPaymentMaker.DEFAULT_CREDIT_CARD));
		String paymentDTOJson = jackson.writeValueAsString(paymentDTO);

		MvcResult mvcResult = mockMvc.perform(doPost(ENDPOINT)
																									.content(paymentDTOJson))
						.andExpect(status().isBadRequest())
						.andReturn();

		CreditCardPaymentResponseDTO result = getFirstResult(mvcResult, paymentResponseType);
		assertThat(result, hasProperty("approved", is(true)));
		assertThat(result, hasProperty("responseMessage", isEmptyOrNullString()));
		assertThat(result, hasProperty("creditCardTransactionId", is(notNullValue(Long.class))));
		assertThat(result, hasProperty("creditCardFeeTransactionId", is(notNullValue(Long.class))));
	}
}
