package com.workmarket.domains.model.banking;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.audit.AuditedEntity;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.utility.StringUtilities;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Table(name="bank_account")
@Entity(name = "abstractBankAccount")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue(AbstractBankAccount.BASE)
@AuditChanges
public abstract class AbstractBankAccount extends AuditedEntity {

	private static final long serialVersionUID = 1L;

	public static final String BASE = "ABA";
	public static final String ACH = "ACH";
	public static final String PAYPAL = "PPA";
	public static final String GCC = "GCC";


	private String bankName;
	private String nameOnAccount;
	private BankAccountType bankAccountType;
	private Company company;
	private Boolean activeFlag;
	private Country country;
	private Boolean confirmedFlag;
	private Boolean autoWithdraw;

	@Column(name = "bank_name", nullable = false, length=36)
	public String getBankName() {
		return bankName;
	}

	@Column(name = "name_on_account", nullable = false, length=45)
	public String getNameOnAccount() {
		return nameOnAccount;
	}

	@ManyToOne(fetch= FetchType.LAZY)
	@JoinColumn(name="bank_account_type_code", referencedColumnName="code")
	public BankAccountType getBankAccountType() {
		return bankAccountType;
	}

	@ManyToOne(fetch=FetchType.LAZY,cascade=CascadeType.ALL)
	@JoinColumn(name="company_id", referencedColumnName="id")
	public Company getCompany() {
		return company;
	}

	@Column(name = "active_flag", nullable = false, length=1)
	@Type(type="yes_no")
	public Boolean getActiveFlag() {
		return activeFlag;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public void setNameOnAccount(String nameOnAccount) {
		this.nameOnAccount = nameOnAccount;
	}

	public void setBankAccountType(BankAccountType bankAccountType) {
		this.bankAccountType = bankAccountType;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public void setActiveFlag(Boolean activeFlag) {
		this.activeFlag = activeFlag;
	}

	@Fetch(FetchMode.JOIN)
	@ManyToOne
	@JoinColumn(name="country_id", referencedColumnName="id", nullable = false)
	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	@Column(name = "confirmed_flag", nullable = false, length=1)
	@Type(type="yes_no")
	public Boolean getConfirmedFlag() {
		return confirmedFlag;
	}

	@Column(name = "auto_withdraw")
	@Type(type="yes_no")
	public Boolean getAutoWithdraw() {
		return autoWithdraw;
	}

	public void setAutoWithdraw(Boolean autoWithdraw) {
		this.autoWithdraw = autoWithdraw;
	}

	public void setConfirmedFlag(Boolean confirmedFlag) {
		this.confirmedFlag = confirmedFlag;
	}

	@Transient
	public abstract String getType();

	@Transient
	public abstract String getAccountDescription();

	@Transient
	public abstract String getBankAccountSecureNumber();

	public static String formatName(String name, String number) {
		number = StringUtilities.removePrepend(number);
		return String.format("%s (%s)", name, StringUtils.right(number, 4));
	}
}
