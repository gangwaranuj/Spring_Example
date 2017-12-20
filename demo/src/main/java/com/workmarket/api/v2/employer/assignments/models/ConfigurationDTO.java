package com.workmarket.api.v2.employer.assignments.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "AssignmentConfiguration")
@JsonDeserialize(builder = ConfigurationDTO.Builder.class)
public class ConfigurationDTO {
	private boolean customFieldsEnabled;
	private boolean shipmentsEnabled;
	private boolean requirementSetsEnabled;
	private boolean deliverablesEnabled;
	private boolean surveysEnabled;
	private boolean uniqueExternalIdEnabled;
	private String uniqueExternalIdDisplayName;
	private boolean documentsEnabled;
	private boolean followersEnabled;
	private final boolean instantNetworkEnabled;
	private final boolean termsOfAgreementEnabled;
	private final String termsOfAgreement;
	private final boolean codeOfConductEnabled;
	private final String codeOfConduct;
	private final boolean useWorkMarketPrintout;
	private final String printSettingsLogoOption;
	private final boolean printSettingsEndUserTermsEnabled;
	private final String printSettingsEndUserTerms;
	private final boolean printSettingsSignatureEnabled;
	private final String printSettingsSignature;
	private final boolean printSettingsBadgeEnabled;
	private final boolean projectBudgetManagementEnabled;
	private final boolean requireClientProjectEnabled;
	private final boolean workerRequirementsEnabled;
	private final boolean autoRateEnabled;
	private final boolean autoCloseEnabled;
	private final String autoCloseHours;
	private final boolean ivrEnabled;
	private final boolean agingAssignmentsEnabled;
	private final String agingAssignmentsEmailAddress;
	private final int paymentTermsDays;
	private final boolean disablePriceNegotiation;

	private ConfigurationDTO(Builder builder) {
		this.customFieldsEnabled = builder.customFieldsEnabled;
		this.shipmentsEnabled = builder.shipmentsEnabled;
		this.requirementSetsEnabled = builder.requirementSetsEnabled;
		this.deliverablesEnabled = builder.deliverablesEnabled;
		this.surveysEnabled = builder.surveysEnabled;
		this.uniqueExternalIdEnabled = builder.uniqueExternalIdEnabled;
		this.uniqueExternalIdDisplayName = builder.uniqueExternalIdDisplayName;
		this.documentsEnabled = builder.documentsEnabled;
		this.followersEnabled = builder.followersEnabled;
		this.instantNetworkEnabled = builder.instantNetworkEnabled;
		this.termsOfAgreementEnabled = builder.termsOfAgreementEnabled;
		this.termsOfAgreement = builder.termsOfAgreement;
		this.codeOfConductEnabled = builder.codeOfConductEnabled;
		this.codeOfConduct = builder.codeOfConduct;
		this.useWorkMarketPrintout = builder.useWorkMarketPrintout;
		this.printSettingsLogoOption = builder.printSettingsLogoOption;
		this.printSettingsEndUserTermsEnabled = builder.printSettingsEndUserTermsEnabled;
		this.printSettingsEndUserTerms = builder.printSettingsEndUserTerms;
		this.printSettingsSignatureEnabled = builder.printSettingsSignatureEnabled;
		this.printSettingsSignature = builder.printSettingsSignature;
		this.printSettingsBadgeEnabled = builder.printSettingsBadgeEnabled;
		this.projectBudgetManagementEnabled = builder.projectBudgetManagementEnabled;
		this.requireClientProjectEnabled = builder.requireClientProjectEnabled;
		this.workerRequirementsEnabled = builder.workerRequirementsEnabled;
		this.autoRateEnabled = builder.autoRateEnabled;
		this.autoCloseEnabled = builder.autoCloseEnabled;
		this.autoCloseHours = builder.autoCloseHours;
		this.ivrEnabled = builder.ivrEnabled;
		this.agingAssignmentsEnabled = builder.agingAssignmentsEnabled;
		this.agingAssignmentsEmailAddress = builder.agingAssignmentsEmailAddress;
		this.paymentTermsDays = builder.paymentTermsDays;
		this.disablePriceNegotiation = builder.disablePriceNegotiation;
	}

