package com.workmarket.domains.model.account;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.math.BigDecimal;

@Embeddable
@Access(AccessType.PROPERTY)
public class AccountRegisterSummaryFields implements Serializable {

	/**
	 * Instance variables and constants
	 */
	private BigDecimal availableCash = BigDecimal.ZERO;
	private BigDecimal generalCash = BigDecimal.ZERO;
	private BigDecimal projectCash = BigDecimal.ZERO;
	private BigDecimal depositedCash = BigDecimal.ZERO;
	private BigDecimal withdrawableCash = BigDecimal.ZERO;
	private BigDecimal pendingEarnedCash = BigDecimal.ZERO;
	private BigDecimal accountsPayableBalance = BigDecimal.ZERO;
	private BigDecimal accountsReceivableBalance = BigDecimal.ZERO;
	private BigDecimal pendingCommitments = BigDecimal.ZERO;
	private BigDecimal actualCash = BigDecimal.ZERO;
	private BigDecimal assignmentThroughput = BigDecimal.ZERO;
	//IF subscription pricing these 2 fields should reset every subscription year
	private BigDecimal assignmentSoftwareThroughput = BigDecimal.ZERO;
	@Deprecated
	private BigDecimal assignmentVorThroughput = BigDecimal.ZERO;

	private static final long serialVersionUID = -5084499753430976484L;
	private static final Log logger = LogFactory.getLog(AccountRegisterSummaryFields.class);

	public AccountRegisterSummaryFields() {

	}


	/**
	 * @return the availableCash
	 */
	@Column(name = "available_cash", nullable = false)
	public BigDecimal getAvailableCash() {
		//logger.debug("availableCash:" + availableCash);
		return availableCash;
	}

	/**
	 * @param availableCash the availableCash to set
	 */
	public void setAvailableCash(BigDecimal availableCash) {
		this.availableCash = availableCash;
	}

	/**
	 * @return the generalCash
	 */
	@Column(name = "general_cash", nullable = false)
	public BigDecimal getGeneralCash() {
		return generalCash;
	}

	/**
	 * @param generalCash the generalCash to set
	 */
	public void setGeneralCash(BigDecimal generalCash) {
		this.generalCash = generalCash;
	}

	/**
	 * @return the projectCash
	 */
	@Column(name = "project_cash", nullable = false)
	public BigDecimal getProjectCash() {
		return projectCash;
	}

	/**
	 * @param projectCash the projectCash to set
	 */
	public void setProjectCash(BigDecimal projectCash) {
		this.projectCash = projectCash;
	}

	/**
	 * @return the depositedCash
	 */
	@Column(name = "deposited_cash", nullable = false)
	public BigDecimal getDepositedCash() {
		return depositedCash;
	}

	/**
	 * @param depositedCash the depositedCash to set
	 */
	public void setDepositedCash(BigDecimal depositedCash) {
		this.depositedCash = depositedCash;
	}

	/**
	 * @return the withdrawableCash
	 */
	@Column(name = "withdrawable_cash", nullable = false)
	public BigDecimal getWithdrawableCash() {
		return withdrawableCash;
	}

	/**
	 * @param withdrawableCash the withdrawableCash to set
	 */
	public void setWithdrawableCash(BigDecimal withdrawableCash) {
		this.withdrawableCash = withdrawableCash;
	}

	/**
	 * @return the pendingEarnedCash
	 */
	@Column(name = "pending_earned_cash", nullable = false)
	public BigDecimal getPendingEarnedCash() {
		return pendingEarnedCash;
	}

	/**
	 * @param pendingEarnedCash the pendingEarnedCash to set
	 */
	public void setPendingEarnedCash(BigDecimal pendingEarnedCash) {
		this.pendingEarnedCash = pendingEarnedCash;
	}

	/**
	 * @return the accountsPayableBalance
	 */
	@Column(name = "accounts_payable_balance", nullable = false)
	public BigDecimal getAccountsPayableBalance() {
		return accountsPayableBalance;
	}

	/**
	 * @param accountsPayableBalance the accountsPayableBalance to set
	 */
	public void setAccountsPayableBalance(BigDecimal accountsPayableBalance) {
		this.accountsPayableBalance = accountsPayableBalance;
	}

	/**
	 * @return the accountsReceivableBalance
	 */
	@Column(name = "accounts_receivable_balance", nullable = false)
	public BigDecimal getAccountsReceivableBalance() {
		return accountsReceivableBalance;
	}

	/**
	 * @param accountsReceivableBalance the accountsReceivableBalance to set
	 */
	public void setAccountsReceivableBalance(BigDecimal accountsReceivableBalance) {
		this.accountsReceivableBalance = accountsReceivableBalance;
	}

	/**
	 * @return the pendingCommitments
	 */
	@Column(name = "pending_commitments", nullable = false)
	public BigDecimal getPendingCommitments() {
		return pendingCommitments;
	}

	/**
	 * @param pendingCommitments the pendingCommitments to set
	 */
	public void setPendingCommitments(BigDecimal pendingCommitments) {
		this.pendingCommitments = pendingCommitments;
	}

	@Column(name = "actual_cash", nullable = false)
	public BigDecimal getActualCash() {
		return actualCash;
	}

