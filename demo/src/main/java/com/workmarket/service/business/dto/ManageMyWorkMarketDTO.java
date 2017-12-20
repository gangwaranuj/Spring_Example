package com.workmarket.service.business.dto;


public class ManageMyWorkMarketDTO {

	private Boolean teamWorkflowEnabledFlag = Boolean.FALSE;
	private Boolean delegationEnabledFlag = Boolean.FALSE;
	private Boolean customFieldsEnabledFlag = Boolean.FALSE;
	private Boolean customFormsEnabledFlag = Boolean.TRUE;
	private Boolean customCloseOutEnabledFlag = Boolean.FALSE;

	private Boolean autocloseEnabledFlag = Boolean.FALSE;
	private Integer autocloseDelayInHours = 72;

	private Boolean hideWorkMarketLogoFlag = Boolean.TRUE;
	private Boolean useCompanyLogoFlag = Boolean.TRUE;

	private Boolean autoRateEnabledFlag = Boolean.FALSE;
	private Boolean partsLogisticsEnabledFlag = Boolean.FALSE;

	private Boolean standardTermsFlag = Boolean.FALSE;
	private Boolean standardInstructionsFlag = Boolean.FALSE;

	private String standardTerms;
	private String standardTermsEndUser;
	private String standardInstructions;

	private Boolean serviceContactFlag = Boolean.FALSE;
	private Boolean buyerSupportContactFlag = Boolean.FALSE;
	private Boolean checkinRequiredFlag = Boolean.FALSE;

	private Boolean showCheckoutNotesFlag = Boolean.FALSE;
	private Boolean checkoutNoteRequiredFlag = Boolean.FALSE;
	private String checkoutNoteInstructions;

	private Boolean ivrEnabledFlag = Boolean.FALSE;

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

	private Integer paymentTermsDays = 0;
	private Boolean paymentTermsEnabled = Boolean.FALSE;
	private Boolean paymentTermsOverride = Boolean.FALSE;

	private Boolean sameServiceContact = Boolean.FALSE;
	private String dunsNumber;
	private Integer businessYears = 0;

	private boolean instantWorkerPoolEnabled;

	private Boolean useMaxSpendPricingDisplayModeFlag = Boolean.TRUE;

	private Boolean assessmentsEnabled = Boolean.FALSE;

	private Boolean autoPayEnabled = Boolean.FALSE;
	private Boolean alertOnLowCashBalance = Boolean.FALSE;
	private Boolean autoSendInvoiceEmail = Boolean.FALSE;

	private boolean enableAssignmentPrintout = true;
	private boolean standardTermsEndUserFlag;
	private boolean enablePrintoutSignature;
	private boolean badgeIncludedOnPrintout = true;
	private String customSignatureLine;
	private Boolean badgeShowClientName = Boolean.FALSE;

	private Boolean agingAssignmentAlertEnabled = Boolean.FALSE;
	private Boolean assignToFirstResource = Boolean.FALSE;
	private Boolean showInFeed = Boolean.FALSE;
	private Boolean useRequirementSets = Boolean.FALSE;

	private Boolean reserveFundsEnabledFlag = Boolean.FALSE;
	private Boolean budgetEnabledFlag = Boolean.FALSE;

	private Boolean requireProjectEnabledFlag = Boolean.FALSE;

	public Boolean getTeamWorkflowEnabledFlag() {
		return teamWorkflowEnabledFlag;
	}

	public Boolean getDelegationEnabledFlag() {
		return delegationEnabledFlag;
	}

	public Boolean getCustomFieldsEnabledFlag() {
		return customFieldsEnabledFlag;
	}

	public Boolean getCustomFormsEnabledFlag() {
		return customFormsEnabledFlag;
	}

	public Boolean getCustomCloseOutEnabledFlag() {
		return customCloseOutEnabledFlag;
	}

	public Boolean getAutocloseEnabledFlag() {
		return autocloseEnabledFlag;
	}

	public Integer getAutocloseDelayInHours() {
		return autocloseDelayInHours;
	}

	public Boolean getHideWorkMarketLogoFlag() {
		return hideWorkMarketLogoFlag;
	}

	public Boolean getUseCompanyLogoFlag() {
		return useCompanyLogoFlag;
	}

	public Boolean getAutoRateEnabledFlag() {
		return autoRateEnabledFlag;
	}

	public Boolean getPartsLogisticsEnabledFlag() {
		return partsLogisticsEnabledFlag;
	}

	public boolean isEnableAssignmentPrintout() {
		return enableAssignmentPrintout;
	}

	public boolean isStandardTermsEndUserFlag() {
		return standardTermsEndUserFlag;
	}

