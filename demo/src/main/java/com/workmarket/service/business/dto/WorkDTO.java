package com.workmarket.service.business.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.workmarket.domains.work.model.AbstractWork;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class WorkDTO implements Serializable {

	private static final long serialVersionUID = -5092878313171514720L;

	private Long id;
	@NotNull
	private String title;
	@NotNull
	private String description;
	private String desiredSkills;
	private String instructions;
	private Boolean privateInstructions = Boolean.FALSE;
	private Boolean isOnsiteAddress;
	private boolean isScheduleRange;
	private String scheduleFrom; // ISO8601
	private String scheduleThrough; // ISO8601
	private String workStatusTypeCode;

	private String contactName;
	private String contactPhone;
	private String contactEmail;
	private String supportName;
	private String supportPhone;
	private String supportEmail;
	private Long industryId;
	private Boolean resourceConfirmation = Boolean.FALSE;
	private Double resourceConfirmationHours = 0.0;
	private Boolean showCheckoutNotes = Boolean.FALSE;
	private Boolean checkinRequired = Boolean.FALSE;
	private String checkinContactName;
	private String checkinContactPhone;
	private Boolean checkoutNoteRequired = Boolean.FALSE;
	private String checkoutNoteInstructions;
	private String appointmentTime;

	@NotNull
	private Long pricingStrategyId;

	private Double flatPrice = 0.0;
	private Double maxFlatPrice;

	private Double perHourPrice = 0.0;
	private Double maxNumberOfHours;

	private Double perUnitPrice = 0.0;
	private Double maxNumberOfUnits;

	private Double initialPerHourPrice = 0.0;
	private Double initialNumberOfHours;
	private Double additionalPerHourPrice = 0.0;
	private Double maxBlendedNumberOfHours;

	private Double initialPerUnitPrice = 0.0;
	private Double initialNumberOfUnits;
	private Double additionalPerUnitPrice = 0.0;
	private Double maxBlendedNumberOfUnits;

	private Boolean useMaxSpendPricingDisplayModeFlag = Boolean.TRUE;

	private Long clientCompanyId;
	private Long locationId;
	private Long addressId;

	private String buyerSupportContactFirstName;
	private String buyerSupportContactLastName;
	private String buyerSupportContactPhone;
	private String buyerSupportContactPhoneExtension;
	private String buyerSupportContactEMail;

	private Boolean sameServiceContact = Boolean.FALSE;
	private Long workTemplateId;

	private Long serviceClientContactId;
	private Long secondaryClientContactId;
	private Long buyerSupportUserId;
	private boolean requireTimetracking;
	private Boolean ivrActive = Boolean.FALSE;

	private String resolution;
	private Long buyerId;
	private Integer paymentTermsDays = 0;
	private boolean paymentTermsEnabled = Boolean.FALSE;
	private boolean autoPayEnabled;
	private boolean checkinCallRequired = Boolean.FALSE;
	private Boolean badgeShowClientName = Boolean.FALSE;
	private Long onBehalfOfId;
	private Long timeZoneId;
	private Boolean disablePriceNegotiation = Boolean.FALSE;
	private boolean assignToFirstResource = Boolean.TRUE;
	private Boolean showInFeed = Boolean.TRUE;
	private boolean partOfBulk;

	private Boolean useRequirementSets = Boolean.FALSE;
	private boolean customFieldsEnabledFlag;
	private boolean customCloseOutEnabledFlag;
	private boolean assessmentsEnabled;
	private boolean partsLogisticsEnabledFlag;
	private boolean documentsEnabled;

	private String uniqueExternalId;

	private boolean offlinePayment;

	public WorkDTO() {
	}

	public WorkDTO(AbstractWork work) {
		id = work.getId();
		title = work.getTitle();
	}

	public Long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public String getDesiredSkills() {
		return desiredSkills;
	}

	public String getInstructions() {
		return instructions;
	}

	public Boolean getPrivateInstructions() {
		return privateInstructions;
	}

	public Boolean getIsOnsiteAddress() {
		return isOnsiteAddress;
	}

	public boolean getIsScheduleRange() {
		return isScheduleRange;
	}

	public String getScheduleFromString() {
		return scheduleFrom;
	}

	public String getScheduleThroughString() {
		return scheduleThrough;
	}

	public String getWorkStatusTypeCode() {
		return workStatusTypeCode;
	}

	public String getContactName() {
		return contactName;
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public String getSupportName() {
		return supportName;
	}

	public String getSupportPhone() {
		return supportPhone;
	}

	public String getSupportEmail() {
		return supportEmail;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDesiredSkills(String desiredSkills) {
		this.desiredSkills = desiredSkills;
	}

	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}

	public void setPrivateInstructions(Boolean privateInstructions) {
		this.privateInstructions = privateInstructions;
	}

	public void setIsOnsiteAddress(Boolean isOnsiteAddress) {
		this.isOnsiteAddress = isOnsiteAddress;
	}

	public void setIsScheduleRange(boolean isScheduleRange) {
		this.isScheduleRange = isScheduleRange;
	}

	public void setScheduleFromString(String scheduleFrom) {
		this.scheduleFrom = scheduleFrom;
	}

	public void setScheduleThroughString(String scheduleThrough) {
		this.scheduleThrough = scheduleThrough;
	}

	public void setWorkStatusTypeCode(String workStatusTypeCode) {
		this.workStatusTypeCode = workStatusTypeCode;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public void setSupportName(String supportName) {
		this.supportName = supportName;
	}

	public void setSupportPhone(String supportPhone) {
		this.supportPhone = supportPhone;
	}

	public void setSupportEmail(String supportEmail) {
		this.supportEmail = supportEmail;
	}

	public Long getIndustryId() {
		return industryId;
	}

	public void setIndustryId(Long industryId) {
		this.industryId = industryId;
	}

	public Boolean isResourceConfirmationRequired() {
		return resourceConfirmation;
	}

	public void setResourceConfirmationRequired(Boolean resourceConfirmation) {
		this.resourceConfirmation = resourceConfirmation;
	}

	public Long getPricingStrategyId() {
		return pricingStrategyId;
	}

	public void setPricingStrategyId(Long pricingStrategyId) {
		this.pricingStrategyId = pricingStrategyId;
	}

	public Double getFlatPrice() {
		return flatPrice;
	}

	public void setFlatPrice(Double flatPrice) {
		this.flatPrice = flatPrice;
	}

	public Double getMaxFlatPrice() {
		return maxFlatPrice;
	}

	public void setMaxFlatPrice(Double maxFlatPrice) {
		this.maxFlatPrice = maxFlatPrice;
	}

	public Double getPerHourPrice() {
		return perHourPrice;
	}

	public void setPerHourPrice(Double perHourPrice) {
		this.perHourPrice = perHourPrice;
	}

	public Double getMaxNumberOfHours() {
		return maxNumberOfHours;
	}

	public void setMaxNumberOfHours(Double maxNumberOfHours) {
		this.maxNumberOfHours = maxNumberOfHours;
	}

	public Double getInitialPerHourPrice() {
		return initialPerHourPrice;
	}

	public void setInitialPerHourPrice(Double initialPerHourPrice) {
		this.initialPerHourPrice = initialPerHourPrice;
	}

	public Double getInitialNumberOfHours() {
		return initialNumberOfHours;
	}

	public void setInitialNumberOfHours(Double initialNumberOfHours) {
		this.initialNumberOfHours = initialNumberOfHours;
	}

	public Double getAdditionalPerHourPrice() {
		return additionalPerHourPrice;
	}

	public void setAdditionalPerHourPrice(Double additionalPerHourPrice) {
		this.additionalPerHourPrice = additionalPerHourPrice;
	}

	public Double getMaxBlendedNumberOfHours() {
		return maxBlendedNumberOfHours;
	}

	public void setMaxBlendedNumberOfHours(Double maxBlendedNumberOfHours) {
		this.maxBlendedNumberOfHours = maxBlendedNumberOfHours;
	}

	public Double getPerUnitPrice() {
		return perUnitPrice;
	}

	public void setPerUnitPrice(Double perUnitPrice) {
		this.perUnitPrice = perUnitPrice;
	}

	public Double getMaxNumberOfUnits() {
		return maxNumberOfUnits;
	}

	public void setMaxNumberOfUnits(Double maxNumberOfUnits) {
		this.maxNumberOfUnits = maxNumberOfUnits;
	}

	public Double getInitialPerUnitPrice() {
		return initialPerUnitPrice;
	}

	public void setInitialPerUnitPrice(Double initialPerUnitPrice) {
		this.initialPerUnitPrice = initialPerUnitPrice;
	}

	public Double getInitialNumberOfUnits() {
		return initialNumberOfUnits;
	}

	public void setInitialNumberOfUnits(Double initialNumberOfUnits) {
		this.initialNumberOfUnits = initialNumberOfUnits;
	}

	public Double getAdditionalPerUnitPrice() {
		return additionalPerUnitPrice;
	}

	public void setAdditionalPerUnitPrice(Double additionalPerUnitPrice) {
		this.additionalPerUnitPrice = additionalPerUnitPrice;
	}

	public Double getMaxBlendedNumberOfUnits() {
		return maxBlendedNumberOfUnits;
	}

	public void setMaxBlendedNumberOfUnits(Double maxBlendedNumberOfUnits) {
		this.maxBlendedNumberOfUnits = maxBlendedNumberOfUnits;
	}

	public Boolean getUseMaxSpendPricingDisplayModeFlag() {
		return useMaxSpendPricingDisplayModeFlag;
	}

	public void setUseMaxSpendPricingDisplayModeFlag(Boolean useMaxSpendPricingDisplayModeFlag) {
		this.useMaxSpendPricingDisplayModeFlag = useMaxSpendPricingDisplayModeFlag;
	}

	public Boolean isCheckinRequired() {
		return checkinRequired;
	}

	public void setCheckinRequired(Boolean checkinRequired) {
		this.checkinRequired = checkinRequired;
	}

	public Boolean getShowCheckoutNotes() {
		return showCheckoutNotes;
	}

	public void setShowCheckoutNotes(Boolean showCheckoutNotes) {
		this.showCheckoutNotes = showCheckoutNotes;
	}

	public Boolean isCheckoutNoteRequired() {
		return checkoutNoteRequired;
	}

	public void setCheckoutNoteRequired(Boolean checkoutNoteRequired) {
		this.checkoutNoteRequired = checkoutNoteRequired;
	}

	public String getCheckoutNoteInstructions() {
		return checkoutNoteInstructions;
	}

	public void setCheckoutNoteInstructions(String checkoutNoteInstructions) {
		this.checkoutNoteInstructions = checkoutNoteInstructions;
	}

	public String getCheckinContactName() {
		return checkinContactName;
	}

	public void setCheckinContactName(String checkinContactName) {
		this.checkinContactName = checkinContactName;
	}

	public String getCheckinContactPhone() {
		return checkinContactPhone;
	}

	public void setCheckinContactPhone(String checkinContactPhone) {
		this.checkinContactPhone = checkinContactPhone;
	}

	public Long getClientCompanyId() {
		return clientCompanyId;
	}

	public void setClientCompanyId(Long clientCompanyId) {
		this.clientCompanyId = clientCompanyId;
	}

	public Long getLocationId() {
		return locationId;
	}

	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}

	public Long getAddressId() {
		return addressId;
	}

	public void setAddressId(Long addressId) {
		this.addressId = addressId;
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

	public Boolean isSameServiceContact() {
		return sameServiceContact;
	}

	public void setSameServiceContact(Boolean sameServiceContact) {
		this.sameServiceContact = sameServiceContact;
	}

	public void setWorkTemplateId(Long workTemplateId) {
		this.workTemplateId = workTemplateId;
	}

	public Long getWorkTemplateId() {
		return workTemplateId;
	}

	public Long getServiceClientContactId() {
		return serviceClientContactId;
	}

	public void setServiceClientContactId(Long serviceClientContactId) {
		this.serviceClientContactId = serviceClientContactId;
	}

	public Long getBuyerSupportUserId() {
		return buyerSupportUserId;
	}

	public void setBuyerSupportUserId(Long buyerSupportUserId) {
		this.buyerSupportUserId = buyerSupportUserId;
	}

	public void setRequireTimetracking(boolean requireTimetracking) {
		this.requireTimetracking = requireTimetracking;
	}

	public boolean isRequireTimetracking() {
		return requireTimetracking;
	}

	public Boolean isIvrActive() {
		return this.ivrActive;
	}

	public void setIvrActive(Boolean ivrActive) {
		this.ivrActive = ivrActive;
	}

	public String getResolution() {
		return resolution;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
	}

	public void setResourceConfirmationHours(Double resourceConfirmationHours) {
		this.resourceConfirmationHours = resourceConfirmationHours;
	}

	public Double getResourceConfirmationHours() {
		return resourceConfirmationHours;
	}

	public Long getBuyerId() {
		return buyerId;
	}

	public void setBuyerId(Long buyerId) {
		this.buyerId = buyerId;
	}

	public boolean isAutoPayEnabled() {
		return autoPayEnabled;
	}

	public void setAutoPayEnabled(boolean autoPayEnabled) {
		this.autoPayEnabled = autoPayEnabled;
	}

	public Integer getPaymentTermsDays() {
		return paymentTermsDays;
	}

	public void setPaymentTermsDays(Integer paymentTermsDays) {
		this.paymentTermsDays = paymentTermsDays;
	}

	public boolean isPaymentTermsEnabled() {
		return paymentTermsEnabled;
	}

	public void setPaymentTermsEnabled(boolean paymentTermsEnabled) {
		this.paymentTermsEnabled = paymentTermsEnabled;
	}

	public void setSecondaryClientContactId(Long secondaryClientContactId) {
		this.secondaryClientContactId = secondaryClientContactId;
	}

	public Long getSecondaryClientContactId() {
		return secondaryClientContactId;
	}

	public void setAppointmentTimeString(String appointmentTime) {
		this.appointmentTime = appointmentTime;
	}

	public String getAppointmentTimeString() {
		return appointmentTime;
	}

	@JsonIgnore
	public boolean isSetOnsiteAddress() {
		return isOnsiteAddress != null;
	}

	public void setCheckinCallRequired(boolean checkinCallRequired) {
		this.checkinCallRequired = checkinCallRequired;
	}

	public boolean isCheckinCallRequired() {
		return checkinCallRequired;
	}

	public Boolean isBadgeShowClientName() {
		return badgeShowClientName;
	}

	public void setBadgeShowClientName(Boolean badgeShowClientName) {
		this.badgeShowClientName = badgeShowClientName;
	}

	public Long getOnBehalfOfId() {
		return onBehalfOfId;
	}

	public void setOnBehalfOfId(Long onBehalfOfId) {
		this.onBehalfOfId = onBehalfOfId;
	}

	public void setTimeZoneId(Long timeZoneId) {
		this.timeZoneId = timeZoneId;
	}

	public Long getTimeZoneId() {
		return timeZoneId;
	}

	public Boolean getDisablePriceNegotiation() {
		return disablePriceNegotiation;
	}

	public void setDisablePriceNegotiation(Boolean disablePriceNegotiation) {
		this.disablePriceNegotiation = disablePriceNegotiation;
	}

	public boolean isAssignToFirstResource() {
		return assignToFirstResource;
	}

	public void setAssignToFirstResource(Boolean assignToFirstResource) {
		this.assignToFirstResource = assignToFirstResource;
	}

	public Boolean isShowInFeed() {
		return showInFeed;
	}

	public void setShowInFeed(Boolean showInFeed) {
		this.showInFeed = showInFeed;
	}

	public boolean isPartOfBulk() {
		return partOfBulk;
	}

	public void setPartOfBulk(boolean partOfBulk) {
		this.partOfBulk = partOfBulk;
	}

	public void setUseRequirementSets(Boolean useRequirementSets) {
		this.useRequirementSets = useRequirementSets;
	}

	public Boolean getUseRequirementSets() {
		return useRequirementSets;
	}

	public void setCustomFieldsEnabledFlag(boolean customFieldsEnabledFlag) {
		this.customFieldsEnabledFlag = customFieldsEnabledFlag;
	}

	public boolean isCustomFieldsEnabledFlag() {
		return customFieldsEnabledFlag;
	}

	public void setCustomCloseOutEnabledFlag(boolean customCloseOutEnabledFlag) {
		this.customCloseOutEnabledFlag = customCloseOutEnabledFlag;
	}

	public boolean isCustomCloseOutEnabledFlag() {
		return customCloseOutEnabledFlag;
	}

	public void setAssessmentsEnabled(boolean assessmentsEnabled) {
		this.assessmentsEnabled = assessmentsEnabled;
	}

	public boolean isAssessmentsEnabled() {
		return assessmentsEnabled;
	}

	public void setPartsLogisticsEnabledFlag(boolean partsLogisticsEnabledFlag) {
		this.partsLogisticsEnabledFlag = partsLogisticsEnabledFlag;
	}

	public boolean isPartsLogisticsEnabledFlag() {
		return partsLogisticsEnabledFlag;
	}

	public String getUniqueExternalId() {
		return uniqueExternalId;
	}

	public void setUniqueExternalId(String uniqueExternalId) {
		this.uniqueExternalId = uniqueExternalId;
	}

	public boolean isOfflinePayment() {
		return offlinePayment;
	}

	public void setOfflinePayment(boolean offlinePayment) {
		this.offlinePayment = offlinePayment;
	}

	public boolean isDocumentsEnabled() {
		return documentsEnabled;
	}

	public void setDocumentsEnabled(boolean documentsEnabled) {
		this.documentsEnabled = documentsEnabled;
	}
}
