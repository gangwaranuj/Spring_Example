package com.workmarket.search.response.work;

import com.google.common.collect.Lists;
import com.workmarket.data.report.work.WorkSubStatusTypeReportRow;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.DateRangeUtilities;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.search.request.work.WorkSearchRequestUserType;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.NumberUtilities;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DashboardResult implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private String workNumber;
	private Long parentId;
	private String parentTitle;
	private String parentDescription;
	private String title;
	private String client;
	private DashboardAddress address;
	private long scheduleFrom;
	private DashboardResource resource;
	private double spendLimit;
	private double spendLimitWithFee;
	private String pricingType;
	private String buyerFullName;
	private String nextWorkflowStatusName;
	private long ownerCompanyId;
	private String ownerCompanyName;
	private double amountEarned;

	//private long createdDate;
	//private long scheduleFromDate;
	//private long approvedDate;
	private long sentDate;
	//private long paidDate;
	//private long dueDate;
	//private long scheduleThroughDate;
	private long completedDate;

	private long paidOn;
	private long invoiceId;
	private String invoiceNumber;
	private double buyerTotalCost;
	private String unResolvedSubstatuses;
	private long dueDate;
	private long modifiedOn;
	private long createdOn;
	private long scheduleThrough;
	private String modifierLastName;
	private String modifierFirstName;
	private String timeZoneId;
	private Map<String, String> customFieldMap;
	private DashboardResultFlags resultFlags;
	private String workStatusTypeCode;
	private String workStatusTypeDescription;
	private long buyerId;
	private int paymentTermsDays;
	private List<WorkSubStatusTypeReportRow> unresolvedWorkSubStatuses;
	private long appointmentFrom;
	private long appointmentThrough;
	private String projectName;
	private List<String> dispatchCandidateNames;
	private String recurrenceUUID;
	private Set<String> externalUniqueIds;
	private Long projectId;

	public DashboardResult() {
		this.spendLimit = 0D;
		this.buyerTotalCost = 0D;
		this.spendLimitWithFee = 0D;
	}

	public long getId() {
		return this.id;
	}

	public DashboardResult setId(long id) {
		this.id = id;
		return this;
	}

	public boolean isSetId() {
		return (id > 0L);
	}

	public String getWorkNumber() {
		return this.workNumber;
	}

	public DashboardResult setWorkNumber(String workNumber) {
		this.workNumber = workNumber;
		return this;
	}

	public Long getParentId() {
		return this.parentId;
	}

	public DashboardResult setParentId(Long parentId) {
		this.parentId = parentId;
		return this;
	}

	public String getParentTitle() {
		return this.parentTitle;
	}

	public DashboardResult setParentTitle(String parentTitle) {
		this.parentTitle = parentTitle;
		return this;
	}

	public String getParentDescription() {
		return this.parentDescription;
	}

	public DashboardResult setParentDescription(String parentDescription) {
		this.parentDescription = parentDescription;
		return this;
	}


	public boolean isSetWorkNumber() {
		return this.workNumber != null;
	}

	public String getTitle() {
		return this.title;
	}

	public DashboardResult setTitle(String title) {
		this.title = title;
		return this;
	}

	public boolean isSetTitle() {
		return this.title != null;
	}

	public String getClient() {
		return this.client;
	}

	public DashboardResult setClient(String client) {
		this.client = client;
		return this;
	}

	public boolean isSetClient() {
		return this.client != null;
	}

	public DashboardAddress getAddress() {
		return this.address;
	}

	public DashboardResult setAddress(DashboardAddress address) {
		this.address = address;
		return this;
	}

	public boolean isSetAddress() {
		return this.address != null;
	}

	public long getScheduleFrom() {
		return this.scheduleFrom;
	}

	public DashboardResult setScheduleFrom(long scheduleFrom) {
		this.scheduleFrom = scheduleFrom;
		return this;
	}

	public boolean isSetScheduleFrom() {
		return (scheduleFrom > 0L);
	}

	public long getAppointmentFrom() {
		return this.appointmentFrom;
	}

	public DashboardResult setAppointmentFrom(long appointmentFrom) {
		this.appointmentFrom = appointmentFrom;
		return this;
	}

	public boolean isSetAppointmentFrom() {
		return appointmentFrom > 0L;
	}

	public long getAppointmentThrough() {
		return this.appointmentThrough;
	}

	public DashboardResult setAppointmentThrough(long appointmentThrough) {
		this.appointmentThrough = appointmentThrough;
		return this;
	}

	public boolean isSetAppointmentThrough() {
		return appointmentThrough > 0L;
	}

	public DateRange getAssignmentAppointment() {
		DateRange scheduledTime = DateRangeUtilities.getDateRange(getScheduleFrom(), getScheduleThrough());
		DateRange appointmentTime = DateRangeUtilities.getDateRange(getAppointmentFrom(), getAppointmentThrough());
		return DateRangeUtilities.getAppointmentTime(scheduledTime, appointmentTime);
	}

	public DashboardResource getResource() {
		return this.resource;
	}

	public DashboardResult setResource(DashboardResource resource) {
		this.resource = resource;
		return this;
	}

	public boolean isSetResource() {
		return this.resource != null;
	}

	public String getProjectName() {
		return projectName;
	}

	public DashboardResult setProjectName(String projectName) {
		this.projectName = projectName;
		return this;
	}

	public boolean isSetProjectName() {
		return this.projectName != null;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public Long getProjectId() {
		return projectId;
	}

	public boolean isSetProjectId() {
		return this.projectId != null;
	}

	public double getSpendLimit() {
		return this.spendLimit;
	}

	public DashboardResult setSpendLimit(double spendLimit) {
		this.spendLimit = spendLimit;
		return this;
	}

	public double getSpendLimitWithFee() {
		return spendLimitWithFee;
	}

	public DashboardResult setSpendLimitWithFee(double spendLimitWithFee) {
		this.spendLimitWithFee = spendLimitWithFee;
		return this;
	}

	public boolean isSetSpendLimit() {
		return (spendLimit > 0D);
	}

	public String getPricingType() {
		return pricingType;
	}

	public void setPricingType(String pricingType) {
		this.pricingType = pricingType;
	}

	public String getBuyerFullName() {
		return this.buyerFullName;
	}

	public DashboardResult setBuyerFullName(String buyerFullName) {
		this.buyerFullName = buyerFullName;
		return this;
	}

	public boolean isSetBuyerFullName() {
		return this.buyerFullName != null;
	}

	public String getNextWorkflowStatusName() {
		return this.nextWorkflowStatusName;
	}

	public DashboardResult setNextWorkflowStatusName(String nextWorkflowStatusName) {
		this.nextWorkflowStatusName = nextWorkflowStatusName;
		return this;
	}

	public boolean isSetNextWorkflowStatusName() {
		return this.nextWorkflowStatusName != null;
	}

	public long getOwnerCompanyId() {
		return this.ownerCompanyId;
	}

	public DashboardResult setOwnerCompanyId(long ownerCompanyId) {
		this.ownerCompanyId = ownerCompanyId;
		return this;
	}

	public boolean isSetOwnerCompanyId() {
		return (ownerCompanyId > 0L);
	}

	public String getOwnerCompanyName() {
		return this.ownerCompanyName;
	}

	public DashboardResult setOwnerCompanyName(String ownerCompanyName) {
		this.ownerCompanyName = ownerCompanyName;
		return this;
	}

	public boolean isSetOwnerCompanyName() {
		return this.ownerCompanyName != null;
	}

	public double getAmountEarned() {
		return this.amountEarned;
	}

	public DashboardResult setAmountEarned(double amountEarned) {
		this.amountEarned = amountEarned;
		return this;
	}

	public boolean isSetAmountEarned() {
		return (amountEarned > 0D);
	}

	public long getPaidOn() {
		return this.paidOn;
	}

	public DashboardResult setPaidOn(long paidOn) {
		this.paidOn = paidOn;
		return this;
	}

	public boolean isSetPaidOn() {
		return (paidOn > 0L);
	}

	public long getInvoiceId() {
		return this.invoiceId;
	}

	public DashboardResult setInvoiceId(long invoiceId) {
		this.invoiceId = invoiceId;
		return this;
	}

	public boolean isSetInvoiceId() {
		return (invoiceId > 0L);
	}

	public String getInvoiceNumber() {
		return this.invoiceNumber;
	}

	public DashboardResult setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
		return this;
	}

	public boolean isSetInvoiceNumber() {
		return this.invoiceNumber != null;
	}

	public double getBuyerTotalCost() {
		return this.buyerTotalCost;
	}

	public DashboardResult setBuyerTotalCost(double buyerTotalCost) {
		this.buyerTotalCost = buyerTotalCost;
		return this;
	}

	public boolean isSetBuyerTotalCost() {
		return (buyerTotalCost > 0D);
	}

	public String getUnResolvedSubstatuses() {
		return this.unResolvedSubstatuses;
	}

	public DashboardResult setUnResolvedSubstatuses(String unResolvedSubstatuses) {
		this.unResolvedSubstatuses = unResolvedSubstatuses;
		return this;
	}

	public boolean isSetUnResolvedSubstatuses() {
		return this.unResolvedSubstatuses != null;
	}

	public long getDueDate() {
		return this.dueDate;
	}

	public DashboardResult setDueDate(long dueDate) {
		this.dueDate = dueDate;
		return this;
	}

	public boolean isSetDueDate() {
		return (dueDate > 0L);
	}

	public long getSentDate() {
		return this.sentDate;
	}

	public DashboardResult setSentDate(long sentDate) {
		this.sentDate = sentDate;
		return this;
	}

	public boolean isSetSentDate() {
		return (sentDate > 0L);
	}

	public long getCompletedDate() {
		return this.completedDate;
	}

	public DashboardResult setCompletedDate(long completedDate) {
		this.completedDate = completedDate;
		return this;
	}

	public boolean isSetCompletedDate() {
		return (completedDate > 0L);
	}

	public long getModifiedOn() {
		return this.modifiedOn;
	}

	public DashboardResult setModifiedOn(long modifiedOn) {
		this.modifiedOn = modifiedOn;
		return this;
	}

	public boolean isSetModifiedOn() {
		return (modifiedOn > 0L);
	}

	public long getCreatedOn() {
		return this.createdOn;
	}

	public DashboardResult setCreatedOn(long createdOn) {
		this.createdOn = createdOn;
		return this;
	}

	public boolean isSetCreatedOn() {
		return (createdOn > 0L);
	}

	public long getScheduleThrough() {
		return this.scheduleThrough;
	}

	public DashboardResult setScheduleThrough(long scheduleThrough) {
		this.scheduleThrough = scheduleThrough;
		return this;
	}

	public boolean isSetScheduleThrough() {
		return (scheduleThrough > 0L);
	}

	public String getModifierLastName() {
		return this.modifierLastName;
	}

	public DashboardResult setModifierLastName(String modifierLastName) {
		this.modifierLastName = modifierLastName;
		return this;
	}

	public boolean isSetModifierLastName() {
		return this.modifierLastName != null;
	}

	public String getModifierFirstName() {
		return this.modifierFirstName;
	}

	public DashboardResult setModifierFirstName(String modifierFirstName) {
		this.modifierFirstName = modifierFirstName;
		return this;
	}

	public boolean isSetModifierFirstName() {
		return this.modifierFirstName != null;
	}

	public String getTimeZoneId() {
		return this.timeZoneId;
	}

	public DashboardResult setTimeZoneId(String timeZoneId) {
		this.timeZoneId = timeZoneId;
		return this;
	}

	public boolean isSetTimeZoneId() {
		return this.timeZoneId != null;
	}


	public DashboardResultFlags getResultFlags() {
		return this.resultFlags;
	}

	public DashboardResult setResultFlags(DashboardResultFlags resultFlags) {
		this.resultFlags = resultFlags;
		return this;
	}

	public boolean isSetResultFlags() {
		return this.resultFlags != null;
	}

	public String getWorkStatusTypeCode() {
		return this.workStatusTypeCode;
	}

	public DashboardResult setWorkStatusTypeCode(String workStatusTypeCode) {
		this.workStatusTypeCode = workStatusTypeCode;
		return this;
	}

	public String getWorkStatusTypeDescription() {
		return this.workStatusTypeDescription;
	}

	public DashboardResult setWorkStatusTypeDescription(String workStatusTypeDescription) {
		this.workStatusTypeDescription = workStatusTypeDescription;
		return this;
	}

	public boolean isSetWorkStatusTypeCode() {
		return this.workStatusTypeCode != null;
	}

	public long getBuyerId() {
		return this.buyerId;
	}

	public DashboardResult setBuyerId(long buyerId) {
		this.buyerId = buyerId;
		return this;
	}

	public boolean isSetBuyerId() {
		return (buyerId > 0L);
	}

	public int getPaymentTermsDays() {
		return this.paymentTermsDays;
	}

	public DashboardResult setPaymentTermsDays(int paymentTermsDays) {
		this.paymentTermsDays = paymentTermsDays;
		return this;
	}

	public boolean isSetPaymentTermsDays() {
		return (paymentTermsDays > 0);
	}

	public int getUnresolvedWorkSubStatusesSize() {
		return (this.unresolvedWorkSubStatuses == null) ? 0 : this.unresolvedWorkSubStatuses.size();
	}

	public java.util.Iterator<WorkSubStatusTypeReportRow> getUnresolvedWorkSubStatusesIterator() {
		return (this.unresolvedWorkSubStatuses == null) ? null : this.unresolvedWorkSubStatuses.iterator();
	}

	public void addToUnresolvedWorkSubStatuses(WorkSubStatusTypeReportRow elem) {
		if (this.unresolvedWorkSubStatuses == null) {
			this.unresolvedWorkSubStatuses = new ArrayList<WorkSubStatusTypeReportRow>();
		}
		this.unresolvedWorkSubStatuses.add(elem);
	}

	public List<WorkSubStatusTypeReportRow> getUnresolvedWorkSubStatuses() {
		return this.unresolvedWorkSubStatuses;
	}

	public DashboardResult setUnresolvedWorkSubStatuses(List<WorkSubStatusTypeReportRow> unresolvedWorkSubStatuses) {
		this.unresolvedWorkSubStatuses = unresolvedWorkSubStatuses;
		return this;
	}

	public boolean isSetUnresolvedWorkSubStatuses() {
		return this.unresolvedWorkSubStatuses != null;
	}

	public boolean isAllDayWork() {
		return getAssignmentAppointment().getDaysBetweenDateRange() >= 24;
	}

	public Map<String, String> getCustomFieldMap() {
		return customFieldMap;
	}

	public void setCustomFieldMap(Map<String, String> customFieldMap) {
		this.customFieldMap = customFieldMap;
	}

	public List<String> getDispatchCandidateNames() { return this.dispatchCandidateNames; }

	public DashboardResult setDispatchCandidateNames(List<String> dispatchCandidateNames) {
		this.dispatchCandidateNames = dispatchCandidateNames;
		return this;
	}

	public boolean isSetDispatchCandidateNames() { return this.dispatchCandidateNames != null; }

	public String getFormattedPrice(boolean isResource) {
		if (getResultFlags().isInternal()) {
			return "Internal";
		} else if (Lists.newArrayList(WorkStatusType.COMPLETE, WorkStatusType.PAYMENT_PENDING, WorkStatusType.PAID).contains(getWorkStatusTypeCode())) {
			if (isResource) {
				return NumberUtilities.currency(getAmountEarned());
			} else {
				//buyer_total_cost
				if (WorkStatusType.COMPLETE.equals(getWorkStatusTypeCode())) {
					return NumberUtilities.currency(getSpendLimitWithFee());
				} else {
					return NumberUtilities.currency(getBuyerTotalCost());
				}
			}
		}
		if (isResource) {
			return NumberUtilities.currency(getSpendLimit());
		}
		return NumberUtilities.currency(getSpendLimitWithFee());
	}

	public String getFormattedLastModifiedDate() {
		// Last modified on
		if (isSetModifiedOn()) {
			long diff = getModifiedOn() - DateUtilities.getCalendarNow().getTimeInMillis();
			return DateUtilities.getSimpleDurationBreakdown(diff);
		}
		return StringUtils.EMPTY;
	}

	public String getFormattedWorkStatusType(WorkSearchRequestUserType workSearchRequestUserType, long currentUserCompanyId, String selectedStatus) {
		if (WorkStatusType.SENT.equals(this.getWorkStatusTypeCode()) &&
			WorkStatusType.AVAILABLE.equals(selectedStatus)) {
			return "Available";
		}

		WorkStatusType workStatusType = new WorkStatusType(this.getWorkStatusTypeCode());
		if(shouldFormatForEmployee(workSearchRequestUserType, currentUserCompanyId)) {
			return workStatusType.getEmployeeFormatted();
		} else {
			return workStatusType.getEmployerFormatted(resultFlags.isConfirmed(), resultFlags.isResourceConfirmationRequired());
		}
	}

	private boolean shouldFormatForEmployee(WorkSearchRequestUserType workSearchRequestUserType, long currentUserCompanyId) {
		return WorkSearchRequestUserType.RESOURCE.equals(workSearchRequestUserType) ||
			this.getOwnerCompanyId() != currentUserCompanyId;
	}

	public String getRecurrenceUUID() {
		return recurrenceUUID;
	}

	public void setRecurrenceUUID(String recurrenceUUID) {
		this.recurrenceUUID = recurrenceUUID;
	}

	public Set<String> getExternalUniqueIds() {
		return externalUniqueIds;
	}

	public void setExternalUniqueIds(final Set<String> externalUniqueIds) {
		this.externalUniqueIds = externalUniqueIds;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof DashboardResult)){
			return false;
		}

		DashboardResult that = (DashboardResult) o;

		return new EqualsBuilder()
			.append(id, that.getId())
			.append(workNumber, that.getWorkNumber())
			.append(parentId, that.getParentId())
			.append(parentTitle, that.getParentTitle())
			.append(parentDescription, that.getParentDescription())
			.append(title, that.getTitle())
			.append(client, that.getClient())
			.append(address, that.getAddress())
			.append(scheduleFrom, that.getScheduleFrom())
			.append(resource, that.getResource())
			.append(spendLimit, that.getSpendLimit())
			.append(spendLimitWithFee, that.getSpendLimitWithFee())
			.append(pricingType, that.getPricingType())
			.append(buyerFullName, that.getBuyerFullName())
			.append(nextWorkflowStatusName, that.getNextWorkflowStatusName())
			.append(ownerCompanyId, that.getOwnerCompanyId())
			.append(ownerCompanyName, that.getOwnerCompanyName())
			.append(amountEarned, that.getAmountEarned())
			.append(paidOn, that.getPaidOn())
			.append(invoiceId, that.getInvoiceId())
			.append(invoiceNumber, that.getInvoiceNumber())
			.append(buyerTotalCost, that.getBuyerTotalCost())
			.append(unResolvedSubstatuses, that.getUnResolvedSubstatuses())
			.append(dueDate, that.getDueDate())
			.append(modifiedOn, that.getModifiedOn())
			.append(createdOn, that.getCreatedOn())
			.append(scheduleThrough, that.getScheduleThrough())
			.append(modifierLastName, that.getModifierLastName())
			.append(modifierFirstName, that.getModifierFirstName())
			.append(timeZoneId, that.getTimeZoneId())
			.append(customFieldMap, that.getCustomFieldMap())
			.append(resultFlags, that.getResultFlags())
			.append(workStatusTypeCode, that.getWorkStatusTypeCode())
			.append(buyerId, that.getBuyerId())
			.append(paymentTermsDays, that.getPaymentTermsDays())
			.append(unresolvedWorkSubStatuses, that.getUnResolvedSubstatuses())
			.append(appointmentFrom, that.getAppointmentFrom())
			.append(appointmentThrough, that.getAppointmentThrough())
			.append(projectName, that.getProjectName())
			.append(dispatchCandidateNames, that.getDispatchCandidateNames())
			.append(recurrenceUUID, that.getRecurrenceUUID())
			.append(externalUniqueIds, that.getExternalUniqueIds())
			.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
			.append(id)
			.append(workNumber)
			.append(parentId)
			.append(parentTitle)
			.append(parentDescription)
			.append(title)
			.append(client)
			.append(address)
			.append(scheduleFrom)
			.append(resource)
			.append(spendLimit)
			.append(spendLimitWithFee)
			.append(pricingType)
			.append(buyerFullName)
			.append(nextWorkflowStatusName)
			.append(ownerCompanyId)
			.append(ownerCompanyName)
			.append(amountEarned)
			.append(paidOn)
			.append(invoiceId)
			.append(invoiceNumber)
			.append(buyerTotalCost)
			.append(unResolvedSubstatuses)
			.append(dueDate)
			.append(modifiedOn)
			.append(createdOn)
			.append(scheduleThrough)
			.append(modifierLastName)
			.append(modifierFirstName)
			.append(timeZoneId)
			.append(customFieldMap)
			.append(resultFlags)
			.append(workStatusTypeCode)
			.append(buyerId)
			.append(paymentTermsDays)
			.append(unresolvedWorkSubStatuses)
			.append(appointmentFrom)
			.append(appointmentThrough)
			.append(projectName)
			.append(dispatchCandidateNames)
			.append(recurrenceUUID)
			.append(externalUniqueIds)
			.toHashCode();
	}

	@Override
	public String toString() {
		return "DashboardResult{" +
			"id=" + id +
			", workNumber='" + workNumber + '\'' +
			", parentId=" + parentId +
			", parentTitle='" + parentTitle + '\'' +
			", parentDescription='" + parentDescription + '\'' +
			", title='" + title + '\'' +
			", client='" + client + '\'' +
			", address=" + address +
			", scheduleFrom=" + scheduleFrom +
			", resource=" + resource +
			", spendLimit=" + spendLimit +
			", spendLimitWithFee=" + spendLimitWithFee +
			", pricingType='" + pricingType + '\'' +
			", buyerFullName='" + buyerFullName + '\'' +
			", nextWorkflowStatusName='" + nextWorkflowStatusName + '\'' +
			", ownerCompanyId=" + ownerCompanyId +
			", ownerCompanyName='" + ownerCompanyName + '\'' +
			", amountEarned=" + amountEarned +
			", paidOn=" + paidOn +
			", invoiceId=" + invoiceId +
			", invoiceNumber='" + invoiceNumber + '\'' +
			", buyerTotalCost=" + buyerTotalCost +
			", unResolvedSubstatuses='" + unResolvedSubstatuses + '\'' +
			", dueDate=" + dueDate +
			", modifiedOn=" + modifiedOn +
			", createdOn=" + createdOn +
			", scheduleThrough=" + scheduleThrough +
			", modifierLastName='" + modifierLastName + '\'' +
			", modifierFirstName='" + modifierFirstName + '\'' +
			", timeZoneId='" + timeZoneId + '\'' +
			", customFieldMap=" + customFieldMap +
			", resultFlags=" + resultFlags +
			", workStatusTypeCode='" + workStatusTypeCode + '\'' +
			", buyerId=" + buyerId +
			", paymentTermsDays=" + paymentTermsDays +
			", unresolvedWorkSubStatuses=" + unresolvedWorkSubStatuses +
			", appointmentFrom=" + appointmentFrom +
			", appointmentThrough=" + appointmentThrough +
			", projectName='" + projectName + '\'' +
			", dispatchCandidateNames=" + dispatchCandidateNames + '\'' +
			", recurrenceUUID=" + recurrenceUUID + '\'' +
			", externalUniqueIds=" + externalUniqueIds +
			'}';
	}
}
