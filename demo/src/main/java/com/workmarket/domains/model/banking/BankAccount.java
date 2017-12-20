package com.workmarket.domains.model.banking;

import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.utility.StringUtilities;
import com.workmarket.vault.models.Securable;
import com.workmarket.vault.models.Secured;
import com.workmarket.vault.models.Vaultable;
import com.workmarket.vault.models.Vaulted;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.Calendar;

@Entity(name="bank_account")
@DiscriminatorValue(AbstractBankAccount.ACH)
@AuditChanges
@Vaultable
@Securable
public class BankAccount extends AbstractBankAccount {
	// Vaulted field names; this is for convenience. We should look to refactor the VaultHelper interface to not
	// take strings at some point soon.
	public static String VAULTED_FIELD_ACCOUNT_NUMBER = "accountNumber";

	private static final long serialVersionUID = 1L;

	private String routingNumber;
	@Vaulted @Secured private String accountNumber;
	private Calendar confirmedOn;
	private Integer confirmationAttempts = 0;

	public BankAccount() {
		super();
	}

	@Column(name = "routing_number", nullable = false, length=9)
	public String getRoutingNumber() {
		return routingNumber;
	}

	@Column(name = "account_number", nullable = false, length=20)
	public String getAccountNumber() {
		return accountNumber;
	}

	@Column(name = "confirmed_on")
	public Calendar getConfirmedOn() {
		return confirmedOn;
	}

	public void setRoutingNumber(String routingNumber) {
		this.routingNumber = routingNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public void setConfirmedOn(Calendar confirmedOn) {
		this.confirmedOn = confirmedOn;
	}

	@Column(name = "confirmation_attempts", nullable = false, length=1)
	public Integer getConfirmationAttempts() {
		return confirmationAttempts;
	}

	public void setConfirmationAttempts(Integer confirmationAttempts) {
		this.confirmationAttempts = confirmationAttempts;
	}

	@Override
	@Transient
	public String getType() {
		return AbstractBankAccount.ACH;
	}

	@Override
	@Transient
	public String getAccountDescription() {
		return formatName(getBankName(), accountNumber);
	}

	@Override
	@Transient
	public String getBankAccountSecureNumber() {
		String acctNum = getAccountNumberSanitized();
		String lastFourDigit = StringUtilities.getBankAccountLastFourDigits(acctNum);
		if (StringUtils.isNotBlank(lastFourDigit)) {
			return getBankName().toUpperCase() + " - XXXXXXXX" + lastFourDigit;
		}
		return "N/A";
	}

	/**
	 * Return the account number without the prefix obfuscation value, if there is one. The prefix is added as
	 * a temporary obfuscation marker before full obfuscation, so this method will only be needed temporarily.
	 *
	 * @return tax number without the obfuscation prefix
	 */
	@Transient
	public String getAccountNumberSanitized() {
		return StringUtilities.removePrepend(accountNumber);
	}
}
