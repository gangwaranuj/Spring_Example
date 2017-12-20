package com.workmarket.data.report.work;

import com.google.common.collect.Lists;
import com.workmarket.utility.DateUtilities;
import org.apache.commons.lang.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.TimeZone;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

public class AccountStatementFilters {

	public static final String DATE_DUE = "due";
	public static final String DATE_CREATED = "created";
	public static final String DATE_PAID = "paid";
	public static final String DATE_WORK = "work";

	private List<Long> buyerId;
	private List<Long> projectId;
	private List<Long> clientCompanyId;
	private List<Integer> assignedResourceId;
	private Boolean paidStatus;
	private String invoiceType;
	//For multiple filters, if this is set, invoiceType will be ignored when building the query.
	private List<String> invoiceTypes;
	private Boolean payables;
	private String dateFilterType;

	@DateTimeFormat(pattern="MM/dd/yyyy")
	private Calendar fromDate;
	@DateTimeFormat(pattern="MM/dd/yyyy")
	private Calendar toDate;
	private Calendar fromPaidDate;
	private Calendar toPaidDate;
	private Calendar fromDueDate;
	private Calendar toDueDate;
	private Calendar fromWorkDate;
	private Calendar toWorkDate;

	private Long statementId;
	private Long invoiceSummaryId;
	private Collection<Long> invoiceSummaryIds;
	private List<String> workNumbers = Lists.newArrayList();
	private Long invoiceId;
	private Boolean bundledInvoices;
	private Long salesUserId;
	private String userTimezone;

	private Boolean ignoreStatements = false;

	public List<Long> getBuyerId() {
		return buyerId;
	}

	public AccountStatementFilters setBuyerId(List<Long> buyerId) {
		this.buyerId = buyerId;
		return this;
	}

	public List<Long> getProjectId() {
		return projectId;
	}

	public AccountStatementFilters setProjectId(List<Long> projectId) {
		this.projectId = projectId;
		return this;
	}

	public List<Long> getClientCompanyId() {
		return clientCompanyId;
	}

	public AccountStatementFilters setClientCompanyId(List<Long> clientCompanyId) {
		this.clientCompanyId = clientCompanyId;
		return this;
	}

	public List<Integer> getAssignedResourceId() {
		return assignedResourceId;
	}

	public AccountStatementFilters setAssignedResourceId(List<Integer> assignedResourceId) {
		this.assignedResourceId = assignedResourceId;
		return this;
	}

	public Boolean getPaidStatus() {
		return paidStatus;
	}

	public AccountStatementFilters setPaidStatus(Boolean paidStatus) {
		this.paidStatus = paidStatus;
		return this;
	}

	public AccountStatementFilters setPaymentPendingStatus(Boolean paymentPending) {
		this.paidStatus = !paymentPending;
		return this;
	}