	public ConfigurationDTO(ConfigurationDTO configurationDTO) {
		this.customFieldsEnabled = configurationDTO.customFieldsEnabled;
		this.shipmentsEnabled = configurationDTO.shipmentsEnabled;
		this.requirementSetsEnabled = configurationDTO.requirementSetsEnabled;
		this.deliverablesEnabled = configurationDTO.deliverablesEnabled;
		this.surveysEnabled = configurationDTO.surveysEnabled;
		this.uniqueExternalIdEnabled = configurationDTO.uniqueExternalIdEnabled;
		this.uniqueExternalIdDisplayName = configurationDTO.uniqueExternalIdDisplayName;
		this.documentsEnabled = configurationDTO.documentsEnabled;
		this.followersEnabled = configurationDTO.followersEnabled;
		this.instantNetworkEnabled = configurationDTO.instantNetworkEnabled;
		this.termsOfAgreementEnabled = configurationDTO.termsOfAgreementEnabled;
		this.termsOfAgreement = configurationDTO.termsOfAgreement;
		this.codeOfConductEnabled = configurationDTO.codeOfConductEnabled;
		this.codeOfConduct = configurationDTO.codeOfConduct;
		this.useWorkMarketPrintout = configurationDTO.useWorkMarketPrintout;
		this.printSettingsLogoOption = configurationDTO.printSettingsLogoOption;
		this.printSettingsEndUserTermsEnabled = configurationDTO.printSettingsEndUserTermsEnabled;
		this.printSettingsEndUserTerms = configurationDTO.printSettingsEndUserTerms;
		this.printSettingsSignatureEnabled = configurationDTO.printSettingsSignatureEnabled;
		this.printSettingsSignature = configurationDTO.printSettingsSignature;
		this.printSettingsBadgeEnabled = configurationDTO.printSettingsBadgeEnabled;
		this.projectBudgetManagementEnabled = configurationDTO.projectBudgetManagementEnabled;
		this.requireClientProjectEnabled = configurationDTO.requireClientProjectEnabled;
		this.workerRequirementsEnabled = configurationDTO.workerRequirementsEnabled;
		this.autoRateEnabled = configurationDTO.autoRateEnabled;
		this.autoCloseEnabled = configurationDTO.autoCloseEnabled;
		this.autoCloseHours = configurationDTO.autoCloseHours;
		this.ivrEnabled = configurationDTO.ivrEnabled;
		this.agingAssignmentsEnabled = configurationDTO.agingAssignmentsEnabled;
		this.agingAssignmentsEmailAddress = configurationDTO.agingAssignmentsEmailAddress;
		this.paymentTermsDays = configurationDTO.paymentTermsDays;
		this.disablePriceNegotiation = configurationDTO.disablePriceNegotiation;
	}

	@ApiModelProperty(name = "customFieldsEnabled")
	@JsonProperty("customFieldsEnabled")
	public boolean isCustomFieldsEnabled() {
		return customFieldsEnabled;
	}

	@ApiModelProperty(name = "shipmentsEnabled")
	@JsonProperty("shipmentsEnabled")
	public boolean isShipmentsEnabled() {
		return shipmentsEnabled;
	}

	@ApiModelProperty(name = "requirementSetsEnabled")
	@JsonProperty("requirementSetsEnabled")
	public boolean isRequirementSetsEnabled() {
		return requirementSetsEnabled;
	}

	@ApiModelProperty(name = "deliverablesEnabled")
	@JsonProperty("deliverablesEnabled")
	public boolean isDeliverablesEnabled() {
		return deliverablesEnabled;
	}

	@ApiModelProperty(name = "surveysEnabled")
	@JsonProperty("surveysEnabled")
	public boolean isSurveysEnabled() {
		return surveysEnabled;
	}

	@ApiModelProperty(name = "uniqueExternalIdEnabled")
	@JsonProperty("uniqueExternalIdEnabled")
	public boolean isUniqueExternalIdEnabled() {
		return uniqueExternalIdEnabled;
	}

	@ApiModelProperty(name = "uniqueExternalIdDisplayName")
	@JsonProperty("uniqueExternalIdDisplayName")
	public String getUniqueExternalIdDisplayName() {
		return uniqueExternalIdDisplayName;
	}