	public boolean isEnablePrintoutSignature() {
		return enablePrintoutSignature;
	}

	public boolean isBadgeIncludedOnPrintout() {
		return badgeIncludedOnPrintout;
	}

	public String getCustomSignatureLine() {
		return customSignatureLine;
	}

	public Boolean getBadgeShowClientName() {
		return badgeShowClientName;
	}

	public void setTeamWorkflowEnabledFlag(Boolean teamWorkflowEnabledFlag) {
		this.teamWorkflowEnabledFlag = teamWorkflowEnabledFlag;
	}

	public void setDelegationEnabledFlag(Boolean delegationEnabledFlag) {
		this.delegationEnabledFlag = delegationEnabledFlag;
	}

	public void setCustomFieldsEnabledFlag(Boolean customFieldsEnabledFlag) {
		this.customFieldsEnabledFlag = customFieldsEnabledFlag;
	}

	public void setEnableAssignmentPrintout(boolean enableAssignmentPrintout) {
		this.enableAssignmentPrintout = enableAssignmentPrintout;
	}

	public void setStandardTermsEndUserFlag(boolean standardTermsEndUserFlag) {
		this.standardTermsEndUserFlag = standardTermsEndUserFlag;
	}

	public void setEnablePrintoutSignature(boolean enablePrintoutSignature) {
		this.enablePrintoutSignature = enablePrintoutSignature;
	}

	public void setCustomSignatureLine(String customSignatureLine) {
		this.customSignatureLine = customSignatureLine;
	}

	public void setBadgeIncludedOnPrintout(boolean badgeIncludedOnPrintout) {
		this.badgeIncludedOnPrintout = badgeIncludedOnPrintout;
	}

	public void setBadgeShowClientName(Boolean badgeShowClientName) {
		this.badgeShowClientName = badgeShowClientName;
	}

	public void setCustomFormsEnabledFlag(Boolean customFormsEnabledFlag) {
		this.customFormsEnabledFlag = customFormsEnabledFlag;
	}

	public void setCustomCloseOutEnabledFlag(Boolean customCloseOutEnabledFlag) {
		this.customCloseOutEnabledFlag = customCloseOutEnabledFlag;
	}

	public void setAutocloseEnabledFlag(Boolean autocloseEnabledFlag) {
		this.autocloseEnabledFlag = autocloseEnabledFlag;
	}

	public void setAutocloseDelayInHours(Integer autocloseDelayInHours) {
		this.autocloseDelayInHours = autocloseDelayInHours;
	}

	public void setHideWorkMarketLogoFlag(Boolean hideWorkMarketLogoFlag) {
		this.hideWorkMarketLogoFlag = hideWorkMarketLogoFlag;
	}

	public void setUseCompanyLogoFlag(Boolean useCompanyLogoFlag) {
		this.useCompanyLogoFlag = useCompanyLogoFlag;
	}

	public void setAutoRateEnabledFlag(Boolean autoRateEnabledFlag) {
		this.autoRateEnabledFlag = autoRateEnabledFlag;
	}

	public void setPartsLogisticsEnabledFlag(Boolean partsLogisticsEnabledFlag) {
		this.partsLogisticsEnabledFlag = partsLogisticsEnabledFlag;
	}

	public Boolean getStandardTermsFlag() {
		return standardTermsFlag;
	}

	public void setStandardTermsFlag(Boolean standardTermsFlag) {
		this.standardTermsFlag = standardTermsFlag;
	}

	public Boolean getStandardInstructionsFlag() {
		return standardInstructionsFlag;
	}

	public void setStandardInstructionsFlag(Boolean standardInstructionsFlag) {
		this.standardInstructionsFlag = standardInstructionsFlag;
	}

	public String getStandardTerms() {
		return standardTerms;
	}

	public void setStandardTerms(String standardTerms) {
		this.standardTerms = standardTerms;
	}

	public String getStandardTermsEndUser() {
		return standardTermsEndUser;
	}

	public void setStandardTermsEndUser(String standardTermsEndUser) {
		this.standardTermsEndUser = standardTermsEndUser;
	}

	public String getStandardInstructions() {
		return standardInstructions;
	}

	public void setStandardInstructions(String standardInstructions) {
		this.standardInstructions = standardInstructions;
	}

	public Boolean getServiceContactFlag() {
		return serviceContactFlag;
	}

	public void setServiceContactFlag(Boolean serviceContactFlag) {
		this.serviceContactFlag = serviceContactFlag;
	}

	public Boolean getBuyerSupportContactFlag() {
		return buyerSupportContactFlag;
	}

	public void setBuyerSupportContactFlag(Boolean buyerSupportContactFlag) {
		this.buyerSupportContactFlag = buyerSupportContactFlag;
	}

