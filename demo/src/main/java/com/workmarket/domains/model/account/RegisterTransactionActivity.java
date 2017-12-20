package com.workmarket.domains.model.account;

import com.workmarket.domains.model.banking.BankAccount;
import com.workmarket.domains.model.invoice.AdHocInvoice;
import com.workmarket.domains.model.invoice.CreditMemo;
import com.workmarket.domains.model.invoice.InvoiceSummary;
import com.workmarket.domains.model.invoice.Statement;
import com.workmarket.domains.model.invoice.SubscriptionInvoice;
import com.workmarket.data.report.work.DecoratedWorkReportRow;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.NumberUtilities;
import com.workmarket.utility.StringUtilities;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.Calendar;

public class RegisterTransactionActivity extends DecoratedWorkReportRow {

	private static final long serialVersionUID = 8674477442986768057L;

	private static final String DEPOSIT_PENDING = "Deposit pending";

	private Long registerTransactionId;
	private Calendar registerTransactionDate;
	private BigDecimal amount;
	private String registerTransactionTypeCode;
	private String registerTransactionTypeDescription;
	private boolean showAssignmentTitle;
	private String workTitle;
	private String workNumber;
	private Long invoiceId;
	private String invoiceNumber;
	private Long workResourceId;
	private String workResourceUserNumber;
	private String workResourceFirstName;
	private String workResourceLastName;
	private String displayTypeCode;
	private String displayTypeDescription;
	private BigDecimal availableCash;
	private BigDecimal generalCash;
	private BigDecimal projectCash;
	private BigDecimal actualCash;
	private boolean owner;
	private String ownerCompanyName;
	private String clientName;
	private Calendar scheduleDate;
	private Calendar closedOn;
	private Calendar paidOn;
	private Long paidInvoiceSummaryId;
	private String paidInvoiceSummaryNumber;
	private String paidInvoiceSummaryType;
	private String paidInvoiceSummaryDescription;
	private String invoiceSummaryNumber;
	private boolean bulkInvoicePayment;
	private Calendar statementPeriodStartDate;
	private Calendar statementPeriodEndDate;
	private String bankAccountName;
	private String bankAccountNumber;
	private String bankAccountTransactionStatus;
	private boolean creditCardTransaction = false;
	private boolean bankAccountTransaction = false;
	private boolean pendingTransaction = false;

	public Long getRegisterTransactionId() {
		return registerTransactionId;
	}

	public void setRegisterTransactionId(Long registerTransactionId) {
		this.registerTransactionId = registerTransactionId;
	}

	public Calendar getRegisterTransactionDate() {
		return registerTransactionDate;
	}

