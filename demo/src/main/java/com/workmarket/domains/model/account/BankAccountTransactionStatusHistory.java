package com.workmarket.domains.model.account;

import com.workmarket.domains.model.AbstractEntity;
import com.workmarket.domains.model.User;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Calendar;

@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@Entity(name = "bank_account_transaction_status_history")
@Table(name = "bank_account_transaction_status_history")
public class BankAccountTransactionStatusHistory extends AbstractEntity {

	private static final long serialVersionUID = 1L;
	private User updater;
	private BankAccountTransactionStatus bankAccountTransactionStatus;
	private String notes;
	private Calendar changeDate;
	private BankAccountTransaction bankAccountTransaction;

	public BankAccountTransactionStatusHistory() {
	}

	public BankAccountTransactionStatusHistory(BankAccountTransaction bankAccountTransaction, BankAccountTransactionStatus bankAccountTransactionStatus,
											   Calendar changeDate, String notes, User updater) {
		this.bankAccountTransaction = bankAccountTransaction;
		this.bankAccountTransactionStatus = bankAccountTransactionStatus;
		this.changeDate = changeDate;
		this.notes = notes;
		this.updater = updater;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "updater_id", referencedColumnName = "id")
	public User getUpdater() {
		return updater;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bank_account_transaction_status_code", referencedColumnName = "code")
	public BankAccountTransactionStatus getBankAccountTransactionStatus() {
		return bankAccountTransactionStatus;
	}

	@Column(name = "notes", nullable = false)
	public String getNotes() {
		return notes;
	}

	@Column(name = "change_date", nullable = false)
	public Calendar getChangeDate() {
		return changeDate;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "transaction_id", referencedColumnName = "id")
	public BankAccountTransaction getBankAccountTransaction() {
		return bankAccountTransaction;
	}

	public void setUpdater(User updater) {
		this.updater = updater;
	}

	public void setBankAccountTransactionStatus(
		BankAccountTransactionStatus bankAccountTransactionStatus) {
		this.bankAccountTransactionStatus = bankAccountTransactionStatus;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public void setChangeDate(Calendar changeDate) {
		this.changeDate = changeDate;
	}

	public void setBankAccountTransaction(
		BankAccountTransaction bankAccountTransaction) {
		this.bankAccountTransaction = bankAccountTransaction;
	}

}