	@ApiModelProperty(name = "instantNetworkEnabled")
	@JsonProperty("instantNetworkEnabled")
	public boolean isInstantNetworkEnabled() {
		return instantNetworkEnabled;
	}

	@ApiModelProperty(name = "termsOfAgreementEnabled")
	@JsonProperty("termsOfAgreementEnabled")
	public boolean isTermsOfAgreementEnabled() {
		return termsOfAgreementEnabled;
	}

	@ApiModelProperty(name = "termsOfAgreement")
	@JsonProperty("termsOfAgreement")
	public String getTermsOfAgreement() {
		return termsOfAgreement;
	}

	@ApiModelProperty(name = "codeOfConductEnabled")
	@JsonProperty("codeOfConductEnabled")
	public boolean isCodeOfConductEnabled() {
		return codeOfConductEnabled;
	}

	@ApiModelProperty(name = "codeOfConduct")
	@JsonProperty("codeOfConduct")
	public String getCodeOfConduct() {
		return codeOfConduct;
	}

	@ApiModelProperty(name = "documentsEnabled")
	@JsonProperty("documentsEnabled")
	public boolean getDocumentsEnabled() {
		return documentsEnabled;
	}

	@ApiModelProperty(name = "autoCloseHours")
	@JsonProperty("autoCloseHours")
	public String getAutoCloseHours() {
		return autoCloseHours;
	}

	@ApiModelProperty(name = "useWorkMarketPrintout")
	@JsonProperty("useWorkMarketPrintout")
	public boolean isUseWorkMarketPrintout() {
		return useWorkMarketPrintout;
	}

	@ApiModelProperty(name = "printSettingsLogoOption")
	@JsonProperty("printSettingsLogoOption")
	public String getPrintSettingsLogoOption() {
		return printSettingsLogoOption;
	}

	@ApiModelProperty(name = "printSettingsEndUserTermsEnabled")
	@JsonProperty("printSettingsEndUserTermsEnabled")
	public boolean isPrintSettingsEndUserTermsEnabled() {
		return printSettingsEndUserTermsEnabled;
	}

	@ApiModelProperty(name = "printSettingsEndUserTerms")
	@JsonProperty("printSettingsEndUserTerms")
	public String getPrintSettingsEndUserTerms() {
		return printSettingsEndUserTerms;
	}

	@ApiModelProperty(name = "printSettingsSignatureEnabled")
	@JsonProperty("printSettingsSignatureEnabled")
	public boolean isPrintSettingsSignatureEnabled() {
		return printSettingsSignatureEnabled;
	}

	@ApiModelProperty(name = "printSettingsSignature")
	@JsonProperty("printSettingsSignature")
	public String getPrintSettingsSignature() {
		return printSettingsSignature;
	}

	@ApiModelProperty(name = "printSettingsBadgeEnabled")
	@JsonProperty("printSettingsBadgeEnabled")
	public boolean isPrintSettingsBadgeEnabled() {
		return printSettingsBadgeEnabled;
	}

	@ApiModelProperty(name = "projectBudgetManagementEnabled")
	@JsonProperty("projectBudgetManagementEnabled")
	public boolean isProjectBudgetManagementEnabled() {
		return projectBudgetManagementEnabled;
	}

	@ApiModelProperty(name = "requireClientProjectEnabled")
	@JsonProperty("requireClientProjectEnabled")
	public boolean isRequireClientProjectEnabled() {
		return requireClientProjectEnabled;
	}

	@ApiModelProperty(name = "workerRequirementsEnabled")
	@JsonProperty("workerRequirementsEnabled")
	public boolean isWorkerRequirementsEnabled() {
		return workerRequirementsEnabled;
	}

	@ApiModelProperty(name = "autoRateEnabled")
	@JsonProperty("autoRateEnabled")
	public boolean isAutoRateEnabled() {
		return autoRateEnabled;
	}

	@ApiModelProperty(name = "autoCloseEnabled")
	@JsonProperty("autoCloseEnabled")
	public boolean isAutoCloseEnabled() {
		return autoCloseEnabled;
	}

	@ApiModelProperty(name = "ivrEnabled")
	@JsonProperty("ivrEnabled")
	public boolean isIvrEnabled() {
		return ivrEnabled;
	}

