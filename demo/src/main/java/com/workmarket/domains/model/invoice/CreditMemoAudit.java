package com.workmarket.domains.model.invoice;

import com.workmarket.domains.model.AbstractEntity;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Calendar;

@Entity(name = "creditMemoAudit")
@Table(name = "credit_memo_audit")
@AuditChanges
public class CreditMemoAudit extends AbstractEntity {

	private CreditMemo creditMemo;
	private AbstractServiceInvoice serviceInvoice;
	private Integer reasonId;
	private String note;
	private Calendar createdOn;

	@Column(name = "created_on", nullable = false, updatable = false)
	public Calendar getCreatedOn() {
		return createdOn;
	}

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "invoice_id")
	public CreditMemo getCreditMemo() {
		return creditMemo;
	}

	public CreditMemoAudit setCreditMemo(CreditMemo creditMemo) {
		this.creditMemo = creditMemo;
		return this;
	}

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "original_invoice_id")
	public AbstractServiceInvoice getServiceInvoice() {
		return serviceInvoice;
	}

	public CreditMemoAudit setServiceInvoice(AbstractServiceInvoice serviceInvoice) {
		this.serviceInvoice = serviceInvoice;
		return this;
	}

	@Column(name = "reason", nullable = true)
	public Integer getReasonId() {
		return reasonId;
	}

	public CreditMemoAudit setReasonId(Integer reasonId) {
		this.reasonId = reasonId;
		return this;
	}

	@Column(name = "note", nullable = true)
	public String getNote() {
		return note;
	}

	public CreditMemoAudit setNote(String note) {
		this.note = note;
		return this;
	}

	public CreditMemoAudit setCreatedOn(Calendar createdOn) {
		this.createdOn = createdOn;
		return this;
	}
}
