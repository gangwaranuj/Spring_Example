package com.workmarket.data.report.work;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.invoice.Invoice;
import com.workmarket.domains.model.invoice.InvoiceStatusType;
import com.workmarket.domains.model.invoice.InvoiceSummary;
import com.workmarket.domains.model.invoice.SubscriptionInvoice;
import com.workmarket.domains.model.postalcode.PostalCodeUtilities;
import com.workmarket.utility.DateUtilities;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

public class AccountStatementDetailRow extends DecoratedWorkReportRow {

	private static final long serialVersionUID = -2548159929222844451L;
	private String workNumber;
	private String workStatusTypeCode;
	private String workTitle;
	private Calendar workDate;
	private Calendar workCloseDate;
	private String workCity;
	private String workState;
	private String workPostalCode;
	private String workCountry;
	private String workResourceName;
	private String workResourceCompanyName;
	private String clientCompanyName;
	private String clientFirstName;
	private String clientLastName;
	private String buyerFullName;
	private Long buyerId;
	private String buyerPhone;
	private String buyerPhoneExtension;
	private Long companyId;
	private String companyName;
	private BigDecimal amountEarned = BigDecimal.ZERO;
	private BigDecimal buyerTotalCost = BigDecimal.ZERO;
	private Long invoiceId;
	private String invoiceNumber;
	private String invoiceDescription;
	private String invoiceStatusTypeCode;
	private BigDecimal invoiceBalance;
	private String invoiceType;
	private Long invoiceSummaryId;
	private String invoiceSummaryNumber;
	private String invoiceSummaryDescription;
	private Calendar invoiceSummaryDueDate;
	private Calendar invoiceDueDate;
	private Calendar invoiceVoidDate;
	private Calendar invoiceCreatedDate;
	private Calendar invoicePaymentDate;
	private Boolean invoiceIsBundled;
	private Integer paymentTermsDays;
	private boolean paymentTermsEnabled;
	private String timeZoneId;
	private List<AccountStatementDetailRow> bundledInvoices = Lists.newArrayList();
	private boolean owner = true;
	private String invoiceFulfillmentStatus;
	private BigDecimal invoiceRemainingBalance;
	private boolean pendingPaymentFulfillment;
	private Integer numberOfInvoices;
	private boolean editable;
	private Calendar downloadedOn;
	private String uniqueIdDisplayName;
	private String uniqueIdValue;


	public String getWorkNumber() {
		return workNumber;
	}

	public void setWorkNumber(String workNumber) {
		this.workNumber = workNumber;
	}

	public String getWorkStatusTypeCode() {
		return workStatusTypeCode;
	}

	public void setWorkStatusTypeCode(String workStatusTypeCode) {
		this.workStatusTypeCode = workStatusTypeCode;
	}

	public String getWorkTitle() {
		return workTitle;
	}

	public void setWorkTitle(String workTitle) {
		this.workTitle = workTitle;
	}

	public Calendar getWorkDate() {
		return workDate;
	}

	public void setWorkDate(Calendar workDate) {
		this.workDate = workDate;
	}

	public Calendar getWorkCloseDate() {
		return workCloseDate;
	}

	public void setWorkCloseDate(Calendar workCloseDate) {
		this.workCloseDate = workCloseDate;
	}

	public String getWorkCity() {
		return workCity;
	}

	public void setWorkCity(String workCity) {
		this.workCity = workCity;
	}

	public String getWorkState() {
		return workState;
	}

	public void setWorkState(String workState) {
		this.workState = workState;
	}

	public String getWorkPostalCode() {
		return workPostalCode;
	}

	public void setWorkPostalCode(String workPostalCode) {
		this.workPostalCode = workPostalCode;
	}

	public String getWorkCountry() {
		return workCountry;
	}

	public void setWorkCountry(String workCountry) {
		this.workCountry = workCountry;
	}

	public String getWorkResourceName() {
		return workResourceName;
	}

	public void setWorkResourceName(String workResourceName) {
		this.workResourceName = workResourceName;
	}

	public String getWorkResourceCompanyName() {
		return workResourceCompanyName;
	}

	public void setWorkResourceCompanyName(String workResourceCompanyName) {
		this.workResourceCompanyName = workResourceCompanyName;
	}

	public String getFormattedAddressShort() {
		return PostalCodeUtilities.formatAddressShort(workCity, workState, workPostalCode, workCountry);
	}

	public String getClientCompanyName() {
		return clientCompanyName;
	}

	public void setClientCompanyName(String clientCompanyName) {
		this.clientCompanyName = clientCompanyName;
	}

	public String getBuyerFullName() {
		return buyerFullName;
	}

	public void setBuyerFullName(String buyerFullName) {
		this.buyerFullName = buyerFullName;
	}

	public Long getBuyerId() {
		return buyerId;
	}