	public Boolean getCheckinRequiredFlag() {
		return checkinRequiredFlag;
	}

	public void setCheckinRequiredFlag(Boolean checkinRequiredFlag) {
		this.checkinRequiredFlag = checkinRequiredFlag;
	}

	public Boolean getShowCheckoutNotesFlag() {
		return showCheckoutNotesFlag;
	}

	public void setShowCheckoutNotesFlag(Boolean showCheckoutNotesFlag) {
		this.showCheckoutNotesFlag = showCheckoutNotesFlag;
	}


	public Boolean getCheckoutNoteRequiredFlag() {
		return checkoutNoteRequiredFlag;
	}


	public void setCheckoutNoteRequiredFlag(Boolean checkoutNoteRequiredFlag) {
		this.checkoutNoteRequiredFlag = checkoutNoteRequiredFlag;
	}


	public String getCheckoutNoteInstructions() {
		return checkoutNoteInstructions;
	}


	public void setCheckoutNoteInstructions(String checkoutNoteInstructions) {
		this.checkoutNoteInstructions = checkoutNoteInstructions;
	}


	public Boolean getIvrEnabledFlag() {
		return ivrEnabledFlag;
	}

	public void setIvrEnabledFlag(Boolean ivrEnabledFlag) {
		this.ivrEnabledFlag = ivrEnabledFlag;
	}

	public String getServiceContactFirstName() {
		return serviceContactFirstName;
	}

	public void setServiceContactFirstName(String serviceContactFirstName) {
		this.serviceContactFirstName = serviceContactFirstName;
	}

	public String getServiceContactLastName() {
		return serviceContactLastName;
	}

	public void setServiceContactLastName(String serviceContactLastName) {
		this.serviceContactLastName = serviceContactLastName;
	}

	public String getServiceContactPhone() {
		return serviceContactPhone;
	}

	public void setServiceContactPhone(String serviceContactPhone) {
		this.serviceContactPhone = serviceContactPhone;
	}

	public String getServiceContactPhoneExtension() {
		return serviceContactPhoneExtension;
	}

	public void setServiceContactPhoneExtension(String serviceContactPhoneExtension) {
		this.serviceContactPhoneExtension = serviceContactPhoneExtension;
	}

	public String getServiceContactEMail() {
		return serviceContactEMail;
	}

	public void setServiceContactEMail(String serviceContactEMail) {
		this.serviceContactEMail = serviceContactEMail;
	}

	public String getServiceContactInstructions() {
		return serviceContactInstructions;
	}

	public void setServiceContactInstructions(String serviceContactInstructions) {
		this.serviceContactInstructions = serviceContactInstructions;
	}

	public String getBuyerSupportContactFirstName() {
		return buyerSupportContactFirstName;
	}

	public void setBuyerSupportContactFirstName(String buyerSupportContactFirstName) {
		this.buyerSupportContactFirstName = buyerSupportContactFirstName;
	}

	public String getBuyerSupportContactLastName() {
		return buyerSupportContactLastName;
	}

	public void setBuyerSupportContactLastName(String buyerSupportContactLastName) {
		this.buyerSupportContactLastName = buyerSupportContactLastName;
	}

	public String getBuyerSupportContactPhone() {
		return buyerSupportContactPhone;
	}

	public void setBuyerSupportContactPhone(String buyerSupportContactPhone) {
		this.buyerSupportContactPhone = buyerSupportContactPhone;
	}

	public String getBuyerSupportContactPhoneExtension() {
		return buyerSupportContactPhoneExtension;
	}

	public void setBuyerSupportContactPhoneExtension(String buyerSupportContactPhoneExtension) {
		this.buyerSupportContactPhoneExtension = buyerSupportContactPhoneExtension;
	}

	public String getBuyerSupportContactEMail() {
		return buyerSupportContactEMail;
	}

	public void setBuyerSupportContactEMail(String buyerSupportContactEMail) {
		this.buyerSupportContactEMail = buyerSupportContactEMail;
	}

	public String getBuyerSupportContactInstructions() {
		return buyerSupportContactInstructions;
	}

	public void setBuyerSupportContactInstructions(
			String buyerSupportContactInstructions) {
		this.buyerSupportContactInstructions = buyerSupportContactInstructions;
	}

	public Boolean getSameServiceContact() {
		return sameServiceContact;
	}

	public void setSameServiceContact(Boolean sameServiceContact) {
		this.sameServiceContact = sameServiceContact;
	}

	public void setPaymentTermsDays(Integer paymentTermsDays) {
		this.paymentTermsDays = paymentTermsDays;
	}

	public Integer getPaymentTermsDays() {
		return paymentTermsDays;
	}

