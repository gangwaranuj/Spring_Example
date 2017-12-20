package com.workmarket.domains.model.account;

import com.workmarket.domains.model.LookupEntity;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.Entity;
import javax.persistence.Table;

@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@Entity(name = "bank_account_transaction_status")
@Table(name = "bank_account_transaction_status")
public class BankAccountTransactionStatus extends LookupEntity {

	private static final long serialVersionUID = 1L;

	public static final String SUBMITTED = "submitted";
	public static final String APPROVED = "approved";
	public static final String REJECTED = "rejected";
	public static final String PROCESSED = "processed";
	public static final String PROCESSING = "processing";

	public BankAccountTransactionStatus() {
		super();
	}

	public BankAccountTransactionStatus(String code) {
		super(code);
	}
}
