package com.workmarket.api.v2.employer.settings.controllers;

import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.employer.settings.controllers.support.TaxInfoMaker;
import com.workmarket.api.v2.employer.settings.models.TaxInfoDTO;
import com.workmarket.api.ApiBaseError;
import com.workmarket.domains.model.tax.TaxVerificationStatusType;
import com.workmarket.test.IntegrationTest;
import com.workmarket.web.controllers.ControllerIT;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.MakeItEasy.with;
import static com.natpryce.makeiteasy.MakeItEasy.withNull;
import static com.workmarket.api.v2.employer.assignments.controllers.support.TypeReferences.errorType;
import static com.workmarket.api.v2.employer.assignments.controllers.support.TypeReferences.taxEntityType;
import static com.workmarket.api.v2.employer.settings.controllers.support.TaxInfoMaker.address;
import static com.workmarket.api.v2.employer.settings.controllers.support.TaxInfoMaker.businessName;
import static com.workmarket.api.v2.employer.settings.controllers.support.TaxInfoMaker.city;
import static com.workmarket.api.v2.employer.settings.controllers.support.TaxInfoMaker.countryOfIncorporation;
import static com.workmarket.api.v2.employer.settings.controllers.support.TaxInfoMaker.deliveryPolicyFlag;
import static com.workmarket.api.v2.employer.settings.controllers.support.TaxInfoMaker.lastName;
import static com.workmarket.api.v2.employer.settings.controllers.support.TaxInfoMaker.postalCode;
import static com.workmarket.api.v2.employer.settings.controllers.support.TaxInfoMaker.signature;
import static com.workmarket.api.v2.employer.settings.controllers.support.TaxInfoMaker.signatureDateString;
import static com.workmarket.api.v2.employer.settings.controllers.support.TaxInfoMaker.state;
import static com.workmarket.api.v2.employer.settings.controllers.support.TaxInfoMaker.taxEntityTypeCode;
import static com.workmarket.api.v2.employer.settings.controllers.support.TaxInfoMaker.taxName;
import static com.workmarket.api.v2.employer.settings.controllers.support.TaxInfoMaker.taxNumber;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class TaxInfoControllerIT extends ControllerIT {

	private static final String ENDPOINT = "/employer/v2/settings/tax/";

	@Before
	public void setUp() throws Exception {
		login();
	}

	@Test
	public void saveUSBusinessTaxEntityWithInvalidAddress() throws Exception {
		TaxInfoDTO taxInfoDTO = make(a(TaxInfoMaker.USBusinessTaxEntityDTO,
			withNull(address),
			withNull(city),
			withNull(state),
			withNull(postalCode)
		));
		String taxInfoDTOJson = jackson.writeValueAsString(taxInfoDTO);

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT)
				.content(taxInfoDTOJson)
		).andExpect(status().isBadRequest()).andReturn();

		ApiV2Response<ApiBaseError> response = expectApiV2Response(mvcResult, errorType);
		List<ApiBaseError> results = response.getResults();
		expectAddress(results);
	}

	private void expectAddress(List<ApiBaseError> results) {
		expectApiErrorCode(results, "address", "NotEmpty");
		expectApiErrorMessage(results, "address", "Address must not be empty");
		expectApiErrorCode(results, "city", "NotEmpty");
		expectApiErrorMessage(results,"city","This location does not have a city.");
		expectApiErrorCode(results, "state", "NotEmpty");
		expectApiErrorMessage(results, "state","State cannot be empty.");
		expectApiErrorCode(results, "postalCode", "NotEmpty");
		expectApiErrorMessage(results, "postalCode","Please enter a valid location address or postal code.");
	}

	@Test
	public void saveUSBusinessTaxEntityWithEmptyEmployerIdentificationNumber() throws Exception {
		TaxInfoDTO taxInfoDTO = make(a(TaxInfoMaker.USBusinessTaxEntityDTO,
			withNull(taxNumber)
		));
		String taxInfoDTOJson = jackson.writeValueAsString(taxInfoDTO);

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT)
				.content(taxInfoDTOJson)
		).andExpect(status().isBadRequest()).andReturn();

		ApiV2Response<ApiBaseError> response = expectApiV2Response(mvcResult, errorType);
		List<ApiBaseError> results = response.getResults();
		expectApiErrorCode(results, "taxNumber", "account.tax.ein.invalid");
		expectApiErrorMessage(results, "taxNumber", "You must enter a valid Employer Identification Number");
	}

	@Test
	public void saveUSBusinessTaxEntityWithInvalidEmployerIdentificationNumber() throws Exception {
		TaxInfoDTO taxInfoDTO = make(a(TaxInfoMaker.USBusinessTaxEntityDTO,
			with(taxNumber, "99-9999999")
		));
		String taxInfoDTOJson = jackson.writeValueAsString(taxInfoDTO);

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT)
				.content(taxInfoDTOJson)
		).andExpect(status().isBadRequest()).andReturn();

		ApiV2Response<ApiBaseError> response = expectApiV2Response(mvcResult, errorType);
		List<ApiBaseError> results = response.getResults();
		expectApiErrorCode(results, "taxNumber", "account.tax.ein.invalid");
		expectApiErrorMessage(results, "taxNumber", "You must enter a valid Employer Identification Number");
	}

	@Test
	public void saveUSBusinessTaxEntityWithEmptyCompanyName() throws Exception {
		TaxInfoDTO taxInfoDTO = make(a(TaxInfoMaker.USBusinessTaxEntityDTO,
			withNull(lastName)
		));
		String taxInfoDTOJson = jackson.writeValueAsString(taxInfoDTO);

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT)
				.content(taxInfoDTOJson)
		).andExpect(status().isBadRequest()).andReturn();

		ApiV2Response<ApiBaseError> response = expectApiV2Response(mvcResult, errorType);
		List<ApiBaseError> results = response.getResults();
		expectApiErrorCode(results, "lastName", "NotEmpty.companyName");
		expectApiErrorMessage(results, "lastName", "Company name must not be empty.");
	}

	@Test
	public void saveUSBusinessTaxEntityWithEmptyTaxEntityType() throws Exception {
		TaxInfoDTO taxInfoDTO = make(a(TaxInfoMaker.USBusinessTaxEntityDTO,
			withNull(taxEntityTypeCode)
		));
		String taxInfoDTOJson = jackson.writeValueAsString(taxInfoDTO);

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT)
				.content(taxInfoDTOJson)
		).andExpect(status().isBadRequest()).andReturn();

		ApiV2Response<ApiBaseError> response = expectApiV2Response(mvcResult, errorType);
		List<ApiBaseError> results = response.getResults();
		expectApiErrorCode(results, "taxEntityTypeCode", "Pattern");
		expectApiErrorMessage(results, "taxEntityTypeCode", "Tax Entity Type is not in the correct format");
	}

	@Test
	public void saveUSBusinessTaxEntityWithEmptyBusinessNameWhenEnabled() throws Exception {
		TaxInfoDTO taxInfoDTO = make(a(TaxInfoMaker.USBusinessTaxEntityDTO,
			withNull(businessName)
		));
		String taxInfoDTOJson = jackson.writeValueAsString(taxInfoDTO);

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT)
				.content(taxInfoDTOJson)
		).andExpect(status().isBadRequest()).andReturn();

		ApiV2Response<ApiBaseError> response = expectApiV2Response(mvcResult, errorType);
		List<ApiBaseError> results = response.getResults();
		expectApiErrorCode(results, "businessName", "account.tax.business_name.invalid");
		expectApiErrorMessage(results, "businessName", "You must enter a valid Business Name");
	}

	@Test
	public void saveUSBusinessTaxEntityWithUncheckedDeliveryPolicy() throws Exception {
		TaxInfoDTO taxInfoDTO = make(a(TaxInfoMaker.USBusinessTaxEntityDTO,
			with(deliveryPolicyFlag, Boolean.FALSE)
		));
		String taxInfoDTOJson = jackson.writeValueAsString(taxInfoDTO);

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT)
				.content(taxInfoDTOJson)
		).andExpect(status().isBadRequest()).andReturn();

		ApiV2Response<ApiBaseError> response = expectApiV2Response(mvcResult, errorType);
		List<ApiBaseError> results = response.getResults();
		expectApiErrorCode(results, "deliveryPolicyFlag", "account.tax.delivery_policy.error");
		expectApiErrorMessage(results, "deliveryPolicyFlag", "You must agree to the Electronic Communication Delivery Policy.");
	}

	@Test
	public void saveUSBusinessTaxEntityWithoutSignatureOrDate() throws Exception {
		TaxInfoDTO taxInfoDTO = make(a(TaxInfoMaker.USBusinessTaxEntityDTO,
			withNull(signature),
			withNull(signatureDateString)
		));
		String taxInfoDTOJson = jackson.writeValueAsString(taxInfoDTO);

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT)
				.content(taxInfoDTOJson)
		).andExpect(status().isBadRequest()).andReturn();

		ApiV2Response<ApiBaseError> response = expectApiV2Response(mvcResult, errorType);
		List<ApiBaseError> results = response.getResults();
		expectApiErrorCode(results, "signature", "account.tax.signing.empty");
		expectApiErrorMessage(results, "signature", "You must sign and date your tax information");
	}

	@Test
	public void saveUSBusinessTaxInfoSuccess() throws Exception {
		TaxInfoDTO taxInfoDTO = make(a(TaxInfoMaker.USBusinessTaxEntityDTO));
		String taxInfoDTOJson = jackson.writeValueAsString(taxInfoDTO);

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT).content(taxInfoDTOJson)
		).andExpect(status().isOk()).andReturn();

		TaxInfoDTO result = getFirstResult(mvcResult, taxEntityType);
		assertThat(result, hasProperty("taxCountry", is(taxInfoDTO.getTaxCountry())));
		assertThat(result, hasProperty("taxEntityTypeCode", is(taxInfoDTO.getTaxEntityTypeCode())));
		assertThat(result, hasProperty("taxName", is(taxInfoDTO.getTaxName())));
		assertThat(result, hasProperty("lastName", is(taxInfoDTO.getLastName())));
		assertThat(result, hasProperty("taxVerificationStatusCode", equalTo(TaxVerificationStatusType.UNVERIFIED)));
		assertThat(result, hasProperty("address", is(taxInfoDTO.getAddress())));
		assertThat(result, hasProperty("city", is(taxInfoDTO.getCity())));
		assertThat(result, hasProperty("state", is(taxInfoDTO.getState())));
		assertThat(result, hasProperty("postalCode", is(taxInfoDTO.getPostalCode())));
		assertThat(result, hasProperty("businessName", is(taxInfoDTO.getBusinessName())));
	}

	@Test
	public void saveCanadaBusinessTaxEntityWithInvalidAddress() throws Exception {
		TaxInfoDTO taxInfoDTO = make(a(TaxInfoMaker.CANADABusinessTaxEntityDTO,
			withNull(address),
			withNull(city),
			withNull(state),
			withNull(postalCode)
		));
		String taxInfoDTOJson = jackson.writeValueAsString(taxInfoDTO);

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT).content(taxInfoDTOJson)
		).andExpect(status().isBadRequest()).andReturn();

		ApiV2Response<ApiBaseError> response = expectApiV2Response(mvcResult, errorType);
		List<ApiBaseError> results = response.getResults();
		expectAddress(results);
	}

	@Test
	public void saveCanadaBusinessTaxEntityWithEmptyBusinessNumber() throws Exception {
		TaxInfoDTO taxInfoDTO = make(a(TaxInfoMaker.CANADABusinessTaxEntityDTO));
		taxInfoDTO = new TaxInfoDTO.Builder(taxInfoDTO).setTaxNumber(null).build();
		String taxInfoDTOJson = jackson.writeValueAsString(taxInfoDTO);

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT).content(taxInfoDTOJson)
		).andExpect(status().isBadRequest()).andReturn();

		ApiV2Response<ApiBaseError> response = expectApiV2Response(mvcResult, errorType);
		List<ApiBaseError> results = response.getResults();
		expectApiErrorCode(results, "taxNumber", "account.tax.bn.invalid");
		expectApiErrorMessage(results, "taxNumber", "You must enter a valid Business Number");
	}

	@Test
	public void saveCanadaBusinessTaxEntityWithInvalidBusinessNumber() throws Exception {
		TaxInfoDTO taxInfoDTO = make(a(TaxInfoMaker.CANADABusinessTaxEntityDTO));
		taxInfoDTO = new TaxInfoDTO.Builder(taxInfoDTO).setTaxNumber("123456789-00-2345").build();
		String taxInfoDTOJson = jackson.writeValueAsString(taxInfoDTO);

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT).content(taxInfoDTOJson)
		).andExpect(status().isBadRequest()).andReturn();

		ApiV2Response<ApiBaseError> response = expectApiV2Response(mvcResult, errorType);
		List<ApiBaseError> results = response.getResults();
		expectApiErrorCode(results, "taxNumber", "account.tax.bn.invalid");
		expectApiErrorMessage(results, "taxNumber", "You must enter a valid Business Number");
	}

	@Test
	public void saveCanadaBusinessTaxEntityWithEmptyBusinessName() throws Exception {
		TaxInfoDTO taxInfoDTO = make(a(TaxInfoMaker.CANADABusinessTaxEntityDTO,
			withNull(taxName)
		));
		String taxInfoDTOJson = jackson.writeValueAsString(taxInfoDTO);

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT).content(taxInfoDTOJson)
		).andExpect(status().isBadRequest()).andReturn();

		ApiV2Response<ApiBaseError> response = expectApiV2Response(mvcResult, errorType);
		List<ApiBaseError> results = response.getResults();
		expectApiErrorCode(results, "businessName", "account.tax.company_name.empty");
		expectApiErrorMessage(results, "businessName", "You must provide the Company Name");
	}

	@Test
	public void saveCanadaBusinessTaxInfoSuccess() throws Exception {
		TaxInfoDTO taxInfoDTO = make(a(TaxInfoMaker.CANADABusinessTaxEntityDTO));
		String taxInfoDTOJson = jackson.writeValueAsString(taxInfoDTO);

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT).content(taxInfoDTOJson)
		).andExpect(status().isOk()).andReturn();

		TaxInfoDTO result = getFirstResult(mvcResult, taxEntityType);
		assertThat(result, hasProperty("taxCountry", is(taxInfoDTO.getTaxCountry())));
		assertThat(result, hasProperty("taxName", is(taxInfoDTO.getTaxName())));
		assertThat(result, hasProperty("lastName", is(taxInfoDTO.getLastName())));
		assertThat(result, hasProperty("taxVerificationStatusCode", equalTo(TaxVerificationStatusType.VALIDATED)));
		assertThat(result, hasProperty("address", is(taxInfoDTO.getAddress())));
		assertThat(result, hasProperty("city", is(taxInfoDTO.getCity())));
		assertThat(result, hasProperty("state", is(taxInfoDTO.getState())));
		assertThat(result, hasProperty("postalCode", is(taxInfoDTO.getPostalCode())));
		assertThat(result, hasProperty("businessName", is(notNullValue())));
	}

	@Test
	public void saveForeignBusinesstaxInfoDTOWithInvalidAddress() throws Exception {
		TaxInfoDTO taxInfoDTO = make(a(TaxInfoMaker.ForeignBusinessTaxEntityDTO,
			withNull(address),
			withNull(city),
			withNull(state),
			withNull(postalCode)
		));
		String taxInfoDTOJson = jackson.writeValueAsString(taxInfoDTO);

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT).content(taxInfoDTOJson)
		).andExpect(status().isBadRequest()).andReturn();

		ApiV2Response<ApiBaseError> response = expectApiV2Response(mvcResult, errorType);
		List<ApiBaseError> results = response.getResults();
		expectAddress(results);
	}

	@Test
	public void saveForeignBusinesstaxInfoDTOWithEmptyForeignTaxIdentifier() throws Exception {
		TaxInfoDTO taxInfoDTO = make(a(TaxInfoMaker.ForeignBusinessTaxEntityDTO,
			withNull(taxNumber)
		));
		String taxInfoDTOJson = jackson.writeValueAsString(taxInfoDTO);

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT).content(taxInfoDTOJson)
		).andExpect(status().isBadRequest()).andReturn();

		ApiV2Response<ApiBaseError> response = expectApiV2Response(mvcResult, errorType);
		List<ApiBaseError> results = response.getResults();
		expectApiErrorCode(results, "taxNumber", "account.tax.fti.invalid");
		expectApiErrorMessage(results, "taxNumber", "You must enter a Foreign Tax Identifier");
	}

	@Test
	public void saveForeignBusinesstaxInfoDTOWithEmptytaxInfoDTOType() throws Exception {
		TaxInfoDTO taxInfoDTO = make(a(TaxInfoMaker.ForeignBusinessTaxEntityDTO,
			withNull(taxEntityTypeCode)
		));
		String taxInfoDTOJson = jackson.writeValueAsString(taxInfoDTO);

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT).content(taxInfoDTOJson)
		).andExpect(status().isBadRequest()).andReturn();

		ApiV2Response<ApiBaseError> response = expectApiV2Response(mvcResult, errorType);
		List<ApiBaseError> results = response.getResults();
		expectApiErrorCode(results, "taxEntityTypeCode", "NotNull");
		expectApiErrorMessage(results, "taxEntityTypeCode", "Tax Entity Type is a required field");
	}

	@Test
	public void saveForeignBusinesstaxInfoDTOWithEmptyCountryOfIncorporation() throws Exception {
		TaxInfoDTO taxInfoDTO = make(a(TaxInfoMaker.ForeignBusinessTaxEntityDTO,
			withNull(countryOfIncorporation)
		));
		String taxInfoDTOJson = jackson.writeValueAsString(taxInfoDTO);

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT).content(taxInfoDTOJson)
		).andExpect(status().isBadRequest()).andReturn();

		ApiV2Response<ApiBaseError> response = expectApiV2Response(mvcResult, errorType);
		List<ApiBaseError> results = response.getResults();
		expectApiErrorCode(results, "countryOfIncorporation", "NotNull");
		expectApiErrorMessage(results, "countryOfIncorporation", "Country of incorporation is a required field");
	}


	@Test
	public void saveForeignBusinesstaxInfoDTOWithInvalidCountryOfIncorporation() throws Exception {
		TaxInfoDTO taxInfoDTO = make(a(TaxInfoMaker.ForeignBusinessTaxEntityDTO,
			with(countryOfIncorporation, "fr")
		));
		String taxInfoDTOJson = jackson.writeValueAsString(taxInfoDTO);

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT).content(taxInfoDTOJson)
		).andExpect(status().isBadRequest()).andReturn();

		ApiV2Response<ApiBaseError> response = expectApiV2Response(mvcResult, errorType);
		List<ApiBaseError> results = response.getResults();
		expectApiErrorCode(results, "countryOfIncorporation", "Pattern");
		expectApiErrorMessage(results, "countryOfIncorporation", "Country of incorporation does not match the required format.");
	}
}