	public void setBuyerId(Long buyerId) {
		this.buyerId = buyerId;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getClientFirstName() {
		return clientFirstName;
	}

	public void setClientFirstName(String clientFirstName) {
		this.clientFirstName = clientFirstName;
	}

	public String getClientLastName() {
		return clientLastName;
	}

	public void setClientLastName(String clientLastName) {
		this.clientLastName = clientLastName;
	}

	public BigDecimal getAmountEarned() {
		return amountEarned;
	}

	public void setAmountEarned(BigDecimal amountEarned) {
		this.amountEarned = amountEarned;
	}

	public BigDecimal getBuyerTotalCost() {
		return buyerTotalCost;
	}

	public void setBuyerTotalCost(BigDecimal buyerTotalCost) {
		this.buyerTotalCost = buyerTotalCost;
	}

	public Long getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(Long invoiceId) {
		this.invoiceId = invoiceId;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public String getInvoiceDescription() {
		if (invoiceType.equals(Invoice.INVOICE_TYPE)) {
			return invoiceDescription + " (" + workNumber + ")";
		}
		return invoiceDescription;
	}

	public void setInvoiceDescription(String invoiceDescription) {
		this.invoiceDescription = invoiceDescription;
	}

	public String getInvoiceStatusTypeCode() {
		return invoiceStatusTypeCode;
	}

	public void setInvoiceStatusTypeCode(String invoiceStatusTypeCode) {
		this.invoiceStatusTypeCode = invoiceStatusTypeCode;
	}

	public BigDecimal getInvoiceBalance() {
		return invoiceBalance;
	}

	public void setInvoiceBalance(BigDecimal invoiceBalance) {
		this.invoiceBalance = invoiceBalance;
	}

	public String getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
	}

	public Long getInvoiceSummaryId() {
		return invoiceSummaryId;
	}

	public void setInvoiceSummaryId(Long invoiceSummaryId) {
		this.invoiceSummaryId = invoiceSummaryId;
	}

	public String getInvoiceSummaryNumber() {
		return invoiceSummaryNumber;
	}

	public void setInvoiceSummaryNumber(String invoiceSummaryNumber) {
		this.invoiceSummaryNumber = invoiceSummaryNumber;
	}

	public String getInvoiceSummaryDescription() {
		return invoiceSummaryDescription;
	}

	public void setInvoiceSummaryDescription(String invoiceSummaryDescription) {
		this.invoiceSummaryDescription = invoiceSummaryDescription;
	}

	public Calendar getInvoiceSummaryDueDate() {
		return invoiceSummaryDueDate;
	}

	public void setInvoiceSummaryDueDate(Calendar invoiceSummaryDueDate) {
		this.invoiceSummaryDueDate = invoiceSummaryDueDate;
	}

	public Calendar getInvoiceDueDate() {
		return invoiceDueDate;
	}

	public void setInvoiceDueDate(Calendar invoiceDueDate) {
		this.invoiceDueDate = invoiceDueDate;
	}

	public Calendar getInvoiceVoidDate() {
		return invoiceVoidDate;
	}

	public void setInvoiceVoidDate(Calendar invoiceVoidDate) {
		this.invoiceVoidDate = invoiceVoidDate;
	}

	public Calendar getInvoiceCreatedDate() {
		return invoiceCreatedDate;
	}

	public void setInvoiceCreatedDate(Calendar invoiceCreatedDate) {
		this.invoiceCreatedDate = invoiceCreatedDate;
	}

	public Calendar getInvoiceEarliestDueDate() {
		if(invoiceSummaryDueDate != null) {
			return invoiceSummaryDueDate;
		}
		return invoiceDueDate;
	}

	public boolean isInvoicePastDue() {
		if (InvoiceStatusType.PAID.equals(invoiceStatusTypeCode))
			return false;
		if (invoiceDueDate == null)
			return false;
		if (DateUtilities.isInPast(invoiceDueDate)) {
			return DateUtilities.getDaysBetween(Calendar.getInstance(), invoiceDueDate, true) >= 0;
		}
		return false;
	}

	public boolean isInvoiceDueWithinWeek() {
		if (InvoiceStatusType.PAID.equals(invoiceStatusTypeCode))
			return false;
		if (isInvoicePastDue())
			return false;
		if (invoiceDueDate == null)
			return false;
		return DateUtilities.getMidnightNextWeek().after(invoiceDueDate);
	}

	public Calendar getInvoicePaymentDate() {
		return invoicePaymentDate;
	}

	public void setInvoicePaymentDate(Calendar invoicePaymentDate) {
		this.invoicePaymentDate = invoicePaymentDate;
	}

	public Boolean getInvoiceIsBundled() {
		return invoiceIsBundled;
	}

	public void setInvoiceIsBundled(Boolean invoiceIsBundled) {
		this.invoiceIsBundled = invoiceIsBundled;
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

	public String getTimeZoneId() {
		return timeZoneId;
	}

	public void setTimeZoneId(String timeZoneId) {
		this.timeZoneId = timeZoneId;
	}

	public List<AccountStatementDetailRow> getBundledInvoices() {
		return bundledInvoices;
	}

	public void setBundledInvoices(List<AccountStatementDetailRow> bundledInvoices) {
		this.bundledInvoices = bundledInvoices;
	}

	public boolean isOwner() {
		return owner;
	}

	public void setOwner(boolean owner) {
		this.owner = owner;
	}

	public String getUniqueIdDisplayName() {
		return uniqueIdDisplayName;
	}

	public void setUniqueIdDisplayName(String uniqueIdDisplayName) {
		this.uniqueIdDisplayName = uniqueIdDisplayName;
	}

	public String getUniqueIdValue() {
		return uniqueIdValue;
	}

	public void setUniqueIdValue(String uniqueIdValue) {
		this.uniqueIdValue = uniqueIdValue;
	}

	@Override
	public String toString() {
		return "AccountStatementDetailRow [workNumber=" + workNumber + ", workStatusTypeCode=" + workStatusTypeCode + ", workTitle=" + workTitle + ", workCloseDate=" + workCloseDate
				+ ", clientCompanyName=" + clientCompanyName + ", buyerFullName=" + buyerFullName + ", buyerId=" + buyerId
				+ ", companyId=" + companyId + ", companyName=" + companyName + ", amountEarned=" + amountEarned + ", buyerTotalCost=" + buyerTotalCost + ", invoiceId=" + invoiceId
				+ ", invoiceNumber=" + invoiceNumber + ", invoiceDescription=" + invoiceDescription + ", invoiceStatusTypeCode=" + invoiceStatusTypeCode + ", invoiceBalance=" + invoiceBalance
				+ ", invoiceType=" + invoiceType + ", invoiceSummaryId=" + invoiceSummaryId + ", invoiceSummaryNumber=" + invoiceSummaryNumber + ", invoiceSummaryDescription="
				+ invoiceSummaryDescription + ", invoiceDueDate=" + invoiceDueDate + ", invoiceVoidDate=" + invoiceVoidDate + ", invoiceCreatedDate=" + invoiceCreatedDate + ", invoicePaymentDate=" + invoicePaymentDate + ", paymentTermsDays=" + paymentTermsDays + ", paymentTermsEnabled="
				+ paymentTermsEnabled + ", timeZoneId=" + timeZoneId + ", bundledInvoices=" + bundledInvoices + ", owner=" + owner + ", getWorkId()=" + getWorkId() + ", getCustomFields()="
				+ getCustomFields() + ", getUniqueIdDisplayName()=" + getUniqueIdDisplayName() + ", getUniqueIdValue()=" + getUniqueIdValue() + "]";
	}

	public String getBuyerPhone() {
		return buyerPhone;
	}

	public void setBuyerPhone(String buyerPhone) {
		this.buyerPhone = buyerPhone;
	}

	public String getBuyerPhoneExtension() {
		return buyerPhoneExtension;
	}

	public void setBuyerPhoneExtension(String buyerPhoneExtension) {
		this.buyerPhoneExtension = buyerPhoneExtension;
	}

	public String getInvoiceFulfillmentStatus() {
		return invoiceFulfillmentStatus;
	}

	public void setInvoiceFulfillmentStatus(String invoiceFulfillmentStatus) {
		this.invoiceFulfillmentStatus = invoiceFulfillmentStatus;
	}

	public BigDecimal getInvoiceRemainingBalance() {
		return invoiceRemainingBalance;
	}

	public void setInvoiceRemainingBalance(BigDecimal invoiceRemainingBalance) {
		this.invoiceRemainingBalance = invoiceRemainingBalance;
	}

	public boolean isPendingPaymentFulfillment() {
		return pendingPaymentFulfillment;
	}

	public void setPendingPaymentFulfillment(boolean pendingPaymentFulfillment) {
		this.pendingPaymentFulfillment = pendingPaymentFulfillment;
	}

	public Integer getNumberOfInvoices() {
		return numberOfInvoices;
	}

	public void setNumberOfInvoices(Integer numberOfInvoices) {
		this.numberOfInvoices = numberOfInvoices;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public boolean isBundle() {
		return InvoiceSummary.INVOICE_SUMMARY_TYPE.equals(invoiceType);
	}

	public boolean isSubscriptionInvoice() {
		return SubscriptionInvoice.SUBSCRIPTION_INVOICE_TYPE.equals(invoiceType);
	}

	public Calendar getDownloadedOn() {
		return downloadedOn;
	}

	public void setDownloadedOn(Calendar downloadedOn) {
		this.downloadedOn = downloadedOn;
	}
}