	public Boolean getPaymentTermsEnabled() {
		return paymentTermsEnabled;
	}

	public void setPaymentTermsEnabled(Boolean paymentTermsEnabled) {
		this.paymentTermsEnabled = paymentTermsEnabled;
	}

	public Boolean getPaymentTermsOverride() {
		return paymentTermsOverride;
	}

	public void setPaymentTermsOverride(Boolean paymentTermsOverride) {
		this.paymentTermsOverride = paymentTermsOverride;
	}

	public String getDunsNumber() {
		return dunsNumber;
	}

	public void setDunsNumber(String dunsNumber) {
		this.dunsNumber = dunsNumber;
	}

	public Integer getBusinessYears() {
		return businessYears;
	}

	public void setBusinessYears(Integer businessYears) {
		this.businessYears = businessYears;
	}

	public boolean isInstantWorkerPoolEnabled() {
		return instantWorkerPoolEnabled;
	}

	public void setInstantWorkerPoolEnabled(boolean instantWorkerPoolEnabled) {
		this.instantWorkerPoolEnabled = instantWorkerPoolEnabled;
	}

	public Boolean getUseMaxSpendPricingDisplayModeFlag() {
		return useMaxSpendPricingDisplayModeFlag;
	}

	public void setUseMaxSpendPricingDisplayModeFlag(Boolean useMaxSpendPricingDisplayModeFlag) {
		this.useMaxSpendPricingDisplayModeFlag = useMaxSpendPricingDisplayModeFlag;
	}

	public Boolean getAssessmentsEnabled() {
		return assessmentsEnabled;
	}

	public void setAssessmentsEnabled(Boolean assessmentsEnabled) {
		this.assessmentsEnabled = assessmentsEnabled;
	}

	/**
	 * @return the autoPayEnabled
	 */
	public Boolean getAutoPayEnabled() {
		return autoPayEnabled;
	}

	/**
	 * @param autoPayEnabled the autoPayEnabled to set
	 */
	public void setAutoPayEnabled(Boolean autoPayEnabled) {
		this.autoPayEnabled = autoPayEnabled;
	}

	/**
	 * @return the alertOnLowCashBalance
	 */
	public Boolean getAlertOnLowCashBalance() {
		return alertOnLowCashBalance;
	}

	/**
	 * @param alertOnLowCashBalance the alertOnLowCashBalance to set
	 */
	public void setAlertOnLowCashBalance(Boolean alertOnLowCashBalance) {
		this.alertOnLowCashBalance = alertOnLowCashBalance;
	}

	/**
	 * @return the autoSendInvoiceEmail
	 */
	public Boolean getAutoSendInvoiceEmail() {
		return autoSendInvoiceEmail;
	}

	/**
	 * @param autoSendInvoiceEmail the autoSendInvoiceEmail to set
	 */
	public void setAutoSendInvoiceEmail(Boolean autoSendInvoiceEmail) {
		this.autoSendInvoiceEmail = autoSendInvoiceEmail;
	}

	public Boolean getAgingAssignmentAlertEnabled() {
		return agingAssignmentAlertEnabled;
	}

	public void setAgingAssignmentAlertEnabled(Boolean agingAssignmentAlertEnabled) {
		this.agingAssignmentAlertEnabled = agingAssignmentAlertEnabled;
	}

	public Boolean getAssignToFirstResource() {
		return assignToFirstResource;
	}

	public void setAssignToFirstResource(Boolean assignToFirstResource) {
		this.assignToFirstResource = assignToFirstResource;
	}

	public Boolean getShowInFeed() {
		return showInFeed;
	}

	public void setShowInFeed(Boolean showInFeed) {
		this.showInFeed = showInFeed;
	}

	public Boolean getUseRequirementSets() {
		return useRequirementSets;
	}

	public void setUseRequirementSets(Boolean useRequirementSets) {
		this.useRequirementSets = useRequirementSets;
	}

	public Boolean getReserveFundsEnabledFlag() {
		return reserveFundsEnabledFlag;
	}

	public void setReserveFundsEnabledFlag(Boolean reserveFundsEnabledFlag) {
		this.reserveFundsEnabledFlag = reserveFundsEnabledFlag;
	}

	public Boolean getBudgetEnabledFlag() {
		return budgetEnabledFlag;
	}

	public void setBudgetEnabledFlag(Boolean budgetEnabledFlag) {
		this.budgetEnabledFlag = budgetEnabledFlag;
	}

	public Boolean getRequireProjectEnabledFlag() {
		return requireProjectEnabledFlag;
	}

	public void setRequireProjectEnabledFlag(Boolean requireProjectEnabledFlag) {
		this.requireProjectEnabledFlag = requireProjectEnabledFlag;
	}

}