	public void setRegisterTransactionDate(Calendar registerTransactionDate) {
		this.registerTransactionDate = registerTransactionDate;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getRegisterTransactionTypeCode() {
		return registerTransactionTypeCode;
	}

	public void setRegisterTransactionTypeCode(String registerTransactionTypeCode) {
		this.registerTransactionTypeCode = registerTransactionTypeCode;
	}

	public String getRegisterTransactionTypeDescription() {
		return registerTransactionTypeDescription;
	}

	public void setRegisterTransactionTypeDescription(String registerTransactionTypeDescription) {
		this.registerTransactionTypeDescription = registerTransactionTypeDescription;
	}

	public boolean isShowAssignmentTitle() {
		return showAssignmentTitle;
	}

	public void setShowAssignmentTitle(boolean showAssignmentTitle) {
		this.showAssignmentTitle = showAssignmentTitle;
	}

	public String getWorkTitle() {
		return workTitle;
	}

	public void setWorkTitle(String workTitle) {
		this.workTitle = workTitle;
	}

	public String getWorkNumber() {
		return workNumber;
	}

	public void setWorkNumber(String workNumber) {
		this.workNumber = workNumber;
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

	public Long getWorkResourceId() {
		return workResourceId;
	}

	public void setWorkResourceId(Long workResourceId) {
		this.workResourceId = workResourceId;
	}

	public String getWorkResourceUserNumber() {
		return workResourceUserNumber;
	}

	public void setWorkResourceUserNumber(String workResourceUserNumber) {
		this.workResourceUserNumber = workResourceUserNumber;
	}

	public String getWorkResourceFirstName() {
		return workResourceFirstName;
	}

	public void setWorkResourceFirstName(String workResourceFirstName) {
		this.workResourceFirstName = workResourceFirstName;
	}

	public String getWorkResourceLastName() {
		return workResourceLastName;
	}

	public void setWorkResourceLastName(String workResourceLastName) {
		this.workResourceLastName = workResourceLastName;
	}

	public String getDisplayTypeCode() {
		return displayTypeCode;
	}

	public void setDisplayTypeCode(String displayTypeCode) {
		this.displayTypeCode = displayTypeCode;
	}

	public String getDisplayTypeDescription() {
		if (registerTransactionTypeCode.equals(RegisterTransactionType.INVOICE_PAYMENT)) {
			return displayTypeDescription + " (" + paidInvoiceSummaryNumber + ")";
		}
		return displayTypeDescription;
	}

	public void setDisplayTypeDescription(String displayTypeDescription) {
		this.displayTypeDescription = displayTypeDescription;
	}

	public BigDecimal getAvailableCash() {
		return availableCash;
	}

	public void setAvailableCash(BigDecimal availableCash) {
		this.availableCash = availableCash;
	}

	public BigDecimal getGeneralCash() {
		return generalCash;
	}

	public void setGeneralCash(BigDecimal generalCash) {
		this.generalCash = generalCash;
	}

	public BigDecimal getProjectCash() {
		return projectCash;
	}

	public void setProjectCash(BigDecimal projectCash) {
		this.projectCash = projectCash;
	}

	public BigDecimal getActualCash() {
		return actualCash;
	}

	public void setActualCash(BigDecimal actualCash) {
		this.actualCash = actualCash;
	}

	public boolean isOwner() {
		return owner;
	}

	public void setOwner(boolean owner) {
		this.owner = owner;
	}

	public String getOwnerCompanyName() {
		return ownerCompanyName;
	}

	public void setOwnerCompanyName(String ownerCompanyName) {
		this.ownerCompanyName = ownerCompanyName;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public Calendar getScheduleDate() {
		return scheduleDate;
	}

	public void setScheduleDate(Calendar scheduleDate) {
		this.scheduleDate = scheduleDate;
	}

	public Calendar getClosedOn() {
		return closedOn;
	}

	public void setClosedOn(Calendar closedOn) {
		this.closedOn = closedOn;
	}

	public Calendar getPaidOn() {
		return paidOn;
	}

	public void setPaidOn(Calendar paidOn) {
		this.paidOn = paidOn;
	}

	public Long getPaidInvoiceSummaryId() {
		return paidInvoiceSummaryId;
	}

	public void setPaidInvoiceSummaryId(Long paidInvoiceSummaryId) {
		this.paidInvoiceSummaryId = paidInvoiceSummaryId;
	}

	public String getPaidInvoiceSummaryNumber() {
		return paidInvoiceSummaryNumber;
	}

	public void setPaidInvoiceSummaryNumber(String paidInvoiceSummaryNumber) {
		this.paidInvoiceSummaryNumber = paidInvoiceSummaryNumber;
	}

	public String getPaidInvoiceSummaryType() {
		return paidInvoiceSummaryType;
	}

	public void setPaidInvoiceSummaryType(String paidInvoiceSummaryType) {
		this.paidInvoiceSummaryType = paidInvoiceSummaryType;
	}

	public String getPaidInvoiceSummaryDescription() {
		return paidInvoiceSummaryDescription;
	}

	public void setPaidInvoiceSummaryDescription(String paidInvoiceSummaryDescription) {
		this.paidInvoiceSummaryDescription = paidInvoiceSummaryDescription;
	}

	public String getInvoiceSummaryNumber() {
		return invoiceSummaryNumber;
	}

	public void setInvoiceSummaryNumber(String invoiceSummaryNumber) {
		this.invoiceSummaryNumber = invoiceSummaryNumber;
	}

	public boolean isBulkInvoicePayment() {
		return bulkInvoicePayment;
	}

	public void setBulkInvoicePayment(boolean bulkInvoicePayment) {
		this.bulkInvoicePayment = bulkInvoicePayment;
	}

	public Calendar getStatementPeriodStartDate() {
		return statementPeriodStartDate;
	}

	public void setStatementPeriodStartDate(Calendar statementPeriodStartDate) {
		this.statementPeriodStartDate = statementPeriodStartDate;
	}

	public Calendar getStatementPeriodEndDate() {
		return statementPeriodEndDate;
	}

	public void setStatementPeriodEndDate(Calendar statementPeriodEndDate) {
		this.statementPeriodEndDate = statementPeriodEndDate;
	}

	public String getBankAccountName() {
		return bankAccountName;
	}

	public void setBankAccountName(String bankAccountName) {
		this.bankAccountName = bankAccountName;
	}

	public String getBankAccountNumber() {
		return bankAccountNumber;
	}

	public void setBankAccountNumber(String bankAccountNumber) {
		this.bankAccountNumber = bankAccountNumber;
	}

	public boolean isWorkInvoice() {
		return invoiceId != null;
	}

	public boolean isBundle() {
		return InvoiceSummary.INVOICE_SUMMARY_TYPE.equals(paidInvoiceSummaryType);
	}

	public boolean isServiceInvoice() {
		return SubscriptionInvoice.SUBSCRIPTION_INVOICE_TYPE.equals(paidInvoiceSummaryType) ||
				AdHocInvoice.AD_HOC_INVOICE_TYPE.equals(paidInvoiceSummaryType);
	}

	public boolean isCreditMemo(){
		return CreditMemo.CREDIT_MEMO_TYPE.equals(paidInvoiceSummaryType);
	}

	public boolean isStatement() {
		return Statement.STATEMENT_TYPE.equals(paidInvoiceSummaryType);
	}

	public boolean isSubscriptionInvoice() {
		return SubscriptionInvoice.SUBSCRIPTION_INVOICE_TYPE.equals(paidInvoiceSummaryType);
	}

	public String getBankAccountTransactionStatus() {
		return bankAccountTransactionStatus;
	}

	public void setBankAccountTransactionStatus(String bankAccountTransactionStatus) {
		this.bankAccountTransactionStatus = bankAccountTransactionStatus;
	}

	public boolean isPendingBankAccountTransaction() {
		return BankAccountTransactionStatus.SUBMITTED.equals(getBankAccountTransactionStatus());
	}

	public boolean isPreFundAssignmentAuthorization() {
		return StringUtils.equals(RegisterTransactionType.BUYER_COMMITMENT_TO_PAY, registerTransactionTypeCode);
	}

	// Presentational accessors

	public String getFormattedId() {
		return workNumber;
	}

	public String getFormattedTypeDescription() {
		if (isPreFundAssignmentAuthorization()) {
			return StringUtils.EMPTY;
		} else if (isBundle() || isStatement()) {
			return registerTransactionTypeDescription;
		} else if (RegisterTransactionType.REMOVE_FUNDS_GCC.equals(registerTransactionTypeCode)) {
			return String.format("%s (%s)", displayTypeDescription, StringUtilities.getBankAccountLastFourDigits(bankAccountNumber));
		} else if (RegisterTransactionType.CREDIT_MEMO.equals(registerTransactionTypeCode)){
			return paidInvoiceSummaryDescription;
		}

		return displayTypeDescription;
	}

	public String getFormattedDescription() {
		String description;
		if (isShowAssignmentTitle()) {
			description = workTitle;
		} else if (isBundle() || isServiceInvoice() || isCreditMemo()) {
			description = paidInvoiceSummaryDescription;
		} else if (isStatement()) {
			description = String.format("Statement for %s - %s", DateUtilities.format("M/d/YYYY", statementPeriodStartDate), DateUtilities.format("M/d/YYYY", statementPeriodEndDate));
		} else if (registerTransactionTypeCode.equals(RegisterTransactionType.REMOVE_FUNDS)) {
			if (StringUtilities.all(bankAccountName, bankAccountNumber)) {
				return String.format("%s to %s", registerTransactionTypeDescription, BankAccount.formatName(bankAccountName, bankAccountNumber));
			}
			return registerTransactionTypeDescription;
		} else if (registerTransactionTypeCode.equals(RegisterTransactionType.ADD_FUNDS)) {
			if (isPendingTransaction()) {
				if (isBankAccountTransaction()) {
					return DEPOSIT_PENDING + " via ACH (processing)";
				}
				if (isCreditCardTransaction()) {
					return DEPOSIT_PENDING + " via Credit Card (processing)";
				}
			}
			if (StringUtilities.all(bankAccountName, bankAccountNumber)) {
				return String.format("%s from %s", registerTransactionTypeDescription, BankAccount.formatName(bankAccountName, bankAccountNumber));
			}
			if (isBankAccountTransaction()) {
				return registerTransactionTypeDescription + " via ACH";
			}
			if (isCreditCardTransaction()) {
				return registerTransactionTypeDescription + " via Credit Card";
			}
			return registerTransactionTypeDescription;
		} else if (RegisterTransactionType.CREDIT_MEMO.equals(registerTransactionTypeCode)) {
			return paidInvoiceSummaryDescription;
		} else {
			description = registerTransactionTypeDescription;
		}
		if (amount.compareTo(BigDecimal.ZERO) == 0) {
			return description;
		}
		return isPreFundAssignmentAuthorization() ?
				"In Progress Cash Assignments On Immediate Terms" :
				description;
	}

	public String getFormattedWithdrawal() {
		if (NumberUtilities.isNegative(amount)) {
			return NumberUtilities.currency(amount);
		}
		return StringUtils.EMPTY;
	}

	public String getFormattedDeposit() {
		//If it's a work payment show the amount even if it's zero
		if (NumberUtilities.isPositive(amount) || RegisterTransactionType.RESOURCE_WORK_PAYMENT.equals(registerTransactionTypeCode)) {
			return NumberUtilities.currency(amount);
		}
		return StringUtils.EMPTY;
	}

	public String getFormattedBalance() {
		if (isPendingBankAccountTransaction() || isPreFundAssignmentAuthorization()) {
			return StringUtils.EMPTY;
		}
		return NumberUtilities.currency(actualCash);
	}

	public String getFormattedTransactionDate() {
		return DateUtilities.format("MM/dd/yyyy", getRegisterTransactionDate());
	}

	public String getFormattedTransactionDateWithTimeZone(String timeZoneId) {
		if (isPreFundAssignmentAuthorization()) {
			return StringUtils.EMPTY;
		}
		return DateUtilities.formatMillis("MM/dd/yyyy h:mma", getRegisterTransactionDate().getTimeInMillis(), timeZoneId);
	}

	public boolean isSetWorkNumber() {
		return workNumber != null;
	}

	public String getDepositPaymentType() {
		if (RegisterTransactionType.ADD_FUNDS.equals(getRegisterTransactionTypeCode())) {
			if(isCreditCardTransaction()) {
				return "credit";
			}
			return "bank";
		}
		else if (RegisterTransactionType.CREDIT_WIRE_DIRECT_DEPOSIT.equals(getRegisterTransactionTypeCode())) {
			return "wire";
		}
		else if (RegisterTransactionType.CREDIT_CHECK_DEPOSIT.equals(getRegisterTransactionTypeCode())) {
			return "check";
		}
		return "";
	}

	public boolean isCreditCardTransaction() {
		return creditCardTransaction;
	}

	public void setCreditCardTransaction(boolean creditCardTransaction) {
		this.creditCardTransaction = creditCardTransaction;
	}

	public boolean isBankAccountTransaction() {
		return bankAccountTransaction;
	}

	public void setBankAccountTransaction(boolean bankAccountTransaction) {
		this.bankAccountTransaction = bankAccountTransaction;
	}

	public boolean isPendingTransaction() {
		return pendingTransaction;
	}

	public void setPendingTransaction(String pendingFlag) {
		this.pendingTransaction = pendingFlag.equals("Y");
	}
}