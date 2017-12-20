package com.workmarket.domains.model.banking;

import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.audit.AuditChanges;
import org.apache.commons.lang.StringUtils;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity(name = "payPalAccount")
@DiscriminatorValue(AbstractBankAccount.PAYPAL)
@AuditChanges
public class PayPalAccount extends AbstractBankAccount {

	private static final long serialVersionUID = 1L;

	private String emailAddress;

	public PayPalAccount() {
		super();
		setConfirmedFlag(true);
		setActiveFlag(true);
	}

	@Column(name = "email", nullable = false, length = Constants.EMAIL_MAX_LENGTH)
	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	@Override
	@Transient
	public String getType() {
		return AbstractBankAccount.PAYPAL;
	}

	@Override
	@Transient
	public String getAccountDescription() {
		return emailAddress;
	}

	@Override
	@Transient
	public String getBankAccountSecureNumber() {
		return StringUtils.EMPTY;
	}
}

