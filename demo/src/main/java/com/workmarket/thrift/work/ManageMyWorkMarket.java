package com.workmarket.thrift.work;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;

public class ManageMyWorkMarket implements Serializable {
	private static final long serialVersionUID = 1L;

	private boolean teamWorkflowEnabledFlag;
	private boolean delegationEnabledFlag;
	private boolean customFieldsEnabledFlag;
	private boolean customFormsEnabledFlag;
	private boolean customCloseOutEnabledFlag;
	private boolean autocloseEnabledFlag;
	private int autocloseDelayInHours;
	private boolean hideWorkMarketLogoFlag;
	private boolean useCompanyLogoFlag;
	private boolean autoRateEnabledFlag;
	private boolean partsLogisticsEnabledFlag;
	private boolean standardTermsFlag;
	private boolean standardInstructionsFlag;
	private String standardTerms;
	private String standardTermsEndUser;
	private String standardInstructions;
	private boolean serviceContactFlag;
	private boolean buyerSupportContactFlag;
	private boolean checkinRequiredFlag;
	private boolean ivrEnabledFlag;
	private String serviceContactFirstName;
	private String serviceContactLastName;
	private String serviceContactPhone;
	private String serviceContactPhoneExtension;
	private String serviceContactEMail;
	private String serviceContactInstructions;
	private String buyerSupportContactFirstName;
	private String buyerSupportContactLastName;
	private String buyerSupportContactPhone;
	private String buyerSupportContactPhoneExtension;
	private String buyerSupportContactEMail;
	private String buyerSupportContactInstructions;
	private int paymentTermsDays;
	private boolean sameServiceContact;
	private boolean useMaxSpendPricingDisplayModeFlag;
	private boolean assessmentsEnabled;
	private boolean autoPayEnabled;
	private boolean autoSendInvoiceEmail;
	private boolean enableAssignmentPrintout;
	private boolean standardTermsEndUserFlag;
	private boolean enablePrintoutSignature;
	private boolean badgeIncludedOnPrintout;
	private boolean badgeShowClientName;
	private boolean disablePriceNegotiation;
	private boolean showCheckoutNotesFlag;
	private boolean checkoutNoteRequiredFlag;
	private String checkoutNoteInstructions;
	private String checkinContactName;
	private String checkinContactPhone;
	private boolean assignToFirstResource;
	private Boolean showInFeed;
	private boolean useRequirementSets;
	private boolean smartRoute;

	public ManageMyWorkMarket() {
	}

