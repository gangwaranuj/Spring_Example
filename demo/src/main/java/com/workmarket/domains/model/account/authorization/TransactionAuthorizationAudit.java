package com.workmarket.domains.model.account.authorization;

import com.workmarket.domains.model.audit.AuditedEntity;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import java.util.Calendar;


@Entity(name = "transactionAuthorizationAudit")
@Table(name = "transaction_authorization_audit")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("TAA")
@AuditChanges
public abstract class TransactionAuthorizationAudit extends AuditedEntity {

	private static final long serialVersionUID = 1L;

	private String providerName;
	private String firstName;
	private String lastName;
	private String creditCardType;
	private String transactionResponse;
	private String transactionOrderId;
	private Calendar transactionDate;
	private String transactionErrorMessage;
	private String transactionApprovalCode;

	protected TransactionAuthorizationAudit() {
	}

	@Column(name = "provider_name", nullable = false, length = 20)
	public String getProviderName() {
		return providerName;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}

	@Column(name = "first_name", length = 50)
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Column(name = "last_name", length = 50)
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Column(name = "credit_card_type", length = 15)
	public String getCreditCardType() {
		return creditCardType;
	}

	public void setCreditCardType(String creditCardType) {
		this.creditCardType = creditCardType;
	}

	@Column(name = "transaction_response", length = 100)
	public String getTransactionResponse() {
		return transactionResponse;
	}

	public void setTransactionResponse(String transactionResponse) {
		this.transactionResponse = transactionResponse;
	}

	@Column(name = "transaction_order_id", length = 100)
	public String getTransactionOrderId() {
		return transactionOrderId;
	}

	public void setTransactionOrderId(String transactionOrderId) {
		this.transactionOrderId = transactionOrderId;
	}

	@Column(name = "transaction_date")
	public Calendar getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Calendar transactionDate) {
		this.transactionDate = transactionDate;
	}

	@Column(name = "transaction_error_message", length = 100)
	public String getTransactionErrorMessage() {
		return transactionErrorMessage;
	}

	public void setTransactionErrorMessage(String transactionErrorCode) {
		this.transactionErrorMessage = transactionErrorCode;
	}

	@Column(name = "transaction_approval_code", length = 100)
	public String getTransactionApprovalCode() {
		return transactionApprovalCode;
	}

	public void setTransactionApprovalCode(String transactionApprovalCode) {
		this.transactionApprovalCode = transactionApprovalCode;
	}
}