	public String getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
	}

	public List<String> getInvoiceTypes() {
		return invoiceTypes;
	}

	public void setInvoiceTypes(List<String> invoiceTypes) {
		this.invoiceTypes = invoiceTypes;
	}

	public Boolean getPayables() {
		return payables;
	}

	public AccountStatementFilters setPayables(Boolean payables) {
		this.payables = payables;
		return this;
	}

	public AccountStatementFilters setReceivables(Boolean receivables) {
		this.payables = !receivables;
		return this;
	}

	public String getDateFilterType() {
		return dateFilterType;
	}

	public AccountStatementFilters setDateFilterType(String dateFilterType) {
		this.dateFilterType = dateFilterType;
		return this;
	}

	public Calendar getFromDate() {
		return fromDate;
	}

	public AccountStatementFilters setFromDate(Calendar fromDate) {
		this.fromDate = fromDate;
		return this;
	}

	public Calendar getToDate() {
		return toDate;
	}

	public AccountStatementFilters setToDate(Calendar toDate) {
		this.toDate = toDate;
		return this;
	}

	public Calendar getFromPaidDate() {
		return fromPaidDate;
	}

	public AccountStatementFilters setFromPaidDate(Calendar fromPaidDate) {
		this.fromPaidDate = fromPaidDate;
		return this;
	}

	public Calendar getToPaidDate() {
		return toPaidDate;
	}

	public AccountStatementFilters setToPaidDate(Calendar toPaidDate) {
		this.toPaidDate = toPaidDate;
		return this;
	}

	public Calendar getFromWorkDate() {
		return fromWorkDate;
	}

	public AccountStatementFilters setFromWorkDate(Calendar fromWorkDate) {
		this.fromWorkDate = fromWorkDate;
		return this;
	}

	public Calendar getToWorkDate() {
		return toWorkDate;
	}

	public AccountStatementFilters setToWorkDate(Calendar toWorkDate) {
		this.toWorkDate = toWorkDate;
		return this;
	}

	public Calendar getFromDueDate() {
		return fromDueDate;
	}

	public AccountStatementFilters setFromDueDate(Calendar fromDueDate) {
		this.fromDueDate = fromDueDate;
		return this;
	}

	public Calendar getToDueDate() {
		return toDueDate;
	}

	public AccountStatementFilters setToDueDate(Calendar toDueDate) {
		this.toDueDate = toDueDate;
		return this;
	}

	public Long getStatementId() {
		return statementId;
	}

	public AccountStatementFilters setStatementId(Long statementId) {
		this.statementId = statementId;
		return this;
	}

	public List<String> getWorkNumbers() {
		return workNumbers;
	}

	public AccountStatementFilters setWorkNumbers(List<String> workNumbers) {
		this.workNumbers = workNumbers;
		return this;
	}

	public Long getInvoiceSummaryId() {
		return invoiceSummaryId;
	}

	public AccountStatementFilters setInvoiceSummaryId(Long invoiceSummaryId) {
		this.invoiceSummaryId = invoiceSummaryId;
		return this;
	}

	public Collection<Long> getInvoiceSummaryIds() {
		return invoiceSummaryIds;
	}

	public AccountStatementFilters setInvoiceSummaryIds(Collection<Long> invoiceSummaryIds) {
		this.invoiceSummaryIds = invoiceSummaryIds;
		return this;
	}

	public Long getInvoiceId() {
		return invoiceId;
	}
	
	public AccountStatementFilters setInvoiceId(Long invoiceId) {
		this.invoiceId = invoiceId;
		return this;
	}

	public AccountStatementFilters setBundledInvoices(Boolean bundledInvoices) {
		this.bundledInvoices = bundledInvoices;
		return this;
	}

	public Boolean getBundledInvoices() {
		return bundledInvoices;
	}

	public Long getSalesUserId() {
		return salesUserId;
	}

	public AccountStatementFilters setSalesUserId(Long salesUserId) {
		this.salesUserId = salesUserId;
		return this;
	}

	public boolean hasBuyerFilter() {
		return isNotEmpty(getBuyerId());
	}

	public boolean hasProjectFilter() {
		return isNotEmpty(getProjectId());
	}

	public boolean hasClientCompanyFilter() {
		return isNotEmpty(getClientCompanyId());
	}

	public boolean hasAssignedResourceIdFilter() {
		return isNotEmpty(getAssignedResourceId());
	}

	public boolean hasDateFilterType() {
		return StringUtils.isNotEmpty(dateFilterType);
	}

	public boolean hasDateFilter() {
		return getFromDate() != null || getToDate() != null;
	}

	public boolean hasPaidDateFilter() {
		return getFromPaidDate() != null || getToPaidDate() != null;
	}

	public boolean hasDueDateFilter() {
		return getFromDueDate() != null || getToDueDate() != null;
	}

	public boolean hasWorkDateFilter() {
		return getFromWorkDate() != null || getToWorkDate() != null;
	}

	public boolean hasPaidStatusFilter() {
		return getPaidStatus() != null;
	}

	public boolean hasReportTypeFilter() {
		return getPayables() != null;
	}

	public boolean hasStatementFilter() {
		return getStatementId() != null;
	}

	public boolean hasWorkNumbersFilter() {
		return !getWorkNumbers().isEmpty();
	}

	public boolean hasInvoiceSummaryIdFilter() {
		return getInvoiceSummaryId() != null;
	}

	public boolean hasInvoiceSummaryIdsFilter() {
		return isNotEmpty(invoiceSummaryIds);
	}
	
	public boolean hasInvoiceIdFilter() {
		return getInvoiceId() != null;
	}
	
	public boolean hasBundledInvoicesFilter() {
		return getBundledInvoices() != null;
	}

	public boolean hasSalesUserFilter() {
		return getSalesUserId() != null;
	}

	public AccountStatementFilters resetDateFilters() {
		dateFilterType = null;
		fromDate = null;
		toDate = null;
		fromDueDate = null;
		toDueDate = null;
		fromPaidDate = null;
		toPaidDate = null;
		fromWorkDate = null;
		toWorkDate = null;
		return this;
	}

	public AccountStatementFilters setDateFiltersInclusive() {
		if (fromDate != null) {
			if (StringUtils.isNotBlank(getUserTimezone())) {
				fromDate.setTimeInMillis(DateUtilities.changeTimeZoneRetainFields(fromDate.getTimeInMillis(), getUserTimezone()));
			}
		}
		if (toDate != null) {
			toDate.add(Calendar.DAY_OF_MONTH, 1);
			if (StringUtils.isNotBlank(getUserTimezone())) {
				toDate.setTimeInMillis(DateUtilities.changeTimeZoneRetainFields(toDate.getTimeInMillis(), getUserTimezone()));
			}
		}
		if (fromDueDate != null) {
			if (StringUtils.isNotBlank(getUserTimezone())) {
				fromDueDate.setTimeInMillis(DateUtilities.changeTimeZoneRetainFields(fromDueDate.getTimeInMillis(), getUserTimezone()));
			}
		}
		if (toDueDate != null) {
			toDueDate.add(Calendar.DAY_OF_MONTH, 1);
			if (StringUtils.isNotBlank(getUserTimezone())) {
				toDueDate.setTimeInMillis(DateUtilities.changeTimeZoneRetainFields(toDueDate.getTimeInMillis(), getUserTimezone()));
			}
		}
		if (fromPaidDate != null) {
			if (StringUtils.isNotBlank(getUserTimezone())) {
				fromPaidDate.setTimeInMillis(DateUtilities.changeTimeZoneRetainFields(fromPaidDate.getTimeInMillis(), getUserTimezone()));
			}
		}
		if (toPaidDate != null) {
			toPaidDate.add(Calendar.DAY_OF_MONTH, 1);
			if (StringUtils.isNotBlank(getUserTimezone())) {
				toPaidDate.setTimeInMillis(DateUtilities.changeTimeZoneRetainFields(toPaidDate.getTimeInMillis(), getUserTimezone()));
			}
		}
		return this;
	}

	public AccountStatementFilters setDateFiltersAccordingToType() {
		if (StringUtils.isEmpty(dateFilterType)) return this;

		Calendar from = DateUtilities.cloneCalendar(fromDate);
		Calendar to = DateUtilities.cloneCalendar(toDate);

		fromDate = null;
		toDate = null;

		if (DATE_CREATED.equalsIgnoreCase(dateFilterType)) {
			fromDate = from;
			toDate = to;
		} else if (DATE_DUE.equalsIgnoreCase(dateFilterType)) {
			fromDueDate = from;
			toDueDate = to;
		} else if (DATE_PAID.equalsIgnoreCase(dateFilterType)) {
			fromPaidDate = from;
			toPaidDate = to;
		} else if (DATE_WORK.equalsIgnoreCase(dateFilterType)) {
			fromWorkDate = from;
			toWorkDate = to;
		}

		return this;
	}

	public String getUserTimezone() {
		return userTimezone;
	}

	public void setUserTimezone(String userTimezone) {
		this.userTimezone = userTimezone;
	}

	public boolean hasAnyInvoiceSummaryFilter() {
		return (hasStatementFilter() || hasInvoiceSummaryIdFilter() || hasInvoiceSummaryIdsFilter());
	}
	
	public boolean isIgnoreStatementsFilter() {
		return getIgnoreStatements() != null && getIgnoreStatements();
	}
	
	public Boolean getIgnoreStatements() {
		return ignoreStatements;
	}
	
	public AccountStatementFilters setIgnoreStatements(Boolean ignoreStatements) {
		this.ignoreStatements = ignoreStatements;
		return this;
	}	
}