	public ManageMyWorkMarket(
			boolean teamWorkflowEnabledFlag,
			boolean delegationEnabledFlag,
			boolean customFieldsEnabledFlag,
			boolean customFormsEnabledFlag,
			boolean customCloseOutEnabledFlag,
			boolean autocloseEnabledFlag,
			int autocloseDelayInHours,
			boolean hideWorkMarketLogoFlag,
			boolean useCompanyLogoFlag,
			boolean autoRateEnabledFlag,
			boolean partsLogisticsEnabledFlag,
			boolean standardTermsFlag,
			boolean standardInstructionsFlag,
			String standardTerms,
			String standardTermsEndUser,
			String standardInstructions,
			boolean serviceContactFlag,
			boolean buyerSupportContactFlag,
			boolean checkinRequiredFlag,
			boolean ivrEnabledFlag,
			String serviceContactFirstName,
			String serviceContactLastName,
			String serviceContactPhone,
			String serviceContactPhoneExtension,
			String serviceContactEMail,
			String serviceContactInstructions,
			String buyerSupportContactFirstName,
			String buyerSupportContactLastName,
			String buyerSupportContactPhone,
			String buyerSupportContactPhoneExtension,
			String buyerSupportContactEMail,
			String buyerSupportContactInstructions,
			int paymentTermsDays,
			boolean sameServiceContact,
			boolean useMaxSpendPricingDisplayModeFlag,
			boolean assessmentsEnabled,
			boolean autoPayEnabled,
			boolean autoSendInvoiceEmail,
			boolean enableAssignmentPrintout,
			boolean standardTermsEndUserFlag,
			boolean enablePrintoutSignature,
			boolean badgeIncludedOnPrintout,
			boolean badgeShowClientName,
			boolean disablePriceNegotiation,
			boolean showCheckoutNotesFlag,
			boolean checkoutNoteRequiredFlag,
			String checkoutNoteInstructions,
			String checkinContactName,
			String checkinContactPhone,
			boolean assignToFirstResource) {
		this();
		this.teamWorkflowEnabledFlag = teamWorkflowEnabledFlag;
		this.delegationEnabledFlag = delegationEnabledFlag;
		this.customFieldsEnabledFlag = customFieldsEnabledFlag;
		this.customFormsEnabledFlag = customFormsEnabledFlag;
		this.customCloseOutEnabledFlag = customCloseOutEnabledFlag;
		this.autocloseEnabledFlag = autocloseEnabledFlag;
		this.autocloseDelayInHours = autocloseDelayInHours;
		this.hideWorkMarketLogoFlag = hideWorkMarketLogoFlag;
		this.useCompanyLogoFlag = useCompanyLogoFlag;
		this.autoRateEnabledFlag = autoRateEnabledFlag;
		this.partsLogisticsEnabledFlag = partsLogisticsEnabledFlag;
		this.standardTermsFlag = standardTermsFlag;
		this.standardTermsEndUserFlag = standardTermsEndUserFlag;
		this.standardInstructionsFlag = standardInstructionsFlag;
		this.standardTerms = standardTerms;
		this.standardInstructions = standardInstructions;
		this.serviceContactFlag = serviceContactFlag;
		this.buyerSupportContactFlag = buyerSupportContactFlag;
		this.checkinRequiredFlag = checkinRequiredFlag;
		this.ivrEnabledFlag = ivrEnabledFlag;
		this.serviceContactFirstName = serviceContactFirstName;
		this.serviceContactLastName = serviceContactLastName;
		this.serviceContactPhone = serviceContactPhone;
		this.serviceContactPhoneExtension = serviceContactPhoneExtension;
		this.serviceContactEMail = serviceContactEMail;
		this.serviceContactInstructions = serviceContactInstructions;
		this.buyerSupportContactFirstName = buyerSupportContactFirstName;
		this.buyerSupportContactLastName = buyerSupportContactLastName;
		this.buyerSupportContactPhone = buyerSupportContactPhone;
		this.buyerSupportContactPhoneExtension = buyerSupportContactPhoneExtension;
		this.buyerSupportContactEMail = buyerSupportContactEMail;
		this.buyerSupportContactInstructions = buyerSupportContactInstructions;
		this.paymentTermsDays = paymentTermsDays;
		this.sameServiceContact = sameServiceContact;
		this.useMaxSpendPricingDisplayModeFlag = useMaxSpendPricingDisplayModeFlag;
		this.assessmentsEnabled = assessmentsEnabled;
		this.autoPayEnabled = autoPayEnabled;
		this.autoSendInvoiceEmail = autoSendInvoiceEmail;
		this.enableAssignmentPrintout = enableAssignmentPrintout;
		this.enablePrintoutSignature = enablePrintoutSignature;
		this.standardTermsEndUser = standardTermsEndUser;
		this.badgeIncludedOnPrintout = badgeIncludedOnPrintout;
		this.badgeShowClientName = badgeShowClientName;
		this.disablePriceNegotiation = disablePriceNegotiation;
		this.showCheckoutNotesFlag = showCheckoutNotesFlag;
		this.checkoutNoteRequiredFlag = checkoutNoteRequiredFlag;
		this.checkoutNoteInstructions = checkoutNoteInstructions;
		this.checkinContactName = checkinContactName;
		this.checkinContactPhone = checkinContactPhone;
		this.assignToFirstResource = assignToFirstResource;
	}

	public boolean isTeamWorkflowEnabledFlag() {
		return this.teamWorkflowEnabledFlag;
	}

	public ManageMyWorkMarket setTeamWorkflowEnabledFlag(boolean teamWorkflowEnabledFlag) {
		this.teamWorkflowEnabledFlag = teamWorkflowEnabledFlag;
		return this;
	}

	public boolean isDelegationEnabledFlag() {
		return this.delegationEnabledFlag;
	}

