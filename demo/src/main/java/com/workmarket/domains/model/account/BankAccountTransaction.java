package com.workmarket.domains.model.account;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.banking.AbstractBankAccount;
import com.workmarket.domains.model.audit.AuditChanges;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Fetch;

import javax.persistence.*;
import java.util.Calendar;
import java.util.List;


@Entity(name = "bank_account_transaction")
@Table(name = "bank_account_transaction")
@AuditChanges
public class BankAccountTransaction extends RegisterTransaction {

	private static final long serialVersionUID = 1L;

	private AbstractBankAccount bankAccount;
	private BankAccountTransactionStatus bankAccountTransactionStatus;
	private List<BankAccountTransactionStatusHistory> bankAccountTransactionStatusHistories;
	private Calendar approvedByBankDate;
	private RegisterTransaction parentRegisterTransaction;

	@Fetch(FetchMode.JOIN)
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "bank_account_id", referencedColumnName = "id", nullable = false)
	public AbstractBankAccount getBankAccount() {
		return bankAccount;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bank_account_transaction_status_code", referencedColumnName = "code")
	public BankAccountTransactionStatus getBankAccountTransactionStatus() {
		return bankAccountTransactionStatus;
	}

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "transaction_id", referencedColumnName = "id")
	public List<BankAccountTransactionStatusHistory> getBankAccountTransactionStatusHistories() {
		return bankAccountTransactionStatusHistories;
	}

	public void setBankAccountTransactionStatus(BankAccountTransactionStatus bankAccountTransactionStatus) {
		this.bankAccountTransactionStatus = bankAccountTransactionStatus;
	}

	public void setBankAccountTransactionStatusHistories(
		List<BankAccountTransactionStatusHistory> bankAccountTransactionStatusHistories) {
		this.bankAccountTransactionStatusHistories = bankAccountTransactionStatusHistories;
	}

	public void setBankAccount(AbstractBankAccount bankAccount) {
		this.bankAccount = bankAccount;
	}


	@Column(name = "approved_by_bank_date")
	public Calendar getApprovedByBankDate() {
		return approvedByBankDate;
	}

	public void setApprovedByBankDate(Calendar approvedByBankDate) {
		this.approvedByBankDate = approvedByBankDate;
	}

	@Transient
	public String getType() {
		return "ACH";
	}

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_transaction_id", referencedColumnName = "id")
	public RegisterTransaction getParentRegisterTransaction() {
		return parentRegisterTransaction;
	}

	public void setParentRegisterTransaction(RegisterTransaction parentRegisterTransaction) {
		this.parentRegisterTransaction = parentRegisterTransaction;
	}

	@Transient
	@Override
	public void copyStatus(RegisterTransaction tx) {
		super.copyStatus(tx);
		if (tx instanceof BankAccountTransaction) {
			BankAccountTransaction btx = (BankAccountTransaction) tx;
			setApprovedByBankDate(btx.getApprovedByBankDate());
			setBankAccountTransactionStatus(btx.getBankAccountTransactionStatus());
		}
	}

	@Transient
	public void addHistory(BankAccountTransactionStatusHistory history) {
		if (bankAccountTransactionStatusHistories == null) {
			setBankAccountTransactionStatusHistories(Lists.<BankAccountTransactionStatusHistory>newArrayList());
		}
		bankAccountTransactionStatusHistories.add(history);
	}
}