	@ApiModelProperty(name = "agingAssignmentsEnabled")
	@JsonProperty("agingAssignmentsEnabled")
	public boolean isAgingAssignmentsEnabled() {
		return agingAssignmentsEnabled;
	}

	@ApiModelProperty(name = "agingAssignmentsEmailAddress")
	@JsonProperty("agingAssignmentsEmailAddress")
	public String getAgingAssignmentsEmailAddress() {
		return agingAssignmentsEmailAddress;
	}

	@ApiModelProperty(name = "followersEnabled")
	@JsonProperty("followersEnabled")
	public boolean isFollowersEnabled() {
		return followersEnabled;
	}

	public int getPaymentTermsDays() {
		return paymentTermsDays;
	}

	@ApiModelProperty(name = "disablePriceNegotiation")
	@JsonProperty("disablePriceNegotiation")
	public boolean getDisablePriceNegotiation() {
		return disablePriceNegotiation;
	}

	public static class Builder implements AbstractBuilder<ConfigurationDTO> {
		private boolean customFieldsEnabled = false;
		private boolean shipmentsEnabled = false;
		private boolean requirementSetsEnabled = false;
		private boolean deliverablesEnabled = false;
		private boolean surveysEnabled = false;
		private boolean uniqueExternalIdEnabled = false;
		private String uniqueExternalIdDisplayName;
		private boolean documentsEnabled = true;
		private boolean followersEnabled = false;
		private boolean instantNetworkEnabled = true;
		private boolean termsOfAgreementEnabled = true;
		private String termsOfAgreement;
		private boolean codeOfConductEnabled = true;
		private String codeOfConduct;
		private boolean useWorkMarketPrintout = true;
		private String printSettingsLogoOption;
		private boolean printSettingsEndUserTermsEnabled;
		private String printSettingsEndUserTerms;
		private boolean printSettingsSignatureEnabled;
		private String printSettingsSignature;
		private boolean printSettingsBadgeEnabled;
		private boolean projectBudgetManagementEnabled = false;
		private boolean requireClientProjectEnabled = false;
		private boolean workerRequirementsEnabled = false;
		private boolean autoRateEnabled = false;
		private boolean autoCloseEnabled = false;
		private String autoCloseHours;
		private boolean ivrEnabled = false;
		private boolean agingAssignmentsEnabled = false;
		private String agingAssignmentsEmailAddress;
		private int paymentTermsDays = 0;
		private boolean disablePriceNegotiation = false;

		public Builder() {}

		public Builder(ConfigurationDTO configurationDTO) {
			this.customFieldsEnabled = configurationDTO.customFieldsEnabled;
			this.shipmentsEnabled = configurationDTO.shipmentsEnabled;
			this.requirementSetsEnabled = configurationDTO.requirementSetsEnabled;
			this.deliverablesEnabled = configurationDTO.deliverablesEnabled;
			this.surveysEnabled = configurationDTO.surveysEnabled;
			this.uniqueExternalIdEnabled = configurationDTO.uniqueExternalIdEnabled;
			this.uniqueExternalIdDisplayName = configurationDTO.uniqueExternalIdDisplayName;
			this.documentsEnabled = configurationDTO.documentsEnabled;
			this.followersEnabled = configurationDTO.followersEnabled;
			this.instantNetworkEnabled =  configurationDTO.instantNetworkEnabled;
			this.termsOfAgreementEnabled =  configurationDTO.termsOfAgreementEnabled;
			this.termsOfAgreement =  configurationDTO.termsOfAgreement;
			this.codeOfConductEnabled =  configurationDTO.codeOfConductEnabled;
			this.codeOfConduct =  configurationDTO.codeOfConduct;
			this.useWorkMarketPrintout =  configurationDTO.useWorkMarketPrintout;
			this.printSettingsLogoOption =  configurationDTO.printSettingsLogoOption;
			this.printSettingsEndUserTermsEnabled =  configurationDTO.printSettingsEndUserTermsEnabled;
			this.printSettingsEndUserTerms =  configurationDTO.printSettingsEndUserTerms;
			this.printSettingsSignatureEnabled =  configurationDTO.printSettingsSignatureEnabled;
			this.printSettingsSignature =  configurationDTO.printSettingsSignature;
			this.printSettingsBadgeEnabled =  configurationDTO.printSettingsBadgeEnabled;
			this.projectBudgetManagementEnabled =  configurationDTO.projectBudgetManagementEnabled;
			this.requireClientProjectEnabled =  configurationDTO.requireClientProjectEnabled;
			this.workerRequirementsEnabled =  configurationDTO.workerRequirementsEnabled;
			this.autoRateEnabled =  configurationDTO.autoRateEnabled;
			this.autoCloseEnabled =  configurationDTO.autoCloseEnabled;
			this.autoCloseHours =  configurationDTO.autoCloseHours;
			this.ivrEnabled =  configurationDTO.ivrEnabled;
			this.agingAssignmentsEnabled =  configurationDTO.agingAssignmentsEnabled;
			this.agingAssignmentsEmailAddress =  configurationDTO.agingAssignmentsEmailAddress;
			this.paymentTermsDays = configurationDTO.paymentTermsDays;
			this.disablePriceNegotiation = configurationDTO.disablePriceNegotiation;
		}