	public ManageMyWorkMarket setDelegationEnabledFlag(boolean delegationEnabledFlag) {
		this.delegationEnabledFlag = delegationEnabledFlag;
		return this;
	}

	public boolean isCustomFieldsEnabledFlag() {
		return this.customFieldsEnabledFlag;
	}

	public ManageMyWorkMarket setCustomFieldsEnabledFlag(boolean customFieldsEnabledFlag) {
		this.customFieldsEnabledFlag = customFieldsEnabledFlag;
		return this;
	}

	public boolean isCustomFormsEnabledFlag() {
		return this.customFormsEnabledFlag;
	}

	public ManageMyWorkMarket setCustomFormsEnabledFlag(boolean customFormsEnabledFlag) {
		this.customFormsEnabledFlag = customFormsEnabledFlag;
		return this;
	}

	public boolean isCustomCloseOutEnabledFlag() {
		return this.customCloseOutEnabledFlag;
	}

	public ManageMyWorkMarket setCustomCloseOutEnabledFlag(boolean customCloseOutEnabledFlag) {
		this.customCloseOutEnabledFlag = customCloseOutEnabledFlag;
		return this;
	}

	public boolean isAutocloseEnabledFlag() {
		return this.autocloseEnabledFlag;
	}

	public ManageMyWorkMarket setAutocloseEnabledFlag(boolean autocloseEnabledFlag) {
		this.autocloseEnabledFlag = autocloseEnabledFlag;
		return this;
	}

	public int getAutocloseDelayInHours() {
		return this.autocloseDelayInHours;
	}

	public ManageMyWorkMarket setAutocloseDelayInHours(int autocloseDelayInHours) {
		this.autocloseDelayInHours = autocloseDelayInHours;
		return this;
	}

	public boolean isSetAutocloseDelayInHours() {
		return (autocloseDelayInHours > 0);
	}

	public boolean isHideWorkMarketLogoFlag() {
		return this.hideWorkMarketLogoFlag;
	}

	public ManageMyWorkMarket setHideWorkMarketLogoFlag(boolean hideWorkMarketLogoFlag) {
		this.hideWorkMarketLogoFlag = hideWorkMarketLogoFlag;
		return this;
	}

	public boolean isUseCompanyLogoFlag() {
		return this.useCompanyLogoFlag;
	}

	public ManageMyWorkMarket setUseCompanyLogoFlag(boolean useCompanyLogoFlag) {
		this.useCompanyLogoFlag = useCompanyLogoFlag;
		return this;
	}

	public boolean isAutoRateEnabledFlag() {
		return this.autoRateEnabledFlag;
	}

	public ManageMyWorkMarket setAutoRateEnabledFlag(boolean autoRateEnabledFlag) {
		this.autoRateEnabledFlag = autoRateEnabledFlag;
		return this;
	}

	public boolean isPartsLogisticsEnabledFlag() {
		return this.partsLogisticsEnabledFlag;
	}

	public ManageMyWorkMarket setPartsLogisticsEnabledFlag(boolean partsLogisticsEnabledFlag) {
		this.partsLogisticsEnabledFlag = partsLogisticsEnabledFlag;
		return this;
	}

	public boolean isStandardTermsFlag() {
		return this.standardTermsFlag;
	}

	public ManageMyWorkMarket setStandardTermsFlag(boolean standardTermsFlag) {
		this.standardTermsFlag = standardTermsFlag;
		return this;
	}

	public boolean isStandardInstructionsFlag() {
		return this.standardInstructionsFlag;
	}

	public ManageMyWorkMarket setStandardInstructionsFlag(boolean standardInstructionsFlag) {
		this.standardInstructionsFlag = standardInstructionsFlag;
		return this;
	}

	public String getStandardTerms() {
		return this.standardTerms;
	}

	public ManageMyWorkMarket setStandardTerms(String standardTerms) {
		this.standardTerms = standardTerms;
		return this;
	}

	public boolean isSetStandardTerms() {
		return this.standardTerms != null;
	}

	public String getStandardTermsEndUser() {
		return standardTermsEndUser;
	}

