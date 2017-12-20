package com.workmarket.domains.model;

import com.workmarket.configuration.Constants;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Access(AccessType.PROPERTY)
public class ManageMyWorkMarket implements Serializable {
	private static final long serialVersionUID = 1L;

	private Boolean customFieldsEnabledFlag = Boolean.FALSE;
	private Boolean customFormsEnabledFlag = Boolean.TRUE;
	private Boolean customCloseOutEnabledFlag = Boolean.FALSE;

	private Boolean autocloseEnabledFlag = Boolean.FALSE;
	private Integer autocloseDelayInHours = 72;

	private Boolean hideWorkMarketLogoFlag = Boolean.TRUE;
	private Boolean useCompanyLogoFlag = Boolean.TRUE;

	private Boolean autoRateEnabledFlag = Boolean.TRUE;
	private Boolean partsLogisticsEnabledFlag = Boolean.FALSE;

	private Boolean standardTermsFlag = Boolean.FALSE;
	private Boolean standardInstructionsFlag = Boolean.FALSE;

	private String standardTerms;
	private String standardTermsEndUser;
	private String standardInstructions;

	private Boolean ivrEnabledFlag = Boolean.FALSE;

	private Boolean checkinRequiredFlag = Boolean.FALSE;
	private String checkinContactName;
	private String checkinContactPhone;

	private Integer paymentTermsDays = 0;
	private Boolean paymentTermsEnabled = Boolean.FALSE;
	private Boolean paymentTermsOverride = Boolean.FALSE;

	private Integer businessYears = 0;

	private boolean instantWorkerPoolEnabled = true;

	private Boolean useMaxSpendPricingDisplayModeFlag = Boolean.TRUE;

	private Boolean assessmentsEnabled = Boolean.FALSE;

	private Boolean autoPayEnabled = Boolean.FALSE;
	private Boolean alertOnLowCashBalance = Boolean.FALSE;
	private Boolean autoSendInvoiceEmail = Boolean.FALSE;

	private boolean enableAssignmentPrintout = true;
	private boolean standardTermsEndUserFlag = false;
	private boolean enablePrintoutSignature = false;
	private boolean badgeIncludedOnPrintout = true;
	private Boolean badgeShowClientName = Boolean.FALSE;

	private Boolean statementsEnabled = Boolean.FALSE;

	private Boolean disablePriceNegotiation = Boolean.FALSE;

	private Boolean showCheckoutNotesFlag = Boolean.FALSE;
	private Boolean checkoutNoteRequiredFlag = Boolean.FALSE;
	private String checkoutNoteInstructions;
	private Boolean agingAssignmentAlertEnabled = Boolean.FALSE;

	private Boolean assignToFirstResource = Boolean.FALSE;
	private Boolean showInFeed = Boolean.TRUE;
	private Boolean useRequirementSets = Boolean.FALSE;

	private Boolean reserveFundsEnabledFlag = Boolean.FALSE;
	private Boolean budgetEnabledFlag = Boolean.FALSE;
	private Boolean requireProjectEnabledFlag = Boolean.FALSE;

	@Column(name = "custom_fields_enabled_flag", nullable = true)
	public Boolean getCustomFieldsEnabledFlag() {
		return customFieldsEnabledFlag;
	}


	@Column(name = "custom_forms_enabled_flag", nullable = true)
	public Boolean getCustomFormsEnabledFlag() {
		return customFormsEnabledFlag;
	}


	@Column(name = "custom_close_out_enabled_flag", nullable = true)
	public Boolean getCustomCloseOutEnabledFlag() {
		return customCloseOutEnabledFlag;
	}


	@Column(name = "autoclose_enabled_flag", nullable = true)
	public Boolean getAutocloseEnabledFlag() {
		return autocloseEnabledFlag;
	}


	@Column(name = "autoclose_delay_in_hours", nullable = true)
	public Integer getAutocloseDelayInHours() {
		return autocloseDelayInHours;
	}


