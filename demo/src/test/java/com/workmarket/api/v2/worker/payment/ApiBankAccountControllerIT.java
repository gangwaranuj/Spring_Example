package com.workmarket.api.v2.worker.payment;

import com.fasterxml.jackson.core.type.TypeReference;
import com.workmarket.api.ApiBaseError;
import com.workmarket.api.v2.ApiV2BaseIT;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.model.ApiBankAccountDTO;
import com.workmarket.domains.model.postalcode.Country;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static com.workmarket.api.v2.employer.assignments.controllers.support.TypeReferences.errorType;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
public class ApiBankAccountControllerIT extends ApiV2BaseIT {

	private static final String ENDPOINT = "/worker/v2/accounts";
	private static final TypeReference<ApiV2Response<ApiBankAccountDTO>> API_BANK_ACCOUNT_TYPE = new TypeReference<ApiV2Response<ApiBankAccountDTO>>(){};

	@Before
	public void setUp() throws Exception {
		login();
	}

	@Test
	public void testCreatePayPalAccount() throws Exception {
		final ApiBankAccountDTO dto = new ApiBankAccountDTO.Builder()
			.setType(ApiBankAccountDTO.Type.PPA)
			.setCountry(Country.CANADA)
			.setName("foo@bar.com")
			.build();

		final MvcResult mvcResult = sendSaveRequest(dto)
			.andExpect(status().isOk())
			.andReturn();

		final ApiBankAccountDTO result = getFirstResult(mvcResult, API_BANK_ACCOUNT_TYPE);

		assertNotNull(result.getId());
		assertTrue(result.getVerified());
		assertEquals("foo@bar.com", result.getName());
		assertEquals(Country.CANADA, result.getCountry());
		assertEquals(ApiBankAccountDTO.Type.PPA, result.getType());
	}

	@Test
	public void testCreatePayPalAccountWithInvalidCountryValues() throws Exception {
		final ApiBankAccountDTO dto = new ApiBankAccountDTO.Builder()
			.setType(ApiBankAccountDTO.Type.PPA)
			.setName("foo@bar.com")
			.build();

		final MvcResult mvcResult = sendSaveRequest(dto)
			.andExpect(status().isBadRequest())
			.andReturn();

		final ApiBaseError result = getFirstResult(mvcResult, errorType);
		expectApiErrorMessage(result, "country", "This location does not have a valid country.");
	}

	@Test
	public void testCreatePayPalAccountWithInvalidEmailValues() throws Exception {
		final ApiBankAccountDTO dto = new ApiBankAccountDTO.Builder()
			.setType(ApiBankAccountDTO.Type.PPA)
			.setCountry(Country.CANADA)
			.setName("baz")
			.build();

		final MvcResult mvcResult = sendSaveRequest(dto)
			.andExpect(status().isBadRequest())
			.andReturn();

		final ApiBaseError result = getFirstResult(mvcResult, errorType);
		expectApiErrorMessage(result, "name", "Must a valid email address (Ex: xxx@yyy.zzz).");
	}

	@Test
	public void testCreateACHAccountUSA() throws Exception {
		final ApiBankAccountDTO dto = new ApiBankAccountDTO.Builder()
			.setAccountType(ApiBankAccountDTO.AccountType.CHECKING)
			.setType(ApiBankAccountDTO.Type.ACH)
			.setBankName("Bank of America")
			.setAccountNumber("0123456789")
			.setRoutingNumber("011000138")
			.setAccountHolder("Foo Bar")
			.setCountry(Country.USA)
			.build();

		final MvcResult mvcResult = sendSaveRequest(dto)
			.andExpect(status().isOk())
			.andReturn();

		final ApiBankAccountDTO result = getFirstResult(mvcResult, API_BANK_ACCOUNT_TYPE);

		assertNotNull(result.getId());
		assertFalse(result.getVerified());
		assertEquals(Country.USA, result.getCountry());
		assertEquals("Foo Bar", result.getAccountHolder());
		assertEquals(ApiBankAccountDTO.Type.ACH, result.getType());
		assertEquals("011000138", result.getRoutingNumber());
		assertEquals("XXXXXXXXxxxx", result.getAccountNumber());
		assertEquals("Bank of America", result.getBankName());
		assertEquals(ApiBankAccountDTO.AccountType.CHECKING, result.getAccountType());
	}