	public ManageMyWorkMarket setStandardTermsEndUser(String standardTermsEndUser) {
		this.standardTermsEndUser = standardTermsEndUser;
		return this;
	}

	public String getStandardInstructions() {
		return this.standardInstructions;
	}

	public ManageMyWorkMarket setStandardInstructions(String standardInstructions) {
		this.standardInstructions = standardInstructions;
		return this;
	}

	public boolean isSetStandardInstructions() {
		return this.standardInstructions != null;
	}

	public boolean isServiceContactFlag() {
		return this.serviceContactFlag;
	}

	public ManageMyWorkMarket setServiceContactFlag(boolean serviceContactFlag) {
		this.serviceContactFlag = serviceContactFlag;
		return this;
	}

	public boolean isBuyerSupportContactFlag() {
		return this.buyerSupportContactFlag;
	}

	public ManageMyWorkMarket setBuyerSupportContactFlag(boolean buyerSupportContactFlag) {
		this.buyerSupportContactFlag = buyerSupportContactFlag;
		return this;
	}

	public boolean isCheckinRequiredFlag() {
		return this.checkinRequiredFlag;
	}

	public ManageMyWorkMarket setCheckinRequiredFlag(boolean checkinRequiredFlag) {
		this.checkinRequiredFlag = checkinRequiredFlag;
		return this;
	}

	public boolean isIvrEnabledFlag() {
		return this.ivrEnabledFlag;
	}

	public ManageMyWorkMarket setIvrEnabledFlag(boolean ivrEnabledFlag) {
		this.ivrEnabledFlag = ivrEnabledFlag;
		return this;
	}

	public String getServiceContactFirstName() {
		return this.serviceContactFirstName;
	}

	public ManageMyWorkMarket setServiceContactFirstName(String serviceContactFirstName) {
		this.serviceContactFirstName = serviceContactFirstName;
		return this;
	}

	public boolean isSetServiceContactFirstName() {
		return this.serviceContactFirstName != null;
	}

	public String getServiceContactLastName() {
		return this.serviceContactLastName;
	}

	public ManageMyWorkMarket setServiceContactLastName(String serviceContactLastName) {
		this.serviceContactLastName = serviceContactLastName;
		return this;
	}

	public boolean isSetServiceContactLastName() {
		return this.serviceContactLastName != null;
	}

	public String getServiceContactPhone() {
		return this.serviceContactPhone;
	}

	public ManageMyWorkMarket setServiceContactPhone(String serviceContactPhone) {
		this.serviceContactPhone = serviceContactPhone;
		return this;
	}

	public boolean isSetServiceContactPhone() {
		return this.serviceContactPhone != null;
	}

	public String getServiceContactPhoneExtension() {
		return this.serviceContactPhoneExtension;
	}

	public ManageMyWorkMarket setServiceContactPhoneExtension(String serviceContactPhoneExtension) {
		this.serviceContactPhoneExtension = serviceContactPhoneExtension;
		return this;
	}

	public boolean isSetServiceContactPhoneExtension() {
		return this.serviceContactPhoneExtension != null;
	}

	public String getServiceContactEMail() {
		return this.serviceContactEMail;
	}

	public ManageMyWorkMarket setServiceContactEMail(String serviceContactEMail) {
		this.serviceContactEMail = serviceContactEMail;
		return this;
	}

	public boolean isSetServiceContactEMail() {
		return this.serviceContactEMail != null;
	}

	public String getServiceContactInstructions() {
		return this.serviceContactInstructions;
	}

	public ManageMyWorkMarket setServiceContactInstructions(String serviceContactInstructions) {
		this.serviceContactInstructions = serviceContactInstructions;
		return this;
	}

	public boolean isSetServiceContactInstructions() {
		return this.serviceContactInstructions != null;
	}

	public String getBuyerSupportContactFirstName() {
		return this.buyerSupportContactFirstName;
	}

	public ManageMyWorkMarket setBuyerSupportContactFirstName(String buyerSupportContactFirstName) {
		this.buyerSupportContactFirstName = buyerSupportContactFirstName;
		return this;
	}

	public boolean isSetBuyerSupportContactFirstName() {
		return this.buyerSupportContactFirstName != null;
	}