	@Column(name = "assignment_printout_enabled_flag", nullable = false)
	public boolean isEnableAssignmentPrintout() {
		return enableAssignmentPrintout;
	}


	@Column(name = "standard_terms_end_user_flag", nullable = false)
	public boolean isStandardTermsEndUserFlag() {
		return standardTermsEndUserFlag;
	}


	@Column(name = "signature_printout_enabled_flag", nullable = false)
	public boolean isEnablePrintoutSignature() {
		return enablePrintoutSignature;
	}


	@Column(name = "badge_included_on_printout", nullable = false)
	public boolean isBadgeIncludedOnPrintout() {
		return badgeIncludedOnPrintout;
	}

	@Column(name = "hide_work_market_logo_flag ", nullable = true)
	public Boolean getHideWorkMarketLogoFlag() {
		return hideWorkMarketLogoFlag;
	}


	@Column(name = "use_company_logo_flag ", nullable = true)
	public Boolean getUseCompanyLogoFlag() {
		return useCompanyLogoFlag;
	}


	@Column(name = "autorate_enabled_flag ", nullable = true)
	public Boolean getAutoRateEnabledFlag() {
		return autoRateEnabledFlag;
	}


	@Column(name = "parts_logistics_enabled_flag ", nullable = true)
	public Boolean getPartsLogisticsEnabledFlag() {
		return partsLogisticsEnabledFlag;
	}

