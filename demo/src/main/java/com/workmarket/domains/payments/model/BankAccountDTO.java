package com.workmarket.domains.payments.model;

import com.google.common.collect.ImmutableMap;
import com.workmarket.domains.model.banking.BankAccountType;
import com.workmarket.dto.AddressDTO;
import com.workmarket.utility.StringUtilities;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BankAccountDTO extends AddressDTO {
	private String type;

	// ACH
	private String bankName;
	private String nameOnAccount;
	private String routingNumber;
	private String institutionNumber; // also known as FIN number for Canada
	private String branchNumber;
	private String accountNumber;
	private String accountNumberConfirm;
	private String bankAccountTypeCode = BankAccountType.CHECKING;

	// PayPal
	private String emailAddress;
	private String countryCode;

	//GCC
	private String firstName;
	private String lastName;
	private String govId;
	private String govIdType;
	private Integer dobDay;
	private Integer dobMonth;
	private Integer dobYear;
	private String ccLastFour;

	//GCC Physical address
	/* This are temporary fields which we need in case if user used PO BOX for address
		we will just use this values to pass over to GCC no need to save them in db
		since we will have primary address.
	 */
	private String firstName2;
	private String lastName2;
	private String city2;
	private String state2;
	private String postalCode2;
	private String country2;
	private String alternativeAddress;
	private String alternativeAddress2;

	private boolean mainAddressIsDifferentThenPermanent = false;

	public static final Map<String, String> GOV_ID_TYPE_MAP = new ImmutableMap.Builder<String, String>()
			.put("SSN", "Social Security Number")
			.put("NONUSTAXID", "Non US Tax ID")
			.put("USSTATEID", "US State ID")
			.build();

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getNameOnAccount() {
		return nameOnAccount;
	}

	public void setNameOnAccount(String nameOnAccount) {
		this.nameOnAccount = nameOnAccount;
	}

	public String getRoutingNumber() {
		return routingNumber;
	}

	public void setRoutingNumber(String routingNumber) {
		this.routingNumber = routingNumber;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getAccountNumberConfirm() {
		return accountNumberConfirm;
	}

	public void setAccountNumberConfirm(String accountNumberConfirm) {
		this.accountNumberConfirm = accountNumberConfirm;
	}

	public String getBankAccountTypeCode() {
		return bankAccountTypeCode;
	}

	public void setBankAccountTypeCode(String bankAccountTypeCode) {
		this.bankAccountTypeCode = bankAccountTypeCode;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}


	public String getGovId() {
		return govId;
	}

	public void setGovId(String govId) {
		this.govId = govId;
	}

	public String getGovIdType() {
		return govIdType;
	}

	public void setGovIdType(String govIdType) {
		this.govIdType = govIdType;
	}

	public Integer getDobDay() {
		return dobDay;
	}

	public void setDobDay(Integer dobDay) {
		this.dobDay = dobDay;
	}

	public Integer getDobMonth() {
		return dobMonth;
	}

	public void setDobMonth(Integer dobMonth) {
		this.dobMonth = dobMonth;
	}

	public Integer getDobYear() {
		return dobYear;
	}

	public void setDobYear(Integer dobYear) {
		this.dobYear = dobYear;
	}

	public String getCcLastFour() {
		return ccLastFour;
	}

	public void setCcLastFour(String ccLastFour) {
		this.ccLastFour = ccLastFour;
	}

	public String getFirstName2() {
		return firstName2;
	}

	public void setFirstName2(String firstName2) {
		this.firstName2 = firstName2;
	}

	public String getLastName2() {
		return lastName2;
	}

	public void setLastName2(String lastName2) {
		this.lastName2 = lastName2;
	}

	public String getCity2() {
		return city2;
	}

	public void setCity2(String city2) {
		this.city2 = city2;
	}

	public String getState2() {
		return state2;
	}

	public void setState2(String state2) {
		this.state2 = state2;
	}

	public String getPostalCode2() {
		return postalCode2;
	}

	public void setPostalCode2(String postalCode2) {
		this.postalCode2 = postalCode2;
	}


	public String getCountry2() {
		return country2;
	}

	public void setCountry2(String country2) {
		this.country2 = country2;
	}

	public String getAlternativeAddress() {
		return alternativeAddress;
	}

	public void setAlternativeAddress(String alternativeAddress) {
		this.alternativeAddress = alternativeAddress;
	}

	public boolean isMainAddressIsDifferentThenPermanent() {
		return mainAddressIsDifferentThenPermanent;
	}

	public void setMainAddressIsDifferentThenPermanent(boolean mainAddressIsDifferentThenPermanent) {
		this.mainAddressIsDifferentThenPermanent = mainAddressIsDifferentThenPermanent;
	}

	public String getAlternativeAddress2() {
		return alternativeAddress2;
	}

	public void setAlternativeAddress2(String alternativeAddress2) {
		this.alternativeAddress2 = alternativeAddress2;
	}

	public String getInstitutionNumber() { return institutionNumber;}

	public void setInstitutionNumber(String institutionNumber) {this.institutionNumber = institutionNumber;}

	public String getBranchNumber() { return branchNumber; }

	public void setBranchNumber(String branchNumber) { this.branchNumber = branchNumber; }

	public List<NameValuePair> toNameValuePairs() {
		List<NameValuePair> params = new LinkedList<>();

		boolean isAddressPOBox = StringUtilities.isPoBox(String.valueOf(getAddress1()));


		params.add(new BasicNameValuePair("firstname", String.valueOf(getFirstName())));
		params.add(new BasicNameValuePair("lastname", String.valueOf(getLastName())));
		params.add(new BasicNameValuePair("address", String.valueOf(getAddress1())));
		params.add(new BasicNameValuePair("city", String.valueOf(getCity())));
		params.add(new BasicNameValuePair("country", String.valueOf(getCountry())));
		params.add(new BasicNameValuePair("state", String.valueOf(getState())));
		params.add(new BasicNameValuePair("zipcode", String.valueOf(getPostalCode())));
		params.add(new BasicNameValuePair("govid", String.valueOf(getGovId())));
		params.add(new BasicNameValuePair("govidtype", String.valueOf(getGovIdType())));
		params.add(new BasicNameValuePair("dob", String.valueOf(getDobMonth() + "/" + getDobDay() + "/" + getDobYear())));
		params.add(new BasicNameValuePair("institutionNumber", String.valueOf(getInstitutionNumber())));
		params.add(new BasicNameValuePair("branchNumber", String.valueOf(getBranchNumber())));

		if(Boolean.TRUE.equals(isMainAddressIsDifferentThenPermanent()) && Boolean.TRUE.equals(isAddressPOBox)){
			params.add(new BasicNameValuePair("address2", String.valueOf(getAlternativeAddress())));
			params.add(new BasicNameValuePair("city2", String.valueOf(getCity2())));
			params.add(new BasicNameValuePair("country2", String.valueOf(getCountry2())));
			params.add(new BasicNameValuePair("state2", String.valueOf(getState2())));
			params.add(new BasicNameValuePair("zipcode2", String.valueOf(getPostalCode2())));
		}

		return params;
	}

	public String toString() {
		return String.format("BankAccountDTO{" +
				"firstname=%s " +
				", lastname=%s " +
				", address=%s" +
				", city=%s  " +
				", state=%s " +
				", country=%s  " +
				", zipcode=%s  " +
				", govidtype=%s " +
				", institutionNumber=%s " +
				", branchNumber=%s " +
				", dob=%s}",
				getFirstName(),
				getLastName(),
				getAddress1(),
				getCity(),
				getState(),
				getCountry(),
				getPostalCode(),
				getGovIdType(),
				getInstitutionNumber(),
				getBranchNumber(),
				String.valueOf(getDobMonth() + "/" + getDobDay() + "/" + getDobYear()));
	}

}
