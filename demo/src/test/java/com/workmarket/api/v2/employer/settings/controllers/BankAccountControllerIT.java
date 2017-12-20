package com.workmarket.api.v2.employer.settings.controllers;

import com.workmarket.api.ApiBaseError;
import com.workmarket.api.v2.employer.settings.controllers.support.BankAccountMaker;
import com.workmarket.api.v2.employer.settings.models.ACHBankAccountDTO;
import com.workmarket.api.v2.ApiV2BaseIT;
import com.workmarket.api.v2.worker.model.Error;
import com.workmarket.domains.model.banking.BankAccount;
import com.workmarket.test.IntegrationTest;
import com.workmarket.utility.StringUtilities;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.MakeItEasy.with;
import static com.natpryce.makeiteasy.MakeItEasy.withNull;
import static com.workmarket.api.v2.employer.assignments.controllers.support.TypeReferences.bankAccountType;
import static com.workmarket.api.v2.employer.assignments.controllers.support.TypeReferences.errorType;
import static com.workmarket.api.v2.employer.assignments.controllers.support.TypeReferences.listType;
import static com.workmarket.api.v2.employer.settings.controllers.support.BankAccountMaker.accountNumber;
import static com.workmarket.api.v2.employer.settings.controllers.support.BankAccountMaker.accountNumberConfirm;
import static com.workmarket.api.v2.employer.settings.controllers.support.BankAccountMaker.bankName;
import static com.workmarket.api.v2.employer.settings.controllers.support.BankAccountMaker.nameOnAccount;
import static com.workmarket.api.v2.employer.settings.controllers.support.BankAccountMaker.routingNumber;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class BankAccountControllerIT extends ApiV2BaseIT {

	private static final String ENDPOINT = "/employer/v2/settings/funds/accounts";
	private static final String ADMIN_USERS_ENDPOINT = ENDPOINT + "/admins";

	@Before
	public void setUp() throws Exception {
		login();
	}

	@Test
	public void getAllAdminAndControllerUsers() throws Exception {
		MvcResult mvcResult = mockMvc.perform(doGet(ADMIN_USERS_ENDPOINT))
			.andExpect(status().isOk())
			.andReturn();

		List<String> result = getFirstResult(mvcResult, listType);
		assertThat(result, not(empty()));
	}

	@Test
	public void saveACHBankAccountWithEmptyBankName() throws Exception {
		ACHBankAccountDTO bankAccountDTO = make(a(BankAccountMaker.ACHBankAccountDTO, withNull(bankName)));
		String bankAccountDTOJson = jackson.writeValueAsString(bankAccountDTO);

		MvcResult mvcResult = mockMvc.perform(doPost(ENDPOINT)
			.content(bankAccountDTOJson))
			.andExpect(status().isBadRequest())
			.andReturn();

		ApiBaseError result = getFirstResult(mvcResult, errorType);
		expectApiErrorMessage(result, "bankName", "BankName is a required field.");
	}

	@Test
	public void saveACHBankAccountWithInvalidBankNameLength() throws Exception {
		ACHBankAccountDTO bankAccountDTO = make(a(BankAccountMaker.ACHBankAccountDTO, with(bankName, RandomStringUtils.random(101))));
		String bankAccountDTOJson = jackson.writeValueAsString(bankAccountDTO);

		MvcResult mvcResult = mockMvc.perform(doPost(ENDPOINT)
			.content(bankAccountDTOJson))
			.andExpect(status().isBadRequest())
			.andReturn();

		ApiBaseError result = getFirstResult(mvcResult, errorType);
		expectApiErrorMessage(result, "bankName", "bank name must be between 1 and 100 characters.");
	}

	@Test
	public void saveACHBankAccountWithEmptyNameOnAccount() throws Exception {
		ACHBankAccountDTO bankAccountDTO = make(a(BankAccountMaker.ACHBankAccountDTO, withNull(nameOnAccount)));
		String bankAccountDTOJson = jackson.writeValueAsString(bankAccountDTO);

		MvcResult mvcResult = mockMvc.perform(doPost(ENDPOINT)
			.content(bankAccountDTOJson))
			.andExpect(status().isBadRequest())
			.andReturn();

		ApiBaseError result = getFirstResult(mvcResult, errorType);
		expectApiErrorMessage(result, "nameOnAccount", "NameOnAccount is a required field.");
	}

	@Test
	public void saveACHBankAccountWithInvalidNameOnAccountLength() throws Exception {
		ACHBankAccountDTO bankAccountDTO = make(a(BankAccountMaker.ACHBankAccountDTO, with(nameOnAccount, RandomStringUtils.random(46))));
		String bankAccountDTOJson = jackson.writeValueAsString(bankAccountDTO);

		MvcResult mvcResult = mockMvc.perform(doPost(ENDPOINT)
			.content(bankAccountDTOJson))
			.andExpect(status().isBadRequest())
			.andReturn();

		ApiBaseError result = getFirstResult(mvcResult, errorType);
		expectApiErrorMessage(result, "nameOnAccount", "name on account must be between 1 and 45 characters.");
	}

	@Test
	public void saveACHBankAccountWithEmptyAccountNumber() throws Exception {
		ACHBankAccountDTO bankAccountDTO = make(a(BankAccountMaker.ACHBankAccountDTO, withNull(accountNumber)));
		String bankAccountDTOJson = jackson.writeValueAsString(bankAccountDTO);

		MvcResult mvcResult = mockMvc.perform(doPost(ENDPOINT)
			.content(bankAccountDTOJson))
			.andExpect(status().isBadRequest())
			.andReturn();

		ApiBaseError result = getFirstResult(mvcResult, errorType);
		expectApiErrorMessage(result, "accountNumber", "AccountNumber is a required field.");
	}

	@Test
	public void saveACHBankAccountWithEmptyAccountNumberConfirm() throws Exception {
		ACHBankAccountDTO bankAccountDTO = make(a(BankAccountMaker.ACHBankAccountDTO, withNull(accountNumberConfirm)));
		String bankAccountDTOJson = jackson.writeValueAsString(bankAccountDTO);

		MvcResult mvcResult = mockMvc.perform(doPost(ENDPOINT)
			.content(bankAccountDTOJson))
			.andExpect(status().isBadRequest())
			.andReturn();

		ApiBaseError result = getFirstResult(mvcResult, errorType);
		expectApiErrorMessage(result, "accountNumberConfirm", "AccountNumberConfirm is a required field.");
	}

	@Test
	public void saveACHBankAccountWithMismatchedAccountNumber() throws Exception {
		ACHBankAccountDTO bankAccountDTO = make(a(BankAccountMaker.ACHBankAccountDTO, with(accountNumberConfirm, "012345678")));
		String bankAccountDetailsDTOJson = jackson.writeValueAsString(bankAccountDTO);

		MvcResult mvcResult = mockMvc.perform(doPost(ENDPOINT)
			.content(bankAccountDetailsDTOJson))
			.andExpect(status().isBadRequest())
			.andReturn();

		ApiBaseError result = getFirstResult(mvcResult, errorType);
		expectApiErrorMessage(result, "accountNumber", "Account Number must be the same as Confirm Account Number.");
	}


	@Test
	public void saveACHBankAccountWithEmptyRoutingNumber() throws Exception {
		ACHBankAccountDTO bankAccountDTO = make(a(BankAccountMaker.ACHBankAccountDTO, withNull(routingNumber)));
		String bankAccountDTOJson = jackson.writeValueAsString(bankAccountDTO);

		MvcResult mvcResult = mockMvc.perform(doPost(ENDPOINT)
			.content(bankAccountDTOJson))
			.andExpect(status().isBadRequest())
			.andReturn();

		ApiBaseError result = getFirstResult(mvcResult, errorType);
		expectApiErrorMessage(result,
			"routingNumber",
			"Routing Number must consist of exactly nine digits (Ex: 012345678).");
	}

	@Test
	public void saveACHBankAccountWithInvalidRoutingNumberLength() throws Exception {
		ACHBankAccountDTO bankAccountDTO = make(a(BankAccountMaker.ACHBankAccountDTO, with(routingNumber, RandomStringUtils.random(10))));
		String bankAccountDTOJson = jackson.writeValueAsString(bankAccountDTO);

		MvcResult mvcResult = mockMvc.perform(doPost(ENDPOINT)
			.content(bankAccountDTOJson))
			.andExpect(status().isBadRequest())
			.andReturn();

		ApiBaseError result = getFirstResult(mvcResult, errorType);
		expectApiErrorMessage(result,
			"routingNumber",
			"Routing Number must consist of exactly nine digits (Ex: 012345678).");
	}

	@Test
	public void saveACHBankAccountWithInvalidRoutingNumber() throws Exception {
		ACHBankAccountDTO bankAccountDTO = make(a(BankAccountMaker.ACHBankAccountDTO, with(routingNumber, RandomStringUtils.randomNumeric(9))));
		String bankAccountDTOJson = jackson.writeValueAsString(bankAccountDTO);

		MvcResult mvcResult = mockMvc.perform(doPost(ENDPOINT)
			.content(bankAccountDTOJson))
			.andExpect(status().isBadRequest())
			.andReturn();

		ApiBaseError result = getFirstResult(mvcResult, errorType);
		expectApiErrorMessage(result, "routingNumber", "The given Routing Number did not match any US banks in our list.");
	}

	@Test
	public void saveACHBankAccountSuccess() throws Exception {
		ACHBankAccountDTO bankAccountDTO = make(a(BankAccountMaker.ACHBankAccountDTO));
		String bankAccountDTOJson = jackson.writeValueAsString(bankAccountDTO);

		MvcResult mvcResult = mockMvc.perform(doPost(ENDPOINT)
			.content(bankAccountDTOJson)).andExpect(status().isOk()).andReturn();

		ACHBankAccountDTO result = getFirstResult(mvcResult, bankAccountType);
		assertThat(result, hasProperty("bankName", is(bankAccountDTO.getBankName())));
		assertThat(result, hasProperty("bankAccountTypeCode", is(bankAccountDTO.getBankAccountTypeCode())));
		assertThat(result, hasProperty("type", is(bankAccountDTO.getType())));
		assertThat(result, hasProperty("accountNumber", containsString("XXXXXXXX")));
		assertThat(result, hasProperty("country", is(bankAccountDTO.getCountry())));
	}
}
