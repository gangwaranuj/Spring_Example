package com.workmarket.domains.model.banking;

import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.utility.StringUtilities;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;
import java.util.Calendar;

@Entity(name = "globalCashCardAccount")
@DiscriminatorValue(AbstractBankAccount.GCC)
@AuditChanges
@NamedQueries({
		@NamedQuery(name = "gcc.accountNumber", query = "select accountNumber from globalCashCardAccount where id = :id")
})
public class GlobalCashCardAccount extends AbstractBankAccount {

	private static final long serialVersionUID = 1L;

	private String accountNumber;
	private Calendar confirmedOn;

	public GlobalCashCardAccount() {
		super();
		setConfirmedFlag(true);
		setActiveFlag(true);
	}

	@Override
	@Transient
	public String getType() {
		return AbstractBankAccount.GCC;
	}

	@Override
	@Transient
	public String getAccountDescription() {
		return "WM Card";
	}

	@Column(name = "account_number", nullable = false)
	public String getAccountNumber() {
		return accountNumber;
	}

	@Column(name = "confirmed_on")
	public Calendar getConfirmedOn() {
		return confirmedOn;
	}

	public void setConfirmedOn(Calendar confirmedOn) {
		this.confirmedOn = confirmedOn;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	@Override
	@Transient
	public String getBankAccountSecureNumber() {
		String lastFourDigit = StringUtilities.getBankAccountLastFourDigits(accountNumber);
		if (StringUtils.isNotBlank(lastFourDigit)) {
			return getBankName().toUpperCase() + " - xxxxxxxx" + lastFourDigit;
		}
		return "N/A";
	}

	@Transient
	public String getKeyfieldLastFour() {
		return StringUtilities.getBankAccountLastFourDigits(accountNumber);
	}
}