		@JsonProperty("customFieldsEnabled") public Builder setCustomFieldsEnabled(boolean customFieldsEnabled) {
			this.customFieldsEnabled = customFieldsEnabled;
			return this;
		}

		@JsonProperty("shipmentsEnabled") public Builder setShipmentsEnabled(boolean shipmentsEnabled) {
			this.shipmentsEnabled = shipmentsEnabled;
			return this;
		}

		@JsonProperty("requirementSetsEnabled") public Builder setRequirementSetsEnabled(boolean requirementSetsEnabled) {
			this.requirementSetsEnabled = requirementSetsEnabled;
			return this;
		}

		@JsonProperty("deliverablesEnabled") public Builder setDeliverablesEnabled(boolean deliverablesEnabled) {
			this.deliverablesEnabled = deliverablesEnabled;
			return this;
		}

		@JsonProperty("surveysEnabled") public Builder setSurveysEnabled(boolean surveysEnabled) {
			this.surveysEnabled = surveysEnabled;
			return this;
		}

		@JsonProperty("uniqueExternalIdEnabled") public Builder setUniqueExternalIdEnabled(boolean uniqueExternalIdEnabled) {
			this.uniqueExternalIdEnabled = uniqueExternalIdEnabled;
			return this;
		}

		@JsonProperty("uniqueExternalIdDisplayName") public Builder setUniqueExternalIdDisplayName(String uniqueExternalIdDisplayName) {
			this.uniqueExternalIdDisplayName = uniqueExternalIdDisplayName;
			return this;
		}

		@JsonProperty("instantNetworkEnabled")
		public Builder setInstantNetworkEnabled(boolean instantNetworkEnabled) {
			this.instantNetworkEnabled = instantNetworkEnabled;
			return this;
		}

		@JsonProperty("termsOfAgreementEnabled")
		public Builder setTermsOfAgreementEnabled(boolean termsOfAgreementEnabled) {
			this.termsOfAgreementEnabled = termsOfAgreementEnabled;
			return this;
		}

		@JsonProperty("termsOfAgreement")
		public Builder setTermsOfAgreement(String termsOfAgreement) {
			this.termsOfAgreement = termsOfAgreement;
			return this;
		}

		@JsonProperty("codeOfConductEnabled")
		public Builder setCodeOfConductEnabled(boolean codeOfConductEnabled) {
			this.codeOfConductEnabled = codeOfConductEnabled;
			return this;
		}

		@JsonProperty("codeOfConduct")
		public Builder setCodeOfConduct(String codeOfConduct) {
			this.codeOfConduct = codeOfConduct;
			return this;
		}

		@JsonProperty("useWorkMarketPrintout")
		public Builder setUseWorkMarketPrintout(boolean useWorkMarketPrintout) {
			this.useWorkMarketPrintout = useWorkMarketPrintout;
			return this;
		}

		@JsonProperty("printSettingsLogoOption")
		public Builder setPrintSettingsLogoOption(String printSettingsLogoOption) {
			this.printSettingsLogoOption = printSettingsLogoOption;
			return this;
		}