	public String getBuyerSupportContactLastName() {
		return this.buyerSupportContactLastName;
	}

	public ManageMyWorkMarket setBuyerSupportContactLastName(String buyerSupportContactLastName) {
		this.buyerSupportContactLastName = buyerSupportContactLastName;
		return this;
	}

	public boolean isSetBuyerSupportContactLastName() {
		return this.buyerSupportContactLastName != null;
	}

	public String getBuyerSupportContactPhone() {
		return this.buyerSupportContactPhone;
	}

	public ManageMyWorkMarket setBuyerSupportContactPhone(String buyerSupportContactPhone) {
		this.buyerSupportContactPhone = buyerSupportContactPhone;
		return this;
	}

	public boolean isSetBuyerSupportContactPhone() {
		return this.buyerSupportContactPhone != null;
	}

	public String getBuyerSupportContactPhoneExtension() {
		return this.buyerSupportContactPhoneExtension;
	}

	public ManageMyWorkMarket setBuyerSupportContactPhoneExtension(String buyerSupportContactPhoneExtension) {
		this.buyerSupportContactPhoneExtension = buyerSupportContactPhoneExtension;
		return this;
	}

	public boolean isSetBuyerSupportContactPhoneExtension() {
		return this.buyerSupportContactPhoneExtension != null;
	}

	public String getBuyerSupportContactEMail() {
		return this.buyerSupportContactEMail;
	}

	public ManageMyWorkMarket setBuyerSupportContactEMail(String buyerSupportContactEMail) {
		this.buyerSupportContactEMail = buyerSupportContactEMail;
		return this;
	}

	public boolean isSetBuyerSupportContactEMail() {
		return this.buyerSupportContactEMail != null;
	}

	public String getBuyerSupportContactInstructions() {
		return this.buyerSupportContactInstructions;
	}

	public ManageMyWorkMarket setBuyerSupportContactInstructions(String buyerSupportContactInstructions) {
		this.buyerSupportContactInstructions = buyerSupportContactInstructions;
		return this;
	}

	public boolean isSetBuyerSupportContactInstructions() {
		return this.buyerSupportContactInstructions != null;
	}

	public int getPaymentTermsDays() {
		return this.paymentTermsDays;
	}

	public ManageMyWorkMarket setPaymentTermsDays(int paymentTermsDays) {
		this.paymentTermsDays = paymentTermsDays;
		return this;
	}

	public boolean isSetPaymentTermsDays() {
		return (paymentTermsDays > 0);
	}

	public boolean isSameServiceContact() {
		return this.sameServiceContact;
	}

	public ManageMyWorkMarket setSameServiceContact(boolean sameServiceContact) {
		this.sameServiceContact = sameServiceContact;
		return this;
	}

	public boolean isUseMaxSpendPricingDisplayModeFlag() {
		return this.useMaxSpendPricingDisplayModeFlag;
	}

	public ManageMyWorkMarket setUseMaxSpendPricingDisplayModeFlag(boolean useMaxSpendPricingDisplayModeFlag) {
		this.useMaxSpendPricingDisplayModeFlag = useMaxSpendPricingDisplayModeFlag;
		return this;
	}


	public boolean isAssessmentsEnabled() {
		return this.assessmentsEnabled;
	}

	public ManageMyWorkMarket setAssessmentsEnabled(boolean assessmentsEnabled) {
		this.assessmentsEnabled = assessmentsEnabled;
		return this;
	}

	public boolean isAutoPayEnabled() {
		return this.autoPayEnabled;
	}

	public ManageMyWorkMarket setAutoPayEnabled(boolean autoPayEnabled) {
		this.autoPayEnabled = autoPayEnabled;
		return this;
	}

	public boolean isAutoSendInvoiceEmail() {
		return this.autoSendInvoiceEmail;
	}

	public ManageMyWorkMarket setAutoSendInvoiceEmail(boolean autoSendInvoiceEmail) {
		this.autoSendInvoiceEmail = autoSendInvoiceEmail;
		return this;
	}

	public boolean isEnableAssignmentPrintout() {
		return this.enableAssignmentPrintout;
	}