	public void setCustomFieldsEnabledFlag(Boolean customFieldsEnabledFlag) {
		this.customFieldsEnabledFlag = customFieldsEnabledFlag;
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


	public void setEnableAssignmentPrintout(boolean enableAssignmentPrintout) {
		this.enableAssignmentPrintout = enableAssignmentPrintout;
	}


	public void setStandardTermsEndUserFlag(boolean standardTermsEndUserFlag) {
		this.standardTermsEndUserFlag = standardTermsEndUserFlag;
	}


	public void setEnablePrintoutSignature(boolean enablePrintoutSignature) {
		this.enablePrintoutSignature = enablePrintoutSignature;
	}


	public void setBadgeIncludedOnPrintout(boolean badgeIncludedOnPrintout) {
		this.badgeIncludedOnPrintout = badgeIncludedOnPrintout;
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


	@Column(name = "standard_terms_flag", nullable = false)
	public Boolean getStandardTermsFlag() {
		return standardTermsFlag;
	}


	public void setStandardTermsFlag(Boolean standardTermsFlag) {
		this.standardTermsFlag = standardTermsFlag;
	}

	@Column(name = "standard_instructions_flag", nullable = false)
	public Boolean getStandardInstructionsFlag() {
		return standardInstructionsFlag;
	}


	public void setStandardInstructionsFlag(Boolean standardInstructionsFlag) {
		this.standardInstructionsFlag = standardInstructionsFlag;
	}

	@Column(name = "checkin_required_flag", nullable = false)
	public Boolean getCheckinRequiredFlag() {
		return checkinRequiredFlag;
	}


	public void setCheckinRequiredFlag(Boolean checkinRequieredFlag) {
		this.checkinRequiredFlag = checkinRequieredFlag;
	}


	@Column(name = "ivr_enabled_flag", nullable = false)
	public Boolean getIvrEnabledFlag() {
		return ivrEnabledFlag;
	}


	public void setIvrEnabledFlag(Boolean ivrEnabledFlag) {
		this.ivrEnabledFlag = ivrEnabledFlag;
	}


	@Column(name = "standard_terms", length = Constants.TEXT_LONG)
	public String getStandardTerms() {
		return standardTerms;
	}


	public void setStandardTerms(String standardTerms) {
		this.standardTerms = standardTerms;
	}

	@Column(name = "standard_terms_end_user", length = Constants.TEXT_LONG)
	public String getStandardTermsEndUser() {
		return standardTermsEndUser;
	}

	public void setStandardTermsEndUser(String standardTermsEndUser) {
		this.standardTermsEndUser = standardTermsEndUser;
	}

	@Column(name = "standard_instructions", length = Constants.TEXT_LONG)
	public String getStandardInstructions() {
		return standardInstructions;
	}

	public void setStandardInstructions(String standardInstructions) {
		this.standardInstructions = standardInstructions;
	}

	@Column(name = "checkin_contact_name")
	public String getCheckinContactName() {
		return checkinContactName;
	}

	public void setCheckinContactName(String checkinContactName) {
		this.checkinContactName = checkinContactName;
	}

	@Column(name = "checkin_contact_phone")
	public String getCheckinContactPhone() {
		return checkinContactPhone;
	}

	public void setCheckinContactPhone(String checkinContactPhone) {
		this.checkinContactPhone = checkinContactPhone;
	}

	@Column(name = "payment_terms_days", nullable = false)
	public Integer getPaymentTermsDays() {
		return paymentTermsDays;
	}


	public void setPaymentTermsDays(Integer paymentTermsDays) {
		this.paymentTermsDays = paymentTermsDays;
	}


	@Column(name = "payment_terms_enabled", nullable = false)
	public Boolean getPaymentTermsEnabled() {
		return paymentTermsEnabled;
	}


	public void setPaymentTermsEnabled(Boolean paymentTermsEnabled) {
		this.paymentTermsEnabled = paymentTermsEnabled;
	}


	@Column(name = "payment_terms_override", nullable = false)
	public Boolean getPaymentTermsOverride() {
		return paymentTermsOverride;
	}

	public void setPaymentTermsOverride(Boolean paymentTermsOverride) {
		this.paymentTermsOverride = paymentTermsOverride;
	}

	@Column(name = "business_years", nullable = false)
	public Integer getBusinessYears() {
		return businessYears;
	}


	public void setBusinessYears(Integer businessYears) {
		this.businessYears = businessYears;
	}

	// TODO: alter this column to instant_worker_pool when an acceptable migration strategy is implemented.
	@Column(name = "instant_network", nullable = false)
	public boolean isInstantWorkerPoolEnabled() {
		return instantWorkerPoolEnabled;
	}

	public void setInstantWorkerPoolEnabled(boolean instantWorkerPoolEnabled) {
		this.instantWorkerPoolEnabled = instantWorkerPoolEnabled;
	}

	@Column(name = "use_max_spend_pricing_display_mode_flag", nullable = false)
	public Boolean getUseMaxSpendPricingDisplayModeFlag() {
		return useMaxSpendPricingDisplayModeFlag;
	}


	public void setUseMaxSpendPricingDisplayModeFlag(Boolean useMaxSpendPricingDisplayModeFlag) {
		this.useMaxSpendPricingDisplayModeFlag = useMaxSpendPricingDisplayModeFlag;
	}


	@Column(name = "assessments_enabled", nullable = false)
	public Boolean getAssessmentsEnabled() {
		return assessmentsEnabled;
	}


	public void setAssessmentsEnabled(Boolean assessmentsEnabled) {
		this.assessmentsEnabled = assessmentsEnabled;
	}


	/**
	 * @return the autoPayEnabled
	 */
	@Column(name = "auto_pay_enabled", nullable = false)
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
	@Column(name = "alert_on_low_cash_balance", nullable = false)
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
	@Column(name = "auto_send_invoice_email", nullable = false)
	public Boolean getAutoSendInvoiceEmail() {
		return autoSendInvoiceEmail;
	}


	/**
	 * @param autoSendInvoiceEmail the autoSendInvoiceEmail to set
	 */
	public void setAutoSendInvoiceEmail(Boolean autoSendInvoiceEmail) {
		this.autoSendInvoiceEmail = autoSendInvoiceEmail;
	}

	@Column(name = "badge_show_client_name_flag", nullable = false)
	public Boolean isBadgeShowClientName() {
		return badgeShowClientName;
	}


	public void setBadgeShowClientName(Boolean badgeShowClientName) {
		this.badgeShowClientName = badgeShowClientName;
	}


	@Column(name = "statements_enabled", nullable = false)
	public Boolean getStatementsEnabled() {
		return statementsEnabled;
	}


	public void setStatementsEnabled(Boolean statementsEnabled) {
		this.statementsEnabled = statementsEnabled;
	}


	@Column(name = "disable_price_negotiation", nullable = false)
	public Boolean getDisablePriceNegotiation() {
		return disablePriceNegotiation;
	}


	public void setDisablePriceNegotiation(Boolean disablePriceNegotiation) {
		this.disablePriceNegotiation = disablePriceNegotiation;
	}


	@Column(name = "show_checkout_notes_flag", nullable = false)
	public Boolean getShowCheckoutNotesFlag() {
		return showCheckoutNotesFlag;
	}


	public void setShowCheckoutNotesFlag(Boolean showCheckoutNotesFlag) {
		this.showCheckoutNotesFlag = showCheckoutNotesFlag;
	}


	@Column(name = "checkout_note_required_flag", nullable = false)
	public Boolean getCheckoutNoteRequiredFlag() {
		return checkoutNoteRequiredFlag;
	}

	public void setCheckoutNoteRequiredFlag(Boolean checkoutNoteRequiredFlag) {
		this.checkoutNoteRequiredFlag = checkoutNoteRequiredFlag;
	}

	@Column(name = "checkout_note_instructions", nullable = false)
	public String getCheckoutNoteInstructions() {
		return checkoutNoteInstructions;
	}

	public void setCheckoutNoteInstructions(String checkoutNoteInstructions) {
		this.checkoutNoteInstructions = checkoutNoteInstructions;
	}

	@Column(name = "aging_assignment_alert", nullable = false)
	public Boolean getAgingAssignmentAlertEnabled() {
		return agingAssignmentAlertEnabled;
	}

	public void setAgingAssignmentAlertEnabled(Boolean agingAssignmentAlertEnabled) {
		this.agingAssignmentAlertEnabled = agingAssignmentAlertEnabled;
	}

	@Column(name = "assign_to_first_resource", nullable = false)
	public Boolean getAssignToFirstResource() {
		return assignToFirstResource;
	}

	public void setAssignToFirstResource(Boolean assignToFirstResource) {
		this.assignToFirstResource = assignToFirstResource;
	}

	@Column(name = "show_in_feed", nullable = false)
	public Boolean getShowInFeed() {
		return showInFeed;
	}

	public void setShowInFeed(Boolean showInFeed) {
		this.showInFeed = showInFeed;
	}

	@Column(name = "use_requirement_sets", nullable = false)
	public Boolean getUseRequirementSets() {
		return useRequirementSets;
	}

	public void setUseRequirementSets(Boolean useRequirementSets) {
		this.useRequirementSets = useRequirementSets;
	}

	@Column(name = "reserve_funds_enabled_flag", nullable = false)
	public Boolean getReserveFundsEnabledFlag() {
		return reserveFundsEnabledFlag;
	}

	public void setReserveFundsEnabledFlag(Boolean reserveFundsEnabledFlag) {
		this.reserveFundsEnabledFlag = reserveFundsEnabledFlag;
	}

	@Column(name = "budget_enabled_flag", nullable = false)
	public Boolean getBudgetEnabledFlag() {
		return budgetEnabledFlag;
	}

	public void setBudgetEnabledFlag(Boolean budgetEnabledFlag) {
		this.budgetEnabledFlag = budgetEnabledFlag;
	}

	@Column(name = "require_project_enabled_flag", nullable = false)
	public Boolean getRequireProjectEnabledFlag() {
		return requireProjectEnabledFlag;
	}

	public void setRequireProjectEnabledFlag(Boolean requireProjectEnabledFlag) {
		this.requireProjectEnabledFlag = requireProjectEnabledFlag;
	}

}