		@JsonProperty("printSettingsEndUserTermsEnabled")
		public Builder setPrintSettingsEndUserTermsEnabled(boolean printSettingsEndUserTermsEnabled) {
			this.printSettingsEndUserTermsEnabled = printSettingsEndUserTermsEnabled;
			return this;
		}

		@JsonProperty("printSettingsEndUserTerms")
		public Builder setPrintSettingsEndUserTerms(String printSettingsEndUserTerms) {
			this.printSettingsEndUserTerms = printSettingsEndUserTerms;
			return this;
		}

		@JsonProperty("printSettingsSignatureEnabled")
		public Builder setPrintSettingsSignatureEnabled(boolean printSettingsSignatureEnabled) {
			this.printSettingsSignatureEnabled = printSettingsSignatureEnabled;
			return this;
		}

		@JsonProperty("printSettingsSignature")
		public Builder setPrintSettingsSignature(String printSettingsSignature) {
			this.printSettingsSignature = printSettingsSignature;
			return this;
		}

		@JsonProperty("printSettingsBadgeEnabled")
		public Builder setPrintSettingsBadgeEnabled(boolean printSettingsBadgeEnabled) {
			this.printSettingsBadgeEnabled = printSettingsBadgeEnabled;
			return this;
		}

		@JsonProperty("projectBudgetManagementEnabled")
		public Builder setProjectBudgetManagementEnabled(boolean projectBudgetManagementEnabled) {
			this.projectBudgetManagementEnabled = projectBudgetManagementEnabled;
			return this;
		}

		@JsonProperty("requireClientProjectEnabled")
		public Builder setRequireClientProjectEnabled(boolean requireClientProjectEnabled) {
			this.requireClientProjectEnabled = requireClientProjectEnabled;
			return this;
		}

		@JsonProperty("workerRequirementsEnabled")
		public Builder setWorkerRequirementsEnabled(boolean workerRequirementsEnabled) {
			this.workerRequirementsEnabled = workerRequirementsEnabled;
			return this;
		}

		@JsonProperty("autoRateEnabled")
		public Builder setAutoRateEnabled(boolean autoRateEnabled) {
			this.autoRateEnabled = autoRateEnabled;
			return this;
		}

		@JsonProperty("autoCloseEnabled")
		public Builder setAutoCloseEnabled(boolean autoCloseEnabled) {
			this.autoCloseEnabled = autoCloseEnabled;
			return this;
		}

		@JsonProperty("autoCloseHours")
		public Builder setAutoCloseHours(String autoCloseHours) {
			this.autoCloseHours = autoCloseHours;
			return this;
		}

		@JsonProperty("ivrEnabled")
		public Builder setIvrEnabled(boolean ivrEnabled) {
			this.ivrEnabled = ivrEnabled;
			return this;
		}

		@JsonProperty("agingAssignmentsEnabled")
		public Builder setAgingAssignmentsEnabled(boolean agingAssignmentsEnabled) {
			this.agingAssignmentsEnabled = agingAssignmentsEnabled;
			return this;
		}

		@JsonProperty("agingAssignmentsEmailAddress")
		public Builder setAgingAssignmentsEmailAddress(String agingAssignmentsEmailAddress) {
			this.agingAssignmentsEmailAddress = agingAssignmentsEmailAddress;
			return this;
		}

		@JsonProperty("documentsEnabled")
		public Builder setDocumentsEnabled(boolean documentsEnabled) {
			this.documentsEnabled = documentsEnabled;
			return this;
		}

		@JsonProperty("followersEnabled") public Builder setFollowersEnabled(boolean followersEnabled) {
			this.followersEnabled = followersEnabled;
			return this;
		}

		@JsonProperty("paymentTermsDays")
		public Builder setPaymentTermsDays(int paymentTermsDays) {
			this.paymentTermsDays = paymentTermsDays;
			return this;
		}

		@JsonProperty("disablePriceNegotiation")
		public Builder setDisablePriceNegotiation(boolean disablePriceNegotiation) {
			this.disablePriceNegotiation = disablePriceNegotiation;
			return this;
		}

		@Override
		public ConfigurationDTO build() {
			return new ConfigurationDTO(this);
		}
	}
}