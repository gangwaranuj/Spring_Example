package com.workmarket.data.solr.model;

import com.google.common.collect.Lists;
import com.workmarket.data.report.work.StandardDecoratedWorkReportRow;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.DateRangeUtilities;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.pricing.PricingStrategyType;
import com.workmarket.utility.DateUtilities;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.beans.Field;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SolrWorkData extends StandardDecoratedWorkReportRow implements SolrData, Comparable<SolrWorkData>  {

	private static final long serialVersionUID = -5085393588813694549L;

	private String uuid;
	private String publicTitle;
	private String description;
	private String instructions;
	private String skills;

	private String workStatusTypeCode;
	private String workStatusTypeDescription;

	private String contactName;
	private String contactPhone;
	private String contactEmail;
	private String supportName;
	private String supportPhone;
	private String supportEmail;

	private List<Long> workResourceIds = Lists.newArrayList();
	private List<Long> workResourceCompanyIds = Lists.newArrayList();
	private List<String> workResourceNames = Lists.newArrayList();
	private List<Long> applicantIds = Lists.newArrayList();
	private List<Long> cancelledWorkResourceIds = Lists.newArrayList();
	private List<String> buyerCustomFieldValues = Lists.newArrayList();
	private List<String> buyerCustomFieldNames = Lists.newArrayList();
	private List<String> dispatchCandidateNames = Lists.newArrayList();

	private String buyerUserId;
	private String creatorUserId;
	private Long dispatcherId;

	private Long clientLocationId;
	private String clientLocationName;
	private String clientLocationNumber;

	private Date indexDate = new Date();
	private Date createdDate;
	private Date scheduleFromDate;
	private Date approvedDate;
	private Date sendDate;
	private Date paidDate;
	private Date dueDate;
	private Date scheduleThroughDate;
	private Date completedDate;

	private Double longitude;
	private Double latitude;
	private String location;

	private Long industryId;
	private Boolean showInFeed;

	private Long projectId;
	private String projectName;
	private String companyName;

	private Double buyerFee;
	private Double workPrice;
	private Double amountEarned;
	private Double workFeePercentage;
	private Boolean autoPayEnabled;
	private String pricingType;

	private String modifierLastName;
	private String modifierFirstName;
	private Date lastModifiedDate;

	private Long assignedResourceId;
	private Long assignedResourceCompanyId;
	private String assignedResourceUserNumber;
	private String assignedResourceMobile;
	private String assignedResourceWorkPhoneNumber;
	private String assignedResourceWorkPhoneExtension;

	private List<String> searchableWorkStatusTypeCode;
	private List<String> resourceWorkStatusTypeCode;
	private List<Long> buyerLabelsId;
	private List<String> buyerLabelsIdDescription;
	private List<String> buyerLabelsWorkStatusIdDescription;

	private boolean confirmed;
	private boolean resourceConfirmationRequired;

	private boolean openQuestions;
	private boolean openNegotiations;
	private boolean assignToFirstResource;
	private boolean applied;
	private boolean applicationsPending;

	private List<String> followerIds = Lists.newArrayList();

	private Long parentId;
	private String parentTitle;
	private String parentDescription;

	private Long countyId;
	private String countyName;

	private Date assignedResourceAppointmentFrom;
	private Date assignedResourceAppointmentThrough;
	private List<Long> routedToGroups = Lists.newArrayList();

	private String uniqueExternalId;
	private String recurrenceUUID;
	private List<String> externalUniqueIds;

	@Override
	public long getId() {
		return getWorkId();
	}

	@Field
	public void setId(long id) {
		setWorkId(id);
	}

	public String getUuid() {
		return uuid;
	}

	@Field
	public void setUuid(final String uuid) {
		this.uuid = uuid;
	}

	@Field
	@Override
	public void setTitle(String title) {
		super.setTitle(title);
	}

	@Field
	public void setPublicTitle(String publicTitle) {
		this.publicTitle = publicTitle;
	}

	public String getPublicTitle() {
		return publicTitle;
	}

	@Field
	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	@Field
	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}

	public String getInstructions() {
		return instructions;
	}

	public String getSkills() {
		return skills;
	}

	@Field
	public void setSkills(String skills) {
		this.skills = skills;
	}

	@Field
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getContactName() {
		return contactName;
	}

	@Field
	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}


	public String getContactPhone() {
		return contactPhone;
	}

	@Field
	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public String getSupportName() {
		return supportName;
	}

	@Field
	public void setSupportName(String supportName) {
		this.supportName = supportName;
	}

	public String getSupportPhone() {
		return supportPhone;
	}

	@Field
	public void setSupportPhone(String supportPhone) {
		this.supportPhone = supportPhone;
	}

	public String getSupportEmail() {
		return supportEmail;
	}

	@Field
	public void setSupportEmail(String supportEmail) {
		this.supportEmail = supportEmail;
	}

	public List<Long> getWorkResourceIds() {
		return workResourceIds;
	}

	@Field
	public void setWorkResourceIds(List<Long> workResourceIds) {
		this.workResourceIds = workResourceIds;
	}

	public List<Long> getWorkResourceCompanyIds() {
		return workResourceCompanyIds;
	}

	@Field
	public void setWorkResourceCompanyIds(List<Long> workResourceCompanyIds) {
		this.workResourceCompanyIds = workResourceCompanyIds;
	}

	public List<Long> getApplicantIds() {
		return applicantIds;
	}

	@Field
	public void setApplicantIds(List<Long> applicantIds) {
		this.applicantIds = applicantIds;
	}

	public List<Long> getCancelledWorkResourceIds() {
		return cancelledWorkResourceIds;
	}

	@Field
	public void setCancelledWorkResourceIds(List<Long> cancelledWorkResourceIds) {
		this.cancelledWorkResourceIds = cancelledWorkResourceIds;
	}

	public List<String> getWorkResourceNames() {
		return workResourceNames;
	}

	@Field
	public void setWorkResourceNames(List<String> workResourceNames) {
		this.workResourceNames = workResourceNames;
	}

	public String getBuyerUserId() {
		return buyerUserId;
	}

	@Field
	public void setBuyerUserId(String buyerUserId) {
		this.buyerUserId = buyerUserId;
	}

	public String getCreatorUserId() {
		return creatorUserId;
	}

	@Field
	public void setCreatorUserId(String creatorUserId) {
		this.creatorUserId = creatorUserId;
	}

	public Long getDispatcherId() {
		return dispatcherId;
	}

	@Field
	public void setDispatcherId(Long dispatcherId) {
		this.dispatcherId = dispatcherId;
	}

	@Field
	public void setWorkStatusTypeCode(String workStatusTypeCode) {
		this.workStatusTypeCode = workStatusTypeCode;
	}

	public String getWorkStatusTypeCode() {
		return workStatusTypeCode;
	}

	@Field
	public void setWorkStatusTypeDescription(String workStatusTypeDescription) {
		this.workStatusTypeDescription = workStatusTypeDescription;
	}

	public boolean isSent() {
		return WorkStatusType.SENT.equals(workStatusTypeCode);
	}

	public String getWorkStatusTypeDescription() {
		if (WorkStatusType.INPROGRESS.equals(workStatusTypeCode)) {
			return "In Progress";
		}
		return workStatusTypeDescription;
	}

	public Date getIndexDate() {
		return indexDate;
	}

	@Field
	public void setIndexDate(Date indexDate) {
		this.indexDate = indexDate;
	}

	@Field
	@Override
	public void setCompanyId(Long companyId) {
		super.setCompanyId(companyId);
	}

	@Field
	@Override
	public void setWorkNumber(String workNumber) {
		super.setWorkNumber(workNumber);
	}

	public Long getParentId() {
		return parentId;
	}

	@Field
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public String getParentTitle() {
		return parentTitle;
	}

	@Field
	public void setParentTitle(String parentTitle) {
		this.parentTitle = parentTitle;
	}

	public String getParentDescription() {
		return parentDescription;
	}

	@Field
	public void setParentDescription(String parentDescription) {
		this.parentDescription = description;
	}

	public Long getClientLocationId() {
		return clientLocationId;
	}

	@Field
	public void setClientLocationId(Long clientLocationId) {
		this.clientLocationId = clientLocationId;
	}

	public String getClientLocationName() {
		return clientLocationName;
	}

	@Field
	public void setClientLocationName(String clientLocationName) {
		this.clientLocationName = clientLocationName;
	}

	public String getClientLocationNumber() {
		return clientLocationNumber;
	}

	@Field
	public void setClientLocationNumber(String clientLocationNumber) {
		this.clientLocationNumber = clientLocationNumber;
	}

	@Field
	@Override
	public void setClientCompanyId(Long clientCompanyId) {
		super.setClientCompanyId(clientCompanyId);
	}

	@Field
	@Override
	public void setClientCompanyName(String clientCompanyName) {
		super.setClientCompanyName(clientCompanyName);
	}

	public List<String> getBuyerCustomFieldNames() {
		return buyerCustomFieldNames;
	}

	@Field
	public void setBuyerCustomFieldNames(List<String> buyerCustomFieldNames) {
		this.buyerCustomFieldNames = buyerCustomFieldNames;
	}

	public List<String> getBuyerCustomFieldValues() {
		return buyerCustomFieldValues;
	}

	@Field
	public void setBuyerCustomFieldValues(List<String> buyerCustomFieldValues) {
		this.buyerCustomFieldValues = buyerCustomFieldValues;
	}

	public List<String> getDispatchCandidateNames() {
		return dispatchCandidateNames;
	}

	public void setDispatchCandidateNames(List<String> dispatchCandidateNames) {
		this.dispatchCandidateNames = dispatchCandidateNames;
	}

	public Double getLongitude() {
		return longitude;
	}

	@Field
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	@Field
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public String getLocation() {
		return location;
	}

	@Field
	public void setLocation(String location) {
		this.location = location;
	}

	public Date getCreatedDate() {
		return this.createdDate;
	}

	@Field
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getScheduleFromDate() {
		return this.scheduleFromDate;
	}

	@Field
	public void setScheduleFromDate(Date scheduleFromDate) {
		this.scheduleFromDate = scheduleFromDate;
	}

	public Long getIndustryId() {
		return this.industryId;
	}

	@Field
	public void setIndustryId(Long industryId) {
		this.industryId = industryId;
	}

	@Field
	@Override
	public void setCity(String city) {
		super.setCity(city);
	}

	@Field
	@Override
	public void setState(String state) {
		super.setState(state);
	}

	@Field
	@Override
	public void setPostalCode(String postalCode) {
		super.setPostalCode(postalCode);
	}

	@Field
	@Override
	public void setCountry(String country) {
		super.setCountry(country);
	}

	@Field
	@Override
	public void setBuyerFullName(String buyerFullName) {
		super.setBuyerFullName(buyerFullName);
	}

	public Boolean getShowInFeed() {
		return showInFeed;
	}

	@Field
	public void setShowInFeed(Boolean showInFeed) {
		this.showInFeed = showInFeed;
	}

	public Long getProjectId() {
		return projectId;
	}

	@Field
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public String getProjectName() {
		return projectName;
	}

	@Field
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	@Field
	@Override
	public void setAssignedResourceCompanyName(String assignedResourceCompanyName) {
		super.setAssignedResourceCompanyName(assignedResourceCompanyName);
	}

	public Date getApprovedDate() {
		return approvedDate;
	}

	@Field
	public void setApprovedDate(Date approvedDate) {
		this.approvedDate = approvedDate;
	}

	public Date getPaidDate() {
		return paidDate;
	}

	@Field
	public void setPaidDate(Date paidDate) {
		this.paidDate = paidDate;
	}

	public Date getSendDate() {
		return sendDate;
	}

	@Field
	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}

	public Date getScheduleThroughDate() {
		return scheduleThroughDate;
	}

	@Field
	public void setScheduleThroughDate(Date scheduleThroughDate) {
		this.scheduleThroughDate = scheduleThroughDate;
	}

	@Field
	@Override
	public void setOffSite(Boolean offSite) {
		super.setOffSite(offSite);
	}

	@Field
	@Override
	public void setSpendLimit(Double spendLimit) {
		super.setSpendLimit(spendLimit);
	}

	public String getCompanyName() {
		return companyName;
	}

	@Field
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Double getBuyerFee() {
		return buyerFee;
	}

	@Field
	public void setBuyerFee(Double buyerFee) {
		this.buyerFee = buyerFee;
	}

	@Field
	@Override
	public void setBuyerTotalCost(Double buyerTotalCost) {
		super.setBuyerTotalCost(buyerTotalCost);
	}

	public Double getWorkFeePercentage() {
		return workFeePercentage;
	}

	@Field
	public void setWorkFeePercentage(Double workFeePercentage) {
		this.workFeePercentage = workFeePercentage;
	}

	public Double getWorkPrice() {
		return workPrice;
	}

	@Field
	public void setWorkPrice(Double workPrice) {
		this.workPrice = workPrice;
	}

	@Field
	@Override
	public void setInvoiceId(Long invoiceId) {
		super.setInvoiceId(invoiceId);
	}


	public Date getDueDate() {
		return this.dueDate;
	}

	@Field
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	@Field
	@Override
	public void setInvoiceNumber(String invoiceNumber) {
		super.setInvoiceNumber(invoiceNumber);
	}

	public Boolean getAutoPayEnabled() {
		return autoPayEnabled;
	}

	@Field
	public void setAutoPayEnabled(Boolean autoPayEnabled) {
		this.autoPayEnabled = autoPayEnabled;
	}

	public String getPricingType() {
		return pricingType;
	}

	@Field
	public void setPricingType(String pricingType) {
		this.pricingType = pricingType;
	}

	@Field
	@Override
	public void setTimeZoneId(String timeZoneId) {
		super.setTimeZoneId(timeZoneId);
	}

	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	@Field
	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	@Override
	public int compareTo(SolrWorkData solrWorkData) {
		int result = this.getWorkId().compareTo(solrWorkData.getWorkId());
		return result != 0 ? -result : 0;
	}

	public Double getAmountEarned() {
		return amountEarned;
	}

	@Field
	public void setAmountEarned(Double amountEarned) {
		this.amountEarned = amountEarned;
	}

	public boolean isApplicationsPending() {
		return applicationsPending;
	}

	public boolean getApplicationsPending() {
		return applicationsPending;
	}

	@Field
	public void setApplicationsPending(boolean applicationsPending) {
		this.applicationsPending = applicationsPending;
	}

	public boolean isApplied() {
		return applied;
	}

	public void setApplied(boolean applied) {
		this.applied = applied;
	}

	public Date getAssignedResourceAppointmentFrom() {
		return assignedResourceAppointmentFrom;
	}

	public void setAssignedResourceAppointmentFrom(Calendar assignedResourceAppointmentFrom) {
		if (assignedResourceAppointmentFrom != null) this.assignedResourceAppointmentFrom = assignedResourceAppointmentFrom.getTime();
	}

	@Field
	public void setAssignedResourceAppointmentFrom(Date assignedResourceAppointmentFrom) {
		this.assignedResourceAppointmentFrom = assignedResourceAppointmentFrom;
	}

	public Date getAssignedResourceAppointmentThrough() {
		return assignedResourceAppointmentThrough;
	}

	@Field
	public void setAssignedResourceAppointmentThrough(Date assignedResourceAppointmentThrough) {
		this.assignedResourceAppointmentThrough = assignedResourceAppointmentThrough;
	}

	public void setAssignedResourceAppointmentThrough(Calendar assignedResourceAppointmentThrough) {
		if (assignedResourceAppointmentThrough != null) this.assignedResourceAppointmentThrough = assignedResourceAppointmentThrough.getTime();
	}

	public String getAssignedResourceUserNumber() {
		return assignedResourceUserNumber;
	}

	@Field
	public void setAssignedResourceUserNumber(String assignedResourceUserNumber) {
		this.assignedResourceUserNumber = assignedResourceUserNumber;
	}

	public boolean isAssignToFirstResource() {
		return assignToFirstResource;
	}

	public boolean getAssignToFirstResource() {
		return assignToFirstResource;
	}

	@Field
	public void setAssignToFirstResource(boolean assignToFirstResource) {
		this.assignToFirstResource = assignToFirstResource;
	}

	public boolean isConfirmed() {
		return confirmed;
	}

	public boolean getConfirmed() {
		return confirmed;
	}

	@Field
	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}

	public boolean isInternal() {
		if (StringUtils.isBlank(this.getPricingType())) {
			return false;
		}
		return PricingStrategyType.valueOf(this.pricingType).equals(PricingStrategyType.INTERNAL);
	}

	public long getModifiedOn() {
		if (lastModifiedDate != null) {
			return lastModifiedDate.getTime();
		}
		return 0L;
	}

	public String getModifierFirstName() {
		return modifierFirstName;
	}

	@Field
	public void setModifierFirstName(String modifierFirstName) {
		this.modifierFirstName = modifierFirstName;
	}

	public String getModifierLastName() {
		return modifierLastName;
	}

	@Field
	public void setModifierLastName(String modifierLastName) {
		this.modifierLastName = modifierLastName;
	}

	public boolean isResourceConfirmationRequired() {
		return resourceConfirmationRequired;
	}

	//Solr needs the GET
	public boolean getResourceConfirmationRequired() {
		return resourceConfirmationRequired;
	}

	@Field
	public void setResourceConfirmationRequired(boolean resourceConfirmationRequired) {
		this.resourceConfirmationRequired = resourceConfirmationRequired;
	}

	public Long getAssignedResourceId() {
		return assignedResourceId;
	}

	@Field
	public void setAssignedResourceId(Long assignedResourceId) {
		this.assignedResourceId = assignedResourceId;
	}

	public Long getAssignedResourceCompanyId() {
		return assignedResourceCompanyId;
	}

	@Field
	public void setAssignedResourceCompanyId(Long assignedResourceCompanyId) {
		this.assignedResourceCompanyId = assignedResourceCompanyId;
	}

	public Long getTimeToAppointment() {
		Calendar startTime = DateRangeUtilities.getAppointmentTime(
			new DateRange(getScheduleFrom(), getScheduleThrough()),
			new DateRange(DateUtilities.getCalendarFromDate(getAssignedResourceAppointmentFrom()), DateUtilities.getCalendarFromDate(getAssignedResourceAppointmentThrough())))
				.getFrom();

		return DateUtilities.getDifferenceInMillisFromNow(startTime);
	}

	public List<String> getSearchableWorkStatusTypeCode() {
		return searchableWorkStatusTypeCode;
	}

	@Field
	public void setSearchableWorkStatusTypeCode(List<String> searchableWorkStatusTypeCode) {
		this.searchableWorkStatusTypeCode = searchableWorkStatusTypeCode;
	}

	public List<String> getResourceWorkStatusTypeCode() {
		return resourceWorkStatusTypeCode;
	}

	@Field
	public void setResourceWorkStatusTypeCode(List<String> resourceWorkStatusTypeCode) {
		this.resourceWorkStatusTypeCode = resourceWorkStatusTypeCode;
	}

	public List<String> getBuyerLabelsIdDescription() {
		return buyerLabelsIdDescription;
	}

	@Field
	public void setBuyerLabelsIdDescription(List<String> buyerLabelsIdDescription) {
		this.buyerLabelsIdDescription = buyerLabelsIdDescription;
	}

	public List<String> getBuyerLabelsWorkStatusIdDescription() {
		return buyerLabelsWorkStatusIdDescription;
	}

	@Field
	public void setBuyerLabelsWorkStatusIdDescription(List<String> buyerLabelsWorkStatusIdDescription) {
		this.buyerLabelsWorkStatusIdDescription = buyerLabelsWorkStatusIdDescription;
	}

	public List<Long> getBuyerLabelsId() {
		return buyerLabelsId;
	}

	@Field
	public void setBuyerLabelsId(List<Long> buyerLabelsId) {
		this.buyerLabelsId = buyerLabelsId;
	}

	public Date getCompletedDate() {
		return completedDate;
	}

	@Field
	public void setCompletedDate(Date completedDate) {
		this.completedDate = completedDate;
	}

	@Override
	@Field
	public void setAssignedResourceFirstName(String assignedResourceFirstName) {
		super.setAssignedResourceFirstName(assignedResourceFirstName);
	}

	@Override
	@Field
	public void setAssignedResourceLastName(String assignedResourceLastName) {
		super.setAssignedResourceLastName(assignedResourceLastName);
	}

	public boolean isOpenQuestions() {
		return openQuestions;
	}

	public void setOpenQuestions(boolean openQuestions) {
		this.openQuestions = openQuestions;
	}

	public boolean isOpenNegotiations() {
		return openNegotiations;
	}

	public void setOpenNegotiations(boolean openNegotiations) {
		this.openNegotiations = openNegotiations;
	}

	public String getAssignedResourceMobile() {
		return assignedResourceMobile;
	}

	@Field
	public void setAssignedResourceMobile(String assignedResourceMobile) {
		this.assignedResourceMobile = assignedResourceMobile;
	}

	public String getAssignedResourceWorkPhoneExtension() {
		return assignedResourceWorkPhoneExtension;
	}

	@Field
	public void setAssignedResourceWorkPhoneExtension(String assignedResourceWorkPhoneExtension) {
		this.assignedResourceWorkPhoneExtension = assignedResourceWorkPhoneExtension;
	}

	public String getAssignedResourceWorkPhoneNumber() {
		return assignedResourceWorkPhoneNumber;
	}

	@Field
	public void setAssignedResourceWorkPhoneNumber(String assignedResourceWorkPhoneNumber) {
		this.assignedResourceWorkPhoneNumber = assignedResourceWorkPhoneNumber;
	}

	@Field
	@Override
	public void setSpendLimitWithFee(Double spendLimitWithFee) {
		super.setSpendLimitWithFee(spendLimitWithFee);
	}

	public List<String> getFollowerIds() {
		return followerIds;
	}

	@Field
	public void setFollowerIds(List<String> followerIds) {
		this.followerIds = followerIds;
	}

	public List<Long> getRoutedToGroups() {
		return routedToGroups;
	}

	@Field
	public void setRoutedToGroups(List<Long> routedToGroups) {
		this.routedToGroups = routedToGroups;
	}

	public Long getCountyId() {
		return countyId;
	}

	@Field
	public void setCountyId(Long countyId) {
		this.countyId = countyId;
	}

	public String getCountyName() {
		return countyName;
	}

	@Field
	public void setCountyName(String countyName) {
		this.countyName = countyName;
	}

	@Override
	public Calendar getScheduleFrom() {
		if (super.getScheduleFrom() != null) {
			return super.getScheduleFrom();
		}
		return DateUtilities.getCalendarFromDate(scheduleFromDate);
	}

	@Override
	public Calendar getScheduleThrough() {
		if (super.getScheduleThrough() != null) {
			return super.getScheduleThrough();
		}
	 	return DateUtilities.getCalendarFromDate(scheduleThroughDate);
	}

	@Override
	public Calendar getPaidOn() {
		if (super.getPaidOn() != null) {
			return super.getPaidOn();
		}
		return DateUtilities.getCalendarFromDate(paidDate);
	}

	@Override
	public Calendar getCreatedOn() {
		if (super.getCreatedOn() != null) {
			return super.getCreatedOn();
		}
		return DateUtilities.getCalendarFromDate(createdDate);
	}

	@Override
	public Calendar getSentOn() {
		if (super.getSentOn() != null) {
			return super.getSentOn();
		}
		return DateUtilities.getCalendarFromDate(sendDate);
	}

	@Override
	public Calendar getDueOn() {
		if (super.getDueOn() != null) {
			return super.getDueOn();
		}
		return DateUtilities.getCalendarFromDate(dueDate);
	}

	public void clearAssignedResourceData() {
		this.setAssignedResourceCompanyName("");
		this.setAssignedResourceFirstName("");
		this.setAssignedResourceLastName("");
		this.setAssignedResourceUserNumber("");
		this.setAssignedResourceMobile("");
		this.setAssignedResourceWorkPhoneNumber("");
		this.setAssignedResourceWorkPhoneExtension("");
	}

	public String getUniqueExternalId() {
		return uniqueExternalId;
	}

	@Field
	public void setUniqueExternalId(String uniqueExternalId) {
		this.uniqueExternalId = uniqueExternalId;
	}

	public String getRecurrenceUUID() {
		return recurrenceUUID;
	}

	@Field
	public void setRecurrenceUUID(String recurrenceUUID) {
		this.recurrenceUUID = recurrenceUUID;
	}

	public List<String> getExternalUniqueIds() {
		return externalUniqueIds;
	}

	@Field
	public void setExternalUniqueIds(final List<String> externalUniqueIds) {
		this.externalUniqueIds = externalUniqueIds;
	}
}
