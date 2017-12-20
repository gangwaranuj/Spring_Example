package com.workmarket.api.v2.employer.settings.controllers;

import com.workmarket.api.v2.employer.settings.controllers.support.BankAccountMaker;
import com.workmarket.api.v2.employer.settings.controllers.support.CompanyProfileMaker;
import com.workmarket.api.v2.employer.settings.controllers.support.TaxInfoMaker;
import com.workmarket.api.v2.employer.settings.models.ACHBankAccountDTO;
import com.workmarket.api.v2.employer.settings.models.CompanyProfileDTO;
import com.workmarket.api.v2.employer.settings.models.SettingsCompletenessDTO;
import com.workmarket.api.v2.employer.settings.models.TaxInfoDTO;
import com.workmarket.api.v2.employer.settings.services.CompanyProfileService;
import com.workmarket.api.v2.ApiV2BaseIT;
import com.workmarket.domains.model.settings.SettingsActionTypes;
import com.workmarket.domains.payments.model.BankAccountDTO;
import com.workmarket.domains.payments.service.BankingService;
import com.workmarket.service.business.dto.TaxEntityDTO;
import com.workmarket.test.IntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MvcResult;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.workmarket.api.v2.employer.assignments.controllers.support.TypeReferences.settingsCompletenessType;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class SettingsCompletenessControllerIT extends ApiV2BaseIT {

	private static final String ENDPOINT = "/employer/v2/settings/completeness_percentage";
	private @Autowired BankingService bankingService;
	private @Autowired CompanyProfileService companyProfileService;

	@Before
	public void setUp() throws Exception {
		loginAsDefaultFirstNewEmployee();
	}

	@Test
	public void getDefaultSettingsCompletenessSuccess() throws Exception {

		MvcResult mvcResult = mockMvc.perform(
			doGet(ENDPOINT)
		).andExpect(status().isOk()).andReturn();

		SettingsCompletenessDTO result = getFirstResult(mvcResult, settingsCompletenessType);
		assertThat(result, hasProperty("completedActions", hasItem(SettingsActionTypes.ASSIGNMENT_SETTINGS)));
		assertThat(result, hasProperty("missingActions", hasItem(SettingsActionTypes.OVERVIEW)));
		assertThat(result, hasProperty("missingActions", hasItem(SettingsActionTypes.BANK)));
		assertThat(result, hasProperty("missingActions", hasItem(SettingsActionTypes.FUNDS)));
		assertThat(result, hasProperty("missingActions", hasItem(SettingsActionTypes.TAX)));
		assertThat(result, hasProperty("percentage", is(20.0F)));
	}

	@Test
	public void getDefaultSettingsCompletenessWithCompanyProfileCompletedSuccess() throws Exception {

		CompanyProfileDTO companyProfileDTO = make(a(CompanyProfileMaker.CompanyProfileDTO));
		companyProfileService.saveOrUpdate(companyProfileDTO);

		MvcResult mvcResult = mockMvc.perform(
			doGet(ENDPOINT)
		).andExpect(status().isOk()).andReturn();

		SettingsCompletenessDTO result = getFirstResult(mvcResult, settingsCompletenessType);
		assertThat(result, hasProperty("completedActions", hasItem(SettingsActionTypes.ASSIGNMENT_SETTINGS)));
		assertThat(result, hasProperty("completedActions", hasItem(SettingsActionTypes.OVERVIEW)));
		assertThat(result, hasProperty("missingActions", hasItem(SettingsActionTypes.BANK)));
		assertThat(result, hasProperty("missingActions", hasItem(SettingsActionTypes.FUNDS)));
		assertThat(result, hasProperty("missingActions", hasItem(SettingsActionTypes.TAX)));
		assertThat(result, hasProperty("percentage", is(40.0F)));
	}


	@Test
	public void getDefaultSettingsCompletenessWithTaxCompletedSuccess() throws Exception {

		TaxInfoDTO taxInfoDTO = make(a(TaxInfoMaker.USBusinessTaxEntityDTO));
		TaxEntityDTO taxEntityDTO = new TaxEntityDTO();
		BeanUtils.copyProperties(taxInfoDTO, taxEntityDTO);
		taxService.saveTaxEntity(user.getId(), taxEntityDTO);

		MvcResult mvcResult = mockMvc.perform(
			doGet(ENDPOINT)
		).andExpect(status().isOk()).andReturn();

		SettingsCompletenessDTO result = getFirstResult(mvcResult, settingsCompletenessType);
		assertThat(result, hasProperty("completedActions", hasItem(SettingsActionTypes.ASSIGNMENT_SETTINGS)));
		assertThat(result, hasProperty("completedActions", hasItem(SettingsActionTypes.TAX)));
		assertThat(result, hasProperty("missingActions", hasItem(SettingsActionTypes.OVERVIEW)));
		assertThat(result, hasProperty("missingActions", hasItem(SettingsActionTypes.BANK)));
		assertThat(result, hasProperty("missingActions", hasItem(SettingsActionTypes.FUNDS)));
		assertThat(result, hasProperty("percentage", is(40.0F)));
	}

	@Test
	public void getDefaultSettingsCompletenessWithBankAccountCompletedSuccess() throws Exception {

		ACHBankAccountDTO achBankAccountDTO= make(a(BankAccountMaker.ACHBankAccountDTO));
		BankAccountDTO bankAccountDTO = new BankAccountDTO();
		BeanUtils.copyProperties(achBankAccountDTO, bankAccountDTO);
		bankingService.saveBankAccount(user.getId(), bankAccountDTO);

		MvcResult mvcResult = mockMvc.perform(
			doGet(ENDPOINT)
		).andExpect(status().isOk()).andReturn();

		SettingsCompletenessDTO result = getFirstResult(mvcResult, settingsCompletenessType);
		assertThat(result, hasProperty("completedActions", hasItem(SettingsActionTypes.ASSIGNMENT_SETTINGS)));
		assertThat(result, hasProperty("completedActions", hasItem(SettingsActionTypes.BANK)));
		assertThat(result, hasProperty("missingActions", hasItem(SettingsActionTypes.FUNDS)));
		assertThat(result, hasProperty("missingActions", hasItem(SettingsActionTypes.OVERVIEW)));
		assertThat(result, hasProperty("missingActions", hasItem(SettingsActionTypes.TAX)));
		assertThat(result, hasProperty("percentage", is(40.0F)));
	}

	@Test
	public void getSettingsCompletenessWithAllCompletedSuccess() throws Exception {
		CompanyProfileDTO companyProfileDTO = make(a(CompanyProfileMaker.CompanyProfileDTO));
		companyProfileService.saveOrUpdate(companyProfileDTO);

		ACHBankAccountDTO achBankAccountDTO= make(a(BankAccountMaker.ACHBankAccountDTO));
		BankAccountDTO bankAccountDTO = new BankAccountDTO();
		BeanUtils.copyProperties(achBankAccountDTO, bankAccountDTO);
		bankingService.saveBankAccount(user.getId(), bankAccountDTO);

		TaxInfoDTO taxInfoDTO = make(a(TaxInfoMaker.USBusinessTaxEntityDTO));
		TaxEntityDTO taxEntityDTO = new TaxEntityDTO();
		BeanUtils.copyProperties(taxInfoDTO, taxEntityDTO);
		taxService.saveTaxEntity(user.getId(), taxEntityDTO);

		accountRegisterService.addFundsToRegisterFromWire(user.getCompany().getId(), "1000.00");

		MvcResult mvcResult = mockMvc.perform(
			doGet(ENDPOINT)
		).andExpect(status().isOk()).andReturn();

		SettingsCompletenessDTO result = getFirstResult(mvcResult, settingsCompletenessType);
		assertThat(result, hasProperty("completedActions", hasItem(SettingsActionTypes.ASSIGNMENT_SETTINGS)));
		assertThat(result, hasProperty("completedActions", hasItem(SettingsActionTypes.OVERVIEW)));
		assertThat(result, hasProperty("completedActions", hasItem(SettingsActionTypes.BANK)));
		assertThat(result, hasProperty("completedActions", hasItem(SettingsActionTypes.FUNDS)));
		assertThat(result, hasProperty("completedActions", hasItem(SettingsActionTypes.TAX)));
		assertThat(result, hasProperty("missingActions", is(empty())));
		assertThat(result, hasProperty("percentage", is(100.0F)));
	}
}