	@Test
	public void testCreateACHAccountUsingNameInstedOfAccountHolder() throws Exception {
		final ApiBankAccountDTO dto = new ApiBankAccountDTO.Builder()
			.setAccountType(ApiBankAccountDTO.AccountType.CHECKING)
			.setType(ApiBankAccountDTO.Type.ACH)
			.setName("Account Holder Name")
			.setBankName("Bank of America")
			.setAccountNumber("0123456789")
			.setRoutingNumber("011000138")
			.setCountry(Country.USA)
			.build();

		final MvcResult mvcResult = sendSaveRequest(dto)
			.andExpect(status().isOk())
			.andReturn();

		final ApiBankAccountDTO result = getFirstResult(mvcResult, API_BANK_ACCOUNT_TYPE);

		assertNotNull(result.getId());
		assertFalse(result.getVerified());
		assertEquals(Country.USA, result.getCountry());
		assertEquals(ApiBankAccountDTO.Type.ACH, result.getType());
		assertEquals("Account Holder Name", result.getAccountHolder());
		assertEquals(ApiBankAccountDTO.AccountType.CHECKING, result.getAccountType());
	}

	@Test
	public void testCreateACHAccountUSAWithMissingValues() throws Exception {
		final ApiBankAccountDTO dto = new ApiBankAccountDTO.Builder()
			.setAccountType(ApiBankAccountDTO.AccountType.CHECKING)
			.setType(ApiBankAccountDTO.Type.ACH)
			.setCountry(Country.USA)
			.build();

		final MvcResult mvcResult = sendSaveRequest(dto)
			.andExpect(status().isBadRequest())
			.andReturn();

		final List<ApiBaseError> result = getResults(mvcResult, errorType);

		expectApiErrorCode(result, "bankName", "NotNull");
		expectApiErrorCode(result, "accountHolder", "NotNull");
		expectApiErrorCode(result, "accountNumber", "NotNull");
		expectApiErrorCode(result, "routingNumber", "banking.routingNumber.invalid");
	}

	@Test
	public void testListAccounts() throws Exception {
		final ApiBankAccountDTO dto1 = new ApiBankAccountDTO.Builder()
			.setType(ApiBankAccountDTO.Type.PPA)
			.setCountry(Country.CANADA)
			.setName("foo@bar.com")
			.build();

		final ApiBankAccountDTO dto2 = new ApiBankAccountDTO.Builder()
			.setAccountType(ApiBankAccountDTO.AccountType.CHECKING)
			.setType(ApiBankAccountDTO.Type.ACH)
			.setBankName("Bank of America")
			.setAccountNumber("0123456789")
			.setRoutingNumber("011000138")
			.setAccountHolder("Foo Bar")
			.setCountry(Country.USA)
			.build();

		sendSaveRequest(dto1)
			.andExpect(status().isOk())
			.andReturn();

		sendSaveRequest(dto2)
			.andExpect(status().isOk())
			.andReturn();

		final MvcResult mvcResult = mockMvc.perform(doGet(ENDPOINT))
			.andExpect(status().isOk())
			.andReturn();

		final List<ApiBankAccountDTO> result = getResults(mvcResult, API_BANK_ACCOUNT_TYPE);

		assertEquals(2, result.size());

		// first element
		assertNotNull(result.get(0).getId());
		assertFalse(result.get(0).getVerified());
		assertEquals(Country.USA, result.get(0).getCountry());
		assertEquals("Foo Bar", result.get(0).getAccountHolder());
		assertEquals(ApiBankAccountDTO.Type.ACH, result.get(0).getType());
		assertEquals("011000138", result.get(0).getRoutingNumber());
		assertEquals("XXXXXXXXxxxx", result.get(0).getAccountNumber());
		assertEquals("Bank of America", result.get(0).getBankName());
		assertEquals("Bank of America (xxxx)", result.get(0).getName());
		assertEquals(ApiBankAccountDTO.AccountType.CHECKING, result.get(0).getAccountType());
		// CAN only
		assertNull(result.get(0).getTransitBranchNumber());
		assertNull(result.get(0).getFinancialInstNumber());

		// second element
		assertNotNull(result.get(1).getId());
		assertTrue(result.get(1).getVerified());
		assertEquals("foo@bar.com", result.get(1).getName());
		assertEquals(Country.CANADA, result.get(1).getCountry());
		assertEquals(ApiBankAccountDTO.Type.PPA, result.get(1).getType());
		// USA only
		assertNull(result.get(1).getRoutingNumber());
	}

	private ResultActions sendSaveRequest(ApiBankAccountDTO dto) throws Exception {
		final String bankAccountDTOJson = jackson.writeValueAsString(dto);
		final ResultActions resultActions = mockMvc.perform(doPost(ENDPOINT)
			.content(bankAccountDTOJson));

		return resultActions;
	}
}
