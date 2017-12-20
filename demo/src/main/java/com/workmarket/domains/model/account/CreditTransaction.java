package com.workmarket.domains.model.account;

import com.workmarket.domains.model.audit.AuditChanges;
import org.apache.commons.lang.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;


@Entity(name = "credit_transaction")
@Table(name = "credit_transaction")
@AuditChanges
public class CreditTransaction extends RegisterTransaction {

	private static final long serialVersionUID = 1L;

	private String note;

	@Column(name = "note", nullable = true)
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@Transient
	public String getType() {
		if (getRegisterTransactionType().getCode().equals(RegisterTransactionType.CREDIT_CHECK_DEPOSIT)) {
			return "Check";
		} else if (getRegisterTransactionType().getCode().equals(RegisterTransactionType.CREDIT_WIRE_DIRECT_DEPOSIT)) {
			return "Direct Deposit";
		}
		return StringUtils.EMPTY;
	}
}
