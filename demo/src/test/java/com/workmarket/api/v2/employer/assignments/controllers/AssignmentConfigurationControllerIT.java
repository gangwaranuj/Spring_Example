package com.workmarket.api.v2.employer.assignments.controllers;

import com.workmarket.api.ApiBaseError;
import com.workmarket.api.v2.ApiV2BaseIT;
import com.workmarket.api.v2.employer.assignments.models.ConfigurationDTO;
import com.workmarket.test.IntegrationTest;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MvcResult;

import static com.workmarket.api.v2.employer.assignments.controllers.support.TypeReferences.configurationType;
import static com.workmarket.api.v2.employer.assignments.controllers.support.TypeReferences.errorType;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class AssignmentConfigurationControllerIT extends ApiV2BaseIT {
	private static final String ENDPOINT = "/employer/v2/assignments/configuration";

	@Before
	public void setUp() throws Exception {
		login();
	}

	@Test
	public void getSettings() throws Exception {

		ConfigurationDTO defaultConfiguration = getDefaultConfiguration();

		assertThat(defaultConfiguration, hasProperty("instantNetworkEnabled", is(true)));
		assertThat(defaultConfiguration, hasProperty("customFieldsEnabled", is(false)));
		assertThat(defaultConfiguration, hasProperty("termsOfAgreementEnabled", is(false)));
		assertThat(defaultConfiguration, hasProperty("termsOfAgreement", isEmptyOrNullString()));
		assertThat(defaultConfiguration, hasProperty("codeOfConductEnabled", is(false)));
		assertThat(defaultConfiguration, hasProperty("codeOfConduct", isEmptyOrNullString()));
		assertThat(defaultConfiguration, hasProperty("useWorkMarketPrintout", is(true)));
		assertThat(defaultConfiguration, hasProperty("printSettingsLogoOption", is("company")));
		assertThat(defaultConfiguration, hasProperty("printSettingsEndUserTermsEnabled", is(false)));
		assertThat(defaultConfiguration, hasProperty("printSettingsEndUserTerms", isEmptyOrNullString()));
		assertThat(defaultConfiguration, hasProperty("printSettingsSignatureEnabled", is(false)));
		assertThat(defaultConfiguration, hasProperty("printSettingsSignature", isEmptyOrNullString()));
		assertThat(defaultConfiguration, hasProperty("printSettingsBadgeEnabled", is(true)));
		assertThat(defaultConfiguration, hasProperty("projectBudgetManagementEnabled", is(false)));
		assertThat(defaultConfiguration, hasProperty("requireClientProjectEnabled", is(false)));
		assertThat(defaultConfiguration, hasProperty("requirementSetsEnabled", is(false)));
		assertThat(defaultConfiguration, hasProperty("shipmentsEnabled", is(false)));
		assertThat(defaultConfiguration, hasProperty("surveysEnabled", is(false)));
		assertThat(defaultConfiguration, hasProperty("autoRateEnabled", is(true)));
		assertThat(defaultConfiguration, hasProperty("uniqueExternalIdEnabled", is(false)));
		assertThat(defaultConfiguration, hasProperty("uniqueExternalIdDisplayName", isEmptyOrNullString()));
		assertThat(defaultConfiguration, hasProperty("autoCloseEnabled", is(false)));
		assertThat(defaultConfiguration, hasProperty("autoCloseHours", is("72")));
		assertThat(defaultConfiguration, hasProperty("deliverablesEnabled", is(false)));
		assertThat(defaultConfiguration, hasProperty("ivrEnabled", is(false)));
		assertThat(defaultConfiguration, hasProperty("agingAssignmentsEnabled", is(false)));
		assertThat(defaultConfiguration, hasProperty("agingAssignmentsEmailAddress", isEmptyOrNullString()));
		assertThat(defaultConfiguration, hasProperty("disablePriceNegotiation", is(false)));

	}

	@Test
	public void updateAllSettings() throws Exception {

		ConfigurationDTO defaultConfiguration = getDefaultConfiguration();

		// update configuration
		String termsOfAgreement = "termsOfAgreement";
		String codeOfConduct = "termsOfAgreement";
		String printSettingsLogoOption = "wm";
		String printSettingsEndUserTerms = "endUserTerms";
		String printSettingsSignature = "signature";
		String uniqueExternalIdDisplayName = "external id";
		String autoCloseHours = "24";
		String agingAssignmentsEmail = "valid@workmarket.com";
		int paymentTermsDays = 7;

		ConfigurationDTO.Builder configurationDTOBuilder = new ConfigurationDTO.Builder(defaultConfiguration);
		configurationDTOBuilder.setInstantNetworkEnabled(!defaultConfiguration.isInstantNetworkEnabled());
		configurationDTOBuilder.setCustomFieldsEnabled(!defaultConfiguration.isCustomFieldsEnabled());
		configurationDTOBuilder.setTermsOfAgreementEnabled(!defaultConfiguration.isTermsOfAgreementEnabled());
		configurationDTOBuilder.setTermsOfAgreement(termsOfAgreement);
		configurationDTOBuilder.setCodeOfConductEnabled(!defaultConfiguration.isCodeOfConductEnabled());
		configurationDTOBuilder.setCodeOfConduct(codeOfConduct);
		configurationDTOBuilder.setUseWorkMarketPrintout(!defaultConfiguration.isUseWorkMarketPrintout());
		configurationDTOBuilder.setPrintSettingsLogoOption(printSettingsLogoOption);
		configurationDTOBuilder.setPrintSettingsEndUserTermsEnabled(!defaultConfiguration.isPrintSettingsEndUserTermsEnabled());
		configurationDTOBuilder.setPrintSettingsEndUserTerms(printSettingsEndUserTerms);
		configurationDTOBuilder.setPrintSettingsSignatureEnabled(!defaultConfiguration.isPrintSettingsSignatureEnabled());
		configurationDTOBuilder.setPrintSettingsSignature(printSettingsSignature);
		configurationDTOBuilder.setPrintSettingsBadgeEnabled(!defaultConfiguration.isPrintSettingsBadgeEnabled());
		configurationDTOBuilder.setProjectBudgetManagementEnabled(!defaultConfiguration.isProjectBudgetManagementEnabled());
		configurationDTOBuilder.setRequireClientProjectEnabled(!defaultConfiguration.isRequireClientProjectEnabled());
		configurationDTOBuilder.setRequirementSetsEnabled(!defaultConfiguration.isRequirementSetsEnabled());
		configurationDTOBuilder.setShipmentsEnabled(!defaultConfiguration.isShipmentsEnabled());
		configurationDTOBuilder.setSurveysEnabled(!defaultConfiguration.isSurveysEnabled());
		configurationDTOBuilder.setAutoRateEnabled(!defaultConfiguration.isAutoRateEnabled());
		configurationDTOBuilder.setUniqueExternalIdEnabled(!defaultConfiguration.isUniqueExternalIdEnabled());
		configurationDTOBuilder.setUniqueExternalIdDisplayName(uniqueExternalIdDisplayName);
		configurationDTOBuilder.setAutoCloseEnabled(!defaultConfiguration.isAutoCloseEnabled());
		configurationDTOBuilder.setAutoCloseHours(autoCloseHours);
		configurationDTOBuilder.setDeliverablesEnabled(!defaultConfiguration.isDeliverablesEnabled());
		configurationDTOBuilder.setIvrEnabled(!defaultConfiguration.isIvrEnabled());
		configurationDTOBuilder.setAgingAssignmentsEnabled(!defaultConfiguration.isAgingAssignmentsEnabled());
		configurationDTOBuilder.setAgingAssignmentsEmailAddress(agingAssignmentsEmail);
		configurationDTOBuilder.setPaymentTermsDays(paymentTermsDays);
		configurationDTOBuilder.setDisablePriceNegotiation(!defaultConfiguration.getDisablePriceNegotiation());

		ConfigurationDTO updatedConfiguration = updateConfiguration(configurationDTOBuilder);

		assertThat(updatedConfiguration, hasProperty("instantNetworkEnabled", is(!defaultConfiguration.isInstantNetworkEnabled())));
		assertThat(updatedConfiguration, hasProperty("customFieldsEnabled", is(!defaultConfiguration.isCustomFieldsEnabled())));
		assertThat(updatedConfiguration, hasProperty("termsOfAgreementEnabled", is(!defaultConfiguration.isTermsOfAgreementEnabled())));
		assertThat(updatedConfiguration, hasProperty("termsOfAgreement", is(termsOfAgreement)));
		assertThat(updatedConfiguration, hasProperty("codeOfConductEnabled", is(!defaultConfiguration.isCodeOfConductEnabled())));
		assertThat(updatedConfiguration, hasProperty("codeOfConduct", is(codeOfConduct)));
		assertThat(updatedConfiguration, hasProperty("useWorkMarketPrintout", is(!defaultConfiguration.isUseWorkMarketPrintout())));
		assertThat(updatedConfiguration, hasProperty("printSettingsLogoOption", is(printSettingsLogoOption)));
		assertThat(updatedConfiguration, hasProperty("printSettingsEndUserTermsEnabled", is(!defaultConfiguration.isPrintSettingsEndUserTermsEnabled())));
		assertThat(updatedConfiguration, hasProperty("printSettingsEndUserTerms", is(printSettingsEndUserTerms)));
		assertThat(updatedConfiguration, hasProperty("printSettingsSignatureEnabled", is(!defaultConfiguration.isPrintSettingsSignatureEnabled())));
		assertThat(updatedConfiguration, hasProperty("printSettingsSignature", is(printSettingsSignature)));
		assertThat(updatedConfiguration, hasProperty("printSettingsBadgeEnabled", is(!defaultConfiguration.isPrintSettingsBadgeEnabled())));
		assertThat(updatedConfiguration, hasProperty("projectBudgetManagementEnabled", is(!defaultConfiguration.isProjectBudgetManagementEnabled())));
		assertThat(updatedConfiguration, hasProperty("requireClientProjectEnabled", is(!defaultConfiguration.isRequireClientProjectEnabled())));
		assertThat(updatedConfiguration, hasProperty("requirementSetsEnabled", is(!defaultConfiguration.isRequirementSetsEnabled())));
		assertThat(updatedConfiguration, hasProperty("shipmentsEnabled", is(!defaultConfiguration.isShipmentsEnabled())));
		assertThat(updatedConfiguration, hasProperty("surveysEnabled", is(!defaultConfiguration.isSurveysEnabled())));
		assertThat(updatedConfiguration, hasProperty("autoRateEnabled", is(!defaultConfiguration.isAutoRateEnabled())));
		assertThat(updatedConfiguration, hasProperty("uniqueExternalIdEnabled", is(!defaultConfiguration.isUniqueExternalIdEnabled())));
		assertThat(updatedConfiguration, hasProperty("uniqueExternalIdDisplayName", is(uniqueExternalIdDisplayName)));
		assertThat(updatedConfiguration, hasProperty("autoCloseEnabled", is(!defaultConfiguration.isAutoCloseEnabled())));
		assertThat(updatedConfiguration, hasProperty("autoCloseHours", is(autoCloseHours)));
		assertThat(updatedConfiguration, hasProperty("deliverablesEnabled", is(!defaultConfiguration.isDeliverablesEnabled())));
		assertThat(updatedConfiguration, hasProperty("ivrEnabled", is(!defaultConfiguration.isIvrEnabled())));
		assertThat(updatedConfiguration, hasProperty("agingAssignmentsEnabled", is(!defaultConfiguration.isAgingAssignmentsEnabled())));
		assertThat(updatedConfiguration, hasProperty("agingAssignmentsEmailAddress", is(agingAssignmentsEmail)));
		assertThat(updatedConfiguration, hasProperty("paymentTermsDays", is(paymentTermsDays)));
		assertThat(updatedConfiguration, hasProperty("disablePriceNegotiation", is(!defaultConfiguration.getDisablePriceNegotiation())));
	}

	@Test
	public void updateSettings_withNegativeAutoCloseHours_validationError() throws Exception {

		ConfigurationDTO defaultConfiguration = getDefaultConfiguration();

		ConfigurationDTO.Builder configurationDTOBuilder = new ConfigurationDTO.Builder(defaultConfiguration);

		configurationDTOBuilder.setAutoCloseEnabled(true);
		configurationDTOBuilder.setAutoCloseHours("-1");

		ApiBaseError result = updateConfigurationError(configurationDTOBuilder);

		expectApiErrorMessage(result, "autoCloseHours", "Autoclose delay in hours must be a number greater than 0");
		assertThat(result, hasProperty("resource", is("work")));
	}

	@Test
	public void updateSettings_withEmptyAutoCloseHours_validationError() throws Exception {

		ConfigurationDTO defaultConfiguration = getDefaultConfiguration();

		ConfigurationDTO.Builder configurationDTOBuilder = new ConfigurationDTO.Builder(defaultConfiguration);

		configurationDTOBuilder.setAutoCloseEnabled(true);
		configurationDTOBuilder.setAutoCloseHours(null);

		ApiBaseError result = updateConfigurationError(configurationDTOBuilder);

		expectApiErrorMessage(result, "autoCloseHours", "AutoCloseHours is a required field.");
	}

	@Test
	public void updateSettings_withEmptyAgingAssignmentsEmail_validationError() throws Exception {

		ConfigurationDTO defaultConfiguration = getDefaultConfiguration();

		ConfigurationDTO.Builder configurationDTOBuilder = new ConfigurationDTO.Builder(defaultConfiguration);

		configurationDTOBuilder.setAgingAssignmentsEnabled(true);
		configurationDTOBuilder.setAgingAssignmentsEmailAddress(null);

		ApiBaseError result = updateConfigurationError(configurationDTOBuilder);

		expectApiErrorMessage(result, "agingAssignmentsEmailAddress", "Email for assignment aging alert is required.");
	}

	@Test
	public void updateSettings_withInvalidAgingAssignmentsEmail_validationError() throws Exception {

		ConfigurationDTO defaultConfiguration = getDefaultConfiguration();

		ConfigurationDTO.Builder configurationDTOBuilder = new ConfigurationDTO.Builder(defaultConfiguration);

		configurationDTOBuilder.setAgingAssignmentsEnabled(true);
		configurationDTOBuilder.setAgingAssignmentsEmailAddress("invalidEmailAddress");

		ApiBaseError result = updateConfigurationError(configurationDTOBuilder);

		expectApiErrorMessage(result, "agingAssignmentsEmailAddress", "Email for assignment aging alert is invalid.");
	}

	@Test
	public void updateSettings_withEmptyUniqueExternalId_validationError() throws Exception {

		ConfigurationDTO defaultConfiguration = getDefaultConfiguration();

		ConfigurationDTO.Builder configurationDTOBuilder = new ConfigurationDTO.Builder(defaultConfiguration);

		configurationDTOBuilder.setUniqueExternalIdEnabled(true);
		configurationDTOBuilder.setUniqueExternalIdDisplayName(null);

		ApiBaseError result = updateConfigurationError(configurationDTOBuilder);

		expectApiErrorMessage(result, "uniqueExternalIdDisplayName", "The unique ID name is required.");
	}

	@Test
	public void updateSettings_withInvalidUniqueExternalId_validationError() throws Exception {

		ConfigurationDTO defaultConfiguration = getDefaultConfiguration();

		ConfigurationDTO.Builder configurationDTOBuilder = new ConfigurationDTO.Builder(defaultConfiguration);

		configurationDTOBuilder.setUniqueExternalIdEnabled(true);
		configurationDTOBuilder.setUniqueExternalIdDisplayName(StringUtils.repeat("w", 51));

		ApiBaseError result = updateConfigurationError(configurationDTOBuilder);

		expectApiErrorMessage(result, "uniqueExternalIdDisplayName", "The unique ID name should not exceed 50 characters.");
	}

	private ConfigurationDTO getDefaultConfiguration() throws Exception {
		MvcResult mvcResult = mockMvc.perform(doGet(ENDPOINT))
			.andExpect(status().isOk()).andReturn();

		return getFirstResult(mvcResult, configurationType);
	}

	private ConfigurationDTO updateConfiguration(ConfigurationDTO.Builder configurationDTO) throws Exception {

		String configurationJson = jackson.writeValueAsString(configurationDTO.build());
		MvcResult mvcResult = mockMvc.perform(doPut(ENDPOINT)
			.content(configurationJson)
		).andExpect(status().isOk()).andReturn();

		return  getFirstResult(mvcResult, configurationType);
	}

	private ApiBaseError updateConfigurationError(ConfigurationDTO.Builder assignmentConfigurationDTO) throws Exception {

		String configurationJson = jackson.writeValueAsString(assignmentConfigurationDTO.build());
		MvcResult mvcResult = mockMvc.perform(doPut(ENDPOINT)
			      .content(configurationJson))
						.andExpect(status().isBadRequest()).andReturn();


		return getFirstResult(mvcResult, errorType);
	}
}
