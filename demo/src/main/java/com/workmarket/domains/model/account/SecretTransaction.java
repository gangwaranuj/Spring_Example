package com.workmarket.domains.model.account;

import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.*;
import java.util.Calendar;


@Entity(name = "secret_transaction")
@Table(name = "secret_transaction")
@AuditChanges
public class SecretTransaction extends RegisterTransaction {

	private static final long serialVersionUID = 1L;

	private RegisterTransaction parentRegisterTransaction;
	private Calendar approvedDate;

	@Transient
	public String getType() {
		return "SHH";
	}

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_transaction_id", referencedColumnName = "id")
	public RegisterTransaction getParentRegisterTransaction() {
		return parentRegisterTransaction;
	}

	public void setParentRegisterTransaction(RegisterTransaction parentRegisterTransaction) {
		this.parentRegisterTransaction = parentRegisterTransaction;
	}

	@Column(name = "approved_date")
	public Calendar getApprovedDate() {
		return approvedDate;
	}

	public void setApprovedDate(Calendar approvedDate) {
		this.approvedDate = approvedDate;
	}

	@Transient
	@Override
	public void copyStatus(RegisterTransaction tx) {
		super.copyStatus(tx);
		if (tx instanceof BankAccountTransaction) {
			BankAccountTransaction btx = (BankAccountTransaction) tx;
			setApprovedDate(btx.getApprovedByBankDate());
		}
	}
}
