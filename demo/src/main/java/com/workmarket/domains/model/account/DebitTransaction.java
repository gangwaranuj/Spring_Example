package com.workmarket.domains.model.account;

import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


@Entity(name = "debit_transaction")
@Table(name = "debit_transaction")
@AuditChanges
public class DebitTransaction extends RegisterTransaction {

	private static final long serialVersionUID = 1L;

	private String note;

	@Column(name = "note", nullable = true)
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
}
