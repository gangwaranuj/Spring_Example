package com.workmarket.api.v2.employer.assignments.services;

import com.google.common.collect.Lists;
import com.workmarket.api.v2.employer.assignments.models.ConfigurationDTO;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.CompanyPreference;
import com.workmarket.domains.model.option.CompanyOption;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.dto.EmailAddressDTO;
import com.workmarket.service.business.dto.ManageMyWorkMarketDTO;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.option.OptionsService;
import com.workmarket.thrift.core.ConstraintViolation;
import com.workmarket.thrift.core.User;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.Assert;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractAssignmentSettingsUseCase<T, K> implements UseCase<T, K> {

	protected static final Log logger = LogFactory.getLog(UpdateAssignmentSettingsUseCase.class);
	private static final String LOGO_OPTION_WM = "wm";
	private static final String LOGO_OPTION_COMPANY = "company";
	private static final String LOGO_OPTION_NONE = "none";
	private static final int UNIQUE_EXTERNAL_ID_DISPLAY_NAME_MAX_LEN = 50;

	@Autowired private CompanyService companyService;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private ProfileService profileService;
	@Autowired private MessageBundleHelper messageHelper;
	@Qualifier("companyOptionsService") @Autowired private OptionsService<Company> companyOptionsService;

	protected User user;
	protected Long companyId;
	protected ConfigurationDTO configurationDTO;
	protected ConfigurationDTO.Builder configurationDTOBuilder = new ConfigurationDTO.Builder();
	protected CompanyPreference companyPreference;
	protected Company company;
	protected ManageMyWorkMarketDTO manageMyWorkMarketDTO = new ManageMyWorkMarketDTO();
	protected EmailAddressDTO agingAssignmentsEmailAddress = new EmailAddressDTO();
	protected List<ConstraintViolation> errors = new ArrayList<>();
	protected Exception exception;

	protected abstract T me();
	protected abstract T handleExceptions() throws Exception;

	@Override
	public T execute() {
		try {
			failFast();
			init();
			prepare();
			process();
			save();
			finish();
		} catch (ValidationException e) {
			exception = e;
		}
		return me();
	}

	protected void failFast() {
		// no-op default implementation
		// override to add behavior
	}

	protected void init() {
		// no-op default implementation
		// override to add behavior
	}

	protected void prepare() {
		// no-op default implementation
		// override to add behavior
	}

	protected void process() {
		// no-op default implementation
		// override to add behavior
	}

	protected void save() throws ValidationException {
		// no-op default implementation
		// override to add behavior
	}

	protected void finish() {
		// no-op default implementation
		// override to add behavior
	}

	protected void getUser() {
		user = new User().setId(authenticationService.getCurrentUser().getId());
		companyId = authenticationService.getCurrentUserCompanyId();
	}



	protected void copyConfigurationDTO() {
		configurationDTOBuilder = new ConfigurationDTO.Builder(configurationDTO);
	}

	protected void loadConfigurationDTO() {
		getCompany();
		com.workmarket.domains.model.ManageMyWorkMarket manageMyWorkMarket = company.getManageMyWorkMarket();
		CompanyPreference preference = company.getCompanyPreference();

		configurationDTOBuilder
			.setInstantNetworkEnabled(manageMyWorkMarket.isInstantWorkerPoolEnabled())
			.setCustomFieldsEnabled(manageMyWorkMarket.getCustomFieldsEnabledFlag())
			.setTermsOfAgreementEnabled(manageMyWorkMarket.isStandardTermsEndUserFlag())
			.setTermsOfAgreement(manageMyWorkMarket.getStandardTerms())
			.setCodeOfConductEnabled(manageMyWorkMarket.getStandardInstructionsFlag())
			.setCodeOfConduct(manageMyWorkMarket.getStandardInstructions())
			.setUseWorkMarketPrintout(manageMyWorkMarket.isEnableAssignmentPrintout())
			.setPrintSettingsEndUserTermsEnabled(manageMyWorkMarket.getStandardTermsFlag())
			.setPrintSettingsEndUserTerms(manageMyWorkMarket.getStandardTermsEndUser())
			.setPrintSettingsSignatureEnabled(manageMyWorkMarket.isEnablePrintoutSignature())
			.setPrintSettingsSignature(company.getCustomSignatureLine())
			.setPrintSettingsBadgeEnabled(manageMyWorkMarket.isBadgeIncludedOnPrintout())
			.setProjectBudgetManagementEnabled(manageMyWorkMarket.getBudgetEnabledFlag())
			.setRequireClientProjectEnabled(manageMyWorkMarket.getRequireProjectEnabledFlag())
			.setRequirementSetsEnabled(manageMyWorkMarket.getUseRequirementSets())
			.setShipmentsEnabled(manageMyWorkMarket.getPartsLogisticsEnabledFlag())
			.setSurveysEnabled(manageMyWorkMarket.getAssessmentsEnabled())
			.setAutoRateEnabled(manageMyWorkMarket.getAutoRateEnabledFlag())
			.setUniqueExternalIdEnabled(preference.isExternalIdActive())
			.setUniqueExternalIdDisplayName(preference.getExternalIdDisplayName())
			.setAutoCloseEnabled(manageMyWorkMarket.getAutocloseEnabledFlag())
			.setAutoCloseHours(manageMyWorkMarket.getAutocloseDelayInHours() != null ? String.valueOf(manageMyWorkMarket.getAutocloseDelayInHours()) : null)
			.setDeliverablesEnabled(manageMyWorkMarket.getCustomCloseOutEnabledFlag())
			.setIvrEnabled(manageMyWorkMarket.getIvrEnabledFlag())
			.setAgingAssignmentsEnabled(manageMyWorkMarket.getAgingAssignmentAlertEnabled())
			.setAgingAssignmentsEmailAddress(company.getAgingAlertEmails().isEmpty() ? null : company.getAgingAlertEmails().iterator().next().getEmail())
			.setPaymentTermsDays(manageMyWorkMarket.getPaymentTermsDays())
			.setDocumentsEnabled(!companyOptionsService.hasOption(company, CompanyOption.DOCUMENTS_ENABLED, "false"));

			String printSettingsLogOption;
			if (manageMyWorkMarket.getHideWorkMarketLogoFlag() && !manageMyWorkMarket.getUseCompanyLogoFlag()) {
				printSettingsLogOption = LOGO_OPTION_NONE;
			} else if (!manageMyWorkMarket.getHideWorkMarketLogoFlag() && !manageMyWorkMarket.getUseCompanyLogoFlag()) {
				printSettingsLogOption = LOGO_OPTION_WM;
			} else {
				printSettingsLogOption = LOGO_OPTION_COMPANY;
			}
			configurationDTOBuilder.setPrintSettingsLogoOption(printSettingsLogOption);
	}

	protected void loadAssignmentSettings() {
		getCompany();
		Assert.notNull(company);
		Assert.notNull(configurationDTO);
		if(errors.isEmpty()) {
			companyPreference = company.getCompanyPreference();
			companyPreference.setExternalIdActive(configurationDTO.isUniqueExternalIdEnabled());
			companyPreference.setExternalIdDisplayName(configurationDTO.getUniqueExternalIdDisplayName());
			companyOptionsService.setOption(company, CompanyOption.DOCUMENTS_ENABLED, String.valueOf(configurationDTO.getDocumentsEnabled()));

			agingAssignmentsEmailAddress.setEmail(configurationDTO.getAgingAssignmentsEmailAddress());
			BeanUtils.copyProperties(configurationDTO, manageMyWorkMarketDTO);

			boolean useCompanyLog = LOGO_OPTION_COMPANY.equals(configurationDTO.getPrintSettingsLogoOption());
			boolean hideWorkMarketLogo = useCompanyLog || LOGO_OPTION_NONE.equals(configurationDTO.getPrintSettingsLogoOption());
			manageMyWorkMarketDTO.setCustomSignatureLine(configurationDTO.getPrintSettingsSignature());
			manageMyWorkMarketDTO.setInstantWorkerPoolEnabled(configurationDTO.isInstantNetworkEnabled());
			manageMyWorkMarketDTO.setCustomFieldsEnabledFlag(configurationDTO.isCustomFieldsEnabled());
			manageMyWorkMarketDTO.setStandardTermsEndUserFlag(configurationDTO.isTermsOfAgreementEnabled());
			manageMyWorkMarketDTO.setStandardTerms(configurationDTO.getTermsOfAgreement());
			manageMyWorkMarketDTO.setStandardInstructionsFlag(configurationDTO.isCodeOfConductEnabled());
			manageMyWorkMarketDTO.setStandardInstructions(configurationDTO.getCodeOfConduct());
			manageMyWorkMarketDTO.setEnableAssignmentPrintout(configurationDTO.isUseWorkMarketPrintout());
			manageMyWorkMarketDTO.setHideWorkMarketLogoFlag(hideWorkMarketLogo);
			manageMyWorkMarketDTO.setUseCompanyLogoFlag(useCompanyLog);
			manageMyWorkMarketDTO.setStandardTermsFlag(configurationDTO.isPrintSettingsEndUserTermsEnabled());
			manageMyWorkMarketDTO.setStandardTermsEndUser(configurationDTO.getPrintSettingsEndUserTerms());
			manageMyWorkMarketDTO.setEnablePrintoutSignature(configurationDTO.isPrintSettingsSignatureEnabled());
			manageMyWorkMarketDTO.setBadgeIncludedOnPrintout(configurationDTO.isPrintSettingsBadgeEnabled());
			manageMyWorkMarketDTO.setBudgetEnabledFlag(configurationDTO.isProjectBudgetManagementEnabled());
			manageMyWorkMarketDTO.setRequireProjectEnabledFlag(configurationDTO.isRequireClientProjectEnabled());
			manageMyWorkMarketDTO.setUseRequirementSets(configurationDTO.isRequirementSetsEnabled());
			manageMyWorkMarketDTO.setPartsLogisticsEnabledFlag(configurationDTO.isShipmentsEnabled());
			manageMyWorkMarketDTO.setAssessmentsEnabled(configurationDTO.isSurveysEnabled());
			manageMyWorkMarketDTO.setAutoRateEnabledFlag(configurationDTO.isAutoRateEnabled());
			manageMyWorkMarketDTO.setAutocloseEnabledFlag(configurationDTO.isAutoCloseEnabled());
			manageMyWorkMarketDTO.setAutocloseDelayInHours(Integer.parseInt(configurationDTO.getAutoCloseHours()));
			manageMyWorkMarketDTO.setCustomCloseOutEnabledFlag(configurationDTO.isDeliverablesEnabled());
			manageMyWorkMarketDTO.setIvrEnabledFlag(configurationDTO.isIvrEnabled());
			manageMyWorkMarketDTO.setAgingAssignmentAlertEnabled(configurationDTO.isAgingAssignmentsEnabled());
			manageMyWorkMarketDTO.setPaymentTermsDays(configurationDTO.getPaymentTermsDays());
		}
	}

	protected void saveAssignmentSettings() throws ValidationException {
		if (errors.size() > 0) {
			logErrors(errors);
			throw new ValidationException("Unable to update assignment configuration", errors);
		}
		companyService.saveAssignmentAlertEmailToCompany(company.getId(), agingAssignmentsEmailAddress);
		companyService.updateCompanyPreference(companyPreference);
		profileService.updateManageMyWorkMarket(company.getId(), manageMyWorkMarketDTO);
	}

	protected void validateAssignmentSettings() {
		validateAutoClose(errors);
		validateAgingAssignmentsEmail(errors);
		validateUniqueExternalId(errors);
	}

	private void validateAutoClose(List<ConstraintViolation> errors) {
		if (configurationDTO.isAutoCloseEnabled()) {
			if (StringUtils.isEmpty(configurationDTO.getAutoCloseHours())) {
				errors.add(newConstraintViolation("autoCloseHours", "not_empty"));
			} else {
				Integer autoCloseHours = null;
				try {
					autoCloseHours = Integer.parseInt(configurationDTO.getAutoCloseHours());
				} catch (NumberFormatException e) {
					errors.add(newConstraintViolation("autoCloseHours", "invalid", "autoCloseHours"));
				}

				if (autoCloseHours == null || autoCloseHours < 0) {
					errors.add(newConstraintViolation("autoCloseHours", "manage_my_workmarket.autoclose_delay_in_hours.numeric"));
				}
			}
		}
	}

	private void validateAgingAssignmentsEmail(List<ConstraintViolation> errors) {
		if (configurationDTO.isAgingAssignmentsEnabled()) {
			if (StringUtils.isBlank(configurationDTO.getAgingAssignmentsEmailAddress())) {
				errors.add(newConstraintViolation("agingAssignmentsEmailAddress", "mmw.manage.aging_email.empty"));
			} else if (!EmailValidator.getInstance().isValid(configurationDTO.getAgingAssignmentsEmailAddress())) {
				errors.add(newConstraintViolation("agingAssignmentsEmailAddress", "mmw.manage.aging_email.invalid"));
			}
		}
	}

	private void validateUniqueExternalId(List<ConstraintViolation> errors) {
		if (configurationDTO.isUniqueExternalIdEnabled()) {
			if (StringUtils.isBlank(configurationDTO.getUniqueExternalIdDisplayName())) {
				errors.add(newConstraintViolation("uniqueExternalIdDisplayName", "mmw.manage.unique_id_name.empty"));
			} else if (configurationDTO.getUniqueExternalIdDisplayName().length() > UNIQUE_EXTERNAL_ID_DISPLAY_NAME_MAX_LEN) {
				errors.add(newConstraintViolation("uniqueExternalIdDisplayName", "mmw.manage.unique_id_name.max_length"));
			}
		}
	}

	private ConstraintViolation newConstraintViolation(String property, String error, String... params) {
		ConstraintViolation v = new ConstraintViolation()
			.setProperty(property)
			.setError(error)
			.setWhy(messageHelper.getMessage(error));
		v.addToParams(Lists.newArrayList(params));
		return v;
	}

	private void getCompany() {
		company = companyService.findCompanyById(companyId);
	}

	private void logErrors(List<ConstraintViolation> errors) {
		for (ConstraintViolation error : errors)
			logger.error(error);
	}

	protected void handleValidationException() throws ValidationException {
		if (exception instanceof ValidationException) {
			throw (ValidationException) exception;
		}
	}
}