	public void setActualCash(BigDecimal actualCash) {
		this.actualCash = actualCash;
	}

	@Column(name = "assignment_throughput", nullable = false)
	public BigDecimal getAssignmentThroughput() {
		return assignmentThroughput;
	}

	public void setAssignmentThroughput(BigDecimal assignmentThroughput) {
		this.assignmentThroughput = assignmentThroughput;
	}

	@Column(name = "assignment_throughput_software", nullable = false)
	public BigDecimal getAssignmentSoftwareThroughput() {
		return assignmentSoftwareThroughput;
	}

	public void setAssignmentSoftwareThroughput(BigDecimal assignmentSoftwareThroughput) {
		this.assignmentSoftwareThroughput = assignmentSoftwareThroughput;
	}

	@Deprecated
	@Column(name = "assignment_throughput_vor", nullable = false)
	public BigDecimal getAssignmentVorThroughput() {
		return assignmentVorThroughput;
	}

	public void setAssignmentVorThroughput(BigDecimal assignmentVorThroughput) {
		this.assignmentVorThroughput = assignmentVorThroughput;
	}

	@Override
	public String toString() {
		return "AccountRegisterSummaryFields{" +
				"accountsPayableBalance=" + accountsPayableBalance +
				", availableCash=" + availableCash +
				", generalCash=" + generalCash +
				", projectCash=" + projectCash +
				", depositedCash=" + depositedCash +
				", withdrawableCash=" + withdrawableCash +
				", pendingEarnedCash=" + pendingEarnedCash +
				", accountsReceivableBalance=" + accountsReceivableBalance +
				", pendingCommitments=" + pendingCommitments +
				", actualCash=" + actualCash +
				", assignmentThroughput=" + assignmentThroughput +
				", assignmentSoftwareThroughput=" + assignmentSoftwareThroughput +
				", assignmentVorThroughput=" + assignmentVorThroughput +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof AccountRegisterSummaryFields)) return false;

		AccountRegisterSummaryFields that = (AccountRegisterSummaryFields) o;

		if (accountsPayableBalance != null ? !accountsPayableBalance.equals(that.accountsPayableBalance) : that.accountsPayableBalance != null)
			return false;
		if (accountsReceivableBalance != null ? !accountsReceivableBalance.equals(that.accountsReceivableBalance) : that.accountsReceivableBalance != null)
			return false;
		if (actualCash != null ? !actualCash.equals(that.actualCash) : that.actualCash != null) return false;
		if (assignmentSoftwareThroughput != null ? !assignmentSoftwareThroughput.equals(that.assignmentSoftwareThroughput) : that.assignmentSoftwareThroughput != null)
			return false;
		if (assignmentThroughput != null ? !assignmentThroughput.equals(that.assignmentThroughput) : that.assignmentThroughput != null)
			return false;
		if (assignmentVorThroughput != null ? !assignmentVorThroughput.equals(that.assignmentVorThroughput) : that.assignmentVorThroughput != null)
			return false;
		if (availableCash != null ? !availableCash.equals(that.availableCash) : that.availableCash != null)
			return false;
		if (depositedCash != null ? !depositedCash.equals(that.depositedCash) : that.depositedCash != null)
			return false;
		if (generalCash != null ? !generalCash.equals(that.generalCash) : that.generalCash != null) return false;
		if (pendingCommitments != null ? !pendingCommitments.equals(that.pendingCommitments) : that.pendingCommitments != null)
			return false;
		if (pendingEarnedCash != null ? !pendingEarnedCash.equals(that.pendingEarnedCash) : that.pendingEarnedCash != null)
			return false;
		if (projectCash != null ? !projectCash.equals(that.projectCash) : that.projectCash != null) return false;
		if (withdrawableCash != null ? !withdrawableCash.equals(that.withdrawableCash) : that.withdrawableCash != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = availableCash != null ? availableCash.hashCode() : 0;
		result = 31 * result + (generalCash != null ? generalCash.hashCode() : 0);
		result = 31 * result + (projectCash != null ? projectCash.hashCode() : 0);
		result = 31 * result + (depositedCash != null ? depositedCash.hashCode() : 0);
		result = 31 * result + (withdrawableCash != null ? withdrawableCash.hashCode() : 0);
		result = 31 * result + (pendingEarnedCash != null ? pendingEarnedCash.hashCode() : 0);
		result = 31 * result + (accountsPayableBalance != null ? accountsPayableBalance.hashCode() : 0);
		result = 31 * result + (accountsReceivableBalance != null ? accountsReceivableBalance.hashCode() : 0);
		result = 31 * result + (pendingCommitments != null ? pendingCommitments.hashCode() : 0);
		result = 31 * result + (actualCash != null ? actualCash.hashCode() : 0);
		result = 31 * result + (assignmentThroughput != null ? assignmentThroughput.hashCode() : 0);
		result = 31 * result + (assignmentSoftwareThroughput != null ? assignmentSoftwareThroughput.hashCode() : 0);
		result = 31 * result + (assignmentVorThroughput != null ? assignmentVorThroughput.hashCode() : 0);
		return result;
	}
}