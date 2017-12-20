package com.workmarket.domains.model.banking;

import com.workmarket.domains.model.LookupEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity(name="bank_account_type")
@Table(name="bank_account_type")
public class BankAccountType extends LookupEntity{
	
	private static final long serialVersionUID = 1L;
	
	public static String CHECKING = "checking";
	public static String SAVINGS = "savings";
	public static String PAY_PAL = "payPal";
	public static String GLOBAL_CASH_CARD = "GCC";

	public BankAccountType(){}
	public BankAccountType(String code){
		super(code);
	}

	@Transient
	public boolean isCheckingAccount() {
		return CHECKING.equals(this.code);
	}

	@Transient
	public boolean isSavingsAccount() {
		return SAVINGS.equals(this.code);
	}

	@Transient
	public boolean isPayPalAccount() {
		return PAY_PAL.equals(this.code);
	}

	@Transient
	public boolean isGCCAccount() {
		return GLOBAL_CASH_CARD.equals(this.code);
	}
}