	public ManageMyWorkMarket setEnableAssignmentPrintout(boolean enableAssignmentPrintout) {
		this.enableAssignmentPrintout = enableAssignmentPrintout;
		return this;
	}

	public boolean isStandardTermsEndUserFlag() {
		return this.standardTermsEndUserFlag;
	}

	public ManageMyWorkMarket setStandardTermsEndUserFlag(boolean standardTermsEndUserFlag) {
		this.standardTermsEndUserFlag = standardTermsEndUserFlag;
		return this;
	}

	public boolean isEnablePrintoutSignature() {
		return this.enablePrintoutSignature;
	}

	public ManageMyWorkMarket setEnablePrintoutSignature(boolean enablePrintoutSignature) {
		this.enablePrintoutSignature = enablePrintoutSignature;
		return this;
	}

	public boolean isBadgeIncludedOnPrintout() {
		return this.badgeIncludedOnPrintout;
	}

	public ManageMyWorkMarket setBadgeIncludedOnPrintout(boolean badgeIncludedOnPrintout) {
		this.badgeIncludedOnPrintout = badgeIncludedOnPrintout;
		return this;
	}

	public boolean isDisablePriceNegotiation() {
		return this.disablePriceNegotiation;
	}

	public ManageMyWorkMarket setDisablePriceNegotiation(boolean disablePriceNegotiation) {
		this.disablePriceNegotiation = disablePriceNegotiation;
		return this;
	}

	public boolean isShowCheckoutNotesFlag() {
		return this.showCheckoutNotesFlag;
	}

	public ManageMyWorkMarket setShowCheckoutNotesFlag(boolean showCheckoutNotesFlag) {
		this.showCheckoutNotesFlag = showCheckoutNotesFlag;
		return this;
	}

	public boolean isCheckoutNoteRequiredFlag() {
		return this.checkoutNoteRequiredFlag;
	}

	public ManageMyWorkMarket setCheckoutNoteRequiredFlag(boolean checkoutNoteRequiredFlag) {
		this.checkoutNoteRequiredFlag = checkoutNoteRequiredFlag;
		return this;
	}

	public String getCheckoutNoteInstructions() {
		return this.checkoutNoteInstructions;
	}

	public ManageMyWorkMarket setCheckoutNoteInstructions(String checkoutNoteInstructions) {
		this.checkoutNoteInstructions = checkoutNoteInstructions;
		return this;
	}

	public boolean isSetCheckoutNoteInstructions() {
		return this.checkoutNoteInstructions != null;
	}

	public String getCheckinContactName() {
		return checkinContactName;
	}

	public ManageMyWorkMarket setCheckinContactName(String checkinContactName) {
		this.checkinContactName = checkinContactName;
		return this;
	}

	public String getCheckinContactPhone() {
		return checkinContactPhone;
	}

	public ManageMyWorkMarket setCheckinContactPhone(String checkinContactPhone) {
		this.checkinContactPhone = checkinContactPhone;
		return this;
	}

	public boolean isAssignToFirstResource() {
		return assignToFirstResource;
	}

	public ManageMyWorkMarket setAssignToFirstResource(boolean assignToFirstResource) {
		this.assignToFirstResource = assignToFirstResource;
		return this;
	}

	public Boolean isShowInFeed() {
		return showInFeed;
	}

	public ManageMyWorkMarket setShowInFeed(Boolean showInFeed) {
		this.showInFeed = showInFeed;
		return this;
	}

	public Boolean isUseRequirementSets() {
		return useRequirementSets;
	}

	public ManageMyWorkMarket setUseRequirementSets(Boolean useRequirementSets) {
		this.useRequirementSets = useRequirementSets;
		return this;
	}

	public boolean isBadgeShowClientName() {
		return badgeShowClientName;
	}

	public ManageMyWorkMarket setBadgeShowClientName(boolean badgeShowClientName) {
		this.badgeShowClientName = badgeShowClientName;
		return this;
	}

	public boolean isSmartRoute() {
		return smartRoute;
	}

	public ManageMyWorkMarket setSmartRoute(boolean smartRoute) {
		this.smartRoute = smartRoute;
		return this;
	}

	@Override
	public boolean equals(Object that) {
		return EqualsBuilder.reflectionEquals(this, that);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
